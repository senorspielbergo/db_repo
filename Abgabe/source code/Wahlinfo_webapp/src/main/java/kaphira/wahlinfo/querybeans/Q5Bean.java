package kaphira.wahlinfo.querybeans;

import java.io.Serializable;
import kaphira.wahlinfo.database.DatabaseBean;
import kaphira.wahlinfo.entities.Mandat;
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
import kaphira.wahlinfo.database.DbColumns;

/**
 * Backing the Q5.xhtml and its functionality
 *
 * @author theralph
 */
@ManagedBean
@SessionScoped
public class Q5Bean implements Serializable {

    private final Logger logger = Logger.getLogger(Q5Bean.class.getName());

    @ManagedProperty(value = "#{databaseBean}")
    private DatabaseBean databaseBean;

    private List<Mandat> mandate2013;

    private List<Mandat> mandate2009;

    private List<Mandat> selectedMandate;

    @PostConstruct
    public void init() {
        loadAllUeberhangmandate();
        selectedMandate = getMandate2013();
    }

    //*********************************//
    //             QUERIES             //
    //*********************************//
    private void loadAllUeberhangmandate() {

        ResultSet result = databaseBean.queryQ5();

        try {
            while (result.next()) {

                String country = result.getString(DbColumns.CLM_COUNTRY);
                String partyName = result.getString(DbColumns.CLM_PARTY);
                int mandate = Integer.parseInt(result.getString(DbColumns.CLM_MANDAT));
                int year = Integer.parseInt(result.getString(DbColumns.CLM_YEAR));

                Mandat mandat = new Mandat(country, partyName, mandate, year);
                if (mandat.getUeberhang() != 0) {
                    if (year == 2013) {
                        getMandate2013().add(mandat);
                    } else {
                        getMandate2009().add(mandat);
                    }
                }
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }

    //*********************************//
    //         GETTER/SETTER           //
    //*********************************//
    public List<Mandat> getMandate2013() {
        if (mandate2013 == null) {
            mandate2013 = new ArrayList<>();
        }
        return mandate2013;
    }

    public void setMandate2013(List<Mandat> mandate2013) {
        this.mandate2013 = mandate2013;
    }

    public List<Mandat> getMandate2009() {
        if (mandate2009 == null) {
            mandate2009 = new ArrayList<>();
        }
        return mandate2009;
    }

    public void setMandate2009(List<Mandat> mandate2009) {
        this.mandate2009 = mandate2009;
    }

    public List<Mandat> getSelectedMandate() {
        return selectedMandate;
    }

    public void setSelectedMandate(List<Mandat> selectedMandate) {
        this.selectedMandate = selectedMandate;
    }

    public DatabaseBean getDatabaseBean() {
        return databaseBean;
    }

    public void setDatabaseBean(DatabaseBean databaseBean) {
        this.databaseBean = databaseBean;
    }

}
