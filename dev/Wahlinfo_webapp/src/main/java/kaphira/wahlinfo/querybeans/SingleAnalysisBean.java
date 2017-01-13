package kaphira.wahlinfo.querybeans;

import kaphira.wahlinfo.database.DatabaseBean;
import kaphira.wahlinfo.entities.District;
import kaphira.wahlinfo.entities.History;
import kaphira.wahlinfo.entities.Party;
import kaphira.wahlinfo.entities.Politician;
import kaphira.wahlinfo.util.Utils;
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
public class SingleAnalysisBean {

    private static final String QRY_ALL_DISTRICTS = "select * from wahlkreis where nummer in (213,214,215,216,217)";
    
    private List<District> districts;
    private District selectedDistrict;
    private int selectedYear;
    
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
                
                int districtId = Integer.parseInt(result.getString(DbColumns.CLM_ID));
                int allowedVoters = Integer.parseInt(result.getString(DbColumns.CLM_ALLOWED_VOTERS));
                String districtName = result.getString(DbColumns.CLM_NAME);
                String districtCountry = result.getString(DbColumns.CLM_COUNTRY);
                
                
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
        
        ResultSet result = databaseBean.queryQ7_1(district.getId(),selectedYear);
    
        result.next();
        double turnout = Utils.getPercentRoundedDouble(result.getString(DbColumns.CLM_TURNOUT));
    
        district.setWahlbeteiligung(turnout);
    }

    private void loadWinner(District district) throws SQLException {
        
        ResultSet result = databaseBean.queryQ7_2(district.getId(),selectedYear);
    
        result.next();
        String title = result.getString(DbColumns.CLM_TITLE);
        String fullName = result.getString(DbColumns.CLM_FIRSTNAME) +" "+ result.getString(DbColumns.CLM_LASTNAME);
        String party = result.getString(DbColumns.CLM_PARTY);
        int votes = Integer.parseInt(result.getString(DbColumns.CLM_VOTES));
        
        
        Politician politician = new Politician(fullName, party);
        politician.setTitle(title);
        politician.setVotes(votes);
        
        district.setDirektKandidat(politician);
    }
    
    private void loadPartyResults(District district) throws SQLException {
        
        ResultSet result = databaseBean.queryQ7_3(district.getId(), selectedYear);
    
        List<Party> parties = new ArrayList<>();
        
        while(result.next()) {
            String partyName = result.getString(DbColumns.CLM_PARTY);
            int votes = Integer.parseInt(result.getString(DbColumns.CLM_VOTES));
            double percentage = Utils.getPercentRoundedDouble(result.getString(DbColumns.CLM_PERCENTAGE));
            
            Party party = new Party(partyName);
            party.setTotalVotes(votes);
            party.setPercentage(percentage);
            parties.add(party);
        }
        district.setParties(parties);
    }
    
    private void loadHistories(District district) throws SQLException{

        ResultSet result = databaseBean.queryQ7_4_1(district.getId(), selectedYear);
    
        List<History> histories = new ArrayList<>();
        
        while(result.next()) {
            String partyName = result.getString(DbColumns.CLM_PARTY);
            
            int firstVoteDiff = Integer.parseInt(result.getString(DbColumns.CLM_FIRST_VOTE_DIFF));
            int secondVoteDiff = Integer.parseInt(result.getString(DbColumns.CLM_SECOND_VOTE_DIFF));
            double firstPercentage = Utils.getPercentRoundedDouble(result.getString(DbColumns.CLM_FIRST_VOTE_PERCENTAGE));
            double secondPercentage = Utils.getPercentRoundedDouble(result.getString(DbColumns.CLM_SECOND_VOTE_PERCENTAGE));
            
            History history = new History(partyName, firstVoteDiff, secondVoteDiff, firstPercentage, secondPercentage);
            histories.add(history);
        }
        district.setHistories(histories);
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

    public DatabaseBean getDatabaseBean() {
        return databaseBean;
    }

    public void setDatabaseBean(DatabaseBean databaseBean) {
        this.databaseBean = databaseBean;
    }
    
    
}
