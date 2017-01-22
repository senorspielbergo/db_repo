WITH bayerischestimmzettel AS (
SELECT s.wahljahr, s.id, s.direktkandidat, s.landesliste
FROM stimmzettel s
WHERE s.wahlkreis=%wahlkreis_nr%),
bstimmenprokandidat AS (
        SELECT bs.wahljahr, bs.direktkandidat, COUNT(bs.direktkandidat) AS stimmen
        FROM bayerischestimmzettel bs WHERE bs.direktkandidat NOTNULL
        GROUP BY bs.wahljahr, bs.direktkandidat),
bstimmengueltigkeit AS (
    SELECT bs.wahljahr, COUNT(bs.direktkandidat) AS g_erststimmen
    FROM bayerischestimmzettel bs
    WHERE bs.direktkandidat NOTNULL
    GROUP BY bs.wahljahr),
bwahlkreisstimmenprolandesliste AS (
    WITH wgzs AS (
        SELECT bs.wahljahr, COUNT(*) AS stimmen
        FROM bayerischestimmzettel bs
        WHERE bs.landesliste NOTNULL
        GROUP BY bs.wahljahr
        )
    SELECT bs.wahljahr, bs.landesliste, COUNT(bs.landesliste) AS stimmen,
    CAST(COUNT(bs.landesliste) AS NUMERIC) / ges.stimmen AS prozent
    FROM bayerischestimmzettel bs JOIN wgzs ges ON ges.wahljahr=bs.wahljahr
    WHERE bs.landesliste NOTNULL
    GROUP BY bs.wahljahr, bs.landesliste, ges.stimmen),
erststimmen AS (
    SELECT spk.wahljahr, b.partei, SUM(spk.stimmen) as e_absolut, CAST(SUM(spk.stimmen) AS NUMERIC)/sg.g_erststimmen AS e_prozent
    FROM bstimmenprokandidat spk JOIN bewerber b ON spk.direktkandidat=b.id
    JOIN bstimmengueltigkeit sg ON spk.wahljahr=sg.wahljahr
 GROUP BY spk.wahljahr, b.partei, sg.g_erststimmen),
zweitstimmen AS (
    SELECT wspl.wahljahr, l.partei, wspl.stimmen as z_absolut, wspl.prozent AS z_prozent
	FROM bwahlkreisstimmenprolandesliste wspl JOIN landesliste l ON l.id=wspl.landesliste AND wspl.wahljahr=l.wahljahr),
helptable AS (
SELECT e.wahljahr, e.partei, e.e_absolut, e.e_prozent, z.z_absolut, z.z_prozent
FROM erststimmen e JOIN zweitstimmen z ON e.wahljahr=z.wahljahr AND e.partei=z.partei)
SELECT h1.partei, COALESCE(h1.e_absolut, 0) - COALESCE(h2.e_absolut) AS ediffabs,
	COALESCE(h1.e_prozent, 0) - COALESCE(h2.e_prozent, 0) AS ediffpro, 
    COALESCE(h1.z_absolut, 0) - COALESCE(h2.z_absolut, 0) AS zdiffabs,
    COALESCE(h1.z_prozent, 0) - COALESCE(h2.z_prozent, 0) AS zdiffpro
FROM helptable h1 JOIN helptable h2 ON h1.wahljahr > h2.wahljahr AND h1.partei=h2.partei AND h1.wahljahr=%wahljahr%;
