package kaphira.wahlinfo.querybeans;

import kaphira.wahlinfo.entities.Party;
import kaphira.wahlinfo.database.DatabaseBean;
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
import kaphira.wahlinfo.database.DbColumns;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author theralph
 */
@ManagedBean
@SessionScoped
public class Q1Bean implements Serializable{

    @ManagedProperty(value="#{databaseBean}")
    private DatabaseBean databaseBean;
    

    private static final String PIE_CHART_TITLE = "Sitzeverteilung";
    
    private PieChartModel pieChart;
    
    
    private List<Party> parties;
    
    
    private int selectedYear;

    
     @PostConstruct
     private void init(){
         setSelectedYear(2013);
         setParties(queryOberverteilung());
         setPieChart(createPieChart());
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
    
     
     
     /**
      * 
      * @return 
      */
    public List<Party> queryOberverteilung(){
        
        ResultSet result = databaseBean.queryQ1(selectedYear);
        
        List<Party> queriedParties = new ArrayList<>();
        
        try {
            
            while (result.next()) {
                
                String partyName = result.getString(DbColumns.CLM_PARTY);
                int seats = Integer.parseInt(result.getString(DbColumns.CLM_SEATS));
                
                Party party = new Party(partyName);
                party.setSeats(seats);
                queriedParties.add(party);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Q1Bean.class.getName()).log(Level.SEVERE, null, ex);
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
