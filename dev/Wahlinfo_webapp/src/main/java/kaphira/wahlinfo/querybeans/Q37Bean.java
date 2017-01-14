package kaphira.wahlinfo.querybeans;

import java.io.Serializable;
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
import kaphira.wahlinfo.database.DatabaseBean;
import kaphira.wahlinfo.database.DbColumns;
import kaphira.wahlinfo.entities.District;
import kaphira.wahlinfo.entities.History;
import kaphira.wahlinfo.entities.Party;
import kaphira.wahlinfo.entities.Politician;
import kaphira.wahlinfo.util.Utils;

/**
 *
 * @author theralph
 */
@ManagedBean
@SessionScoped
public class Q37Bean implements Serializable {

    private final Logger logger = Logger.getLogger(Q37Bean.class.getName());
    
    @ManagedProperty(value = "#{databaseBean}")
    private DatabaseBean databaseBean;
    
    private List<District> districts2013;
    private List<District> districts2009;
    private List<District> singleAnalysisDistricts;
    private List<District> selectedDistricts;
    private District selectedDistrict;
    private District singleAnalysisDistrict;
    private int selectedYear;

    //*********************************//
    //             SETUP               //
    //*********************************//
    @PostConstruct
    public void init() {
        

        districts2009 = queryAllDistricts(false);
        districts2013 = queryAllDistricts(false);
        
        selectedYear = 2013;
        selectedDistricts = districts2013;
        singleAnalysisDistricts = queryAllDistricts(true);
        
        selectedDistrict = getSelectedDistricts().get(0);
        singleAnalysisDistrict = getSingleAnalysisDistricts().get(0);
        
        loadDistrict(getSelectedDistrict(), false);
        //loadDistrict(getSingleAnalysisDistrict(), true);
        
    }

    //*********************************//
    //             CONTROLS            //
    //*********************************//
    
    public void onDistrictSelection() {
        if (!selectedDistrict.isLoaded()) {
            loadDistrict(selectedDistrict, false);
        }
//        if (!singleAnalysisDistrict.isLoaded()) {
//            loadDistrict(selectedDistrict, true);
//        }
    }

    private void onYearSelection(int year) {
        if (year == 2013) {
            setSelectedDistricts(districts2013);
            setSelectedDistrict(getSelectedDistricts().get(0));
        } else {
            setSelectedDistricts(districts2009);
            setSelectedDistrict(getSelectedDistricts().get(0));
        }
        onDistrictSelection();
    }

    private void loadDistrict(District district, boolean singleAnalysed) {

        try {

            loadTurnout(district, singleAnalysed);
            loadWinner(district, singleAnalysed);
            loadPartyResults(district, singleAnalysed);
            loadHistories(district, singleAnalysed);
            district.setIsLoaded(true);

        } catch (SQLException ex) {
           logger.log(Level.SEVERE, null, ex);
        }
    }

    public District findDistrictByName(String name) {
        if (name != null) {
            for (District district : districts2013) {
                if (district.getName().equals(name)) {
                    return district;
                }
            }
        }
        return null;
    }

    //*********************************//
    //             QUERIES             //
    //*********************************//
    private List<District> queryAllDistricts(boolean singleAnalysed) {

        System.out.println("QUERYING!!!");
        
        ResultSet result;
        if(singleAnalysed){
            result = databaseBean.queryAllSingleDistricts();
        } 
        else {
            result = databaseBean.queryAllDistricts();
        }

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
            logger.log(Level.SEVERE, null, ex);
        }

        return queriedDistricts;
    }

    private void loadTurnout(District district, boolean singleAnalysed) throws SQLException {

        ResultSet result;
        if(singleAnalysed){
            result = databaseBean.queryQ7_1(district.getId(), selectedYear);
        } 
        else {
            result = databaseBean.queryQ3_1(district.getId(), selectedYear);
        }

        result.next();
        double turnout = Utils.getPercentRoundedDouble(result.getString(DbColumns.CLM_TURNOUT));

        district.setWahlbeteiligung(turnout);
    }

    private void loadWinner(District district, boolean singleAnalysed) throws SQLException {

        ResultSet result;
        if(singleAnalysed){
            result = databaseBean.queryQ7_2(district.getId(), selectedYear);
        } 
        else {
            result = databaseBean.queryQ3_2(district.getId(), selectedYear);
        }

        result.next();
        String title = result.getString(DbColumns.CLM_TITLE);
        String fullName = result.getString(DbColumns.CLM_FIRSTNAME) + " " + result.getString(DbColumns.CLM_LASTNAME);
        String party = result.getString(DbColumns.CLM_PARTY);
        int votes = Integer.parseInt(result.getString(DbColumns.CLM_VOTES));

        Politician politician = new Politician(fullName, party);
        politician.setTitle(title);
        politician.setVotes(votes);

        district.setDirektKandidat(politician);
    }

    private void loadPartyResults(District district, boolean singleAnalysed) throws SQLException {

        ResultSet result;
        if(singleAnalysed){
            result = databaseBean.queryQ7_3(district.getId(), selectedYear);
        } 
        else {
            result = databaseBean.queryQ3_3(district.getId(), selectedYear);
        }

        List<Party> parties = new ArrayList<>();

        while (result.next()) {
            System.out.println("FOUND A RESULT!");
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

    private void loadHistories(District district, boolean singleAnalysed) throws SQLException {

        ResultSet result;
        if(singleAnalysed){
            result = databaseBean.queryQ7_4_1(district.getId(), selectedYear);
        } 
        else {
            result = databaseBean.queryQ3_4_1(district.getId(), selectedYear);
        }

        List<History> histories = new ArrayList<>();

        while (result.next()) {
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

    //*********************************//
    //         GETTER/SETTER           //
    //*********************************//

    public DatabaseBean getDatabaseBean() {
        return databaseBean;
    }

    public void setDatabaseBean(DatabaseBean databaseBean) {
        this.databaseBean = databaseBean;
    }

    public List<District> getSingleAnalysisDistricts() {
        return singleAnalysisDistricts;
    }

    public void setSingleAnalysisDistricts(List<District> singleAnalysisDistricts) {
        this.singleAnalysisDistricts = singleAnalysisDistricts;
    }

    public District getSingleAnalysisDistrict() {
        return singleAnalysisDistrict;
    }

    public void setSingleAnalysisDistrict(District singleAnalysisDistrict) {
        this.singleAnalysisDistrict = singleAnalysisDistrict;
    }
    
    public List<District> getSelectedDistricts() {
        return selectedDistricts;
    }

    public void setSelectedDistricts(List<District> selectedDistricts) {
        this.selectedDistricts = selectedDistricts;
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
        if (selectedYear != this.selectedYear) {
            onYearSelection(selectedYear);
        }
        this.selectedYear = selectedYear;
    }

}
