package com.kaphira.wahlinfo.beans;

import com.kaphira.database.DatabaseConnectionManager;
import com.kaphira.database.DbQueries;
import com.kaphira.entities.District;
import com.kaphira.entities.Party;
import com.kaphira.entities.Politician;
import com.kaphira.util.Utils;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped; 
import javax.faces.bean.ManagedBean; 

/**
 *
 * @author theralph
 */
@ManagedBean(name="districtBean")
@SessionScoped
public class DistrictBean implements Serializable {

    private static final String QRY_ALL_DISTRICTS = "select * from wahlkreis";
    private static final String COLUMN_ID = "nummer";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_ALLOWED_VOTERS = "wahlberechtigte";
    private static final String COLUMN_COUNTRY = "bundesland";
    private static final String COLUMN_TURNOUT = "wahlbeteiligung";
    private static final String COLUMN_FIRSTNAME = "vorname";
    private static final String COLUMN_LASTNAME = "nachname";
    private static final String COLUMN_TITLE = "titel";
    private static final String COLUMN_PARTY = "partei";
    private static final String COLUMN_VOTES = "stimmen";
    private static final String COLUMN_PERCENTAGE = "prozent";
    
    private List<District> districts;
    private District selectedDistrict;
    
    @PostConstruct
    private void init(){
        setDistricts(queryAllDistricts());
        setSelectedDistrict(getDistricts().get(0));
        loadDistrict(getSelectedDistrict());
    }

    public void onDistrictSelection() {
        if (selectedDistrict.isLoaded()) {
            return;
        }
        loadDistrict(selectedDistrict);
    }
        
    private void loadDistrict(District district) {
        
        try {
        
            loadTurnout(district);
            loadWinner(district);
            loadPartyResults(district);
            district.setIsLoaded(true);
            
        } catch (SQLException ex) {
            Logger.getLogger(DistrictBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public District findDistrictByName(String name) {
        if(name!=null) {
            for (District district : districts) {
                if (district.getName().equals(name)) {
                    return district;
                }
            }
        }
        return null;
    }

    
    private List<District> queryAllDistricts(){
        
        ResultSet result = DatabaseConnectionManager
                    .getInstance()
                    .executeQuery(QRY_ALL_DISTRICTS);
        
        List<District> queriedDistricts = new ArrayList<>();
        
        try {
            
            while (result.next()) {
                
                int districtId = Integer.parseInt(result.getString(COLUMN_ID));
                int allowedVoters = Integer.parseInt(result.getString(COLUMN_ALLOWED_VOTERS));
                String districtName = result.getString(COLUMN_NAME);
                String districtCountry = result.getString(COLUMN_COUNTRY);
                
                
                District district = new District(districtId, districtName);
                district.setBundesland(districtCountry);
                district.setWahlberechtigte(allowedVoters);
                queriedDistricts.add(district);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BundestagBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return queriedDistricts;
    } 
    
    private void loadTurnout(District district) throws SQLException {
        
        System.out.println(district.getId());
        System.out.println(DbQueries.queryTurnout(district.getId()));
        
        ResultSet result = DatabaseConnectionManager
                    .getInstance()
                    .executeQuery(DbQueries.queryTurnout(district.getId()));
    
        result.next();
        double turnout = Utils.getPercentRoundedDouble(result.getString(COLUMN_TURNOUT));
    
        district.setWahlbeteiligung(turnout);
    }

    private void loadWinner(District district) throws SQLException {
        
        ResultSet result = DatabaseConnectionManager
                    .getInstance()
                    .executeQuery(DbQueries.queryDistrictWinner(district.getId()));
    
        result.next();
        String title = result.getString(COLUMN_TITLE);
        String fullName = result.getString(COLUMN_FIRSTNAME) +" "+ result.getString(COLUMN_LASTNAME);
        String party = result.getString(COLUMN_PARTY);
        int votes = Integer.parseInt(result.getString(COLUMN_VOTES));
        
        
        Politician politician = new Politician(fullName, party);
        politician.setTitle(title);
        politician.setVotes(votes);
        
        district.setDirektKandidat(politician);
    }
    
    private void loadPartyResults(District district) throws SQLException {
        
        ResultSet result = DatabaseConnectionManager
                    .getInstance()
                    .executeQuery(DbQueries.queryDistrictPartyResults(district.getId()));
    
        List<Party> parties = new ArrayList<>();
        
        while(result.next()) {
            String partyName = result.getString(COLUMN_PARTY);
            int votes = Integer.parseInt(result.getString(COLUMN_VOTES));
            double percentage = Utils.getPercentRoundedDouble(result.getString(COLUMN_PERCENTAGE));
            
            Party party = new Party(partyName);
            party.setTotalVotes(votes);
            party.setPercentage(percentage);
            parties.add(party);
        }
        district.setParties(parties);
    }
    
    
    /***********************************
     *          GETTER/SETTER
     ***********************************/

    public List<District> getDistricts() {
        return this.districts;
    }

    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }

    public District getSelectedDistrict() {
        return selectedDistrict;
    }

    public void setSelectedDistrict(District selectedDistrict) {
        this.selectedDistrict = selectedDistrict;
    }
    
}
