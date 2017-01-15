WITH bayerischestimmzettel AS (
WITH bayerischekreise AS (
    SELECT * FROM wahlkreis WHERE bundesland='Bayern' LIMIT 5)
SELECT s.id, s.direktkandidat, s.landesliste, s.wahlkreis 
FROM stimmzettel s JOIN bayerischekreise w ON w.nummer=s.wahlkreis
WHERE s.wahljahr=%wahljahr%),
waehler AS (
    SELECT bs.wahlkreis, COUNT(*) AS anzahl 
	FROM bayerischestimmzettel bs
	GROUP BY bs.wahlkreis
)
SELECT wk.nummer, wk.name, wk.bundesland, 
w.anzahl / CAST(wk.wahlberechtigte AS NUMERIC) AS wahlbeteiligung
FROM wahlkreis wk JOIN waehler w ON wk.nummer=w.wahlkreis
WHERE wk.nummer=%wahlkreis_nr%;