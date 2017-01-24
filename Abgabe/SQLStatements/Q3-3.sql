/*
* Berechnet die absolute und relative Zahl an abgegebenen Zweitstimmen für jede Partei im eingegebenen Wahlkreis für das eingegebene Wahljahr.
* Eingabe: %wahlkreis_nr% = [1; 299] , %wahjahr% = {2009, 2013}
*/

SELECT
   l.partei,
   wspl.stimmen,
   wspl.prozent 
FROM
   wahlkreisstimmenprolandesliste wspl 
   JOIN
      landesliste l 
      ON l.id = wspl.landesliste 
WHERE
   wspl.wahlkreis =% wahlkreis_nr % 
   AND l.wahljahr =% wahljahr % 
ORDER BY
   wspl.stimmen DESC;