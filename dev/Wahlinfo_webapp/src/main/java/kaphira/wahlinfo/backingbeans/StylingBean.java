package kaphira.wahlinfo.backingbeans;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import kaphira.wahlinfo.util.Utils;

/**
 *
 * @author theralph
 */
@ManagedBean
@ApplicationScoped
public class StylingBean {

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
