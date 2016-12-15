package com.kaphira.database;

/**
 *
 * @author theralph
 */
public class DbQueries {
    public static final String ALL_DEVELOPERS = "select * from developer;";
    public static final String BUNDESTAG_DISTRIBUTION_PARTIES_AND_PERCENTAGES = "select * from Oberverteilung;";

    /**
     * Q1: Gibt die Oberverteilung für ein Wahljahr aus
     * @param year
     * @return 
     */
    public static final String queryOberverteilung(int year) {
        return 
                "SELECT * FROM oberverteilung WHERE wahljahr=" + year;
    }
    
    
    /**
     * Q2: Gibt sämtliche Mitglieder des Bundestags aus (bundesland, titel, vorname, nachname, partei)
     * @param year
     * @return 
     */
    public static final String queryAllMembers(int year) {
        return 
                
                "WITH direktmandate AS (\n" +
                "    SELECT d.wahljahr, w.bundesland, b.id, b.titel, b.vorname, b.nachname, b.partei\n" +
                "    FROM wahlkreissieger d JOIN bewerber b ON d.direktkandidat=b.id JOIN wahlkreis w ON d.wahlkreis=w.nummer\n" +
                "    JOIN erlaubteparteien e ON b.partei=e.partei AND e.wahljahr=d.wahljahr),\n" +
                "listenverteilung AS (\n" +
                "    SELECT u.wahljahr, u.bundesland, u.partei, GREATEST(u.sitze - COALESCE(d.count, 0), 0) as sitze\n" +
                "    FROM unterverteilung u LEFT JOIN direktmandateproland d ON u.wahljahr=d.wahljahr AND u.bundesland=d.bundesland AND u.partei=d.partei\n" +
                "),\n" +
                "verfuegbarelistenplaetze AS (\n" +
                "SELECT e.wahljahr, e.listen_id, e.bewerber_id, (row_number() OVER (PARTITION BY e.listen_id ORDER BY e.listenplatz ASC)) as listenplatz\n" +
                "FROM (SELECT e.wahljahr, lp.listen_id, lp.bewerber_id, lp.listenplatz \n" +
                "      FROM erlaubteparteien e JOIN landesliste l ON e.partei=l.partei AND e.wahljahr=l.wahljahr\n" +
                "	  JOIN listenplaetze lp ON lp.listen_id=l.id \n" +
                "	  LEFT JOIN direktmandate d ON d.wahljahr=l.wahljahr AND d.partei=l.partei \n" +
                "      AND d.bundesland=l.bundesland AND d.id=lp.bewerber_id\n" +
                "	  WHERE d.id ISNULL) AS e)\n" +
                "(SELECT l.bundesland, b.titel, b.vorname, b.nachname, b.partei\n" +
                "FROM bewerber b JOIN verfuegbarelistenplaetze lp ON b.id=lp.bewerber_id\n" +
                "JOIN landesliste l ON lp.listen_id=l.id AND lp.wahljahr=l.wahljahr JOIN\n" +
                "listenverteilung u ON l.partei=u.partei AND l.bundesland=u.bundesland AND l.wahljahr=u.wahljahr\n" +
                "WHERE lp.listenplatz <= u.sitze AND l.wahljahr="+ year +")\n" +
                "UNION ALL (\n" +
                "    SELECT d.bundesland, d.titel, d.vorname, d.nachname, d.partei\n" +
                "    FROM direktmandate d WHERE d.wahljahr="+ year +")";
                
    }
    
    /**
     * (Q3-1) Gibt ID, Name, Bundesland und Wahlbeteiligung für einen Wahlkreis aus.
     * 
     * 
     * @param districtId 
     * @return 
     */
    public static String queryTurnout(int districtId, int year) {
        return 
               "SELECT w.nummer, w.name, w.bundesland, \n" +
               "sg.waehler / CAST(w.wahlberechtigte AS NUMERIC) AS wahlbeteiligung\n" +
               "FROM wahlkreis w JOIN stimmengueltigkeit sg ON w.nummer=sg.wahlkreis\n" +
               "WHERE w.nummer="+ districtId +" AND sg.wahljahr=" + year;
 
    }


