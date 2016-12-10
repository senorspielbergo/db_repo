package com.kaphira.database;

/**
 *
 * @author theralph
 */
public class DbQueries {
    public static final String ALL_DEVELOPERS = "select * from developer;";
    public static final String BUNDESTAG_DISTRIBUTION_PARTIES_AND_PERCENTAGES = "select * from Oberverteilung;";

    /**
     * Gibt WahlkreisID, Name, Bundesland, zweitstimmen und erststimmen aus. (Q4) 
     */
    public static final String WINNERS_PER_DISTRICT = 
            "WITH wahlkreiszweitstimmensieger AS(\n" 
          + "SELECT w.wahlkreis MAX(w.stimmen) FROM wahlkreisstimmenprolandesliste w GROUP BY w.wahlkreis)\n"
          + "SELECT DISTINCT w.nummer, w.name, w.bundesland, wspl.stimmen as zweitstimmen, wkzs.stimmen as erststimmen,"
          + "b.partei as erststimmensieger, wkpl.partei as zweitstimmensieger FROM wahlkreis w JOIN wahlkreisstimmenprolandesliste"
          + " wkpl ON w.nummer=wkpl.wahlkreis JOIN wahlkreiszweitstimmensieger wkzs ON w.nummer=wkzs.wahlkreis AND"
          + " wkpl.stimmen=wkzs.stimmen JOIN wahlkreissieger wks ON wks.wahlkreis=w.nummer JOIN bewerber b ON wks.direktkandidat=b.id ";
    
    
    /**
     * Gibt ID, Name, Bundesland und Wahlbeteiligung für einen Wahlkreis aus.
     * 
     * 
     * @param districtId 
     * @return 
     */
    public static String queryTurnout(int districtId) {
        return 
               "SELECT w.nummer, w.name, w.bundesland, sg.waehler / CAST(w.wahlberechtigte AS NUMERIC) "
             + "AS wahlbeteiligung FROM wahlkreis w JOIN stimmengueltigkeit sg ON w.nummer=sg.wahlkreis " 
             + "WHERE w.nummer=" + districtId + " AND sg.wahljahr=2013";
 
    }


    /**
     * Gibt Titel, Vorname, Nachname, Partei und Stimmen eines Wahlkreissiegers aus.
     * @param districtId
     * @return 
     */
    public static String queryDistrictWinner(int districtId) {
        return
                "SELECT b.titel, b.vorname, b.nachname, b.partei, s.stimmen, "
              + " CAST(s.stimmen AS NUMERIC)/sg.g_erststimmen AS prozent\n" 
              + "FROM bewerber b JOIN  wahlkreissieger s ON s.direktkandidat=b.id JOIN\n" 
              + "wahlkreis w ON w.nummer=s.wahlkreis JOIN stimmengueltigkeit sg ON w.nummer=sg.wahlkreis\n" 
              + "WHERE w.nummer= "+ districtId +" AND sg.wahljahr=2013";
    }

    
    /**
     * Gibt die prozentuale und absolute Anzahl an Stimmen fuer jede Partei eines Wahlkreises aus.
     * @param districtId
     * @return 
     */
    public static String queryDistrictPartyResults(int districtId) {
        return
                "SELECT l.partei, wspl.stimmen, wspl.prozent\n" 
              + "FROM wahlkreisstimmenprolandesliste wspl JOIN landesliste l ON wspl.landesliste=l.id\n" 
              + "WHERE wspl.wahlkreis=" + districtId;
                }
    
    
    public static String queryPartyDecisions(String partyName){
        return
                "SELECT wahlkreis, titel, vorname, nachname, differenz "
              + "FROM knappsteentscheidung "
              + "WHERE partei = '" + partyName + "'";
    }
}
