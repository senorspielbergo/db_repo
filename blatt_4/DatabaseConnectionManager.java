
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author theralph
 */
public class DatabaseConnectionManager {

    private static final String POSTGRES_DRIVER = "org.postgresql.Driver";
    private static final String DB_PATH = "jdbc:postgresql:" + File.separator + File.separator + "localhost:5432" + File.separator + "wahlinfo_db";
    private static final String DB_USER = "kaphira";
    private static final String DB_PASSWORD = "cowboyohnepony";
    
    private static DatabaseConnectionManager instance = new DatabaseConnectionManager();
    private Connection connection;
    
    
    private DatabaseConnectionManager(){
    	init();
    }

    public Connection getConnection(){
        return connection;
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
 
}
