/*
* Berechnet die Differenz der abgegebenen Erst- und Zweitstimmen für jede Partei zum Vorjahr (wenn vorhanden) für den eingegebenen Wahlkreis und das eingegebene Wahljahr (absolut und prozentual).
* Eingabe: %wahlkreis_nr% = [1; 299] , %wahjahr% = {2009, 2013}
*/

WITH erststimmen AS -- absolute und prozentuale Zahl an gültigen Erststimmen
(
   SELECT
      spk.wahljahr,
      spk.wahlkreis,
      b.partei,
      SUM(spk.stimmen) as e_absolut,
      CAST(SUM(spk.stimmen) AS NUMERIC) / sg.g_erststimmen AS e_prozent 
   FROM
      stimmenprokandidat spk 
      JOIN
         bewerber b 
         ON spk.direktkandidat = b.id 
      JOIN
         stimmengueltigkeit sg 
         ON spk.wahljahr = sg.wahljahr 
         AND spk.wahlkreis = sg.wahlkreis 
   GROUP BY
      spk.wahljahr,
      spk.wahlkreis,
      b.partei,
      sg.g_erststimmen
)
,
zweitstimmen AS -- absolute und prozentuale Zahl an gültigen Zweitstimmen
(
   SELECT
      wspl.wahljahr,
      wspl.wahlkreis,
      l.partei,
      wspl.stimmen as z_absolut,
      wspl.prozent AS z_prozent 
   FROM
      wahlkreisstimmenprolandesliste wspl 
      JOIN
         landesliste l 
         ON l.id = wspl.landesliste 
         AND wspl.wahljahr = l.wahljahr
)
,
helptable AS 
(
   SELECT
      e.wahljahr,
      e.wahlkreis,
      e.partei,
      e.e_absolut,
      e.e_prozent,
      z.z_absolut,
      z.z_prozent 
   FROM
      erststimmen e 
      JOIN
         zweitstimmen z 
         ON e.wahljahr = z.wahljahr 
         AND e.partei = z.partei 
         AND e.wahlkreis = z.wahlkreis
)
SELECT
   h1.wahlkreis,
   h1.partei,
   COALESCE(h1.e_absolut, 0) - COALESCE(h2.e_absolut) AS ediffabs,
   COALESCE(h1.e_prozent, 0) - COALESCE(h2.e_prozent, 0) AS ediffpro,
   COALESCE(h1.z_absolut, 0) - COALESCE(h2.z_absolut, 0) AS zdiffabs,
   COALESCE(h1.z_prozent, 0) - COALESCE(h2.z_prozent, 0) AS zdiffpro 
FROM
   helptable h1 
   JOIN
      helptable h2 
      ON h1.wahljahr > h2.wahljahr 
      AND h1.wahlkreis = h2.wahlkreis 
      AND h1.partei = h2.partei 
      AND h1.wahlkreis =% wahlkreis_nr % 
      AND h1.wahljahr =% wahljahr %