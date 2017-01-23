WITH bayerischestimmzettel AS (
SELECT s.id, s.direktkandidat, s.landesliste, s.wahlkreis 
FROM stimmzettel s
WHERE s.wahljahr=%wahljahr% AND s.wahlkreis=%wahlkreis_nr%),
wstimmenproliste AS (
    WITH wgzs AS (
        SELECT COUNT(bs.landesliste) AS stimmen
        FROM bayerischestimmzettel bs
        WHERE bs.landesliste NOTNULL
        )
    SELECT bs.landesliste, COUNT(bs.landesliste) AS stimmen,
    CAST(COUNT(bs.landesliste) AS NUMERIC) / ges.stimmen AS prozent
    FROM bayerischestimmzettel bs, wgzs ges
    WHERE bs.landesliste NOTNULL
    GROUP BY bs.landesliste, ges.stimmen)
SELECT l.partei, wspl.stimmen, wspl.prozent
FROM wstimmenproliste wspl JOIN landesliste l ON l.id=wspl.landesliste
WHERE l.wahljahr=%wahljahr%
ORDER BY wspl.stimmen DESC;