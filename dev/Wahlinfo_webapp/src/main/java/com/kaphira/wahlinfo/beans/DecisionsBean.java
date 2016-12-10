package com.kaphira.wahlinfo.beans;

import com.kaphira.database.DatabaseConnectionManager;
import com.kaphira.database.DbQueries;
import com.kaphira.entities.Decision;
import com.kaphira.entities.Party;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author theralph
 */
@ManagedBean
@SessionScoped
public class DecisionsBean {

    private static final String QRY_ALL_PARTIES = "select * from partei";
    private static final String COLUMN_NAME = "name";
    
    private static final String COLUMN_DISTRICT = "wahlkreis";
    private static final String COLUMN_TITLE = "titel";
    private static final String COLUMN_FIRSTNAME = "vorname";
    private static final String COLUMN_LASTNAME = "nachname";
    private static final String COLUMN_DIFFERNCE = "differenz";
            
    private List<Party> parties;

    
    
    @PostConstruct
    public void init() {
        setParties(queryAllParties());
        loadAllDecisions(getParties());
    }
    
    private void loadAllDecisions(List<Party> parties) {
        for (Party party : parties) {
            party.setClosestDecisions(loadPartyDecisions(party.getName()));
        }
    }
    
    
    private List<Decision> loadPartyDecisions(String partyName) {
        
        ResultSet result = DatabaseConnectionManager
                            .getInstance()
                            .executeQuery(DbQueries.queryPartyDecisions(partyName));
        
        List<Decision> decisions = new ArrayList<>();
        
        try {
            while (result.next()) {
                String district = result.getString(COLUMN_DISTRICT);
                String title = result.getString(COLUMN_TITLE);
                String firstName = result.getString(COLUMN_FIRSTNAME);
                String lastName = result.getString(COLUMN_LASTNAME);
                int difference = Integer.parseInt(result.getString(COLUMN_DIFFERNCE));
                
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
        
        ResultSet result = DatabaseConnectionManager
                            .getInstance()
                            .executeQuery(QRY_ALL_PARTIES);
        
        List<Party> queriedParties = new ArrayList<>();
        
        try {
            
            while (result.next()) {
                
                String partyName = result.getString(COLUMN_NAME);
                
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

    
}
