WITH wahlkreiszweitstimmensieger AS (
    SELECT w.wahlkreis, MAX(w.stimmen) as stimmen
    FROM wahlkreisstimmenprolandesliste w
WHERE w.wahljahr=%wahljahr%
    GROUP BY w.wahlkreis)
SELECT w.nummer, w.name, b.partei AS erststimmen_partei, wks.stimmen AS erststimmen,
		l.partei AS zweitstimmen_partei, wkspl.stimmen AS zweitstimmen
FROM wahlkreis w JOIN wahlkreiszweitstimmensieger wkzs ON w.nummer=wkzs.wahlkreis
JOIN wahlkreisstimmenprolandesliste wkspl ON wkspl.wahlkreis=wkzs.wahlkreis AND wkspl.stimmen=wkzs.stimmen
JOIN landesliste l ON wkspl.landesliste=l.id JOIN wahlkreissieger wks ON wks.wahlkreis=wkzs.wahlkreis JOIN bewerber b ON wks.direktkandidat=b.id WHERE wkspl.wahljahr=%wahljahr% AND l.wahljahr=%wahljahr% AND wks.wahljahr=%wahljahr%;
