package kaphira.wahlinfo.database;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import kaphira.wahlinfo.util.Clock;

/**
 * This is the bean which actually communicates with the database
 * All queries are only executed via its methods.
 * The queries are loaded session-wise from the application bean. There is no way to perform
 * other queries than provided this way.
 * 
 * This bean holds a stopwatch item to log the execution times of the queries
 * 
 * @author theralph
 */
@ManagedBean(name = "databaseBean")
@SessionScoped
public class DatabaseBean implements Serializable {

    private static final String POSTGRES_DRIVER = "org.postgresql.Driver";

    private static final String ALL_PARTIES = "select * from partei;";
    private static final String ALL_DISTRICTS = "select * from wahlkreis;";
    private static final String ALL_SINGLE_DISTRICTS = "select * from wahlkreis where nummer in (213,214,215,216,217)";

    private static final String PLACEHOLDER_YEAR = "%wahljahr%";
    private static final String PLACEHOLDER_DISTRICT = "%wahlkreis_nr%";
    private static final String PLACEHOLDER_PARTY = "%partei%";
    private static final String PLACEHOLDER_TITLE = "%titel%";
    private static final String PLACEHOLDER_FIRSTNAME = "%vorname%";
    private static final String PLACEHOLDER_LASTNAME = "%nachname%";
    private static final String PLACEHOLDER_CANDIDATE_PARTY = "%bewerber_partei%";

    private final Logger logger = Logger.getLogger(DatabaseBean.class.getName());
    private final Clock clock = new Clock();

    private Connection connection;

    @ManagedProperty(value = "#{configBean}")
    private ConfigBean configBean;

