package com.kaphira.wahlinfo.beans;

import com.kaphira.database.DatabaseConnectionManager;
import com.kaphira.database.DbQueries;
import com.kaphira.entities.District;
import com.kaphira.entities.Party;
import java.io.Serializable;
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
public class DistrictWinnerBean implements Serializable{

    private static final String COLUMN_ID = "nummer";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_FIRST_VOTE_PARY = "erststimmen_partei";
    private static final String COLUMN_FIRST_VOTES = "erststimmen";
    private static final String COLUMN_SECOND_VOTE_PARTY = "zweitstimmen_partei";
    private static final String COLUMN_SECOND_VOTES = "zweitstimmen";
            
    private int selectedYear = 2013;
    private List<District> districts;
    
    
    @PostConstruct
    public void init() {
        setDistricts(queryDistrictsAndWinners());
    }
    
    public void reload() {
        
    }

    private List<District> queryDistrictsAndWinners(){
        
        ResultSet result = DatabaseConnectionManager
                            .getInstance()
                            .executeQuery(DbQueries.queryAllDistrictWinners(selectedYear));
        
        List<District> queriedDistricts = new ArrayList<>();
        
        try {
            
            while (result.next()) {
                
                int districtId = Integer.parseInt(result.getString(COLUMN_ID));
                int firstVotes = Integer.parseInt(result.getString(COLUMN_FIRST_VOTES));
                int secondVotes = Integer.parseInt(result.getString(COLUMN_SECOND_VOTES));
                
                String districtName = result.getString(COLUMN_NAME);
                String firstVotePartyName = result.getString(COLUMN_FIRST_VOTE_PARY);
                String secondVotePartyName = result.getString(COLUMN_SECOND_VOTE_PARTY);

                District district = new District(districtId, districtName);
                district.setFirstVoteParty(firstVotePartyName);
                district.setSecondVoteParty(secondVotePartyName);
                district.setFirstVotes(firstVotes);
                district.setSecondVotes(secondVotes);
                System.out.println("CREATED:" + districtName);
                queriedDistricts.add(district);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BundestagBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return queriedDistricts;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public int getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(int selectedYear) {
        this.selectedYear = selectedYear;
    }

    public List<District> getDistricts() {
        return districts;
    }

    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }
    
    
}
