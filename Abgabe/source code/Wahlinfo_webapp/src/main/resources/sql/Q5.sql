WITH ueberhang AS (
    WITH hoechstzahlen2013 AS (
        SELECT h.bundesland, h.partei, (row_number() OVER (PARTITION BY h.bundesland ORDER BY h.quotient DESC)) AS rn
           FROM (
        SELECT l.bundesland, l1.partei, CAST(l.stimmen AS NUMERIC)/u.zahl AS quotient
        FROM erlaubtelisten l JOIN landesliste l1 ON l.landesliste = l1.id AND l.wahljahr=l1.wahljahr AND l.wahljahr=2013, ungerade u
        ORDER BY l.bundesland ASC, quotient DESC) AS h
    ), 
    saintlague2013 AS (
        SELECT h.bundesland, h.partei, COUNT(h.partei) AS sitze
        FROM hoechstzahlen2013 h JOIN sitzkontingent s ON s.bundesland = h.bundesland AND s.wahljahr=2013
        WHERE h.rn <= s.kontingent
        GROUP BY h.bundesland, h.partei),
hoechstzahlen2009 AS (
    SELECT h.bundesland, h.partei, (row_number() OVER (PARTITION BY h.partei ORDER BY h.quotient DESC)) AS rn
       FROM (
    SELECT l.bundesland, l1.partei, FLOOR(CAST(l.stimmen AS NUMERIC)/u.zahl) AS quotient
    FROM erlaubtelisten l JOIN landesliste l1 ON l.landesliste = l1.id AND l.wahljahr=l1.wahljahr AND l.wahljahr=2009, ungerade u
	ORDER BY l.bundesland ASC, quotient DESC) AS h
)

    (SELECT s.bundesland, e.partei, ABS(LEAST(s.sitze - d.count, 0))
                                       AS ueberhang, 2013 AS wahljahr
    FROM saintlague2013 s JOIN erlaubteparteien e ON s.partei=e.partei AND e.wahljahr=2013
    LEFT JOIN direktmandateproland d ON d.bundesland=s.bundesland AND d.partei=e.partei
    AND d.wahljahr=2013
    ORDER BY s.bundesland, e.partei)
    UNION ALL (
        SELECT h.bundesland, h.partei, GREATEST(COALESCE(dmpl.count, 0) - COUNT(h.partei), 0) AS ueberhang, 2009 AS wahljahr
    FROM hoechstzahlen2009 h JOIN oberverteilung o ON o.partei=h.partei AND o.wahljahr=2009
    LEFT JOIN direktmandateproland dmpl ON dmpl.bundesland = h.bundesland AND h.partei = dmpl.partei AND dmpl.wahljahr=o.wahljahr
    WHERE h.rn <= o.sitze
    GROUP BY h.bundesland, h.partei, dmpl.count)
)
SELECT * FROM ueberhang u WHERE u.ueberhang > 0;