package kaphira.wahlinfo.beans;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import kaphira.wahlinfo.security.TokenBean;

/**
 *
 * @author theralph
 */
@ManagedBean
@SessionScoped
public class AdminBean implements Serializable {
    
    @ManagedProperty(value="#{tokenBean}")
    private TokenBean tokenBean;
    
    private String tokenValue;
    
    @PostConstruct
    private void init() {
        tokenValue = "-";
    }

    public void buttonClicked() {
        setTokenValue(tokenBean.generateToken());
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
    
    
    
}
