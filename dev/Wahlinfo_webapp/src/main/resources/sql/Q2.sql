    WITH bundestagssitze AS (
WITH hoechstzahlen2009 AS (
    SELECT h.bundesland, h.partei, (row_number() OVER (PARTITION BY h.partei ORDER BY h.quotient DESC)) AS rn
       FROM (
    SELECT l.bundesland, l1.partei, FLOOR(CAST(l.stimmen AS NUMERIC)/u.zahl) AS quotient
    FROM erlaubtelisten l JOIN landesliste l1 ON l.landesliste = l1.id AND l.wahljahr=l1.wahljahr AND l.wahljahr=2009, ungerade u
	ORDER BY l.bundesland ASC, quotient DESC) AS h
),
ueberhang2009 AS (
    WITH ueberhangproland2009 AS (
    SELECT h.bundesland, h.partei, GREATEST(COALESCE(dmpl.count, 0) - COUNT(h.partei), 0) AS ueberhang, 2009 AS wahljahr
    FROM hoechstzahlen2009 h JOIN oberverteilung o ON o.partei=h.partei AND o.wahljahr=2009
    LEFT JOIN direktmandateproland dmpl ON dmpl.bundesland = h.bundesland AND h.partei = dmpl.partei AND dmpl.wahljahr=o.wahljahr
    WHERE h.rn <= o.sitze
    GROUP BY h.bundesland, h.partei, dmpl.count)
    SELECT u.partei, SUM(u.ueberhang) AS ueberhang, u.wahljahr
    FROM ueberhangproland2009 u
    GROUP BY u.partei, u.wahljahr
)
SELECT o.partei, (o.sitze + COALESCE(u.ueberhang, 0)) AS sitze, o.wahljahr
FROM oberverteilung o LEFT JOIN ueberhang2009 u ON o.partei=u.partei AND u.wahljahr=o.wahljahr),
unterverteilung AS (
WITH hoechstzahlen AS (
    SELECT h.wahljahr, h.bundesland, h.partei, (row_number() OVER (PARTITION BY h.wahljahr, h.partei ORDER BY h.quotient DESC)) AS rn
       FROM (
    SELECT l.wahljahr, l.bundesland, l1.partei, FLOOR(CAST(l.stimmen AS NUMERIC)/u.zahl) AS quotient
    FROM erlaubtelisten l JOIN landesliste l1 ON l.landesliste = l1.id AND l.wahljahr=l1.wahljahr, ungerade u
	ORDER BY l.bundesland ASC, quotient DESC) AS h
),
laenderaufteilung AS (
 SELECT h.wahljahr, h.bundesland, h.partei, GREATEST(COUNT(h.partei) - COALESCE(dmpl.count, 0), 0) AS sitze
    FROM hoechstzahlen h JOIN bundestagssitze o ON o.partei=h.partei AND o.wahljahr=h.wahljahr
    LEFT JOIN direktmandateproland dmpl ON dmpl.bundesland = h.bundesland AND h.partei = dmpl.partei AND dmpl.wahljahr=h.wahljahr
    WHERE h.rn <= o.sitze
    GROUP BY h.wahljahr, h.bundesland, h.partei, dmpl.count),
zuverteilendesitze AS (
 SELECT l.wahljahr, l.partei, o.sitze + (o.sitze - (SUM(l.sitze) + SUM(COALESCE(d.count, 0)))) AS sitze
 FROM laenderaufteilung l LEFT JOIN direktmandateproland d ON l.bundesland=d.bundesland AND l.partei=d.partei AND d.wahljahr=l.wahljahr
    JOIN bundestagssitze o ON o.partei=l.partei AND o.wahljahr=l.wahljahr
 GROUP BY l.wahljahr, l.partei, o.sitze
)
SELECT h.wahljahr, h.bundesland, h.partei, COUNT(h.partei) AS sitze
    FROM hoechstzahlen h JOIN zuverteilendesitze z ON z.partei=h.partei AND h.wahljahr=z.wahljahr
    WHERE h.rn <= z.sitze
    GROUP BY h.wahljahr, h.bundesland, h.partei
    ORDER BY h.wahljahr, h.bundesland, h.partei),
direktmandate AS ( 
    SELECT d.wahljahr, w.bundesland, b.id, b.titel, b.vorname, b.nachname, b.partei 
    FROM wahlkreissieger d JOIN bewerber b ON d.direktkandidat=b.id JOIN wahlkreis w ON d.wahlkreis=w.nummer 
    JOIN erlaubteparteien e ON b.partei=e.partei AND e.wahljahr=d.wahljahr), 
listenverteilung AS ( 
    SELECT u.wahljahr, u.bundesland, u.partei, GREATEST(u.sitze - COALESCE(d.count, 0), 0) as sitze 
    FROM unterverteilung u LEFT JOIN direktmandateproland d 
    ON u.wahljahr=d.wahljahr AND u.bundesland=d.bundesland AND u.partei=d.partei 
), 
verfuegbarelistenplaetze AS ( 
SELECT e.wahljahr, e.listen_id, e.bewerber_id, (row_number() OVER (PARTITION BY e.wahljahr, e.listen_id ORDER BY e.listenplatz ASC)) as listenplatz 
FROM (SELECT e.wahljahr, lp.listen_id, lp.bewerber_id, lp.listenplatz  
      FROM erlaubteparteien e JOIN landesliste l ON e.partei=l.partei AND e.wahljahr=l.wahljahr 
	  JOIN listenplaetze lp ON lp.listen_id=l.id  
	  LEFT JOIN direktmandate d ON d.wahljahr=l.wahljahr AND d.partei=l.partei  
      AND d.bundesland=l.bundesland AND d.id=lp.bewerber_id 
	  WHERE d.id ISNULL) AS e),
mitglieder AS ((SELECT l.bundesland, b.titel, b.vorname, b.nachname, b.partei 
FROM bewerber b JOIN verfuegbarelistenplaetze lp ON b.id=lp.bewerber_id 
JOIN landesliste l ON lp.listen_id=l.id AND lp.wahljahr=l.wahljahr JOIN 
listenverteilung u ON l.partei=u.partei AND l.bundesland=u.bundesland AND l.wahljahr=u.wahljahr 
WHERE lp.listenplatz <= u.sitze AND l.wahljahr= %wahljahr%) 
UNION ALL ( 
    SELECT d.bundesland, d.titel, d.vorname, d.nachname, d.partei 
    FROM direktmandate d WHERE d.wahljahr=%wahljahr%))
    SELECT * FROM mitglieder ORDER BY partei, nachname;
