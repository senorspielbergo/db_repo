/*
* Berechnet die Mitglieder des Deutschen Bundestags für das eingegebene Wahljahr.
* Eingabe: %wahljahr% = {2009, 2013}
*/

WITH hoechstzahlen AS -- Höchstzahlverfahren (siehe create_materialized_views.sql für genauere Erklärung)
(
   SELECT
      h.wahljahr,
      h.bundesland,
      h.partei,
      (
         row_number() OVER (PARTITION BY h.wahljahr, h.partei 
      ORDER BY
         h.quotient DESC)
      )
      AS rn 
   FROM
      (
         SELECT
            l.wahljahr,
            l.bundesland,
            l1.partei,
            FLOOR(CAST(l.stimmen AS NUMERIC) / u.zahl) AS quotient 
         FROM
            erlaubtelisten l 
            JOIN
               landesliste l1 
               ON l.landesliste = l1.id 
               AND l.wahljahr = l1.wahljahr,
               ungerade u 
         ORDER BY
            l.bundesland ASC,
            quotient DESC
      )
      AS h 
)
,
unterverteilung AS -- Unterverteilung der Bundestagssitze auf Bundesländer für 2013
(
   WITH laenderaufteilung AS -- Höchstzahlverfahren-Auswertung zur Verteilung der Sitze aus der Oberverteilung auf Bundesländer
   (
      SELECT
         h.bundesland,
         h.partei,
         GREATEST(COUNT(h.partei) - COALESCE(dmpl.count, 0), 0) AS sitze -- Zu verteilende Sitze (Anzahl Partei aus Höchstzahlverfahren minus Direktmandate)
      FROM
         hoechstzahlen h 
         JOIN
            oberverteilung o 
            ON o.partei = h.partei 
            AND o.wahljahr = 2013 
            AND h.wahljahr = 2013 
         LEFT JOIN
            direktmandateproland dmpl 
            ON dmpl.bundesland = h.bundesland 
            AND h.partei = dmpl.partei 
            AND dmpl.wahljahr = 2013 
      WHERE
         h.rn <= o.sitze 
      GROUP BY
         h.bundesland,
         h.partei,
         dmpl.count
   )
,
   zuverteilendesitze AS -- Gesamtsitze einer Partei, die auf die Bundesländer verteilt werden
   (
      SELECT
         l.partei,
         o.sitze + (o.sitze - (SUM(l.sitze) + SUM(COALESCE(d.count, 0)))) AS sitze -- Formel zur Berücksichtigung der Ausgleichsmandate
      FROM
         laenderaufteilung l 
         LEFT JOIN
            direktmandateproland d 
            ON l.bundesland = d.bundesland 
            AND l.partei = d.partei 
            AND d.wahljahr = 2013 
         JOIN
            oberverteilung o 
            ON o.partei = l.partei 
            AND o.wahljahr = 2013 
      GROUP BY
         l.partei,
         o.sitze 
   )
   SELECT
      h.bundesland,
      h.partei,
      COUNT(h.partei) AS sitze 
   FROM
      hoechstzahlen h 
      JOIN
         zuverteilendesitze z 
         ON z.partei = h.partei 
         AND h.wahljahr = 2013 
   WHERE
      h.rn <= z.sitze -- Verteilung auf Landeslisten inklusive Ausgleichsmandate
   GROUP BY
      h.bundesland,
      h.partei 
   ORDER BY
      h.bundesland,
      h.partei
)
,
direktmandate AS -- Bewerber mit Direktmandat
(
   SELECT
      d.wahljahr,
      w.bundesland,
      b.id,
      b.titel,
      b.vorname,
      b.nachname,
      b.partei 
   FROM
      wahlkreissieger d 
      JOIN
         bewerber b 
         ON d.direktkandidat = b.id 
      JOIN
         wahlkreis w 
         ON d.wahlkreis = w.nummer 
      JOIN
         erlaubteparteien e 
         ON b.partei = e.partei 
         AND e.wahljahr = d.wahljahr
)
,
listenverteilung AS 
(
( -- Sitze der Partei pro Land mit Ausgleichsmandaten ohne Direktmandate für 2013
   SELECT
      2013 AS wahljahr, u.bundesland, u.partei, GREATEST(u.sitze - COALESCE(d.count, 0), 0) as sitze
   FROM
      unterverteilung u 
      LEFT JOIN
         direktmandateproland d 
         ON u.bundesland = d.bundesland 
         AND u.partei = d.partei 
         AND d.wahljahr = 2013) 
      UNION ALL
( -- Höchstzahlverfahren-Auswertung Sitze pro Partei pro Land ohne Direktmandate für 2009
      SELECT
         2009 AS wahljahr, h.bundesland, h.partei, GREATEST(COUNT(h.partei) - COALESCE(dmpl.count, 0), 0) AS sitze 
      FROM
         hoechstzahlen h 
         JOIN
            oberverteilung o 
            ON o.partei = h.partei 
            AND o.wahljahr = 2009 
            AND h.wahljahr = 2009 
         LEFT JOIN
            direktmandateproland dmpl 
            ON dmpl.bundesland = h.bundesland 
            AND h.partei = dmpl.partei 
            AND dmpl.wahljahr = o.wahljahr 
      WHERE
         h.rn <= o.sitze 
      GROUP BY
         h.bundesland, h.partei, dmpl.count) 
)
,
verfuegbarelistenplaetze AS -- Bewerber auf den Landeslisten ohne gewählte Direktkandidaten
(
   SELECT
      e.wahljahr,
      e.listen_id,
      e.bewerber_id,
      (
         row_number() OVER (PARTITION BY e.wahljahr, e.listen_id 
      ORDER BY
         e.listenplatz ASC) -- Ordnen und durchnummerieren der übrigen Bewerber pro Wahljahr und Liste nach ihrem Listenplatz
      )
      as listenplatz 
   FROM
      (
         SELECT
            e.wahljahr,
            lp.listen_id,
            lp.bewerber_id,
            lp.listenplatz 
         FROM
            erlaubteparteien e 
            JOIN
               landesliste l 
               ON e.partei = l.partei 
               AND e.wahljahr = l.wahljahr 
            JOIN
               listenplaetze lp 
               ON lp.listen_id = l.id 
            LEFT JOIN
               direktmandate d 
               ON d.wahljahr = l.wahljahr 
               AND d.partei = l.partei 
               AND d.bundesland = l.bundesland 
               AND d.id = lp.bewerber_id 
         WHERE
            d.id ISNULL -- Wähle nur Bewerber, die kein Direktmandat erhalten haben
      )
      AS e
)
,
mitglieder AS -- Mitglieder des Deutschen Bundestags
(
( -- Mitglieder von den Landeslisten anhand der Unterverteilung
   SELECT
      l.bundesland, b.titel, b.vorname, b.nachname, b.partei 
   FROM
      bewerber b 
      JOIN
         verfuegbarelistenplaetze lp 
         ON b.id = lp.bewerber_id 
      JOIN
         landesliste l 
         ON lp.listen_id = l.id 
         AND lp.wahljahr = l.wahljahr 
      JOIN
         listenverteilung u 
         ON l.partei = u.partei 
         AND l.bundesland = u.bundesland 
         AND l.wahljahr = u.wahljahr 
   WHERE
      lp.listenplatz <= u.sitze 
      AND l.wahljahr = % wahljahr % ) 
   UNION ALL
( -- Bewerber mit Direktmandat 
   SELECT
      d.bundesland, d.titel, d.vorname, d.nachname, d.partei 
   FROM
      direktmandate d 
   WHERE
      d.wahljahr =% wahljahr % )
)
SELECT
   * 
FROM
   mitglieder 
ORDER BY
   partei,
   nachname;