    /**
     * (Q3-2)Gibt Titel, Vorname, Nachname, Partei und Stimmen eines Wahlkreissiegers aus.
     * @param districtId
     * @return 
     */
    public static String queryDistrictWinner(int districtId, int year) {
        return
                "SELECT b.titel, b.vorname, b.nachname, b.partei, s.stimmen, CAST(s.stimmen AS NUMERIC)/sg.g_erststimmen AS prozent\n" +
                "FROM bewerber b JOIN  wahlkreissieger s ON s.direktkandidat=b.id JOIN\n" +
                "wahlkreis w ON w.nummer=s.wahlkreis JOIN stimmengueltigkeit sg ON w.nummer=sg.wahlkreis AND sg.wahljahr=s.wahljahr\n" +
                "WHERE w.nummer=" + districtId + " AND sg.wahljahr=" + year;
    }

    
    /**
     * (Q3-3)Gibt die prozentuale und absolute Anzahl an Stimmen fuer jede Partei eines Wahlkreises aus.
     * @param districtId
     * @return 
     */
    public static String queryDistrictPartyResults(int districtId, int year) {
        return
                "SELECT l.partei, wspl.stimmen, wspl.prozent\n" +
                "FROM wahlkreisstimmenprolandesliste wspl JOIN landesliste l ON l.id=wspl.landesliste\n" +
                "WHERE wspl.wahlkreis=" + districtId + " AND l.wahljahr=" + year +
                " ORDER BY wspl.stimmen DESC";
                }
    /**
     * (Q3-4-1) Gibt Partei, ediffabs, ediffpro, zdiffabs, zdiffpro fuer alle Parteien eines Wahlkreises zurück 
     * @param districtId
     * @param year
     * @return 
     */
    public static String queryPartyDifferencesToPreviousYear(int districtId, int year) {
        return
                "WITH erststimmen AS (\n" +
                "    SELECT spk.wahljahr, spk.wahlkreis, b.partei, SUM(spk.stimmen) as e_absolut, CAST(SUM(spk.stimmen) AS NUMERIC)/sg.g_erststimmen AS e_prozent\n" +
                "    FROM stimmenprokandidat spk JOIN bewerber b ON spk.direktkandidat=b.id\n" +
                "    JOIN stimmengueltigkeit sg ON spk.wahljahr=sg.wahljahr AND spk.wahlkreis=sg.wahlkreis\n" +
                "    JOIN erlaubteparteien e ON b.partei=e.partei AND spk.wahljahr=e.wahljahr\n" +
                "    GROUP BY spk.wahljahr, spk.wahlkreis, b.partei, sg.g_erststimmen),\n" +
                "zweitstimmen AS (\n" +
                "    SELECT wspl.wahljahr, wspl.wahlkreis, l.partei, wspl.stimmen as z_absolut, wspl.prozent AS z_prozent\n" +
                "	FROM wahlkreisstimmenprolandesliste wspl JOIN landesliste l ON l.id=wspl.landesliste AND wspl.wahljahr=l.wahljahr),\n" +
                "helptable AS (\n" +
                "SELECT e.wahljahr, e.wahlkreis, e.partei, e.e_absolut, e.e_prozent, z.z_absolut, z.z_prozent\n" +
                "FROM erststimmen e JOIN zweitstimmen z ON e.wahljahr=z.wahljahr AND e.partei=z.partei AND e.wahlkreis=z.wahlkreis)\n" +
                "SELECT h1.wahlkreis, h1.partei, COALESCE(h1.e_absolut, 0) - COALESCE(h2.e_absolut) AS ediffabs,\n" +
                "	COALESCE(h1.e_prozent, 0) - COALESCE(h2.e_prozent, 0) AS ediffpro, \n" +
                "    COALESCE(h1.z_absolut, 0) - COALESCE(h2.z_absolut, 0) AS zdiffabs,\n" +
                "    COALESCE(h1.z_prozent, 0) - COALESCE(h2.z_prozent, 0) AS zdiffpro\n" +
                "FROM helptable h1 JOIN helptable h2 ON h1.wahljahr > h2.wahljahr AND h1.wahlkreis=h2.wahlkreis "+ 
                "AND h1.partei=h2.partei AND h1.wahlkreis=" + districtId + " AND h1.wahljahr=" + year;
    }
    
    
    /**
     * (Q3-4-2)  
     * @param districtId 
     * @param year
     * @return 
     */
    public static String queryDifferencesToPreviousYear(int districtId, int year) {
            return 
                    "SELECT w.nummer, w.name, w.bundesland, \n" +
                    "(sg1.waehler - sg2.waehler) AS waehlerdiffabs, \n" +
                    "(sg1.waehler / CAST(w.wahlberechtigte AS NUMERIC) - sg2.waehler / CAST(w.wahlberechtigte AS NUMERIC)) AS waehlerdiffpro,\n" +
                    "(sg1.g_erststimmen - sg2.g_erststimmen) as gediffabs,\n" +
                    "(sg1.g_erststimmen / CAST(sg1.waehler AS NUMERIC) - sg2.g_erststimmen / CAST(sg2.waehler AS NUMERIC)) AS gediffpro,\n" +
                    "(sg1.ug_erststimmen - sg2.ug_erststimmen) as ugediffabs,\n" +
                    "(sg1.ug_erststimmen / CAST(sg1.waehler AS NUMERIC) - sg2.ug_erststimmen / CAST(sg2.waehler AS NUMERIC)) AS ugediffpro,\n" +
                    "(sg1.g_zweitstimmen - sg2.g_zweitstimmen) as gzdiffabs,\n" +
                    "(sg1.g_zweitstimmen / CAST(sg1.waehler AS NUMERIC) - sg2.g_zweitstimmen / CAST(sg2.waehler AS NUMERIC)) AS gzdiffpro,\n" +
                    "(sg1.ug_zweitstimmen - sg2.ug_zweitstimmen) as ugzdiffabs,\n" +
                    "(sg1.ug_zweitstimmen / CAST(sg1.waehler AS NUMERIC) - sg2.ug_zweitstimmen / CAST(sg2.waehler AS NUMERIC)) AS ugzdiffpro\n" +
                    "FROM wahlkreis w JOIN stimmengueltigkeit sg1 ON w.nummer=sg1.wahlkreis \n" +
                    "JOIN stimmengueltigkeit sg2 ON sg1.wahljahr > sg2.wahljahr AND sg1.wahlkreis=sg2.wahlkreis\n" +
                    "WHERE w.nummer=" + districtId + " AND sg1.wahljahr=" + year;
        }
    
