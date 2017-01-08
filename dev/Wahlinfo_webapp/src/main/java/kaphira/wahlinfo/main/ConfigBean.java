package kaphira.wahlinfo.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author theralph
 */


@ManagedBean
@ApplicationScoped
public class ConfigBean {

    private Properties properties;

    private String Q1;
    private String Q2;
    private String Q3_1;
    private String Q3_2;
    private String Q3_3;
    private String Q3_4_1;
    private String Q3_4_2;
    private String Q4;
    private String Q5;
    private String Q6;
    private String Q7_1;
    private String Q7_2;
    private String Q7_3;
    private String Q7_4_1;
    private String Q7_4_2;
    
    private String insert;
    private String Q2017_1;
    private String Q2017_2;
    
    @PostConstruct
    private void init() {
        
        properties = loadProperties();

        Q1 = loadQueryFromResource("/sql/Q1.sql");
        Q2 = loadQueryFromResource("/sql/Q2.sql");
        Q3_1 = loadQueryFromResource("/sql/Q3-1.sql");
        Q3_2 = loadQueryFromResource("/sql/Q3-2.sql");
        Q3_3 = loadQueryFromResource("/sql/Q3-3.sql");
        Q3_4_1 = loadQueryFromResource("/sql/Q3-4-1.sql");
        Q3_4_2 = loadQueryFromResource("/sql/Q3-4-2.sql");
        Q4 = loadQueryFromResource("/sql/Q4.sql");
        Q5 = loadQueryFromResource("/sql/Q5.sql");
        Q6 = loadQueryFromResource("/sql/Q6.sql");
        Q7_1 = loadQueryFromResource("/sql/Q7-1.sql");
        Q7_2 = loadQueryFromResource("/sql/Q7-2.sql");
        Q7_3 = loadQueryFromResource("/sql/Q7-3.sql");
        Q7_4_1 = loadQueryFromResource("/sql/Q7-4-1.sql");
        Q7_4_2 = loadQueryFromResource("/sql/Q7-4-2.sql");
        
        insert = loadQueryFromResource("/sql/insert.sql");
        Q2017_1 = loadQueryFromResource("/sql/Q2017-1.sql");
        Q2017_2 = loadQueryFromResource("/sql/Q2017-2.sql");
        
    }

    /**
     * 
     * @param resource file of the sql-statement.
     * @return 
     */
    private String loadQueryFromResource(String resource){
        
        BufferedReader reader;
        
        try {
           reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(resource)));  
           return IOUtils.toString(reader);
           
         } catch (Exception e) {
             e.printStackTrace();
         }
        
        return "";
    }

    /**
     * Loading the properties file
     */
    private Properties loadProperties() {
        
        Properties prop = new Properties();
	InputStream input = null;

	try {
		input = getClass().getResourceAsStream("/config/config.properties");
		prop.load(input);

	} catch (IOException ex) {
		ex.printStackTrace();
	} 
        
        return prop;
    }
    
    public String getDatabasePath() {
        StringBuilder builder = new StringBuilder();
        
        builder.append("jdbc:postgresql://")
               .append(properties.getProperty("server"))
               .append(":")
               .append(properties.getProperty("port"))
               .append("/")
               .append(properties.getProperty("dbname"));
        
        return builder.toString();
    }
    
    public String getDbUser() {
        return properties.getProperty("dbuser");
    }
    
    public String getDbPassword() {
        System.out.println("PASSWORD: " + properties.getProperty("dbpassword"));
        return properties.getProperty("dbpassword");
    }

    public String getQ1() {
        return Q1;
    }

    public String getQ2() {
        return Q2;
    }

    public String getQ3_1() {
        return Q3_1;
    }

    public String getQ3_2() {
        return Q3_2;
    }

    public String getQ3_3() {
        return Q3_3;
    }

    public String getQ3_4_1() {
        return Q3_4_1;
    }

    public String getQ3_4_2() {
        return Q3_4_2;
    }

    public String getQ4() {
        return Q4;
    }

    public String getQ5() {
        return Q5;
    }

    public String getQ6() {
        return Q6;
    }

    public String getQ7_1() {
        return Q7_1;
    }

    public String getQ7_2() {
        return Q7_2;
    }

    public String getQ7_3() {
        return Q7_3;
    }

    public String getQ7_4_1() {
        return Q7_4_1;
    }

    public String getQ7_4_2() {
        return Q7_4_2;
    }

    public String getInsert() {
        return insert;
    }

    public String getQ2017_1() {
        return Q2017_1;
    }

    public String getQ2017_2() {
        return Q2017_2;
    }
    
    
}
