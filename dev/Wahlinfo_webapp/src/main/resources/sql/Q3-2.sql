SELECT b.titel, b.vorname, b.nachname, b.partei, s.stimmen, CAST(s.stimmen AS NUMERIC)/sg.g_erststimmen AS prozent
FROM bewerber b JOIN  wahlkreissieger s ON s.direktkandidat=b.id JOIN
wahlkreis w ON w.nummer=s.wahlkreis JOIN stimmengueltigkeit sg ON w.nummer=sg.wahlkreis
WHERE w.nummer=%wahlkreis_nr% AND sg.wahljahr=%wahljahr% AND s.wahljahr=%wahljahr%;