WITH bayerischestimmzettel AS (
SELECT s.id, s.direktkandidat, s.landesliste, s.wahlkreis 
FROM stimmzettel s
WHERE s.wahljahr=%wahljahr% AND s.wahlkreis=%wahlkreis_nr%),
waehler AS (
    SELECT bs.wahlkreis, COUNT(*) AS anzahl 
	FROM bayerischestimmzettel bs
	GROUP BY bs.wahlkreis
)
SELECT wk.nummer, wk.name, wk.bundesland, 
w.anzahl / CAST(wb.wahlberechtigte AS NUMERIC) AS wahlbeteiligung
FROM wahlkreis wk JOIN waehler w ON wk.nummer=w.wahlkreis
JOIN wahlberechtigte wb ON wk.nummer=wb.wahlkreis
WHERE wk.nummer=%wahlkreis_nr% AND wb.wahljahr=%wahljahr%;