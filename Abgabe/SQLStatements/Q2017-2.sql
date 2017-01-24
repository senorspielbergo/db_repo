/*
* Dient der Webapp zur Abfrage aller Parteien, die im Jahr 2013 eine Landesliste im eingegebenen Wahlkreis hatten (Ben�tigt f�r Stimmabgabe).
* Eingabe: %wahlkreis_nr% = [1; 299]
*/

SELECT
   l.partei 
FROM
   landesliste l 
   JOIN
      wahlkreis w 
      ON w.bundesland = l.bundesland 
WHERE
   l.wahljahr = 2013 
   AND w.nummer =% wahlkreis_nr % ;