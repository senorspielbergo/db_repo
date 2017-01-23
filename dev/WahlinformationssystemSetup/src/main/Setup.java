package main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import util.Wahljahr;
import csv.CsvParser;
import database.PostgreSQLDatabase;
import database.User;
import database.UserPrivilege;
import entities.Bewerber;
import entities.Bundesland;
import entities.Direktkandidat;
import entities.Landesliste;
import entities.Listenplaetze;
import entities.Partei;
import entities.Sitzkontingent;
import entities.Stimmzettel;
import entities.Wahlberechtigte;
import entities.Wahlkreis;
import generators.BewerberListenGenerator;
import generators.GeoRegionenGenerator;
import generators.ParteienBerechtigteGenerator;
import generators.SitzkontingenteGenerator;
import generators.StimmzettelGenerator;

public class Setup {

	private InputStream kerg, wkumrechnung2013, wahlkreise, sitzkontingente,
			bewerber2009, bewerber2013, materializedViewStatements;
	private SetupOptions options;

	public void run() {
		if (options.showHelp()) {
			printHelpMessage();
			return;
		}

		System.out.println("Starting... (" + new Date().toString() + ")");
		PostgreSQLDatabase.init(options.getUser());

		if (options.dropOldEntityClasses()) {
			dropOldMaterializedViews();
			dropOldEntityClasses();
		}

		createEntityClasses();
		createEntityContent();

		if (options.insertStimmzettelRange()) {
			createStimmzettelContentForRange(options.getStimmzettelRangeFrom(),
					options.getStimmzettelRangeTo());
		} else {
			createStimmzettelContent();
		}

		if (!options.dropOldEntityClasses()) {
			dropIndicies();
		}

		createIndicies();

		createMaterializedViews();

		createUsers();

		System.out.println("Finished (" + new Date().toString() + ").");
	}

	public Setup(SetupOptions options) throws Exception {
		this.options = options;
		kerg = ClassLoader.getSystemClassLoader().getResourceAsStream(
				"kerg_modified_unicode.csv");
		wkumrechnung2013 = ClassLoader.getSystemClassLoader()
				.getResourceAsStream("wkumrechnung2013_modified_unicode.csv");
		wahlkreise = ClassLoader.getSystemClassLoader().getResourceAsStream(
				"Wahlkreise.csv");
		sitzkontingente = ClassLoader.getSystemClassLoader()
				.getResourceAsStream("sitzkontingente.csv");
		bewerber2009 = ClassLoader.getSystemClassLoader().getResourceAsStream(
				"wahlbewerber2009_mod.csv");
		bewerber2013 = ClassLoader.getSystemClassLoader().getResourceAsStream(
				"wahlbewerber2013_mit_platz.csv");
		materializedViewStatements = ClassLoader.getSystemClassLoader()
				.getResourceAsStream("materialized_view_statements.sql");
	}

