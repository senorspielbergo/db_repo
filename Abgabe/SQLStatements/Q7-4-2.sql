/*
* Identisch zu Q3-4-2.sql; arbeitet jedoch direkt auf den Stimmzetteln und nicht auf voraggregierten Daten.
* Eingabe: %wahlkreis_nr% = [1; 299] , %wahjahr% = {2009, 2013}
*/

WITH bayerischestimmzettel AS -- Stimmzettel für spezifischen Wahlkreis 
(
   SELECT
      s.wahljahr,
      s.id,
      s.direktkandidat,
      s.landesliste 
   FROM
      stimmzettel s 
   WHERE
      s.wahlkreis =% wahlkreis_nr % 
)
,
bstimmengueltigkeit AS -- (Un)Gültige Erst- und Zweitstimmen
(
   WITH bstimmenprokandidat AS -- Erststimmen pro Kandidat und Wahljahr
   (
      SELECT
         bs.wahljahr,
         bs.direktkandidat,
         COUNT(bs.direktkandidat) AS stimmen 
      FROM
         bayerischestimmzettel bs 
      WHERE
         bs.direktkandidat NOTNULL 
      GROUP BY
         bs.wahljahr,
         bs.direktkandidat
   )
,
   bwahlkreisstimmenprolandesliste AS -- Absolute und prozentuale Zahl an Zweitstimmen pro Landesliste und Wahljahr
   (
      WITH wgzs AS -- Gültige Zweitstimmen pro Wahljahr
      (
         SELECT
            bs.wahljahr,
            COUNT(*) AS stimmen 
         FROM
            bayerischestimmzettel bs 
         WHERE
            bs.landesliste NOTNULL 
         GROUP BY
            bs.wahljahr 
      )
      SELECT
         bs.wahljahr,
         bs.landesliste,
         COUNT(bs.landesliste) AS stimmen,
         CAST(COUNT(bs.landesliste) AS NUMERIC) / ges.stimmen AS prozent 
      FROM
         bayerischestimmzettel bs 
         JOIN
            wgzs ges 
            ON ges.wahljahr = bs.wahljahr 
      WHERE
         bs.landesliste NOTNULL 
      GROUP BY
         bs.wahljahr,
         bs.landesliste,
         ges.stimmen 
   )
,
   bewerber_count AS -- Gültige Erststimmen pro Wahljahr
   (
      SELECT
         SUM(spk.stimmen) AS stimmen,
         spk.wahljahr 
      FROM
         bstimmenprokandidat spk 
      GROUP BY
         spk.wahljahr 
   )
,
   listen_count AS -- Gültige Zweitstimmen pro Wahljahr
   (
      SELECT
         SUM(wspl.stimmen) AS stimmen,
         wspl.wahljahr 
      FROM
         bwahlkreisstimmenprolandesliste wspl 
      GROUP BY
         wspl.wahljahr
   )
,
   gesamt AS -- Abgegebene Stimmzettel
   (
      SELECT
         s.wahljahr,
         COUNT(*) AS waehler 
      FROM
         bayerischestimmzettel s 
      GROUP BY
         s.wahljahr 
   )
   SELECT DISTINCT
      ges.wahljahr,
      ges.waehler,
      bc.stimmen AS g_erststimmen,
      lc.stimmen AS g_zweitstimmen,
      ges.waehler - bc.stimmen AS ug_erststimmen,
      ges.waehler - lc.stimmen AS ug_zweitstimmen 
   FROM
      gesamt ges 
      JOIN
         bewerber_count bc 
         ON ges.wahljahr = bc.wahljahr 
      JOIN
         listen_count lc 
         ON ges.wahljahr = lc.wahljahr 
)
SELECT
   w.name,
   w.bundesland,
   (
      sg1.waehler - sg2.waehler
   )
   AS waehlerdiffabs,
   (
      sg1.waehler / CAST(wb1.wahlberechtigte AS NUMERIC) - sg2.waehler / CAST(wb2.wahlberechtigte AS NUMERIC)
   )
   AS waehlerdiffpro,
   (
      sg1.g_erststimmen - sg2.g_erststimmen
   )
   as gediffabs,
   (
      sg1.g_erststimmen / CAST(sg1.waehler AS NUMERIC) - sg2.g_erststimmen / CAST(sg2.waehler AS NUMERIC)
   )
   AS gediffpro,
   (
      sg1.ug_erststimmen - sg2.ug_erststimmen
   )
   as ugediffabs,
   (
      sg1.ug_erststimmen / CAST(sg1.waehler AS NUMERIC) - sg2.ug_erststimmen / CAST(sg2.waehler AS NUMERIC)
   )
   AS ugediffpro,
   (
      sg1.g_zweitstimmen - sg2.g_zweitstimmen
   )
   as gzdiffabs,
   (
      sg1.g_zweitstimmen / CAST(sg1.waehler AS NUMERIC) - sg2.g_zweitstimmen / CAST(sg2.waehler AS NUMERIC)
   )
   AS gzdiffpro,
   (
      sg1.ug_zweitstimmen - sg2.ug_zweitstimmen
   )
   as ugzdiffabs,
   (
      sg1.ug_zweitstimmen / CAST(sg1.waehler AS NUMERIC) - sg2.ug_zweitstimmen / CAST(sg2.waehler AS NUMERIC)
   )
   AS ugzdiffpro 
FROM
   wahlkreis w 
   JOIN
      wahlberechtigte wb1 
      ON w.nummer = wb1.wahlkreis 
   JOIN
      wahlberechtigte wb2 
      ON w.nummer = wb2.wahlkreis 
      AND wb2.wahljahr < wb1.wahljahr,
      bstimmengueltigkeit sg1 
   JOIN
      bstimmengueltigkeit sg2 
      ON sg1.wahljahr > sg2.wahljahr 
WHERE
   w.nummer =% wahlkreis_nr % 
   AND sg1.wahljahr =% wahljahr % 
   AND wb1.wahljahr =% wahljahr % ;