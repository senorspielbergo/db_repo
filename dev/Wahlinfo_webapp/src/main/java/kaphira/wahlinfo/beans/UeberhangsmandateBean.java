package kaphira.wahlinfo.beans;

import kaphira.wahlinfo.main.DatabaseBean;
import kaphira.wahlinfo.entities.Mandat;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author theralph
 */

@ManagedBean
@SessionScoped
public class UeberhangsmandateBean {

    private static final String COLUMN_COUNTRY = "bundesland";
    private static final String COLUMN_PARTY = "partei";
    private static final String COLUMN_MANDAT = "ueberhang";
    private static final String COLUMN_YEAR = "wahljahr";
    
    
    @ManagedProperty(value="#{databaseBean}")
    private DatabaseBean databaseBean;
    
    
    
    private List<Mandat> mandate2013;
    private List<Mandat> mandate2009;
    
    private List<Mandat> selectedMandate;
    
    @PostConstruct
    public void init(){
        loadAllUeberhangmandate();
        selectedMandate = getMandate2013();
    }

    public void setMandate2009(List<Mandat> mandate2009) {
        this.mandate2009 = mandate2009;
    }

    private void loadAllUeberhangmandate() {
        ResultSet result = databaseBean.queryQ5();
        
        
            
        try {
            while (result.next()) {
                
                String country = result.getString(COLUMN_COUNTRY);
                String partyName = result.getString(COLUMN_PARTY);
                int mandate = Integer.parseInt(result.getString(COLUMN_MANDAT));
                int year = Integer.parseInt(result.getString(COLUMN_YEAR));
                
                Mandat mandat = new Mandat(country, partyName, mandate, year);
                
                if (year == 2013) {
                    getMandate2013().add(mandat);
                } else {
                    getMandate2009().add(mandat);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UeberhangsmandateBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    
    public List<Mandat> getMandate2013() {
        if(mandate2013 == null) {
            mandate2013 = new ArrayList<>();
        }
        return mandate2013;
    }
    
    public void setMandate2013(List<Mandat> mandate2013) {
        this.mandate2013 = mandate2013;
    }
    
    public List<Mandat> getMandate2009() {
        if(mandate2009 == null) {
            mandate2009 = new ArrayList<>();
        }
        return mandate2009;
    }

    public List<Mandat> getSelectedMandate() {
        return selectedMandate;
    }

    public void setSelectedMandate(List<Mandat> selectedMandate) {
        this.selectedMandate = selectedMandate;
    }

    public DatabaseBean getDatabaseBean() {
        return databaseBean;
    }

    public void setDatabaseBean(DatabaseBean databaseBean) {
        this.databaseBean = databaseBean;
    }
    
}
