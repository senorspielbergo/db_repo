WITH direktmandate AS ( 
    SELECT d.wahljahr, w.bundesland, b.id, b.titel, b.vorname, b.nachname, b.partei 
    FROM wahlkreissieger d JOIN bewerber b ON d.direktkandidat=b.id JOIN wahlkreis w ON d.wahlkreis=w.nummer 
    JOIN erlaubteparteien e ON b.partei=e.partei AND e.wahljahr=d.wahljahr), 
listenverteilung AS ( 
    SELECT u.wahljahr, u.bundesland, u.partei, GREATEST(u.sitze - COALESCE(d.count, 0), 0) as sitze 
    FROM unterverteilung u LEFT JOIN direktmandateproland d ON u.wahljahr=d.wahljahr AND u.bundesland=d.bundesland AND u.partei=d.partei 
), 
verfuegbarelistenplaetze AS ( 
SELECT e.wahljahr, e.listen_id, e.bewerber_id, (row_number() OVER (PARTITION BY e.listen_id ORDER BY e.listenplatz ASC)) as listenplatz 
FROM (SELECT e.wahljahr, lp.listen_id, lp.bewerber_id, lp.listenplatz  
      FROM erlaubteparteien e JOIN landesliste l ON e.partei=l.partei AND e.wahljahr=l.wahljahr 
	  JOIN listenplaetze lp ON lp.listen_id=l.id  
	  LEFT JOIN direktmandate d ON d.wahljahr=l.wahljahr AND d.partei=l.partei  
      AND d.bundesland=l.bundesland AND d.id=lp.bewerber_id 
	  WHERE d.id ISNULL) AS e) 
(SELECT l.bundesland, b.titel, b.vorname, b.nachname, b.partei 
FROM bewerber b JOIN verfuegbarelistenplaetze lp ON b.id=lp.bewerber_id 
JOIN landesliste l ON lp.listen_id=l.id AND lp.wahljahr=l.wahljahr JOIN 
listenverteilung u ON l.partei=u.partei AND l.bundesland=u.bundesland AND l.wahljahr=u.wahljahr 
WHERE lp.listenplatz <= u.sitze AND l.wahljahr= %wahljahr% ) 
UNION ALL ( 
    SELECT d.bundesland, d.titel, d.vorname, d.nachname, d.partei 
    FROM direktmandate d WHERE d.wahljahr= %wahljahr% );