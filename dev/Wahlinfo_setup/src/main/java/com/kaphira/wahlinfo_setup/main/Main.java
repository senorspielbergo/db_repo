/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kaphira.wahlinfo_setup.main;

import com.kaphira.wahlinfo_setup.database.DatabaseConnectionManager;
import com.kaphira.wahlinfo_setup.database.SqlRunner;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author theralph
 */
public class Main {

    public static void main(String[] args){
        InputStream scriptInputStream = Main.class.getResourceAsStream("/create_tables.sql");
        try {
            SqlRunner.runScript(DatabaseConnectionManager.getInstance().getConnection(), scriptInputStream);
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
}