    @PostConstruct
    private void init() {

        String dbPath = configBean.getDatabasePath();
        String dbUser = configBean.getDbUser();
        String dbPassword = configBean.getDbPassword();

        try {
            Class.forName(POSTGRES_DRIVER);
            connection = DriverManager.getConnection(dbPath, dbUser, dbPassword);

        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    @PreDestroy
    private void onClose() {
        try {
            connection.close();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    //*********************************//
    //        QUERY EXECUTION          //
    //*********************************//
    public ResultSet executeQuery(String query, String queryName) {
        if (connection == null) {
            init();
        }
        try {
            clock.start();
            Statement statement = connection.createStatement();
            log(queryName, clock.stop());
            return statement.executeQuery(query);

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void executeStatement(String query, String queryName) {
        if (connection == null) {
            init();
        }
        try {
            clock.start();
            Statement statement = connection.createStatement();
            statement.execute(query);
            log(queryName, clock.stop());

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //*********************************//
    //             QUERIES             //
    //*********************************//
    /**
     *
     * @return
     */
    public ResultSet queryAllDistricts() {
        return executeQuery(ALL_DISTRICTS, "ALL_DISTRICS");
    }

    /**
     *
     * @return
     */
    public ResultSet queryAllSingleDistricts() {
        return executeQuery(ALL_SINGLE_DISTRICTS, "ALL_LIVE_DISTRICTS");
    }

    /**
     *
     * @return
     */
    public ResultSet queryAllParties() {
        return executeQuery(ALL_PARTIES, "ALL_PARTIES");
    }

    /**
     *
     * @param year
     * @return
     */
    public ResultSet queryQ1(int year) {
        String query = configBean.getQ1();
        query = query.replaceAll(PLACEHOLDER_YEAR, String.valueOf(year));
        return executeQuery(query, "Q1");
    }

    /**
     *
     * @param year
     * @return
     */
    public ResultSet queryQ2(int year) {
        String query = configBean.getQ2();
        query = query.replaceAll(PLACEHOLDER_YEAR, String.valueOf(year));

        return executeQuery(query, "Q2");
    }

    /**
     *
     * @param districtId
     * @param year
     * @return
     */
    public ResultSet queryQ3_1(int districtId, int year) {
        String query = configBean.getQ3_1();
        query = query.replaceAll(PLACEHOLDER_YEAR, String.valueOf(year));
        query = query.replaceAll(PLACEHOLDER_DISTRICT, String.valueOf(districtId));

        return executeQuery(query, "Q3-1");
    }

    /**
     *
     * @param districtId
     * @param year
     * @return
     */
    public ResultSet queryQ3_2(int districtId, int year) {
        String query = configBean.getQ3_2();
        query = query.replaceAll(PLACEHOLDER_YEAR, String.valueOf(year));
        query = query.replaceAll(PLACEHOLDER_DISTRICT, String.valueOf(districtId));

        return executeQuery(query, "Q3-2");
    }

    /**
     *
     * @param districtId
     * @param year
     * @return
     */
    public ResultSet queryQ3_3(int districtId, int year) {
        String query = configBean.getQ3_3();
        query = query.replaceAll(PLACEHOLDER_YEAR, String.valueOf(year));
        query = query.replaceAll(PLACEHOLDER_DISTRICT, String.valueOf(districtId));

        return executeQuery(query, "Q3-3");
    }

    /**
     *
     * @param districtId
     * @param year
     * @return
     */
    public ResultSet queryQ3_4_1(int districtId, int year) {
        String query = configBean.getQ3_4_1();
        query = query.replaceAll(PLACEHOLDER_YEAR, String.valueOf(year));
        query = query.replaceAll(PLACEHOLDER_DISTRICT, String.valueOf(districtId));

        return executeQuery(query, "Q3-4-1");
    }

    /**
     *
     * @param districtId
     * @param year
     * @return
     */
    public ResultSet queryQ3_4_2(int districtId, int year) {
        String query = configBean.getQ3_4_2();
        query = query.replaceAll(PLACEHOLDER_YEAR, String.valueOf(year));
        query = query.replaceAll(PLACEHOLDER_DISTRICT, String.valueOf(districtId));

        return executeQuery(query, "Q3-4-2");
    }

    /**
     *
     * @param year
     * @return
     */
    public ResultSet queryQ4(int year) {
        String query = configBean.getQ4();
        query = query.replaceAll(PLACEHOLDER_YEAR, String.valueOf(year));

        return executeQuery(query, "Q4");
    }

    /**
     *
     * @return
     */
    public ResultSet queryQ5() {
        String query = configBean.getQ5();

        return executeQuery(query, "Q5");
    }

    /**
     *
     * @param partyName
     * @param year
     * @return
     */
    public ResultSet queryQ6(String partyName, int year) {
        String query = configBean.getQ6();
        query = query.replaceAll(PLACEHOLDER_YEAR, String.valueOf(year));
        query = query.replaceAll(PLACEHOLDER_PARTY, partyName);
        return executeQuery(query, "Q6");
    }

    /**
     *
     * @param districtId
     * @param year
     * @return
     */
    public ResultSet queryQ7_1(int districtId, int year) {
        String query = configBean.getQ7_1();
        query = query.replaceAll(PLACEHOLDER_YEAR, String.valueOf(year));
        query = query.replaceAll(PLACEHOLDER_DISTRICT, String.valueOf(districtId));

        return executeQuery(query, "Q7-1");
    }

    /**
     *
     * @param districtId
     * @param year
     * @return
     */
    public ResultSet queryQ7_2(int districtId, int year) {
        String query = configBean.getQ7_2();
        query = query.replaceAll(PLACEHOLDER_YEAR, String.valueOf(year));
        query = query.replaceAll(PLACEHOLDER_DISTRICT, String.valueOf(districtId));

        return executeQuery(query, "Q7-2");
    }

    /**
     *
     * @param districtId
     * @param year
     * @return
     */
    public ResultSet queryQ7_3(int districtId, int year) {
        String query = configBean.getQ7_3();
        query = query.replaceAll(PLACEHOLDER_YEAR, String.valueOf(year));
        query = query.replaceAll(PLACEHOLDER_DISTRICT, String.valueOf(districtId));

        return executeQuery(query, "Q7-3");
    }

    /**
     *
     * @param districtId
     * @param year
     * @return
     */
    public ResultSet queryQ7_4_1(int districtId, int year) {
        String query = configBean.getQ7_4_1();
        query = query.replaceAll(PLACEHOLDER_YEAR, String.valueOf(year));
        query = query.replaceAll(PLACEHOLDER_DISTRICT, String.valueOf(districtId));

        return executeQuery(query, "Q7-4-1");
    }

    /**
     *
     * @param districtId
     * @param year
     * @return
     */
    public ResultSet queryQ7_4_2(int districtId, int year) {
        String query = configBean.getQ7_4_2();
        query = query.replaceAll(PLACEHOLDER_YEAR, String.valueOf(year));
        query = query.replaceAll(PLACEHOLDER_DISTRICT, String.valueOf(districtId));

        return executeQuery(query, "Q7-4-2");
    }

    /**
     *
     * @param districtId
     * @return
     */
    public ResultSet queryQ2017_1(int districtId) {
        String query = configBean.getQ2017_1();
        query = query.replaceAll(PLACEHOLDER_DISTRICT, String.valueOf(districtId));

        return executeQuery(query, "Q2017-1");
    }

    /**
     *
     * @param districtId
     * @return
     */
    public ResultSet queryQ2017_2(int districtId) {
        String query = configBean.getQ2017_2();
        query = query.replaceAll(PLACEHOLDER_DISTRICT, String.valueOf(districtId));

        return executeQuery(query, "Q2017-2");
    }

    /**
     *
     * @param districtId
     * @param title
     * @param firstName
     * @param lastName
     * @param party
     */
    public void insertVote(int districtId, String title, String firstName, String lastName, String candidateParty, String party) {
        String query = configBean.getInsert();
        query = query.replaceAll(PLACEHOLDER_DISTRICT, String.valueOf(districtId));
        query = query.replaceAll(PLACEHOLDER_TITLE, String.valueOf(title));
        query = query.replaceAll(PLACEHOLDER_FIRSTNAME, String.valueOf(firstName));
        query = query.replaceAll(PLACEHOLDER_LASTNAME, String.valueOf(lastName));
        query = query.replaceAll(PLACEHOLDER_PARTY, String.valueOf(party));
        query = query.replaceAll(PLACEHOLDER_CANDIDATE_PARTY, String.valueOf(candidateParty));
        System.out.println(query);
        executeStatement(query, "INSERT_VOTE");
    }

    public void reloadViews() {
        String statement = configBean.getReloadStatement();

        executeStatement(statement, "RELOAD_VIEWS");
    }

    //*********************************//
    //         GETTER/SETTER           //
    //*********************************//
    public ConfigBean getConfigBean() {
        return configBean;
    }

    public void setConfigBean(ConfigBean configBean) {
        this.configBean = configBean;
    }

    //*********************************//
    //         LOGGING                 //
    //*********************************//
    private void log(String queryName, long time) {

        String logString = new StringBuilder("Executed Query ")
                .append(queryName)
                .append(" in ")
                .append(time)
                .append(" ms.")
                .toString();
        logger.log(Level.INFO, logString);

    }

}
