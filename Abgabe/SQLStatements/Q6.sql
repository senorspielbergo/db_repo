/*
* Berechnet pro Wahljahr die höchstens zehn Direktkandidaten jeder Partei, die mit knappstem Vorsprung an Erststimmen gewonnen haben; wenn eine Partei keinen Wahlkreis gewonnen hat, werden stattdessen die Direktkandidaten zurückgegeben, die am knappsten verloren haben.
* Eingabe: %partei% = {p | p in Partei.name} , %wahjahr% = {2009, 2013}
*/

WITH losers AS -- Knappste Verlierer jeder Partei absteigend sortiert nach fehlenden Stimmen (negativ)
(
   SELECT
      b.partei,
      spk.direktkandidat,
      spk.wahlkreis,
      (
         spk.stimmen - wks.stimmen -- Differenz zu Bewerber, der im Wahlkreis gewonnen hat
      )
      AS differenz,
      (
         row_number() OVER (PARTITION BY b.partei 
      ORDER BY
(spk.stimmen - wks.stimmen) DESC)
      )
      as rn 
   FROM
      stimmenprokandidat spk 
      JOIN
         wahlkreissieger wks 
         ON spk.wahlkreis = wks.wahlkreis 
         AND spk.stimmen - wks.stimmen < 0 
      JOIN
         bewerber b 
         ON spk.direktkandidat = b.id 
   WHERE
      b.partei NOTNULL 
      AND spk.wahljahr =% wahljahr % 
      AND wks.wahljahr =% wahljahr % 
)
,
winners AS -- Knappste Gewinner einer Partei aufsteigend sortiert nach Vorsprung
(
   SELECT
      b.partei,
      wks.direktkandidat,
      wks.wahlkreis,
      ABS(bl.differenz) AS differenz,
      (
         row_number() OVER (PARTITION BY b.partei 
      ORDER BY
         ABS(bl.differenz) ASC)
      )
      as rn 
   FROM
      wahlkreissieger wks 
      JOIN
         ( -- Differenz zu Erstunterlegenem pro Wahlkreis
            SELECT
               l.wahlkreis,
               MAX(l.differenz) AS differenz 
            FROM
               losers l 
            GROUP BY
               l.wahlkreis
         )
         as bl 
         ON wks.wahlkreis = bl.wahlkreis 
      JOIN
         bewerber b 
         ON wks.direktkandidat = b.id 
   WHERE
      b.partei NOTNULL 
      AND wks.wahljahr =% wahljahr % 
)
,
combined AS -- Knappste Sieger/Verlierer
(
( -- 10 knappste Sieger
   SELECT
      w.partei, w.wahlkreis, w.direktkandidat, w.differenz 
   FROM
      winners w 
   WHERE
      w.rn <= 10) 
   UNION ALL
( -- 10 knappste Verlierer, wenn KEIN Direktkandidat gewonnen hat
   SELECT
      l.partei, l.wahlkreis, l.direktkandidat, l.differenz 
   FROM
      losers l 
      LEFT JOIN
         winners w 
         ON l.partei = w.partei 
   WHERE
      w.direktkandidat ISNULL 
      AND l.rn <= 10)
)
SELECT
   wk.name AS wahlkreis,
   b.titel,
   b.vorname,
   b.nachname,
   com.differenz 
FROM
   combined com 
   JOIN
      bewerber b 
      ON com.direktkandidat = b.id 
   JOIN
      wahlkreis wk 
      ON com.wahlkreis = wk.nummer 
WHERE
   com.partei = '%partei%' 
ORDER BY
   ABS( - com.differenz) ASC;