package kaphira.wahlinfo.database;

import java.io.Serializable;

/**
 * Constant list of all database columns which are used by the queries of this application
 * @author theralph
 */
public class DbColumns implements Serializable{
    
    private DbColumns(){
        //Overriding public constructor
    }
    
    public static final String CLM_TITLE = "titel";
    public static final String CLM_FIRSTNAME = "vorname";
    public static final String CLM_LASTNAME = "nachname";
    public static final String CLM_PARTY = "partei";
    public static final String CLM_SEATS = "sitze";
    public static final String CLM_NAME = "name";
    public static final String CLM_DISTRICT = "wahlkreis";
    public static final String CLM_DIFFERNCE = "differenz";
    public static final String CLM_ID = "nummer";
    public static final String CLM_ALLOWED_VOTERS = "wahlberechtigte";
    public static final String CLM_COUNTRY = "bundesland";
    public static final String CLM_TURNOUT = "wahlbeteiligung";
    public static final String CLM_VOTES = "stimmen";
    public static final String CLM_PERCENTAGE = "prozent";
    public static final String CLM_SECOND_VOTE_DIFF = "zdiffabs";
    public static final String CLM_FIRST_VOTE_DIFF = "ediffabs";
    public static final String CLM_FIRST_VOTE_PERCENTAGE = "ediffpro";
    public static final String CLM_SECOND_VOTE_PERCENTAGE = "zdiffpro";
    public static final String CLM_FIRST_VOTE_PARY = "erststimmen_partei";
    public static final String CLM_FIRST_VOTES = "erststimmen";
    public static final String CLM_SECOND_VOTE_PARTY = "zweitstimmen_partei";
    public static final String CLM_SECOND_VOTES = "zweitstimmen";
    public static final String CLM_MANDAT = "ueberhang";
    public static final String CLM_YEAR = "wahljahr";
}
