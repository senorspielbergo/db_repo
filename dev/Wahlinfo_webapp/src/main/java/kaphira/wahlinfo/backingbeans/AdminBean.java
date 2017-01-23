package kaphira.wahlinfo.backingbeans;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import kaphira.wahlinfo.database.ConfigBean;
import kaphira.wahlinfo.database.DatabaseBean;
import kaphira.wahlinfo.entities.District;
import kaphira.wahlinfo.security.TokenBean;

/**
 * This bean backs the admin.xhtml interface.
 * It provides all functionality related to generate tokens and reloading the database views
 * @author theralph
 */
@ManagedBean
@SessionScoped
public class AdminBean implements Serializable {
    
    @ManagedProperty(value="#{tokenBean}")
    private TokenBean tokenBean;
    
    @ManagedProperty(value="#{configBean}")
    private ConfigBean configBean;
    
    @ManagedProperty(value="#{districtManagementBean}")
    private DistrictManagementBean districtManagementBean;
    
    @ManagedProperty(value="#{databaseBean}")
    private DatabaseBean databaseBean;
    
    
    
    private String tokenValue;
    private String username = "";
    private String password = "";
    
    private boolean isLoggedIn;

    private List<District> districts;
    private District selectedDistrict;
    
    @PostConstruct
    private void init() {
        tokenValue = "-";
        districts = districtManagementBean.getDistricts2013();
        setIsLoggedIn(false);
    }

    public void login(){
        
        String correctUsername = configBean.getAdminUser();
        String correctPassword = configBean.getAdminPassword();
        
        if(getUsername().equals(correctUsername) && getPassword().equals(correctPassword)) {
            setIsLoggedIn(true);

        }
        messageLogin(isLoggedIn());
    }
    
    public void logout(){
        setIsLoggedIn(false);
        setUsername("");
        setPassword("");
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "info", "Sie haben sich erfolgreich ausgeloggt."));
    }
    
    public void reloadViews(){
        databaseBean.reloadViews();
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "info", "Views werden aktualisiert... Dies kann bis zu 10 Minuten dauern."));
    }

    public void messageLogin(boolean loggedIn) {
            String messageLevel;
            String message;
        
        if (loggedIn) {
           messageLevel = "Info";
           message = "Sie haben sich erfolgreich eingeloggt.";
        } else {
           messageLevel = "Fatal!";
           message = "Benutzername und/oder Kennwort sind leider nicht korrekt.";
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, messageLevel, message));
    }
    
    public void buttonClicked() {
        setTokenValue(tokenBean.generateToken(selectedDistrict.getId()));
    }

    public TokenBean getTokenBean() {
        return tokenBean;
    }

    public void setTokenBean(TokenBean tokenBean) {
        this.tokenBean = tokenBean;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public ConfigBean getConfigBean() {
        return configBean;
    }

    public void setConfigBean(ConfigBean configBean) {
        this.configBean = configBean;
    }

    public DistrictManagementBean getDistrictManagementBean() {
        return districtManagementBean;
    }

    public void setDistrictManagementBean(DistrictManagementBean districtManagementBean) {
        this.districtManagementBean = districtManagementBean;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public List<District> getDistricts() {
        return districts;
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

    public DatabaseBean getDatabaseBean() {
        return databaseBean;
    }

    public void setDatabaseBean(DatabaseBean databaseBean) {
        this.databaseBean = databaseBean;
    }
    
    
}