    /**
     * (Q4) wahljahr, nummer, name, erststimmen_partei, erststimmen, zweitstimmen_partei, zweitstimmen fuer alle wahlkreise;
     * @param year
     * @return 
     */
    public static String queryAllDistrictWinners(int year) {
        return
                "WITH wahlkreiszweitstimmensieger AS (\n" +
                "    SELECT w.wahljahr, w.wahlkreis, MAX(w.stimmen) as stimmen\n" +
                "    FROM wahlkreisstimmenprolandesliste w\n" +
                "    GROUP BY w.wahljahr, w.wahlkreis)\n" +
                "SELECT wkzs.wahljahr, w.nummer, w.name, b.partei AS erststimmen_partei, wks.stimmen AS erststimmen,\n" +
                "		l.partei AS zweitstimmen_partei, wkspl.stimmen AS zweitstimmen\n" +
                "FROM wahlkreis w JOIN wahlkreiszweitstimmensieger wkzs ON w.nummer=wkzs.wahlkreis\n" +
                "JOIN wahlkreisstimmenprolandesliste wkspl ON wkspl.wahlkreis=wkzs.wahlkreis AND wkspl.wahljahr=wkzs.wahljahr \n" +
                "	AND wkspl.stimmen=wkzs.stimmen\n" +
                "JOIN landesliste l ON wkspl.landesliste=l.id AND wkspl.wahljahr=l.wahljahr\n" +
                "JOIN wahlkreissieger wks ON wks.wahlkreis=wkzs.wahlkreis AND wks.wahljahr=wkzs.wahljahr\n" +
                "JOIN bewerber b ON wks.direktkandidat=b.id\n" +
                "WHERE wkspl.wahljahr =" + year;
    }
    
