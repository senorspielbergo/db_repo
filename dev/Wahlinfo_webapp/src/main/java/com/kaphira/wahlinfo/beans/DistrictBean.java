package com.kaphira.wahlinfo.beans;

import com.kaphira.main.DatabaseBean;
import com.kaphira.entities.District;
import com.kaphira.entities.History;
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
import javax.faces.bean.ManagedProperty;

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
    private int selectedYear;
    private String COLUMN_SECOND_VOTE_DIFF = "zdiffabs";
    private String COLUMN_FIRST_VOTE_DIFF = "ediffabs";
    private String COLUMN_FIRST_VOTE_PERCENTAGE = "ediffpro";
    private String COLUMN_SECOND_VOTE_PERCENTAGE = "zdiffpro";
    
    @ManagedProperty(value="#{databaseBean}")
    private DatabaseBean databaseBean;
    
    
    
    @PostConstruct
    private void init(){
        setSelectedYear(2013);
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
            loadHistories(district);
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
        
        ResultSet result = databaseBean.executeQuery(QRY_ALL_DISTRICTS);
        
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
        
        ResultSet result = databaseBean.queryQ3_1(district.getId(), selectedYear);
    
        result.next();
        double turnout = Utils.getPercentRoundedDouble(result.getString(COLUMN_TURNOUT));
    
        district.setWahlbeteiligung(turnout);
    }

    private void loadWinner(District district) throws SQLException {
        
        ResultSet result = databaseBean.queryQ3_2(district.getId(), selectedYear);
    
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
        
        ResultSet result = databaseBean.queryQ3_3(district.getId(), selectedYear);
    
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

    public int getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(int selectedYear) {
        this.selectedYear = selectedYear;
    }

    private void loadHistories(District district) throws SQLException{

        ResultSet result = databaseBean.queryQ3_4_1(district.getId(), selectedYear);
    
        List<History> histories = new ArrayList<>();
        
        while(result.next()) {
            String partyName = result.getString(COLUMN_PARTY);
            
            int firstVoteDiff = Integer.parseInt(result.getString(COLUMN_FIRST_VOTE_DIFF));
            int secondVoteDiff = Integer.parseInt(result.getString(COLUMN_SECOND_VOTE_DIFF));
            double firstPercentage = Utils.getPercentRoundedDouble(result.getString(COLUMN_FIRST_VOTE_PERCENTAGE));
            double secondPercentage = Utils.getPercentRoundedDouble(result.getString(COLUMN_SECOND_VOTE_PERCENTAGE));
            
            History history = new History(partyName, firstVoteDiff, secondVoteDiff, firstPercentage, secondPercentage);
            histories.add(history);
        }
        district.setHistories(histories);
    }
 
    
    public DatabaseBean getDatabaseBean() {
        return databaseBean;
    }

    public void setDatabaseBean(DatabaseBean databaseBean) {
        this.databaseBean = databaseBean;
    }
    
    
}
