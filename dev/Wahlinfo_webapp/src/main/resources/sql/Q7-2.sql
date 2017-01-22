WITH bayerischestimmzettel AS (
SELECT s.id, s.direktkandidat, s.landesliste, s.wahlkreis 
FROM stimmzettel s
WHERE s.wahljahr=%wahljahr% AND s.wahlkreis=%wahlkreis_nr%),
wsieger AS (
        SELECT bs.direktkandidat, COUNT(bs.direktkandidat) AS stimmen
        FROM bayerischestimmzettel bs WHERE bs.direktkandidat NOTNULL
        GROUP BY bs.direktkandidat
    	ORDER BY stimmen DESC
    	LIMIT 1
), 
gstimmen AS (
    SELECT COUNT(bs.direktkandidat) AS stimmen
    FROM bayerischestimmzettel bs
    WHERE bs.direktkandidat NOTNULL)
    
SELECT b.titel, b.vorname, b.nachname, b.partei, s.stimmen, 
CAST(s.stimmen AS NUMERIC)/sg.stimmen AS prozent
FROM bewerber b JOIN  wsieger s ON s.direktkandidat=b.id, wahlkreis w, gstimmen sg
WHERE w.nummer=%wahlkreis_nr%;