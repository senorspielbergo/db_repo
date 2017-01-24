/*
* Identisch zu Q3-3.sql; arbeitet jedoch direkt auf den Stimmzetteln und nicht auf voraggregierten Daten.
* Eingabe: %wahlkreis_nr% = [1; 299] , %wahjahr% = {2009, 2013}
*/

WITH bayerischestimmzettel AS -- Stimmzettel für spezifischen Wahlkreis
(
   SELECT
      s.id,
      s.direktkandidat,
      s.landesliste,
      s.wahlkreis 
   FROM
      stimmzettel s 
   WHERE
      s.wahljahr =% wahljahr % 
      AND s.wahlkreis =% wahlkreis_nr % 
)
,
wstimmenproliste AS -- Absolute und prozentuale Anzahl an Stimmen für die Landeslisten
(
   WITH wgzs AS -- Anzahl gültige Zweitstimmen
   (
      SELECT
         COUNT(bs.landesliste) AS stimmen 
      FROM
         bayerischestimmzettel bs 
      WHERE
         bs.landesliste NOTNULL 
   )
   SELECT
      bs.landesliste,
      COUNT(bs.landesliste) AS stimmen,
      CAST(COUNT(bs.landesliste) AS NUMERIC) / ges.stimmen AS prozent 
   FROM
      bayerischestimmzettel bs,
      wgzs ges 
   WHERE
      bs.landesliste NOTNULL 
   GROUP BY
      bs.landesliste,
      ges.stimmen
)
SELECT
   l.partei,
   wspl.stimmen,
   wspl.prozent 
FROM
   wstimmenproliste wspl 
   JOIN
      landesliste l 
      ON l.id = wspl.landesliste 
WHERE
   l.wahljahr =% wahljahr % 
ORDER BY
   wspl.stimmen DESC;