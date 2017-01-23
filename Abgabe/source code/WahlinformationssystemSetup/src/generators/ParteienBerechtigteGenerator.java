package generators;

import java.sql.SQLException;

import util.Pair;
import util.Wahljahr;
import csv.ParsedCsvFile;
import csv.ParsedCsvFile.Row;
import database.EntityCollection;
import database.PostgreSQLDatabase;
import entities.Partei;
import entities.Wahlberechtigte;
import entities.Wahlkreis;

public class ParteienBerechtigteGenerator implements IEntityGenerator {

	private Wahljahr wahljahr = Wahljahr.Y2009;
	private EntityCollection parteienCollection = new EntityCollection(
			Partei.class);
	private EntityCollection wahlberechtigteCollection = new EntityCollection(
			Wahlberechtigte.class);

	@Override
	public ParteienBerechtigteGenerator generateFrom(ParsedCsvFile file) {
		System.out.println("Generating content of tables for year "
				+ wahljahr.toInt() + ": partei, wahlberechtigte...");
		try {
			Row parteien = file.getRow(0);
			for (int idx = 8; idx < parteien.length(); idx++) {
				String name = parteien.get(idx);
				Partei partei = new Partei(name.substring(0,
						name.lastIndexOf("_S")));
				parteienCollection.addDistinct(partei);
			}

			for (int idx = 1; idx < file.getRowCount(); idx++) {
				Wahlkreis wahlkreis = (Wahlkreis) PostgreSQLDatabase
						.getCurrent()
						.newQuery()
						.from(Wahlkreis.class)
						.where(new Pair<String, Integer>("nummer", Integer
								.valueOf(file.getRow(idx).get(0)))).execute();
				wahlberechtigteCollection.addDistinct(new Wahlberechtigte(
						wahlkreis, wahljahr, Integer.valueOf(file.getRow(idx)
								.get(2))));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this;
	}

	public ParteienBerechtigteGenerator setWahljahr(Wahljahr wahljahr) {
		this.wahljahr = wahljahr;
		return this;
	}

	@Override
	public void commitCollections() {
		try {
			parteienCollection.commitAll();
			wahlberechtigteCollection.commitAll();

			System.out.println("\t partei: " + parteienCollection.size()
					+ " rows inserted.");
			System.out.println("\t wahlberechtigte: "
					+ wahlberechtigteCollection.size() + " rows inserted.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
