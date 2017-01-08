SELECT w.nummer, w.name, w.bundesland, 
(sg1.waehler - sg2.waehler) AS waehlerdiffabs, 
(sg1.waehler / CAST(w.wahlberechtigte AS NUMERIC) - sg2.waehler / CAST(w.wahlberechtigte AS NUMERIC)) AS waehlerdiffpro,
(sg1.g_erststimmen - sg2.g_erststimmen) as gediffabs,
(sg1.g_erststimmen / CAST(sg1.waehler AS NUMERIC) - sg2.g_erststimmen / CAST(sg2.waehler AS NUMERIC)) AS gediffpro,
(sg1.ug_erststimmen - sg2.ug_erststimmen) as ugediffabs,
(sg1.ug_erststimmen / CAST(sg1.waehler AS NUMERIC) - sg2.ug_erststimmen / CAST(sg2.waehler AS NUMERIC)) AS ugediffpro,
(sg1.g_zweitstimmen - sg2.g_zweitstimmen) as gzdiffabs,
(sg1.g_zweitstimmen / CAST(sg1.waehler AS NUMERIC) - sg2.g_zweitstimmen / CAST(sg2.waehler AS NUMERIC)) AS gzdiffpro,
(sg1.ug_zweitstimmen - sg2.ug_zweitstimmen) as ugzdiffabs,
(sg1.ug_zweitstimmen / CAST(sg1.waehler AS NUMERIC) - sg2.ug_zweitstimmen / CAST(sg2.waehler AS NUMERIC)) AS ugzdiffpro
FROM wahlkreis w JOIN stimmengueltigkeit sg1 ON w.nummer=sg1.wahlkreis 
JOIN stimmengueltigkeit sg2 ON sg1.wahljahr > sg2.wahljahr AND sg1.wahlkreis=sg2.wahlkreis
WHERE w.nummer=%wahlkreis_nr% AND sg1.wahljahr=%wahljahr%;