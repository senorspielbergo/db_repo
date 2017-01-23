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
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import kaphira.wahlinfo.entities.District;
import kaphira.wahlinfo.database.DatabaseBean;
import kaphira.wahlinfo.database.DbColumns;
import kaphira.wahlinfo.entities.Politician;
import kaphira.wahlinfo.security.TokenBean;

/**
 * This bean backs the voting.xhtml site
 * It provides the functionality to enter a token and take a vote
 * @author theralph
 */
@ManagedBean
@SessionScoped
public class ElectionBean implements Serializable {

    @ManagedProperty(value = "#{districtManagementBean}")
    private DistrictManagementBean districtManagementBean;

    @ManagedProperty(value = "#{databaseBean}")
    private DatabaseBean databaseBean;

    @ManagedProperty(value = "#{tokenBean}")
    private TokenBean tokenBean;

    private List<Politician> candidates;
    private List<String> parties;

    private District selectedDistrict;
    private Politician selectedCandidate;
    private String selectedParty;

    private String token;
    private int selectedYear;

    private boolean mayVote;

    private String message;
    private boolean finished;

    @PostConstruct
    private void init() {
        setSelectedYear(2013);
        mayVote = false;
    }

    public void register() {
        mayVote = tokenBean.authenticate(getToken());
        if (mayVote) {
            int districtID = tokenBean.decodeDistrictId(getToken());
            selectedDistrict = districtManagementBean.findDistrictByID(districtID);
            onDistrictSelection();
            message(FacesMessage.SEVERITY_INFO, "Info", "Token akzeptiert, bitte wählen Sie.");
            return;
        }
        message(FacesMessage.SEVERITY_FATAL, "Fehler", "Dieser Token ist leider nicht mehr gültig oder inkorrekt");
    }

    public void vote() {

        insertVote();
        mayVote = false;
        token = "";
        selectedCandidate = null;
        selectedParty = null;
        message(FacesMessage.SEVERITY_INFO, "Info", "Vielen Dank für Ihre Stimme!");
    }

    public void onDistrictSelection() {
        candidates = queryCandidates();
        parties = queryParties();
    }

    public void message(Severity severity, String messageLevel, String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, messageLevel, message));
    }

    public Politician findPoliticianByName(String fullName) {
        for (Politician candidate : candidates) {
            if (candidate.getFormattedName().equals(fullName)) {
                return candidate;
            }
        }
        return null;
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

    public List<Politician> queryCandidates() {

        ResultSet result = databaseBean.queryQ2017_1(getSelectedDistrict().getId());

        List<Politician> queriedPoliticians = new ArrayList<>();

        try {

            while (result.next()) {

                String polTitle = result.getString(DbColumns.CLM_TITLE);
                String polName = result.getString(DbColumns.CLM_LASTNAME);
                String polFirstName = result.getString(DbColumns.CLM_FIRSTNAME);
                String polParty = result.getString(DbColumns.CLM_PARTY);

                Politician politician = new Politician(polTitle, polFirstName, polName, polParty);
                queriedPoliticians.add(politician);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ElectionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return queriedPoliticians;
    }

    private void insertVote() {

        int districtId = getSelectedDistrict().getId();
        String title = "null";
        String firstName = "null";
        String lastName = "null";
        String party = "null";
        String candidateParty = "null";
        
        if (selectedCandidate != null) {

            title = selectedCandidate.getTitle();
            firstName = selectedCandidate.getFirstName();
            lastName = selectedCandidate.getLastName();
            candidateParty = selectedCandidate.getParty();
        }

        if (selectedParty != null) {
            party = getSelectedParty();
        }
        databaseBean.insertVote(districtId, title, firstName, lastName, candidateParty, party);
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

    public List<Politician> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Politician> candidates) {
        this.candidates = candidates;
    }

    public List<String> getParties() {
        return parties;
    }

    public void setParties(List<String> parties) {
        this.parties = parties;
    }

    public Politician getSelectedCandidate() {
        return selectedCandidate;
    }

    public void setSelectedCandidate(Politician selectedCandidate) {
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
