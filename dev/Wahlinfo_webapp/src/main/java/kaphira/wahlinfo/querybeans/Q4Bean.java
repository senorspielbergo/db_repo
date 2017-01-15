package kaphira.wahlinfo.querybeans;

import kaphira.wahlinfo.database.DatabaseBean;
import kaphira.wahlinfo.entities.District;
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
import kaphira.wahlinfo.database.DbColumns;

/**
 *
 * @author theralph
 */
@ManagedBean
@SessionScoped
public class Q4Bean implements Serializable {

    private final Logger logger = Logger.getLogger(Q4Bean.class.getName());

    @ManagedProperty(value = "#{databaseBean}")
    private DatabaseBean databaseBean;

    private List<District> districts;

    private int selectedYear;

    @PostConstruct
    public void init() {
        setSelectedYear(2013);
        setDistricts(queryDistrictsAndWinners());
    }
    
    public void onYearSelection(){
        setDistricts(queryDistrictsAndWinners());
    }

    //*********************************//
    //             QUERIES             //
    //*********************************//
    
    private List<District> queryDistrictsAndWinners() {

        ResultSet result = databaseBean.queryQ4(selectedYear);

        List<District> queriedDistricts = new ArrayList<>();

        try {

            while (result.next()) {

                int districtId = Integer.parseInt(result.getString(DbColumns.CLM_ID));
                int firstVotes = Integer.parseInt(result.getString(DbColumns.CLM_FIRST_VOTES));
                int secondVotes = Integer.parseInt(result.getString(DbColumns.CLM_SECOND_VOTES));

                String districtName = result.getString(DbColumns.CLM_NAME);
                String firstVotePartyName = result.getString(DbColumns.CLM_FIRST_VOTE_PARY);
                String secondVotePartyName = result.getString(DbColumns.CLM_SECOND_VOTE_PARTY);

                District district = new District(districtId, districtName);
                district.setFirstVoteParty(firstVotePartyName);
                district.setSecondVoteParty(secondVotePartyName);
                district.setFirstVotes(firstVotes);
                district.setSecondVotes(secondVotes);
                queriedDistricts.add(district);
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        return queriedDistricts;
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