    /**
     *(Q5) bundesland, partei, ueberhang, wahljahr
     *     Alle Ueberhangmandate aller Parteien
     * 
     *         
     */
    public static String queryAllUeberhaenge() {
        return
                "WITH hoechstzahlen2013 AS (\n" +
                "        SELECT h.bundesland, h.partei, (row_number() OVER (PARTITION BY h.bundesland ORDER BY h.quotient DESC)) AS rn\n" +
                "           FROM (\n" +
                "        SELECT l.bundesland, l1.partei, CAST(l.stimmen AS NUMERIC)/u.zahl AS quotient\n" +
                "        FROM erlaubtelisten l JOIN landesliste l1 ON l.landesliste = l1.id AND l.wahljahr=l1.wahljahr AND l.wahljahr=2013, ungerade u\n" +
                "        ORDER BY l.bundesland ASC, quotient DESC) AS h\n" +
                "    ), \n" +
                "    saintlague2013 AS (\n" +
                "        SELECT h.bundesland, h.partei, COUNT(h.partei) AS sitze\n" +
                "        FROM hoechstzahlen2013 h JOIN sitzkontingent s ON s.bundesland = h.bundesland AND s.wahljahr=2013\n" +
                "        WHERE h.rn <= s.kontingent\n" +
                "        GROUP BY h.bundesland, h.partei),\n" +
                "hoechstzahlen2009 AS (\n" +
                "    SELECT h.bundesland, h.partei, (row_number() OVER (PARTITION BY h.partei ORDER BY h.quotient DESC)) AS rn\n" +
                "       FROM (\n" +
                "    SELECT l.bundesland, l1.partei, FLOOR(CAST(l.stimmen AS NUMERIC)/u.zahl) AS quotient\n" +
                "    FROM erlaubtelisten l JOIN landesliste l1 ON l.landesliste = l1.id AND l.wahljahr=l1.wahljahr AND l.wahljahr=2009, ungerade u\n" +
                "	ORDER BY l.bundesland ASC, quotient DESC) AS h\n" +
                ")\n" +
                "\n" +
                "    (SELECT s.bundesland, e.partei, ABS(LEAST(s.sitze - d.count, 0))\n" +
                "                                       AS ueberhang, 2013 AS wahljahr\n" +
                "    FROM saintlague2013 s JOIN erlaubteparteien e ON s.partei=e.partei AND e.wahljahr=2013\n" +
                "    LEFT JOIN direktmandateproland d ON d.bundesland=s.bundesland AND d.partei=e.partei\n" +
                "    AND d.wahljahr=2013\n" +
                "    ORDER BY s.bundesland, e.partei)\n" +
                "    UNION ALL (\n" +
                "        SELECT h.bundesland, h.partei, GREATEST(COALESCE(dmpl.count, 0) - COUNT(h.partei), 0) AS ueberhang, 2009 AS wahljahr\n" +
                "    FROM hoechstzahlen2009 h JOIN oberverteilung o ON o.partei=h.partei AND o.wahljahr=2009\n" +
                "    LEFT JOIN direktmandateproland dmpl ON dmpl.bundesland = h.bundesland AND h.partei = dmpl.partei AND dmpl.wahljahr=o.wahljahr\n" +
                "    WHERE h.rn <= o.sitze\n" +
                "    GROUP BY h.bundesland, h.partei, dmpl.count)";
    }
    
