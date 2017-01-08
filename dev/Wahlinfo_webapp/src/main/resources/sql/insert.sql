WITH bewerber AS (
SELECT * FROM bewerber b WHERE b.vorname='%vorname%' AND b.nachname='%nachname%' 
AND b.partei='%partei%' AND b.titel='%titel%'
),
landesliste AS (
SELECT * FROM landesliste l JOIN wahlkreis w ON w.bundesland=l.bundesland 
WHERE w.nummer='%wahlkreis_nr%' AND l.wahljahr=2013 AND l.partei='%partei%'
)
INSERT INTO Stimmzettel (wahljahr, direktkandidat, landesliste, wahlkreis) SELECT '2017', b.id, l.id, '%wahlkreis_nr%' FROM bewerber b, landesliste l