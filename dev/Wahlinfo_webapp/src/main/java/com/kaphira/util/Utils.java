package com.kaphira.util;

import com.kaphira.entities.Party;

/**
 *
 * @author theralph
 */
public class Utils {
    
    public static final String getColorCodeForParty(Party party){
        return getColorCodeForPartyName(party.getName());
    }

    private static String getColorCodeForPartyName(String name) {
        name = name.toLowerCase();
        
        switch(name){
            case "cdu": return "000000";
            case "csu": return "000000";
            case "spd": return "FF0000";
            case "die linke": return "FE2EF7";
            case "grüne": return "00FF00";
            case "fdp": return "FFFF00";
            case "afd": return "0000FF";
            default: return "848484";
       }
    }
    
    
    
}
