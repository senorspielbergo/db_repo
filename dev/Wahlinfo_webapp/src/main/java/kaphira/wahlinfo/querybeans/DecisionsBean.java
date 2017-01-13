package kaphira.wahlinfo.querybeans;

import kaphira.wahlinfo.database.DatabaseBean;
import kaphira.wahlinfo.entities.Decision;
import kaphira.wahlinfo.entities.Party;
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
import kaphira.wahlinfo.database.DbColumns;

/**
 *
 * @author theralph
 */
@ManagedBean
@SessionScoped
public class DecisionsBean {

    private static final String QRY_ALL_PARTIES = "select * from partei";

            
    private List<Party> parties;
    private int selectedYear;

    @ManagedProperty(value="#{databaseBean}")
    private DatabaseBean databaseBean;
    
    
    
    @PostConstruct
    public void init() {
        setSelectedYear(2013);
        setParties(queryAllParties());
        loadAllDecisions(getParties());
    }
    
    private void loadAllDecisions(List<Party> parties) {
        for (Party party : parties) {
            party.setClosestDecisions(loadPartyDecisions(party.getName()));
        }
    }
    
    
    private List<Decision> loadPartyDecisions(String partyName) {
        
        ResultSet result = databaseBean.queryQ6(partyName, selectedYear);
        
        List<Decision> decisions = new ArrayList<>();
        
        try {
            while (result.next()) {
                String district = result.getString(DbColumns.CLM_DISTRICT);
                String title = result.getString(DbColumns.CLM_TITLE);
                String firstName = result.getString(DbColumns.CLM_FIRSTNAME);
                String lastName = result.getString(DbColumns.CLM_LASTNAME);
                int difference = Integer.parseInt(result.getString(DbColumns.CLM_DIFFERNCE));
                
                String completeName = new StringBuilder(title)
                                            .append(" ").append(firstName)
                                            .append(" ").append(lastName).toString();
                Decision decision = new Decision(district,completeName , difference);
                decisions.add(decision);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DecisionsBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return decisions;
    }

    
    private List<Party> queryAllParties(){
        
        ResultSet result = databaseBean
                            .executeQuery("select * from partei");
        
        List<Party> queriedParties = new ArrayList<>();
        
        try {
            
            while (result.next()) {
                
                String partyName = result.getString(DbColumns.CLM_NAME);
                
                Party party = new Party(partyName);
                queriedParties.add(party);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BundestagBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return queriedParties;
    }
    
    public List<Party> getParties() {
        if (parties == null) {
            parties = new ArrayList<>();
        }
        return parties;
    }
    
    public void setParties(List<Party> parties) {
        this.parties = parties;
    }

    public int getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(int selectedYear) {
        this.selectedYear = selectedYear;
    }

    public DatabaseBean getDatabaseBean() {
        return databaseBean;
    }

    public void setDatabaseBean(DatabaseBean databaseBean) {
        this.databaseBean = databaseBean;
    }
}
