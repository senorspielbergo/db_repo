/*
* Berechnet die Wahlbeteiligung für den eingegebenen Wahlkreis und das eingegebene Wahljahr.
* Eingabe: %wahlkreis_nr% = [1; 299] , %wahjahr% = {2009, 2013}
*/

SELECT
   w.nummer,
   w.name,
   w.bundesland,
   sg.waehler / CAST(wb.wahlberechtigte AS NUMERIC) AS wahlbeteiligung 
FROM
   wahlkreis w 
   JOIN
      stimmengueltigkeit sg 
      ON w.nummer = sg.wahlkreis 
   JOIN
      wahlberechtigte wb 
      ON w.nummer = wb.wahlkreis 
WHERE
   w.nummer =% wahlkreis_nr % 
   AND sg.wahljahr =% wahljahr % 
   AND wb.wahljahr =% wahljahr % ;