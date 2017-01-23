package kaphira.wahlinfo.querybeans;

import kaphira.wahlinfo.backingbeans.DistrictManagementBean;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import kaphira.wahlinfo.entities.District;

/**
 * Backing the Q3.xhtml and its functionality
 *
 * @author theralph
 */
@ManagedBean
@SessionScoped
public class Q3Bean implements Serializable {

    @ManagedProperty(value = "#{districtManagementBean}")
    private DistrictManagementBean districtManagementBean;

    private List<District> districts;
    private District selectedDistrict;
    private int selectedYear;

    @PostConstruct
    private void init() {
        selectedYear = 2013;
        districts = districtManagementBean.getDistricts2013();
        selectedDistrict = districts.get(0);
        onDistrictSelection();
    }

    public void onDistrictSelection() {
            districtManagementBean.loadDistrict(selectedDistrict, selectedYear, false);
    }

    public void onYearSelection() {
        if (selectedYear == 2013) {
            districts = districtManagementBean.getDistricts2013();
        } else {
            districts = districtManagementBean.getDistricts2009();
        }
        selectedDistrict = districts.get(0);
        onDistrictSelection();
    }

    //*********************************//
    //         GETTER/SETTER           //
    //*********************************//
    public DistrictManagementBean getDistrictManagementBean() {
        return districtManagementBean;
    }

    public void setDistrictManagementBean(DistrictManagementBean districtManagementBean) {
        this.districtManagementBean = districtManagementBean;
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

    public int getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(int selectedYear) {
        this.selectedYear = selectedYear;
    }

}
