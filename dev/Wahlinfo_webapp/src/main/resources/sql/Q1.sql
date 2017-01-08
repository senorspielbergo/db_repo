WITH oberverteilung AS (
WITH mindestsitzeproparteiproland AS (
    WITH hoechstzahlen AS (
        SELECT h.bundesland, h.partei, (row_number() OVER (PARTITION BY h.bundesland ORDER BY h.quotient DESC)) AS rn
           FROM (
        SELECT l.bundesland, l1.partei, CAST(l.stimmen AS NUMERIC)/u.zahl AS quotient
        FROM erlaubtelisten l JOIN landesliste l1 ON l.landesliste = l1.id, ungerade u
        WHERE l.wahljahr=2013 AND l1.wahljahr=2013
               ORDER BY l.bundesland ASC, quotient DESC) AS h
    ), 
    saintlague AS (
        SELECT h.bundesland, h.partei, COUNT(h.partei) AS sitze
        FROM hoechstzahlen h JOIN sitzkontingent s ON s.bundesland = h.bundesland
        WHERE h.rn <= s.kontingent AND s.wahljahr=2013
        GROUP BY h.bundesland, h.partei)

    SELECT s.bundesland, e.partei, GREATEST(s.sitze, d.count)
                                       AS sitze
    FROM saintlague s JOIN erlaubteparteien e ON s.partei=e.partei
    LEFT JOIN direktmandateproland d ON d.bundesland=s.bundesland AND d.partei=e.partei
    WHERE e.wahljahr=2013 AND d.wahljahr=2013
    ORDER BY s.bundesland, e.partei
),
mindestsitzzahlen AS (
    SELECT s.partei, SUM(s.sitze) AS sitze
	FROM mindestsitzeproparteiproland s
	GROUP BY s.partei),
bundesdivisor AS (    
    SELECT MIN(FLOOR(CAST(spp.stimmen AS NUMERIC) / (msz.sitze - 0.5))) AS divisor 
    FROM mindestsitzzahlen msz JOIN stimmenpropartei spp ON msz.partei=spp.partei
    WHERE spp.wahljahr=2013),
saintlague2009 AS (
    SELECT h.partei, row_number() OVER(ORDER BY quotient DESC) AS rn
       FROM (
    SELECT ep.partei, CAST(spp.stimmen AS NUMERIC)/u.zahl AS quotient
    FROM erlaubteparteien ep JOIN stimmenpropartei spp 
           ON ep.partei=spp.partei, ungerade u
       		WHERE ep.wahljahr=2009 AND spp.wahljahr=2009) AS h)   
(SELECT spp.partei, round(CAST(spp.stimmen AS NUMERIC)/d.divisor) AS sitze, 2013 AS wahljahr
FROM stimmenpropartei spp JOIN erlaubteparteien e ON spp.partei=e.partei, bundesdivisor d
WHERE e.wahljahr=2013 AND spp.wahljahr=2013)
UNION ALL (
SELECT h.partei, COUNT(h.partei) AS sitze, 2009 AS wahljahr
    FROM saintlague2009 h
    WHERE h.rn <= 598
	GROUP BY  h.partei)
)
SELECT * FROM oberverteilung WHERE wahljahr=%wahljahr%;