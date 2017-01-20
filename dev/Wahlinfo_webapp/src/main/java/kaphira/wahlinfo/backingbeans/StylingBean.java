package kaphira.wahlinfo.backingbeans;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import kaphira.wahlinfo.util.Utils;

/**
 * This bean provides the functionality to dynamically set the css color attributes according to 
 * a specific party / a positive or negative value
 * @author theralph
 */
@ManagedBean
@ApplicationScoped
public class StylingBean implements Serializable {

    @PostConstruct
    private void init(){
        
    }
    
    public String getPartyColor(String partyName){
        return Utils.getColorCodeForPartyName(partyName);
    }
    
    public String getNumberColor(String sNumber){
        if(sNumber.startsWith("-")){
            return "#FF0000";
        }
        return "#00B200";
    }

}
