SELECT b.titel, b.vorname, b.nachname, b.partei
FROM bewerber b JOIN stimmenprokandidat d ON b.id=d.direktkandidat
JOIN wahlkreis w ON d.wahlkreis=w.nummer
WHERE w.nummer=%wahlkreis_nr% AND d.wahljahr=2013;