    /**
     * (Q6)
     * @param partyName
     * @return 
     */
    public static String queryPartyDecisions(String partyName, int year){
        return
                "SELECT wahlkreis, titel, vorname, nachname, differenz "
              + "FROM knappsteentscheidung "
              + "WHERE partei = '" + partyName + "' AND wahljahr=" + year;
    }

    /**
     * (Q7_1)
     * @param districtId
     * @param year
     * @return 
     */
    public static String queryQ7_1(int districtId, int year){
        return 
                "WITH waehler AS (\n" +
                "    SELECT bs.wahljahr, bs.wahlkreis, COUNT(*) AS anzahl \n" +
                "	FROM bayerischestimmzettel bs\n" +
                "	GROUP BY bs.wahljahr, bs.wahlkreis\n" +
                ")\n" +
                "SELECT wk.nummer, wk.name, wk.bundesland, \n" +
                "w.anzahl / CAST(wk.wahlberechtigte AS NUMERIC) AS wahlbeteiligung\n" +
                "FROM wahlkreis wk JOIN waehler w ON wk.nummer=w.wahlkreis\n" +
                "WHERE wk.nummer=" + districtId + " AND w.wahljahr=" + year;
    }

    /**
     * (Q7_2)
     * @param districtId
     * @param year
     * @return 
     */
    public static String queryQ7_2(int districtId, int year) {
        return 
                "WITH wsieger AS (\n" +
                "    WITH wesum AS (\n" +
                "        SELECT bs.wahljahr, bs.wahlkreis, bs.direktkandidat, COUNT(bs.direktkandidat) AS stimmen\n" +
                "        FROM bayerischestimmzettel bs WHERE bs.direktkandidat NOTNULL\n" +
                "        GROUP BY bs.wahljahr, bs.wahlkreis, bs.direktkandidat),\n" +
                "    wemax AS (\n" +
                "    	SELECT w.wahljahr, w.wahlkreis, MAX(w.stimmen) AS stimmen\n" +
                "        FROM wesum w\n" +
                "        GROUP BY w.wahljahr, w.wahlkreis\n" +
                "        )\n" +
                "    SELECT w.wahljahr, w.wahlkreis, ws.direktkandidat, w.stimmen\n" +
                "    FROM wemax w JOIN wesum ws ON w.wahljahr=ws.wahljahr AND w.wahlkreis=ws.wahlkreis AND w.stimmen=ws.stimmen\n" +
                "), \n" +
                "gstimmen AS (\n" +
                "    SELECT bs.wahljahr, bs.wahlkreis, COUNT(bs.direktkandidat) AS stimmen\n" +
                "    FROM bayerischestimmzettel bs\n" +
                "    WHERE bs.direktkandidat NOTNULL\n" +
                "    GROUP BY bs.wahljahr, bs.wahlkreis)\n" +
                "    \n" +
                "SELECT b.titel, b.vorname, b.nachname, b.partei, s.stimmen, \n" +
                "CAST(s.stimmen AS NUMERIC)/sg.stimmen AS prozent\n" +
                "FROM bewerber b JOIN  wsieger s ON s.direktkandidat=b.id \n" +
                "JOIN wahlkreis w ON w.nummer=s.wahlkreis \n" +
                "JOIN gstimmen sg ON w.nummer=sg.wahlkreis AND sg.wahljahr=s.wahljahr\n" +
                "WHERE w.nummer=" + districtId + " AND sg.wahljahr=" +  year;
    }
    
