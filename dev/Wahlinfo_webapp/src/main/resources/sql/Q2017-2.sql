SELECT l.partei 
FROM landesliste l JOIN wahlkreis w ON w.bundesland=l.bundesland
WHERE l.wahljahr=2013 AND w.nummer=%wahlkreis_nr%;