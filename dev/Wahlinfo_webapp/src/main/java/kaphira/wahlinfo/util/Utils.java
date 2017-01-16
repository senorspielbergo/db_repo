package kaphira.wahlinfo.util;

import kaphira.wahlinfo.entities.Party;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 *
 * @author theralph
 */
public class Utils {
    
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.##");
    
    static {
        decimalFormat.setRoundingMode(RoundingMode.CEILING);
    }
    
    public static Double getPercentRoundedDouble(String doubleString) {
        double result = Double.parseDouble(doubleString)*100;
        String sDouble = decimalFormat.format(result).replace(",", ".");
        result =  Double.parseDouble(sDouble);
        return result;
    }
    
    public static String getColorCodeForParty(Party party){
        return getColorCodeForPartyName(party.getName());
    }

    public static String getColorCodeForPartyName(String name) {
        name = name.toLowerCase();
        
        switch(name){
            case "cdu": return "000000";
            case "csu": return "000000";
            case "spd": return "FF0000";
            case "die linke": return "ff93c9";
            case "grüne": return "00b200";
            case "fdp": return "FFFF00";
            case "afd": return "0000FF";
            default: return "3a3a3a";
       }
    }
    
    
    
}