	private void createUsers() {
		try {
			User waehler = new User("waehler", "cowboyohnepony");
			User admin = new User("wahladmin", "montagmorgenquiz");
			User stimme = new User("stimmengeber", "waehlenisttoll");

			waehler.addPrivilege(UserPrivilege.SELECT);

			admin.addPrivilege(UserPrivilege.SELECT);
			admin.addPrivilege(UserPrivilege.INSERT);
			
			stimme.addPrivilege(Stimmzettel.class, UserPrivilege.INSERT);
			stimme.addPrivilege(UserPrivilege.SELECT);

			PostgreSQLDatabase.getCurrent().dropUser(waehler);
			PostgreSQLDatabase.getCurrent().dropUser(stimme);
			PostgreSQLDatabase.getCurrent().dropUser(admin);

			PostgreSQLDatabase.getCurrent().createUser(waehler);
			PostgreSQLDatabase.getCurrent().createUser(stimme);
			PostgreSQLDatabase.getCurrent().createUser(admin);
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private void dropIndicies() {
		try {
			PostgreSQLDatabase.getCurrent().dropIndex(Bewerber.class, "id");
			PostgreSQLDatabase.getCurrent().dropIndex(Bewerber.class, "partei");
			PostgreSQLDatabase.getCurrent().dropIndex(Landesliste.class, "id");
			PostgreSQLDatabase.getCurrent().dropIndex(Landesliste.class,
					"partei");
			PostgreSQLDatabase.getCurrent().dropIndex(Landesliste.class,
					"bundesland");
			PostgreSQLDatabase.getCurrent().dropIndex(Listenplaetze.class,
					"bewerber_id");
			PostgreSQLDatabase.getCurrent().dropIndex(Listenplaetze.class,
					"listen_id");
			PostgreSQLDatabase.getCurrent()
					.dropIndex(Wahlkreis.class, "nummer");
			PostgreSQLDatabase.getCurrent().dropIndex(Wahlkreis.class,
					"bundesland");
			PostgreSQLDatabase.getCurrent().dropIndex(Wahlberechtigte.class,
					"wahlkreis");
			PostgreSQLDatabase.getCurrent().dropIndex(Stimmzettel.class,
					"wahljahr");
			PostgreSQLDatabase.getCurrent().dropIndex(Stimmzettel.class,
					"wahlkreis");
			PostgreSQLDatabase.getCurrent().dropIndex(Stimmzettel.class,
					"direktkandidat");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void createIndicies() {
		try {
			PostgreSQLDatabase.getCurrent().createIndex(Bewerber.class, "id");
			PostgreSQLDatabase.getCurrent().createIndex(Bewerber.class,
					"partei");
			PostgreSQLDatabase.getCurrent()
					.createIndex(Landesliste.class, "id");
			PostgreSQLDatabase.getCurrent().createIndex(Landesliste.class,
					"partei");
			PostgreSQLDatabase.getCurrent().createIndex(Landesliste.class,
					"bundesland");
			PostgreSQLDatabase.getCurrent().createIndex(Listenplaetze.class,
					"bewerber_id");
			PostgreSQLDatabase.getCurrent().createIndex(Listenplaetze.class,
					"listen_id");
			PostgreSQLDatabase.getCurrent().createIndex(Wahlkreis.class,
					"nummer");
			PostgreSQLDatabase.getCurrent().createIndex(Wahlkreis.class,
					"bundesland");
			PostgreSQLDatabase.getCurrent().createIndex(Wahlberechtigte.class,
					"wahlkreis");
			PostgreSQLDatabase.getCurrent().createIndex(Stimmzettel.class,
					"wahljahr");
			PostgreSQLDatabase.getCurrent().createIndex(Stimmzettel.class,
					"wahlkreis");
			PostgreSQLDatabase.getCurrent().createIndex(Stimmzettel.class,
					"direktkandidat");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("deprecation")
	private void createMaterializedViews() {
		try {
			List<String> lines = new ArrayList<String>();
			String l = null;
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					materializedViewStatements, Charset.forName("UTF-8")));
			while ((l = reader.readLine()) != null) {
				lines.add(l);
			}
			for (String line : lines) {
				String action = line.substring(
						0,
						line.contains("AS") ? line.indexOf(" AS") : line
								.indexOf("("));
				System.out.println(action.toLowerCase());
				try {
					PostgreSQLDatabase.getCurrent().executeRaw(line);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("deprecation")
	private void createStimmzettelContentForRange(int from, int to) {
		try {
			PostgreSQLDatabase.getCurrent().executeRaw(
					"delete from stimmzettel where wahlkreis between " + from
							+ " and " + to);
			new StimmzettelGenerator(options.getMaxCollectionSize())
					.setWahljahr(Wahljahr.Y2009)
					.generateRange(CsvParser.parse(wkumrechnung2013, ","),
							from, to).setWahljahr(Wahljahr.Y2013)
					.generateRange(CsvParser.parse(kerg, ","), from, to);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void createEntityContent() {
		try {
			new GeoRegionenGenerator().generateFrom(
					CsvParser.parse(wahlkreise, ",")).commitCollections();

			new ParteienBerechtigteGenerator().setWahljahr(Wahljahr.Y2013)
					.generateFrom(CsvParser.parse(kerg, ","))
					.setWahljahr(Wahljahr.Y2009)
					.generateFrom(CsvParser.parse(wkumrechnung2013, ","))
					.commitCollections();

			new BewerberListenGenerator().setWahljahr(Wahljahr.Y2009)
					.generateFrom(CsvParser.parse(bewerber2009, ","))
					.setWahljahr(Wahljahr.Y2013)
					.generateFrom(CsvParser.parse(bewerber2013, ";"))
					.commitCollections();

			new SitzkontingenteGenerator().generateFrom(
					CsvParser.parse(sitzkontingente, ",")).commitCollections();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void createStimmzettelContent() {
		try {
			new StimmzettelGenerator(options.getMaxCollectionSize())
					.setWahljahr(Wahljahr.Y2009)
					.generateFrom(CsvParser.parse(wkumrechnung2013, ","))
					.setWahljahr(Wahljahr.Y2013)
					.generateFrom(CsvParser.parse(kerg, ","));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void dropOldEntityClasses() {
		try {
			PostgreSQLDatabase.getCurrent().dropEntityClass(Stimmzettel.class);
			PostgreSQLDatabase.getCurrent().dropEntityClass(
					Direktkandidat.class);
			PostgreSQLDatabase.getCurrent()
					.dropEntityClass(Listenplaetze.class);
			PostgreSQLDatabase.getCurrent().dropEntityClass(Landesliste.class);
			PostgreSQLDatabase.getCurrent().dropEntityClass(Bewerber.class);
			PostgreSQLDatabase.getCurrent().dropEntityClass(
					Wahlberechtigte.class);
			PostgreSQLDatabase.getCurrent().dropEntityClass(Wahlkreis.class);
			PostgreSQLDatabase.getCurrent().dropEntityClass(
					Sitzkontingent.class);
			PostgreSQLDatabase.getCurrent().dropEntityClass(Bundesland.class);
			PostgreSQLDatabase.getCurrent().dropEntityClass(Partei.class);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void dropOldMaterializedViews() {
		try {
			PostgreSQLDatabase.getCurrent().dropAllMaterializedViews();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void createEntityClasses() {
		try {
			PostgreSQLDatabase.getCurrent().createEntityClass(Partei.class);
			PostgreSQLDatabase.getCurrent().createEntityClass(Bundesland.class);
			PostgreSQLDatabase.getCurrent().createEntityClass(
					Sitzkontingent.class);
			PostgreSQLDatabase.getCurrent().createEntityClass(Wahlkreis.class);
			PostgreSQLDatabase.getCurrent().createEntityClass(
					Wahlberechtigte.class);
			PostgreSQLDatabase.getCurrent().createEntityClass(Bewerber.class);
			PostgreSQLDatabase.getCurrent()
					.createEntityClass(Landesliste.class);
			PostgreSQLDatabase.getCurrent().createEntityClass(
					Listenplaetze.class);
			PostgreSQLDatabase.getCurrent().createEntityClass(
					Direktkandidat.class);
			PostgreSQLDatabase.getCurrent()
					.createEntityClass(Stimmzettel.class);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static void printHelpMessage() {
		System.out
				.println(new StringBuilder(
						"--- KAPHIRA - WAHLINFO3000 Setup Utility ---\n\n")
						.append("A tool for setting up a PostgreSQL server database containing required data for the 'wahlinfo3000' information system about the parliament elections 2009 and 2013 in Germany.\n")
						.append("NOTE: The PostgreSQL server has to be listening on 'localhost:5432', the name of the database must be 'wahlinfo_db'!\n\n")
						.append("Usage: -java setup.jar [user] [password] [options]\n\n")
						.append("Arguments:\n\t[user]\t\tThe admin of your database\n")
						.append("\t[password]\tThe password of the admin\n\n")
						.append("Options:\nGeneral options:\n\t-nodrop\t\tPrevents the tool from dropping all existing tables in the database\n")
						.append("\t\t\tCAUTION: This option may cause inconsistency as rows are NOT overwritten!\n")
						.append("\t-help\t\tShows this help text\n\n")
						.append("Stimmzettel-specific options:\n\t-srange [from] [to]\tGenerate stimmzettel for wahlkreis entities with wahlkreis.nummer beteen [from] and [to] (old stimmzettel rows corresponding to these wahlkreis entities will be removed).\n")
						.append("\t\t\t\tNOTE: - This option requires all other tables to be existent and filled with the data generated by this tool!\n")
						.append("\t\t\t\tNOTE: - This option automatically enables -nodrop\n")
						.append("\t-max [size]\t\tThe maximum number of stimmzettel entities to be inserted at once (default is infinity)\n\n")
						.append("(c) 2017 Katja Ludwig, Philip Lenzen, Ralph Reithmeier. All rights reserved.")
						.toString());
	}
}
