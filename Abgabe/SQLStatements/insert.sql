/*
* Dient der Webapp zum Einfügen eines neuen Stimmzettels in die Relation Stimmzettel für das Wahljahr 2013.
* Eingabe: %wahlkreis_nr% = [1; 299], %titel% = {t | t in Bewerber.titel}, %vorname% = {v | v in Bewerber.vorname}, 
* %nachname% = {n | n in Bewerber.nachname}, %bewerber_partei% = {bp | bp in Bewerber.partei},
* %partei% = {p | p in Landesliste.partei}
*/

WITH bewerber AS (
SELECT * FROM bewerber b WHERE b.vorname='%vorname%' AND b.nachname='%nachname%' 
AND b.partei='%bewerber_partei%' AND b.titel='%titel%'
),
landesliste AS (
SELECT * FROM landesliste l JOIN wahlkreis w ON w.bundesland=l.bundesland 
WHERE w.nummer='%wahlkreis_nr%' AND l.wahljahr=2013 AND l.partei='%partei%'
)
INSERT INTO Stimmzettel (wahljahr, direktkandidat, landesliste, wahlkreis) SELECT '2013', b.id, l.id, '%wahlkreis_nr%' FROM bewerber b, landesliste l