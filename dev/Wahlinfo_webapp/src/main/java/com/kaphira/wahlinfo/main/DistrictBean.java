package com.kaphira.wahlinfo.main;

import com.kaphira.database.DatabaseConnectionManager;
import com.kaphira.entities.District;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Named;

/**
 *
 * @author theralph
 */
@Named
@ManagedBean
@ApplicationScoped
public class DistrictBean implements Serializable {

    private static final String QRY_ALL_DISTRICTS = "select * from wahlkreis";
    private static final String QRY_LOAD_DISTRICT_INFO = "";
    
    private static final String COLUMN_ID = "nummer";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_ALLOWED_VOTERS = "wahlberechtigte";
    private static final String COLUMN_COUNTRY = "bundesland";
    
    private List<District> districts;
    private District selectedDistrict;
    
    @PostConstruct
    private void init(){
        setDistricts(queryAllDistricts());
        setSelectedDistrict(getDistricts().get(0));
    }

    public void reload(){
        //TODO
    }
    
    public List<District> queryAllDistricts(){
        
        ResultSet result = DatabaseConnectionManager
                    .getInstance()
                    .executeQuery(QRY_ALL_DISTRICTS);
        
        List<District> queriedDistricts = new ArrayList<>();
        
        try {
            
            while (result.next()) {
                
                int districtId = Integer.parseInt(result.getString(COLUMN_ID));
                int allowedVoters = Integer.parseInt(result.getString(COLUMN_ALLOWED_VOTERS));
                String districtName = result.getString(COLUMN_NAME);
                String districtCountry = result.getString(COLUMN_COUNTRY);
                
                
                District district = new District(districtId, districtName);
                district.setBundesland(districtCountry);
                district.setWahlberechtigte(allowedVoters);
                queriedDistricts.add(district);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BundestagBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return queriedDistricts;
    } 
    
    
    private void loadDistrict(District selectedWahlkreis) {
        //TODO
    }
    
    public void onDistrictSelection() {
        System.err.println("Loading " + selectedDistrict.getName() + " with id: " + selectedDistrict.getId());
    }
    
    
    
    /***********************************
     *          GETTER/SETTER
     ***********************************/

    public List<District> getDistricts() {
//        if (districts == null){
//            districts = queryAllDistricts();
//        }
        return this.districts;
    }

    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }

    public District getSelectedDistrict() {
        return selectedDistrict;
    }

    public void setSelectedDistrict(District selectedDistrict) {
        if (!selectedDistrict.isIsLoaded()) {
            loadDistrict(selectedDistrict);
        }
        this.selectedDistrict = selectedDistrict;
    }
}
