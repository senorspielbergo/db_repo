/*
* Berechnet die Differenz der abgegebenen, gültigen und ungültigen Erst- und Zweitstimmen zum Vorjahr (wenn vorhanden) für den eingegebenen Wahlkreis und das eingegebene Wahljahr (absolut und prozentual).
* Eingabe: %wahlkreis_nr% = [1; 299] , %wahjahr% = {2009, 2013}
*/

SELECT
   w.nummer,
   w.name,
   w.bundesland,
   (
      sg1.waehler - sg2.waehler
   )
   AS waehlerdiffabs, -- Absolute Differenz der abgegebenen Stimmen
   (
      sg1.waehler / CAST(wb1.wahlberechtigte AS NUMERIC) - sg2.waehler / CAST(wb2.wahlberechtigte AS NUMERIC)
   )
   AS waehlerdiffpro, -- Prozentuale Differenz der abgegebenen Stimmen
   (
      sg1.g_erststimmen - sg2.g_erststimmen
   )
   as gediffabs, -- Absolute Differenz der gültigen Erststimmen
   (
      sg1.g_erststimmen / CAST(sg1.waehler AS NUMERIC) - sg2.g_erststimmen / CAST(sg2.waehler AS NUMERIC)
   )
   AS gediffpro, -- Prozentuale Differenz der gültigen Erststimmen
   (
      sg1.ug_erststimmen - sg2.ug_erststimmen
   )
   as ugediffabs, -- Absolute Differenz der ungültigen Erststimmen
   (
      sg1.ug_erststimmen / CAST(sg1.waehler AS NUMERIC) - sg2.ug_erststimmen / CAST(sg2.waehler AS NUMERIC)
   )
   AS ugediffpro, -- Prozentuale Differenz der ungültigen Erststimmen
   (
      sg1.g_zweitstimmen - sg2.g_zweitstimmen
   )
   as gzdiffabs, -- Absolute Differenz der gültigen Zweitstimmen
   (
      sg1.g_zweitstimmen / CAST(sg1.waehler AS NUMERIC) - sg2.g_zweitstimmen / CAST(sg2.waehler AS NUMERIC)
   )
   AS gzdiffpro, -- Prozentuale Differenz der gültigen Zweitstimmen
   (
      sg1.ug_zweitstimmen - sg2.ug_zweitstimmen
   )
   as ugzdiffabs, -- Absolute Differenz der ungültigen Zweitstimmen
   (
      sg1.ug_zweitstimmen / CAST(sg1.waehler AS NUMERIC) - sg2.ug_zweitstimmen / CAST(sg2.waehler AS NUMERIC)
   )
   AS ugzdiffpro  -- Prozentuale Differenz der ungültigen Zweitstimmen
FROM
   wahlkreis w 
   JOIN
      wahlberechtigte wb1 
      ON w.nummer = wb1.wahlkreis 
   JOIN
      wahlberechtigte wb2 
      ON w.nummer = wb2.wahlkreis 
      AND wb2.wahljahr < wb1.wahljahr 
   JOIN
      stimmengueltigkeit sg1 
      ON w.nummer = sg1.wahlkreis 
   JOIN
      stimmengueltigkeit sg2 
      ON sg1.wahljahr > sg2.wahljahr 
      AND sg1.wahlkreis = sg2.wahlkreis 
WHERE
   w.nummer =% wahlkreis_nr % 
   AND sg1.wahljahr =% wahljahr % 
   AND wb1.wahljahr =% wahljahr %