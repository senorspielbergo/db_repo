package kaphira.wahlinfo.util;

import java.io.Serializable;
import kaphira.wahlinfo.entities.District;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import kaphira.wahlinfo.querybeans.Q37Bean;

/**
 *
 * @author theralph
 */
@ManagedBean
@SessionScoped
public class DistrictConverter implements Converter, Serializable {
        
    @ManagedProperty(value = "#{q37Bean}")
    private Q37Bean q37Bean;


    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return q37Bean.findDistrictByName(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((District) value).getName();
    }

    public Q37Bean getQ37Bean() {
        return q37Bean;
    }

    public void setQ37Bean(Q37Bean q37Bean) {
        this.q37Bean = q37Bean;
    }

    
}
