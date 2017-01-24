/*
* Von setup.jar ausgeführte Statements zur Erzeugung der in der Technischen Dokumentation erläuterten Materialized Views
*/

CREATE MATERIALIZED VIEW stimmenProKandidat AS 
(
   SELECT
      stimmzettel.wahlkreis,
      stimmzettel.direktkandidat,
      count(stimmzettel.direktkandidat) AS stimmen,
      stimmzettel.wahljahr 
   FROM
      stimmzettel 
   WHERE
      stimmzettel.direktkandidat NOTNULL 
   GROUP BY
      stimmzettel.wahlkreis,
      stimmzettel.direktkandidat,
      stimmzettel.wahljahr
)
;
CREATE MATERIALIZED VIEW wahlkreissieger AS 
(
   WITH maxstimmen AS 
   (
      SELECT
         stimmenprokandidat.wahlkreis,
         MAX(stimmenprokandidat.stimmen) AS stimmen,
         stimmenprokandidat.wahljahr 
      FROM
         stimmenprokandidat 
      GROUP BY
         stimmenprokandidat.wahlkreis,
         stimmenprokandidat.wahljahr
   )
   SELECT
      k1.wahlkreis,
      k1.direktkandidat,
      k1.stimmen,
      k1.wahljahr 
   FROM
      stimmenprokandidat k1 
      JOIN
         maxstimmen m1 
         ON (k1.stimmen = m1.stimmen 
         AND m1.wahlkreis = k1.wahlkreis 
         AND m1.wahljahr = k1.wahljahr) 
   ORDER BY
      m1.wahljahr,
      k1.wahlkreis
)
;
CREATE MATERIALIZED VIEW wahlkreisstimmenprolandesliste AS 
(
   SELECT
      stimmzettel.wahlkreis,
      stimmzettel.landesliste,
      count(stimmzettel.landesliste) AS stimmen,
      (
         cast(count(stimmzettel.landesliste) AS numeric) / (sum(count(stimmzettel.landesliste)) over (partition BY stimmzettel.wahlkreis, stimmzettel.wahljahr))
      )
      AS prozent,  -- Prozentualer Anteil der Stimmen einer Landesliste pro Wahlkreis
      stimmzettel.wahljahr 
   FROM
      stimmzettel 
   WHERE
      stimmzettel.landesliste NOTNULL 
   GROUP BY
      stimmzettel.wahlkreis,
      stimmzettel.landesliste,
      stimmzettel.wahljahr
)
;
CREATE MATERIALIZED VIEW stimmengueltigkeit AS 
(
   WITH bewerber_count AS -- Anzahl der gültigen Erststimmen pro Wahlkreis
   (
      SELECT
         spk.wahlkreis,
         SUM(spk.stimmen) AS stimmen,
         spk.wahljahr 
      FROM
         stimmenprokandidat spk 
      GROUP BY
         spk.wahlkreis,
         spk.wahljahr 
      ORDER BY
         spk.wahljahr,
         spk.wahlkreis
   ) 
,
   listen_count AS -- Anzahl der gültigen Zweitstimmen pro Wahlkreis
   (
      SELECT
         wspl.wahlkreis,
         SUM(wspl.stimmen) AS stimmen,
         wspl.wahljahr 
      FROM
         wahlkreisstimmenprolandesliste wspl 
      GROUP BY
         wspl.wahlkreis,
         wspl.wahljahr 
      ORDER BY
         wspl.wahljahr,
         wspl.wahlkreis
   ) 
,
   gesamt AS -- Gesamtzahl der abgegebenen Stimmzettel
   (
      SELECT
         s.wahlkreis,
         s.wahljahr,
         COUNT(s.wahlkreis) AS waehler 
      FROM
         stimmzettel s 
      GROUP BY
         s.wahlkreis,
         s.wahljahr
   ) 
   SELECT DISTINCT
      ges.wahlkreis,
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
         ON ges.wahlkreis = bc.wahlkreis 
         AND ges.wahljahr = bc.wahljahr 
      JOIN
         listen_count lc 
         ON ges.wahlkreis = lc.wahlkreis 
         AND ges.wahljahr = lc.wahljahr
)
;
CREATE MATERIALIZED VIEW stimmenProLandesliste AS 
(
   WITH gesamtstimmen AS -- Gültige Zweitstimmen für Partei pro Bundesland
   (
      SELECT
         w1.bundesland,
         s1.wahljahr,
         SUM(s1.stimmen) AS gesamt 
      FROM
         wahlkreisstimmenprolandesliste s1 
         JOIN
            wahlkreis w1 
            ON s1.wahlkreis = w1.nummer 
      GROUP BY
         w1.bundesland,
         s1.wahljahr
   ) 
   SELECT
      w1.bundesland,
      s1.landesliste,
      SUM(s1.stimmen) AS stimmen,
      (
         CAST(SUM(s1.stimmen) AS NUMERIC) / g1.gesamt
      )
      AS prozent, -- Prozentualer Anteil der Zweitstimmen im Bundesland
      s1.wahljahr 
   FROM
      wahlkreis w1 
      JOIN
         wahlkreisstimmenprolandesliste s1 
         ON s1.wahlkreis = w1.nummer 
      JOIN
         gesamtstimmen g1 
         ON w1.bundesland = g1.bundesland 
         AND s1.wahljahr = g1.wahljahr 
   GROUP BY
      w1.bundesland,
      s1.landesliste,
      g1.gesamt,
      s1.wahljahr 
   ORDER BY
      s1.wahljahr,
      w1.bundesland,
      s1.landesliste
)
;
CREATE MATERIALIZED VIEW stimmenProPartei AS 
(
   WITH gesamtstimmen AS -- Gesamtzahl der gültigen Zweitstimmen auf Bundesebene
   (
      SELECT
         s1.wahljahr,
         SUM(s1.stimmen) AS gesamt 
      FROM
         stimmenprolandesliste s1 
      GROUP BY
         s1.wahljahr
   )
   SELECT
      l1.partei,
      SUM(s1.stimmen) AS stimmen,
      CAST(SUM(s1.stimmen) AS NUMERIC) / g1.gesamt AS prozent, -- Prozentualer Anteil der Zweitstimmen für die Partei auf Bundesebene
      s1.wahljahr 
   FROM
      stimmenprolandesliste s1 
      JOIN
         landesliste l1 
         ON s1.landesliste = l1.id 
      JOIN
         gesamtstimmen g1 
         ON s1.wahljahr = g1.wahljahr 
   GROUP BY
      s1.wahljahr,
      l1.partei,
      g1.gesamt 
   ORDER BY
      l1.partei
)
;
CREATE MATERIALIZED VIEW direktmandateProPartei AS 
(
   SELECT
      b1.partei,
      COUNT(w1.direktkandidat) AS anzahl,
      w1.wahljahr 
   FROM
      wahlkreissieger w1 
      JOIN
         bewerber b1 
         ON w1.direktkandidat = b1.id 
   GROUP BY
      w1.wahljahr,
      b1.partei 
   ORDER BY
      w1.wahljahr,
      b1.partei
)
;
CREATE MATERIALIZED VIEW erlaubteParteien AS 
(
( -- Mindestens 2 Direktmandate
   SELECT
      partei, wahljahr 
   FROM
      direktmandatepropartei 
   WHERE
      anzahl > 2) 
   UNION
( -- Mindestens 5%
   SELECT
      partei, wahljahr 
   FROM
      stimmenpropartei 
   WHERE
      prozent > 0.05)
)
;
CREATE MATERIALIZED VIEW ungerade AS 
(
   WITH RECURSIVE ungeradeZahlen(zahl) AS 
   (
(
      SELECT
         1) 
      UNION ALL
(
      SELECT
         zahl + 2 
      FROM
         ungeradeZahlen 
      WHERE
         zahl + 2 < 600)
   )
   SELECT
      * 
   FROM
      ungeradeZahlen
)
;
CREATE MATERIALIZED VIEW direktmandateproland AS 
(
   SELECT DISTINCT
      s.wahljahr,
      w.bundesland,
      b.partei,
      (
         COUNT(b.partei) OVER (PARTITION BY s.wahljahr, w.bundesland, b.partei)
          -- Anzahl der in einem Bundesland vorkommenden Direktkandidaten pro Partei
      )
   FROM
      wahlkreissieger s 
      JOIN
         wahlkreis w 
         ON s.wahlkreis = w.nummer 
      JOIN
         bewerber b 
         ON s.direktkandidat = b.id 
   ORDER BY
      w.bundesland,
      b.partei
)
;
CREATE MATERIALIZED VIEW erlaubtelisten AS 
(
   SELECT DISTINCT
      spl.bundesland,
      spl.landesliste,
      spl.stimmen,
      spl.wahljahr 
   FROM
      stimmenprolandesliste spl 
      JOIN
         landesliste l 
         ON spl.landesliste = l.id 
         AND spl.wahljahr = l.wahljahr 
      JOIN
         erlaubteparteien e 
         ON l.partei = e.partei 
         AND spl.wahljahr = e.wahljahr 
   ORDER BY
      spl.bundesland,
      spl.landesliste,
      spl.wahljahr
)
;
CREATE MATERIALIZED VIEW oberverteilung AS 
(
   WITH mindestsitzeproparteiproland AS 
   (
      WITH hoechstzahlen AS -- Hoechstzahlverfahren für 2013
      (
         SELECT
            h.bundesland,
            h.partei,
            (
               row_number() OVER (PARTITION BY h.bundesland 
            ORDER BY
               h.quotient DESC) -- Parteien pro Bundesland nach Divisionsergebnissen (Hoechstzahlen) sortieren und durchnummerieren
            )
            AS rn 
         FROM
            (
               SELECT
                  l.bundesland,
                  l1.partei,
                  CAST(l.stimmen AS NUMERIC) / u.zahl AS quotient -- Dividiere Stimmen einer Partei durch ALLE ungeraden Zahlen aus ungerade (1, 3, 5, ...)
               FROM
                  erlaubtelisten l 
                  JOIN
                     landesliste l1 
                     ON l.landesliste = l1.id,
                     ungerade u 
               WHERE
                  l.wahljahr = 2013 
                  AND l1.wahljahr = 2013 
               ORDER BY
                  l.bundesland ASC,
                  quotient DESC
            )
            AS h
      )
,
      saintlague AS -- Hoechstzahlverfahren-Auswertung für 2013
      (
         SELECT
            h.bundesland,
            h.partei,
            COUNT(h.partei) AS sitze 
         FROM
            hoechstzahlen h 
            JOIN
               sitzkontingent s 
               ON s.bundesland = h.bundesland 
         WHERE
            h.rn <= s.kontingent -- Wähle die höchsten Divisionsergebnisse entsprechend dem Sitzkontingent des Bundeslands 
            AND s.wahljahr = 2013 
         GROUP BY
            h.bundesland,
            h.partei
      )
      SELECT
         s.bundesland,
         e.partei,
         GREATEST(s.sitze, d.count) AS sitze -- Maximum aus Direktmandaten und der in saintlague ermittelten Mindestsitzzahl für eine Partei pro Bundesland
      FROM
         saintlague s 
         JOIN
            erlaubteparteien e 
            ON s.partei = e.partei 
         LEFT JOIN
            direktmandateproland d 
            ON d.bundesland = s.bundesland 
            AND d.partei = e.partei 
      WHERE
         e.wahljahr = 2013 
         AND d.wahljahr = 2013 
      ORDER BY
         s.bundesland,
         e.partei
   )
,
   mindestsitzzahlen AS -- Mindestsitzzahlen einer Partei auf Bundesebene
   (
      SELECT
         s.partei,
         SUM(s.sitze) AS sitze 
      FROM
         mindestsitzeproparteiproland s 
      GROUP BY
         s.partei
   )
,
   bundesdivisor AS -- Bestimmung des Bundesdivisors für 2013 zur Berechnung der Ausgleichsmandate
   (
      SELECT
         MIN(FLOOR(CAST(spp.stimmen AS NUMERIC) / (msz.sitze - 0.5))) AS divisor 
      FROM
         mindestsitzzahlen msz 
         JOIN
            stimmenpropartei spp 
            ON msz.partei = spp.partei 
      WHERE
         spp.wahljahr = 2013
   )
,
   saintlague2009 AS -- Höchstzahlverfahren für 2009
   (
      SELECT
         h.partei,
         row_number() OVER( 
      ORDER BY
         quotient DESC) AS rn 
      FROM
         (
            SELECT
               ep.partei,
               CAST(spp.stimmen AS NUMERIC) / u.zahl AS quotient 
            FROM
               erlaubteparteien ep 
               JOIN
                  stimmenpropartei spp 
                  ON ep.partei = spp.partei,
                  ungerade u 
            WHERE
               ep.wahljahr = 2009 
               AND spp.wahljahr = 2009
         )
         AS h
   )
( -- Bestimmung der finalen Zusammensetzung des Bundestags mit Überhang- und Ausgleichmandaten für 2013
   SELECT
      spp.partei, round(CAST(spp.stimmen AS NUMERIC) / d.divisor) AS sitze, 2013 AS wahljahr 
   FROM
      stimmenpropartei spp 
      JOIN
         erlaubteparteien e 
         ON spp.partei = e.partei, bundesdivisor d 
   WHERE
      e.wahljahr = 2013 
      AND spp.wahljahr = 2013) 
   UNION ALL
( -- Bestimmung der vorläufigen Zusammensetzung des Bundestags OHNE Überhangmandate für 2009
   SELECT
      h.partei, COUNT(h.partei) AS sitze, 2009 AS wahljahr 
   FROM
      saintlague2009 h 
   WHERE
      h.rn <= 598 
   GROUP BY
      h.partei)
)
;
CREATE 
OR REPLACE FUNCTION RefreshAllMaterializedViews(schema_arg TEXT DEFAULT 'public') RETURNS INT AS $$
DECLARE r RECORD;
BEGIN
   FOR r IN 
   SELECT -- Hole Namen aller Materialized Views und rufe Refresh  auf
      matviewname 
   FROM
      pg_matviews 
   WHERE
      schemaname = schema_arg LOOP EXECUTE 'REFRESH MATERIALIZED VIEW ' || schema_arg || '.' || r.matviewname;
END
LOOP;
RETURN 1;
END
$$ LANGUAGE plpgsql;