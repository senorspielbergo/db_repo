package generators;

import java.sql.SQLException;

import util.BundeslandUtil;
import util.Pair;
import util.Wahljahr;
import csv.ParsedCsvFile;
import csv.ParsedCsvFile.Row;
import database.EntityCollection;
import database.PostgreSQLDatabase;
import entities.Bewerber;
import entities.Bundesland;
import entities.Direktkandidat;
import entities.Landesliste;
import entities.Listenplaetze;
import entities.Partei;
import entities.Wahlkreis;

public class BewerberListenGenerator implements IEntityGenerator {

	private Wahljahr wahljahr = Wahljahr.Y2009;
	private EntityCollection bewerberCollection = new EntityCollection(
			Bewerber.class);
	private EntityCollection landeslisteCollection = new EntityCollection(
			Landesliste.class);
	private EntityCollection listenplaetzeCollection = new EntityCollection(
			Listenplaetze.class);
	private EntityCollection direktkandidatCollection = new EntityCollection(
			Direktkandidat.class);

	public BewerberListenGenerator setWahljahr(Wahljahr wahljahr) {
		this.wahljahr = wahljahr;
		return this;
	}

	@Override
	public BewerberListenGenerator generateFrom(ParsedCsvFile file) {
		System.out.println("Generating content of tables for year "
				+ wahljahr.toInt()
				+ ": bewerber, landesliste, listenplaetze, direktkandidat...");
		try {
			int offset = wahljahr == Wahljahr.Y2009 ? 0 : 1;
			for (int idx = 1; idx < file.getRowCount(); idx++) {
				Row row = file.getRow(idx);
				String parteiName;
				if (!row.get(5 + offset).isEmpty()) {
					parteiName = row.get(5 + offset);
				} else {
					parteiName = "Übrige";
				}

				Partei partei = (Partei) PostgreSQLDatabase.getCurrent()
						.newQuery().from(Partei.class)
						.where(new Pair<String, String>("name", parteiName))
						.execute();

				int first = offset > 0 ? 4 : 2;
				Bewerber bewerber = (Bewerber) bewerberCollection.findFirst(
						new Pair<String, String>("titel", row.get(1 + offset)),
						new Pair<String, String>("vorname", row.get(first)),
						new Pair<String, String>("nachname", row.get(3)),
						new Pair<String, Integer>("jahrgang", Integer
								.valueOf(row.get(4 + offset))),
						new Pair<String, Partei>("partei", partei));

				if (bewerber == null) {
					bewerber = new Bewerber(bewerberCollection.size(),
							row.get(1 + offset), row.get(first), row.get(3),
							Integer.valueOf(row.get(4 + offset)), partei);
					bewerberCollection.addDistinct(bewerber);
				}

				int listenplatz = 0;
				if (!row.get(8 + offset).isEmpty()) {
					listenplatz = Integer.valueOf(row.get(8 + offset));
					if (partei != null) {
						String name = BundeslandUtil
								.convertAbkuerzungToName(row.get(7 + offset));

						Bundesland bundesland = (Bundesland) PostgreSQLDatabase
								.getCurrent().newQuery().from(Bundesland.class)
								.where(new Pair<String, String>("name", name))
								.execute();

						Landesliste landesliste = (Landesliste) landeslisteCollection
								.findFirst(new Pair<String, Integer>(
										"wahljahr", wahljahr.toInt()),
										new Pair<String, Partei>("partei",
												partei),
										new Pair<String, Bundesland>(
												"bundesland", bundesland));

						if (landesliste == null) {
							landesliste = new Landesliste(
									landeslisteCollection.size(), wahljahr,
									partei, bundesland);
							landeslisteCollection.addDistinct(landesliste);
						}

						listenplaetzeCollection.addDistinct(new Listenplaetze(
								landesliste, bewerber, listenplatz));
					}
				}

				if (!row.get(6 + offset).isEmpty()) {
					Wahlkreis wahlkreis = (Wahlkreis) PostgreSQLDatabase
							.getCurrent()
							.newQuery()
							.from(Wahlkreis.class)
							.where(new Pair<String, Integer>("nummer", Integer
									.valueOf(row.get(6 + offset)))).execute();
					direktkandidatCollection.addDistinct(new Direktkandidat(
							bewerber, wahlkreis, wahljahr, partei));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this;
	}

	@Override
	public void commitCollections() {
		try {
			bewerberCollection.commitAll();
			landeslisteCollection.commitAll();
			listenplaetzeCollection.commitAll();
			direktkandidatCollection.commitAll();

			System.out.println("\t bewerber: " + bewerberCollection.size()
					+ " rows inserted.");
			System.out.println("\t landesliste: "
					+ landeslisteCollection.size() + " rows inserted.");
			System.out.println("\t listenplaetze: "
					+ listenplaetzeCollection.size() + " rows inserted.");
			System.out.println("\t direktkandidat: "
					+ direktkandidatCollection.size() + " rows inserted.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
