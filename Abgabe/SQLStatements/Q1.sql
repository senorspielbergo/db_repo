/*
* Berechnet die finale Sitzverteilung des Deutschen Bundestags für das eingegebene Wahljahr.
* Eingabe: %wahljahr% = {2009, 2013}
*/

WITH hoechstzahlen2009 AS 
( -- Höchstzahlen für 2009 (siehe create_materialized_views.sql für genauere Erklärung)
   SELECT
      h.bundesland,
      h.partei,
      (
         row_number() OVER (PARTITION BY h.partei 
      ORDER BY
         h.quotient DESC)
      )
      AS rn 
   FROM
      (
         SELECT
            l.bundesland,
            l1.partei,
            FLOOR(CAST(l.stimmen AS NUMERIC) / u.zahl) AS quotient 
         FROM
            erlaubtelisten l 
            JOIN
               landesliste l1 
               ON l.landesliste = l1.id 
               AND l.wahljahr = l1.wahljahr 
               AND l.wahljahr = 2009,
               ungerade u 
         ORDER BY
            l.bundesland ASC,
            quotient DESC
      )
      AS h 
)
,
ueberhang2009 AS -- Überhangmandate pro Partei auf Bundesebene
(
   WITH ueberhangproland2009 AS -- Überhangmandate pro Partei pro Bundesland
   (
      SELECT
         h.bundesland,
         h.partei,
         GREATEST(COALESCE(dmpl.count, 0) - COUNT(h.partei), 0) AS ueberhang,
         2009 AS wahljahr 
      FROM
         hoechstzahlen2009 h 
         JOIN
            oberverteilung o 
            ON o.partei = h.partei 
            AND o.wahljahr = 2009 
         LEFT JOIN
            direktmandateproland dmpl 
            ON dmpl.bundesland = h.bundesland 
            AND h.partei = dmpl.partei 
            AND dmpl.wahljahr = o.wahljahr 
      WHERE
         h.rn <= o.sitze 
      GROUP BY
         h.bundesland,
         h.partei,
         dmpl.count
   )
   SELECT
      u.partei,
      SUM(u.ueberhang) AS ueberhang,
      u.wahljahr 
   FROM
      ueberhangproland2009 u 
   GROUP BY
      u.partei,
      u.wahljahr 
)
SELECT
   o.partei,
   (
      o.sitze + COALESCE(u.ueberhang, 0) -- Addiere den Überhang (falls vorhanden, also für 2009; sonst NULL, da LEFT JOIN)
   )
   AS sitze,
   o.wahljahr 
FROM
   oberverteilung o 
   LEFT JOIN
      ueberhang2009 u 
      ON o.partei = u.partei 
      AND u.wahljahr = o.wahljahr 
WHERE
   o.wahljahr =% wahljahr % ;