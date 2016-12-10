package com.kaphira.util;

import com.kaphira.entities.District;
import com.kaphira.wahlinfo.beans.DistrictBean;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 *
 * @author theralph
 */
@ManagedBean
@SessionScoped
public class DistrictConverter implements Converter {

    @ManagedProperty(value="#{districtBean}")
    private DistrictBean districtBean;

    public DistrictBean getDistrictBean() {
        return districtBean;
    }

    public void setDistrictBean(DistrictBean districtBean) {
        this.districtBean = districtBean;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return districtBean.findDistrictByName(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((District) value).getName();
    }

}
