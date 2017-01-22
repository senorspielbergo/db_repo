SELECT l.partei, wspl.stimmen, wspl.prozent
FROM wahlkreisstimmenprolandesliste wspl JOIN landesliste l ON l.id=wspl.landesliste
WHERE wspl.wahlkreis=%wahlkreis_nr% AND l.wahljahr=%wahljahr%
ORDER BY wspl.stimmen DESC;
