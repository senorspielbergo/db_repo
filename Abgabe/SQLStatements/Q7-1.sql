/*
* Identisch zu Q3-1.sql; arbeitet jedoch direkt auf den Stimmzetteln und nicht auf voraggregierten Daten.
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
waehler AS -- Anzahl abgegebener Stimmzettel
(
   SELECT
      bs.wahlkreis,
      COUNT(*) AS anzahl 
   FROM
      bayerischestimmzettel bs 
   GROUP BY
      bs.wahlkreis 
)
SELECT
   wk.nummer,
   wk.name,
   wk.bundesland,
   w.anzahl / CAST(wb.wahlberechtigte AS NUMERIC) AS wahlbeteiligung 
FROM
   wahlkreis wk 
   JOIN
      waehler w 
      ON wk.nummer = w.wahlkreis 
   JOIN
      wahlberechtigte wb 
      ON wk.nummer = wb.wahlkreis 
WHERE
   wk.nummer =% wahlkreis_nr % 
   AND wb.wahljahr =% wahljahr % ;