package kaphira.wahlinfo.util;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * Converter used for selectionMenus
 * @author theralph
 */
 @SessionScoped
 @ManagedBean
public class StringIntConverter implements Converter,Serializable {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return Integer.parseInt(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return value.toString();
    }


}
