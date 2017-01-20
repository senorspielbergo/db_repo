package kaphira.wahlinfo.util;

import java.io.Serializable;
import kaphira.wahlinfo.entities.District;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import kaphira.wahlinfo.backingbeans.DistrictManagementBean;

/**
 * Converter used for selectionMenus
 * @author theralph
 */
@ManagedBean
@SessionScoped
public class DistrictConverter implements Converter, Serializable {
        
    @ManagedProperty(value = "#{districtManagementBean}")
    private DistrictManagementBean districtManagementBean;


    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return districtManagementBean.findDistrictByName(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if(value == null) {
            return "---";
        }
        return ((District) value).getName();
    }

    public DistrictManagementBean getDistrictManagementBean() {
        return districtManagementBean;
    }

    public void setDistrictManagementBean(DistrictManagementBean districtManagementBean) {
        this.districtManagementBean = districtManagementBean;
    }

    
}
