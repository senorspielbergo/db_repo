package generators;

import java.sql.SQLException;

import util.BundeslandUtil;
import util.Pair;
import util.Wahljahr;
import csv.ParsedCsvFile;
import csv.ParsedCsvFile.Row;
import database.EntityCollection;
import database.PostgreSQLDatabase;
import entities.Bundesland;
import entities.Sitzkontingent;

public class SitzkontingenteGenerator implements IEntityGenerator {

	private EntityCollection sitzkontingentCollection = new EntityCollection(
			Sitzkontingent.class);

	@Override
	public SitzkontingenteGenerator generateFrom(ParsedCsvFile file) {
		System.out
		.println("Generating content of tables: sitzkontingent...");
		try {
			for (int idx = 0; idx < file.getRowCount(); idx++) {
				Row row = file.getRow(idx);
				String bundeslandName = BundeslandUtil.getBundeslandNames()[Integer
						.valueOf(row.get(0))];
				Bundesland bundesland = (Bundesland) PostgreSQLDatabase
						.getCurrent()
						.newQuery()
						.from(Bundesland.class)
						.where(new Pair<String, String>("name", bundeslandName))
						.execute();
				sitzkontingentCollection.addDistinct(new Sitzkontingent(
						bundesland, Wahljahr.parse(row.get(1)), Integer
								.valueOf(row.get(2))));
			}
			sitzkontingentCollection.commitAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this;
	}

	@Override
	public void commitCollections() {
		try {
			sitzkontingentCollection.commitAll();
			System.out.println("\t sitzkontingent: " + sitzkontingentCollection.size() + " rows inserted.");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
