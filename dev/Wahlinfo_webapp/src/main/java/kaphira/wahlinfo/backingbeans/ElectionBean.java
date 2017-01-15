package kaphira.wahlinfo.backingbeans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import kaphira.wahlinfo.entities.District;
import kaphira.wahlinfo.database.DatabaseBean;
import kaphira.wahlinfo.database.DbColumns;
import kaphira.wahlinfo.security.TokenBean;

/**
 *
 * @author theralph
 */
@ManagedBean
@SessionScoped
public class ElectionBean implements Serializable {

    private static final String QRY_ALL_DISTRICTS = "select * from wahlkreis";

    @ManagedProperty(value="#{districtManagementBean}")
    private DistrictManagementBean districtManagementBean;
    
    @ManagedProperty(value = "#{databaseBean}")
    private DatabaseBean databaseBean;

    @ManagedProperty(value = "#{tokenBean}")
    private TokenBean tokenBean;

    private List<String> candidates;
    private List<String> parties;

    private District selectedDistrict;
    private String selectedCandidate;
    private String selectedParty;

    private String token;
    private int selectedYear;

    private boolean mayVote;
    private boolean displayChoices;

    private String message;
    private boolean finished;

    @PostConstruct
    private void init() {
        setSelectedYear(2013);
        mayVote = false;
        displayChoices = false;
    }

    public void register() {
        mayVote = tokenBean.authenticate(getToken());
        messageLogin(mayVote);
        if(mayVote){
            int districtID = tokenBean.decodeDistrictId(getToken());
            selectedDistrict = districtManagementBean.findDistrictByID(districtID);
            onDistrictSelection();
        }
    }

    public void vote() {

        insertVote();
        setMessage("Danke für Ihre Stimme! Sie haben gewählt:" + getSelectedCandidate() + " und " + getSelectedParty());
        mayVote = false;
        displayChoices = false;
        finished = true;
    }

    public void onDistrictSelection() {
        candidates = queryCandidates();
        parties = queryParties();
        displayChoices = true;
    }

    public void messageLogin(boolean loggedIn) {
        String messageLevel;
        String message;

        if (loggedIn) {
            messageLevel = "Info";
            message = "Token akzeptiert";
        } else {
            messageLevel = "Fatal!";
            message = "Dieser Token ist leider nicht mehr gültig oder inkorrekt";
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, messageLevel, message));
    }
    //********************************//
    //      QUERIES                   //
    //********************************//


    private List<String> queryParties() {

        ResultSet result = databaseBean.queryQ2017_2(selectedDistrict.getId());

        List<String> queriedParties = new ArrayList<>();

        try {

            while (result.next()) {

                String party = result.getString(DbColumns.CLM_PARTY);
                queriedParties.add(party);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ElectionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return queriedParties;
    }

    public List<String> queryCandidates() {

        ResultSet result = databaseBean.queryQ2017_1(getSelectedDistrict().getId());

        List<String> queriedPoliticians = new ArrayList<>();

        try {

            while (result.next()) {

                String polTitle = result.getString(DbColumns.CLM_TITLE);
                String polName = result.getString(DbColumns.CLM_LASTNAME);
                String polFirstName = result.getString(DbColumns.CLM_FIRSTNAME);
                String polParty = result.getString(DbColumns.CLM_PARTY);

                if (polTitle == null) {
                    polTitle = "";
                }
                String politician = polTitle + "," + polFirstName + "," + polName + "," + polParty;
                System.out.println("ADDING: " + politician);
                queriedPoliticians.add(politician);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ElectionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return queriedPoliticians;
    }

    private void insertVote() {

        int districtId = getSelectedDistrict().getId();
        String[] splitCandidate = getSelectedCandidate().split(",");

        String title = splitCandidate[0];
        String firstName = splitCandidate[1];
        String lastName = splitCandidate[2];

        String party = getSelectedParty();

        databaseBean.insertVote(districtId, title, firstName, lastName, party);

    }

    //********************************//
    //      GETTER/SETTER             //
    //********************************//
    public DatabaseBean getDatabaseBean() {
        return databaseBean;
    }

    public void setDatabaseBean(DatabaseBean databaseBean) {
        this.databaseBean = databaseBean;
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

    public List<String> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<String> candidates) {
        this.candidates = candidates;
    }

    public List<String> getParties() {
        return parties;
    }

    public void setParties(List<String> parties) {
        this.parties = parties;
    }

    public String getSelectedCandidate() {
        return selectedCandidate;
    }

    public void setSelectedCandidate(String selectedCandidate) {
        this.selectedCandidate = selectedCandidate;
    }

    public String getSelectedParty() {
        return selectedParty;
    }

    public void setSelectedParty(String selectedParty) {
        this.selectedParty = selectedParty;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isMayVote() {
        return mayVote;
    }

    public void setMayVote(boolean mayVote) {
        this.mayVote = mayVote;
    }

    public TokenBean getTokenBean() {
        return tokenBean;
    }

    public void setTokenBean(TokenBean tokenBean) {
        this.tokenBean = tokenBean;
    }

    public boolean isDisplayChoices() {
        return displayChoices;
    }

    public void setDisplayChoices(boolean displayChoices) {
        this.displayChoices = displayChoices;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public DistrictManagementBean getDistrictManagementBean() {
        return districtManagementBean;
    }

    public void setDistrictManagementBean(DistrictManagementBean districtManagementBean) {
        this.districtManagementBean = districtManagementBean;
    }
    
    

}
