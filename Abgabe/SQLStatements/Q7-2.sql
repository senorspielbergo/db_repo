/*
* Identisch zu Q3-2.sql; arbeitet jedoch direkt auf den Stimmzetteln und nicht auf voraggregierten Daten.
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
wsieger AS -- Bewerber mit Direktmandat und seine erhaltenen Stimmen
(
   SELECT
      bs.direktkandidat,
      COUNT(bs.direktkandidat) AS stimmen 
   FROM
      bayerischestimmzettel bs 
   WHERE
      bs.direktkandidat NOTNULL 
   GROUP BY
      bs.direktkandidat 
   ORDER BY
      stimmen DESC LIMIT 1 
)
,
gstimmen AS -- Gültige Erststimmen
(
   SELECT
      COUNT(bs.direktkandidat) AS stimmen 
   FROM
      bayerischestimmzettel bs 
   WHERE
      bs.direktkandidat NOTNULL
)
SELECT
   b.titel,
   b.vorname,
   b.nachname,
   b.partei,
   s.stimmen,
   CAST(s.stimmen AS NUMERIC) / sg.stimmen AS prozent 
FROM
   bewerber b 
   JOIN
      wsieger s 
      ON s.direktkandidat = b.id,
      wahlkreis w,
      gstimmen sg 
WHERE
   w.nummer =% wahlkreis_nr % ;