package kaphira.wahlinfo.beans;

import kaphira.wahlinfo.entities.Party;
import kaphira.wahlinfo.main.DatabaseBean;
import kaphira.wahlinfo.entities.Politician;
import kaphira.wahlinfo.util.Utils;
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
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author theralph
 */
@ManagedBean
@SessionScoped
public class BundestagBean implements Serializable{

    
    private static final String COLUMN_TITLE = "titel";
    private static final String COLUMN_FIRSTNAME = "vorname";
    private static final String COLUMN_LASTNAME = "nachname";
    private static final String COLUMN_PARTY = "partei";
    private static final String COLUMN_SEATS = "sitze";
    private static final String PIE_CHART_TITLE = "Sitzeverteilung";
    
    private List<Party> parties;
    private List<Politician> members;

    @ManagedProperty(value="#{databaseBean}")
    private DatabaseBean databaseBean;

    
    private PieChartModel pieChart;
    private int selectedYear;

    
     @PostConstruct
     private void init(){
         System.out.println("Initializing Q1 Bean...");
         setSelectedYear(2013);
         setParties(queryOberverteilung());
         setPieChart(createPieChart());
         setMembers(queryAllMembers());
         
     }
    
     public void reload(){
         init();
     }
     
     /**
      * 
      * @return 
      */
     public PieChartModel createPieChart(){
         PieChartModel chart = new PieChartModel();
         for (Party party : getParties()) {
             chart.set(party.getName() + "(" + party.getSeats() + ")", party.getSeats());
         }
         chart.setTitle(PIE_CHART_TITLE);
         chart.setLegendPosition("e");
         chart.setSliceMargin(1);
         
         return chart;
     }
    
     
     
    public List<Politician> queryAllMembers(){
        
        ResultSet result = databaseBean.queryQ2(selectedYear);
        
        List<Politician> queriedPoliticians = new ArrayList<>();
        
        try {
            
            while (result.next()) {
                
                String polTitle = result.getString(COLUMN_TITLE);
                String polName = result.getString(COLUMN_LASTNAME);
                String polFirstName = result.getString(COLUMN_FIRSTNAME);
                String polParty = result.getString(COLUMN_PARTY);
                
                Politician pol = new Politician(polName + ", " + polFirstName, polParty);
                if (polTitle != null) {
                    pol.setTitle(polTitle);
                }
                queriedPoliticians.add(pol);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BundestagBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return queriedPoliticians;
    } 
     /**
      * 
      * @return 
      */
    public List<Party> queryOberverteilung(){
        
        ResultSet result = databaseBean.queryQ1(selectedYear);
        
        List<Party> queriedParties = new ArrayList<>();
        
        try {
            
            while (result.next()) {
                
                String partyName = result.getString(COLUMN_PARTY);
                int seats = Integer.parseInt(result.getString(COLUMN_SEATS));
                
                Party party = new Party(partyName);
                party.setSeats(seats);
                queriedParties.add(party);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BundestagBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return queriedParties;
    }
    
    
    /**
     * 
     * @return 
     */
    public String getPieChartColorCode(){
        StringBuilder colorCode = new StringBuilder();
        
        for (Party party : parties) {
            colorCode.append(Utils.getColorCodeForParty(party)).append(",");
        }
        colorCode.deleteCharAt(colorCode.lastIndexOf(","));
        
        return colorCode.toString();
    }
    
    
    //********************************************
    //              GETTER/SETTER
    //********************************************
    
    
    public PieChartModel getPieChart() {
        if(pieChart == null) {
            pieChart = createPieChart();
        }
        return pieChart;
    }
    
    public void setPieChart(PieChartModel pieChart) {
        this.pieChart = pieChart;
    }


    public List<Party> getParties() {
        if (this.parties == null){
            this.parties = new ArrayList<>();
        }
        return parties; 
    }
    
    public void setParties(List<Party> parties) {
        this.parties = parties;
    }
    
    public List<Politician> getMembers() {
        if (members == null) {
            members = new ArrayList<>();   
        }
        return members;
    }

    public void setMembers(List<Politician> members) {
        this.members = members;
    }

    public int getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(int selectedYear) {
        this.selectedYear = selectedYear;
    }
    
    public DatabaseBean getDatabaseBean() {
        return databaseBean;
    }

    public void setDatabaseBean(DatabaseBean databaseBean) {
        this.databaseBean = databaseBean;
    }
    
}
