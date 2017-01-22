package generators;

import java.sql.SQLException;

import csv.ParsedCsvFile;
import csv.ParsedCsvFile.Row;
import database.EntityCollection;
import entities.Bundesland;
import entities.Wahlkreis;

public class GeoRegionenGenerator implements IEntityGenerator {

	private EntityCollection wahlkreisCollection = new EntityCollection(
			Wahlkreis.class);
	private EntityCollection bundeslandCollection = new EntityCollection(
			Bundesland.class);

	@Override
	public GeoRegionenGenerator generateFrom(ParsedCsvFile file) {
		System.out
				.println("Generating content of tables: wahlkreis, bundesland...");
		Bundesland bundesland = null;
		for (int idx = 1; idx < file.getRowCount(); idx++) {
			Row row = file.getRow(idx);
			if (bundesland == null || !bundesland.getName().equals(row.get(2))) {
				bundesland = new Bundesland(row.get(2));
				bundeslandCollection.addDistinct(bundesland);
			}

			Wahlkreis wahlkreis = new Wahlkreis(Integer.valueOf(row.get(0)),
					row.get(1), bundesland);

			wahlkreisCollection.addDistinct(wahlkreis);
		}
		return this;
	}

	@Override
	public void commitCollections() {
		try {
			bundeslandCollection.commitAll();
			wahlkreisCollection.commitAll();

			System.out.println("\t bundesland: " + bundeslandCollection.size()
					+ " rows inserted.");
			System.out.println("\t wahlkreis: " + wahlkreisCollection.size()
					+ " rows inserted.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