    /**
     * (Q7_3)
     * @param districtId
     * @param year
     * @return 
     */
    public static String queryQ7_3(int districtId, int year) {
        return 
                "WITH wstimmenproliste AS (\n" +
                "    WITH wgzs AS (\n" +
                "        SELECT bs.wahljahr, bs.wahlkreis, COUNT(*) AS stimmen\n" +
                "        FROM bayerischestimmzettel bs\n" +
                "        WHERE bs.landesliste NOTNULL\n" +
                "        GROUP BY bs.wahljahr, bs.wahlkreis\n" +
                "        )\n" +
                "    SELECT bs.wahljahr, bs.wahlkreis, bs.landesliste, COUNT(bs.landesliste) AS stimmen,\n" +
                "    CAST(COUNT(bs.landesliste) AS NUMERIC) / ges.stimmen AS prozent\n" +
                "    FROM bayerischestimmzettel bs JOIN wgzs ges ON ges.wahljahr=bs.wahljahr AND ges.wahlkreis=bs.wahlkreis\n" +
                "    WHERE bs.landesliste NOTNULL\n" +
                "    GROUP BY bs.wahljahr, bs.wahlkreis, bs.landesliste, ges.stimmen)\n" +
                "\n" +
                "SELECT l.partei, wspl.stimmen, wspl.prozent\n" +
                "FROM wstimmenproliste wspl JOIN landesliste l ON l.id=wspl.landesliste\n" +
                "WHERE wspl.wahlkreis=" + districtId + " AND l.wahljahr="+ year +
                " ORDER BY wspl.stimmen DESC";
    }
    
    
    
    
    /**
     * (Q7_4_1)
     * @param districtId
     * @param year
     * @return 
     */
    public static String queryQ7_4_1(int districtId, int year) {
        return 
                "WITH bstimmenprokandidat AS (\n" +
                "        SELECT bs.wahljahr, bs.wahlkreis, bs.direktkandidat, COUNT(bs.direktkandidat) AS stimmen\n" +
                "        FROM bayerischestimmzettel bs WHERE bs.direktkandidat NOTNULL\n" +
                "        GROUP BY bs.wahljahr, bs.wahlkreis, bs.direktkandidat),\n" +
                "bstimmengueltigkeit AS (\n" +
                "    SELECT bs.wahljahr, bs.wahlkreis, COUNT(bs.direktkandidat) AS g_erststimmen\n" +
                "    FROM bayerischestimmzettel bs\n" +
                "    WHERE bs.direktkandidat NOTNULL\n" +
                "    GROUP BY bs.wahljahr, bs.wahlkreis),\n" +
                "bwahlkreisstimmenprolandesliste AS (\n" +
                "    WITH wgzs AS (\n" +
                "        SELECT bs.wahljahr, bs.wahlkreis, COUNT(*) AS stimmen\n" +
                "        FROM bayerischestimmzettel bs\n" +
                "        WHERE bs.landesliste NOTNULL\n" +
                "        GROUP BY bs.wahljahr, bs.wahlkreis\n" +
                "        )\n" +
                "    SELECT bs.wahljahr, bs.wahlkreis, bs.landesliste, COUNT(bs.landesliste) AS stimmen,\n" +
                "    CAST(COUNT(bs.landesliste) AS NUMERIC) / ges.stimmen AS prozent\n" +
                "    FROM bayerischestimmzettel bs JOIN wgzs ges ON ges.wahljahr=bs.wahljahr AND ges.wahlkreis=bs.wahlkreis\n" +
                "    WHERE bs.landesliste NOTNULL\n" +
                "    GROUP BY bs.wahljahr, bs.wahlkreis, bs.landesliste, ges.stimmen),\n" +
                "erststimmen AS (\n" +
                "    SELECT spk.wahljahr, spk.wahlkreis, b.partei, SUM(spk.stimmen) as e_absolut, CAST(SUM(spk.stimmen) AS NUMERIC)/sg.g_erststimmen AS e_prozent\n" +
                "    FROM bstimmenprokandidat spk JOIN bewerber b ON spk.direktkandidat=b.id\n" +
                "    JOIN bstimmengueltigkeit sg ON spk.wahljahr=sg.wahljahr AND spk.wahlkreis=sg.wahlkreis\n" +
                "    JOIN erlaubteparteien e ON b.partei=e.partei AND spk.wahljahr=e.wahljahr\n" +
                "    GROUP BY spk.wahljahr, spk.wahlkreis, b.partei, sg.g_erststimmen),\n" +
                "zweitstimmen AS (\n" +
                "    SELECT wspl.wahljahr, wspl.wahlkreis, l.partei, wspl.stimmen as z_absolut, wspl.prozent AS z_prozent\n" +
                "	FROM bwahlkreisstimmenprolandesliste wspl JOIN landesliste l ON l.id=wspl.landesliste AND wspl.wahljahr=l.wahljahr),\n" +
                "helptable AS (\n" +
                "SELECT e.wahljahr, e.wahlkreis, e.partei, e.e_absolut, e.e_prozent, z.z_absolut, z.z_prozent\n" +
                "FROM erststimmen e JOIN zweitstimmen z ON e.wahljahr=z.wahljahr AND e.partei=z.partei AND e.wahlkreis=z.wahlkreis)\n" +
                "SELECT h1.wahlkreis, h1.partei, COALESCE(h1.e_absolut, 0) - COALESCE(h2.e_absolut) AS ediffabs,\n" +
                "	COALESCE(h1.e_prozent, 0) - COALESCE(h2.e_prozent, 0) AS ediffpro, \n" +
                "    COALESCE(h1.z_absolut, 0) - COALESCE(h2.z_absolut, 0) AS zdiffabs,\n" +
                "    COALESCE(h1.z_prozent, 0) - COALESCE(h2.z_prozent, 0) AS zdiffpro\n" +
                "FROM helptable h1 JOIN helptable h2 ON h1.wahljahr > h2.wahljahr AND h1.wahlkreis=h2.wahlkreis AND h1.partei=h2.partei AND h1.wahlkreis=" +  districtId + " AND h1.wahljahr=" + year;
    }
    
    
    /**
     * (Q7_4_2)
     * @param districtId
     * @param year
     * @return 
     */
    public static String queryQ7_4_2(int districtId, int year) {
        return 
                "WITH bstimmengueltigkeit AS (\n" +
            "    WITH bstimmenprokandidat AS (\n" +
            "        SELECT bs.wahljahr, bs.wahlkreis, bs.direktkandidat, COUNT(bs.direktkandidat) AS stimmen\n" +
            "        FROM bayerischestimmzettel bs WHERE bs.direktkandidat NOTNULL\n" +
            "        GROUP BY bs.wahljahr, bs.wahlkreis, bs.direktkandidat)\n" +
            ",\n" +
            "bwahlkreisstimmenprolandesliste AS (\n" +
            "    WITH wgzs AS (\n" +
            "        SELECT bs.wahljahr, bs.wahlkreis, COUNT(*) AS stimmen\n" +
            "        FROM bayerischestimmzettel bs\n" +
            "        WHERE bs.landesliste NOTNULL\n" +
            "        GROUP BY bs.wahljahr, bs.wahlkreis\n" +
            "        )\n" +
            "    SELECT bs.wahljahr, bs.wahlkreis, bs.landesliste, COUNT(bs.landesliste) AS stimmen,\n" +
            "    CAST(COUNT(bs.landesliste) AS NUMERIC) / ges.stimmen AS prozent\n" +
            "    FROM bayerischestimmzettel bs JOIN wgzs ges ON ges.wahljahr=bs.wahljahr AND ges.wahlkreis=bs.wahlkreis\n" +
            "    WHERE bs.landesliste NOTNULL\n" +
            "    GROUP BY bs.wahljahr, bs.wahlkreis, bs.landesliste, ges.stimmen\n" +
            "),\n" +
            "bewerber_count AS(\n" +
            "    	SELECT spk.wahlkreis, SUM(spk.stimmen) AS stimmen, spk.wahljahr FROM bstimmenprokandidat spk\n" +
            "    	GROUP BY spk.wahlkreis, spk.wahljahr ORDER BY spk.wahljahr, spk.wahlkreis\n" +
            "	),\n" +
            "	listen_count AS(\n" +
            "    	SELECT wspl.wahlkreis, SUM(wspl.stimmen) AS stimmen, wspl.wahljahr FROM bwahlkreisstimmenprolandesliste wspl\n" +
            "    	GROUP BY wspl.wahlkreis, wspl.wahljahr ORDER BY wspl.wahljahr, wspl.wahlkreis\n" +
            "	),\n" +
            "    gesamt AS(\n" +
            "        SELECT s.wahlkreis, s.wahljahr, COUNT(s.wahlkreis) AS waehler FROM\n" +
            "        bayerischestimmzettel s GROUP BY s.wahlkreis, s.wahljahr\n" +
            "    )\n" +
            "    SELECT DISTINCT ges.wahlkreis, ges.wahljahr, ges.waehler, bc.stimmen AS g_erststimmen,\n" +
            "    lc.stimmen AS g_zweitstimmen, ges.waehler - bc.stimmen AS ug_erststimmen, \n" +
            "    ges.waehler - lc.stimmen AS ug_zweitstimmen\n" +
            "    FROM gesamt ges JOIN bewerber_count bc ON ges.wahlkreis=bc.wahlkreis AND ges.wahljahr=bc.wahljahr JOIN\n" +
            "    listen_count lc ON ges.wahlkreis=lc.wahlkreis AND ges.wahljahr=lc.wahljahr\n" +
            ")\n" +
            "SELECT w.nummer, w.name, w.bundesland, \n" +
            "(sg1.waehler - sg2.waehler) AS waehlerdiffabs, \n" +
            "(sg1.waehler / CAST(w.wahlberechtigte AS NUMERIC) - sg2.waehler / CAST(w.wahlberechtigte AS NUMERIC)) AS waehlerdiffpro,\n" +
            "(sg1.g_erststimmen - sg2.g_erststimmen) as gediffabs,\n" +
            "(sg1.g_erststimmen / CAST(sg1.waehler AS NUMERIC) - sg2.g_erststimmen / CAST(sg2.waehler AS NUMERIC)) AS gediffpro,\n" +
            "(sg1.ug_erststimmen - sg2.ug_erststimmen) as ugediffabs,\n" +
            "(sg1.ug_erststimmen / CAST(sg1.waehler AS NUMERIC) - sg2.ug_erststimmen / CAST(sg2.waehler AS NUMERIC)) AS ugediffpro,\n" +
            "(sg1.g_zweitstimmen - sg2.g_zweitstimmen) as gzdiffabs,\n" +
            "(sg1.g_zweitstimmen / CAST(sg1.waehler AS NUMERIC) - sg2.g_zweitstimmen / CAST(sg2.waehler AS NUMERIC)) AS gzdiffpro,\n" +
            "(sg1.ug_zweitstimmen - sg2.ug_zweitstimmen) as ugzdiffabs,\n" +
            "(sg1.ug_zweitstimmen / CAST(sg1.waehler AS NUMERIC) - sg2.ug_zweitstimmen / CAST(sg2.waehler AS NUMERIC)) AS ugzdiffpro\n" +
            "FROM wahlkreis w JOIN bstimmengueltigkeit sg1 ON w.nummer=sg1.wahlkreis \n" +
            "JOIN bstimmengueltigkeit sg2 ON sg1.wahljahr > sg2.wahljahr AND sg1.wahlkreis=sg2.wahlkreis\n" +
            "WHERE w.nummer=" + districtId + " AND sg1.wahljahr=" + year;
    }
    
    
}
