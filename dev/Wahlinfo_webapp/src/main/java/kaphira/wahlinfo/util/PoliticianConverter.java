package kaphira.wahlinfo.util;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import kaphira.wahlinfo.backingbeans.ElectionBean;
import kaphira.wahlinfo.entities.Politician;

/**
 *
 * @author theralph
 */
@ManagedBean
@SessionScoped
public class PoliticianConverter implements Converter, Serializable {

    @ManagedProperty(value="#{electionBean}")
    private ElectionBean electionBean;
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if(value.equals("Ungültig")){
            return null;
        }
        return electionBean.findPoliticianByName(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value != null) {
            return ((Politician) value).getFormattedName();
        }
        return "Ungültig";
    }

    public ElectionBean getElectionBean() {
        return electionBean;
    }

    public void setElectionBean(ElectionBean electionBean) {
        this.electionBean = electionBean;
    }
}
