/*
* Dient der Webapp zur Abfrage aller existierenden Bewerber eines eingegebenen Wahlkreises, die im Jahr 2013 wählbar waren (Benötigt für Stimmabgabe).
* Eingabe: %wahlkreis_nr% = [1; 299]
*/

SELECT
   b.titel,
   b.vorname,
   b.nachname,
   b.partei 
FROM
   bewerber b 
   JOIN
      stimmenprokandidat d 
      ON b.id = d.direktkandidat 
   JOIN
      wahlkreis w 
      ON d.wahlkreis = w.nummer 
WHERE
   w.nummer =% wahlkreis_nr % 
   AND d.wahljahr = 2013;