package kaphira.wahlinfo.querybeans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import kaphira.wahlinfo.database.DatabaseBean;
import kaphira.wahlinfo.database.DbColumns;
import kaphira.wahlinfo.entities.Politician;

/**
 *
 * @author theralph
 */
@ManagedBean
@SessionScoped
public class Q2Bean implements Serializable {

    private final Logger logger = Logger.getLogger(Q2Bean.class.getName());
    
    @ManagedProperty(value = "#{databaseBean}")
    private DatabaseBean databaseBean;

    private List<Politician> governmentMembers;

    private int selectedYear;
    
    @PostConstruct
    private void init() {
        setSelectedYear(2013);
        setGovernmentMembers(queryAllMembers());
    }

    public void onYearSelection(){
        setGovernmentMembers(queryAllMembers());
    }
    
    
    //*********************************//
    //             QUERIES             //
    //*********************************//
    
    public List<Politician> queryAllMembers(){
        
        ResultSet result = databaseBean.queryQ2(selectedYear);
        
        List<Politician> queriedPoliticians = new ArrayList<>();
        
        try {
            
            while (result.next()) {
                
                String polTitle = result.getString(DbColumns.CLM_TITLE);
                String polName = result.getString(DbColumns.CLM_LASTNAME);
                String polFirstName = result.getString(DbColumns.CLM_FIRSTNAME);
                String polParty = result.getString(DbColumns.CLM_PARTY);
                
                Politician pol = new Politician(polName + ", " + polFirstName, polParty);
                if (polTitle != null) {
                    pol.setTitle(polTitle);
                }
                queriedPoliticians.add(pol);
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        
        return queriedPoliticians;
    } 
    
    //*********************************//
    //         GETTER/SETTER           //
    //*********************************//
    
    public List<Politician> getGovernmentMembers() {
        if (governmentMembers == null) {
            governmentMembers = new ArrayList<>();
        }
        return governmentMembers;
    }

    public void setGovernmentMembers(List<Politician> governmentMembers) {
        this.governmentMembers = governmentMembers;
    }
    
    public DatabaseBean getDatabaseBean() {
        return databaseBean;
    }

    public void setDatabaseBean(DatabaseBean databaseBean) {
        this.databaseBean = databaseBean;
    }

    public int getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(int selectedYear) {
            this.selectedYear = selectedYear;
    }
    
}
