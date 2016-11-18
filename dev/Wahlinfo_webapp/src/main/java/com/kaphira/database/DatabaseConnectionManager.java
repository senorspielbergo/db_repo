package com.kaphira.database;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author theralph
 */
public class DatabaseConnectionManager {

    private static final String POSTGRES_DRIVER = "org.postgresql.Driver";
    private static final String SLASH = System.getProperty("file.separator");
    private static final String DB_PATH = "jdbc:postgresql:" + SLASH + SLASH + "localhost:5433" + SLASH + "wahlinfo_db";
    private static final String DB_USER = "kaphira";
    private static final String DB_PASSWORD = "cowboyohnepony";
    
    private static DatabaseConnectionManager instance = new DatabaseConnectionManager();
    private Connection connection;
    
    
    private DatabaseConnectionManager(){
    }
    
    
    
    public static DatabaseConnectionManager getInstance(){
        return instance;
    }
    public void init(){
        
        try {
            Class.forName(POSTGRES_DRIVER);
            connection = DriverManager.getConnection(DB_PATH, DB_USER, DB_PASSWORD);
        
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DatabaseConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public ResultSet executeQuery(String query){
        if (connection == null){
            init();
        }
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
