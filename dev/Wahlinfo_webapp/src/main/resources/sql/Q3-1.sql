SELECT w.nummer, w.name, w.bundesland, 
sg.waehler / CAST(w.wahlberechtigte AS NUMERIC) AS wahlbeteiligung
FROM wahlkreis w JOIN stimmengueltigkeit sg ON w.nummer=sg.wahlkreis
WHERE w.nummer=%wahlkreis_nr% AND sg.wahljahr=%wahljahr%;