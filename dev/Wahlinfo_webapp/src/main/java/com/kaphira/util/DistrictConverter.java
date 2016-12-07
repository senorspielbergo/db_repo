package com.kaphira.util;

import com.kaphira.entities.District;
import com.kaphira.wahlinfo.main.DistrictBean;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

/**
 *
 * @author theralph
 */
@ManagedBean
@FacesConverter
public class DistrictConverter implements Converter {
   
    private List<District> districts; 
   
    @Inject
    private DistrictBean districtBean;
    
    @PostConstruct
    public void init() {
        
    }
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if(value == null) {
            return null;
        }
        return findDistrictByName(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if(value == null) {
            return null;
        }
        return ((District)value).getName();
    }

    private District findDistrictByName(String name) {
        
        for (District district : this.districts) {
            if (district.getName().equals(name)) {
                return district;
            } 
        }
        return null;
    }
}
