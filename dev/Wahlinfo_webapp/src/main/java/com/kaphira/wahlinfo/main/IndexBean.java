package com.kaphira.wahlinfo.main;

import com.kaphira.database.DatabaseConnectionManager;
import com.kaphira.database.DatabaseQueries;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author theralph
 */
@ManagedBean
@RequestScoped
public class IndexBean {
    
    private String title = "Wahlinfo 37!";

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
    private String message = "Whoop Whoop, Server läuft! Hier entwickeln:";
    
    public String getMessage(){
        return this.message;
    }
    
    public void setMessage(String message){
        this.message = message;
        
    }
    /**
     * Krasser Methoden-Abfuck!
     * Zum kotzen hässlich, aber auch nur zu Testzwecken hier!
     * @return Im IdealFall "Katja, Philip, Ralph"
     */
    public String getAllDevelopers() throws SQLException{
        
        ResultSet result = DatabaseConnectionManager
                            .getInstance()
                            .executeQuery(DatabaseQueries.ALL_DEVELOPERS);
        String resultString = "";
        while (result.next()) {
            resultString = resultString + result.getString("username") + ", ";
        }
        return resultString;
    }
}
    