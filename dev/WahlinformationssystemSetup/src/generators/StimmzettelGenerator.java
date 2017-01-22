package generators;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Pair;
import util.Wahljahr;
import csv.ParsedCsvFile;
import csv.ParsedCsvFile.Row;
import database.DatabaseEntity;
import database.EntityCollection;
import database.PostgreSQLDatabase;
import entities.Bewerber;
import entities.Bundesland;
import entities.Direktkandidat;
import entities.Landesliste;
import entities.Partei;
import entities.Stimmzettel;
import entities.Wahlkreis;

public class StimmzettelGenerator implements IEntityGenerator {

	private int maxCollectionSize;
	private Wahljahr wahljahr;

	public StimmzettelGenerator(int maxCollectionSize) {
		this.maxCollectionSize = maxCollectionSize;
	}

	public StimmzettelGenerator setWahljahr(Wahljahr wahljahr) {
		this.wahljahr = wahljahr;
		return this;
	}

	public StimmzettelGenerator generateRange(ParsedCsvFile file, int from,
			int to) {
		System.out.println("Generating content of tables: stimmzettel...");
		Row columnNames = file.getRow(0);
		try {
			for (int idx = from; idx < to; idx++) {
				Row row = file.getRow(idx);
				processSingleRow(columnNames, row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this;
	}

	@Override
	public StimmzettelGenerator generateFrom(ParsedCsvFile file) {
		return generateRange(file, 1, file.getRowCount());
	}

	public void processSingleRow(Row columnNames, Row row) throws SQLException {
		System.out.print("\tstimmzettel: Processing Wahlkreis " + row.get(0) + "... ");

		int totalCount = 0;

		Map<Partei, Pair<Integer, Integer>> parteiStimmen = new HashMap<Partei, Pair<Integer, Integer>>();

		EntityCollection wahlkreisStimmzettel = new EntityCollection(
				Stimmzettel.class);

		Wahlkreis wahlkreis = (Wahlkreis) PostgreSQLDatabase
				.getCurrent()
				.newQuery()
				.from(Wahlkreis.class)
				.where(new Pair<String, Integer>("nummer", Integer.valueOf(row
						.get(0)))).execute();

		int[] sumNotVotedStimmen = new int[2];

		for (int parteiIdx = 8; parteiIdx < row.length(); parteiIdx++) {
			String parteiColumn = columnNames.get(parteiIdx);
			String parteiName = parteiColumn.substring(0,
					parteiColumn.lastIndexOf("_S"));
			Partei partei = (Partei) PostgreSQLDatabase.getCurrent()
					.newQuery().from(Partei.class)
					.where(new Pair<String, String>("name", parteiName))
					.execute();

			boolean isErststimme = parteiColumn.contains(parteiName + "_S1");

			Pair<Integer, Integer> stimmen;
			if (!parteiStimmen.containsKey(partei)) {
				stimmen = new Pair<Integer, Integer>(0, 0);
			} else {
				stimmen = parteiStimmen.get(partei);
			}

			if (isErststimme) {
				stimmen.first = row.get(parteiIdx).isEmpty() ? 0 : Integer
						.valueOf(row.get(parteiIdx));
				sumNotVotedStimmen[0] += stimmen.first;
			} else {
				stimmen.second = row.get(parteiIdx).isEmpty() ? 0 : Integer
						.valueOf(row.get(parteiIdx));
				sumNotVotedStimmen[1] += stimmen.second;
			}

			parteiStimmen.put(partei, stimmen);
		}

		List<Partei> parteien = new ArrayList<Partei>(parteiStimmen.keySet());
		int pdidx = -1, plidx = -1;

		Landesliste landesliste = null;
		Bewerber bewerber = null;
		Partei parteiDirekt = null;
		Partei parteiListe = null;

		while (sumNotVotedStimmen[0] > 0 || sumNotVotedStimmen[1] > 0) {
			if (sumNotVotedStimmen[0] > 0) {
				if (bewerber == null
						|| parteiStimmen.get(parteiDirekt).first == 0) {
					int pidx = pdidx;
					bewerber = null;
					do {
						parteiDirekt = parteien.get(pdidx = (pdidx + 1)
								% parteien.size());
						if (pdidx == pidx) {
							sumNotVotedStimmen[0] = 0;
							break;
						}
					} while (parteiStimmen.get(parteiDirekt).first == 0);
					if (parteiStimmen.get(parteiDirekt).first > 0) {
						DatabaseEntity entity = PostgreSQLDatabase
								.getCurrent()
								.newQuery()
								.from(Direktkandidat.class)
								.where(new Pair<String, Integer>("wahljahr",
										wahljahr.toInt()),
										new Pair<String, Wahlkreis>(
												"wahlkreis", wahlkreis),
										new Pair<String, Partei>("partei",
												parteiDirekt)).execute();

						// Workaround: Das Ergebnis von 2009 hat teils
						// falsche Stimmen
						// aufgrund der Umrechnung auf die Wahlkreise von
						// 2013 (bspw.
						// Wahlkreis 12 - Partei Übrige, Wahlkreis 41 - Partei
						// MLPD)
						if (entity != null) {
							if (((Direktkandidat) entity).getBewerber() != null) {
								bewerber = (Bewerber) ((Direktkandidat) entity)
										.getBewerber();
							}
						} else {
							parteiStimmen.get(parteiDirekt).first = 0;
						}
					}
				}
			} else {
				bewerber = null;
			}

			if (sumNotVotedStimmen[1] > 0) {
				if (landesliste == null
						|| parteiStimmen.get(parteiListe).second == 0) {

					int pidx = plidx;
					landesliste = null;
					do {
						parteiListe = parteien.get(plidx = (plidx + 1)
								% parteien.size());
						if (plidx == pidx) {
							sumNotVotedStimmen[1] = 0;
							break;
						}
					} while (parteiStimmen.get(parteiListe).second == 0);
					if (parteiStimmen.get(parteiListe).second > 0) {
						landesliste = (Landesliste) PostgreSQLDatabase
								.getCurrent()
								.newQuery()
								.from(Landesliste.class)
								.where(new Pair<String, Integer>("wahljahr",
										wahljahr.toInt()),
										new Pair<String, Partei>("partei",
												parteiListe),
										new Pair<String, Bundesland>(
												"bundesland", wahlkreis
														.getBundesland()))
								.execute();
					}
				}
			} else {
				landesliste = null;
			}

			if (landesliste != null || bewerber != null) {
				if (bewerber != null) {
					parteiStimmen.get(parteiDirekt).first--;
					sumNotVotedStimmen[0]--;
				}
				if (landesliste != null) {
					sumNotVotedStimmen[1]--;
					parteiStimmen.get(parteiListe).second--;
				}

				wahlkreisStimmzettel.add(new Stimmzettel(wahljahr, bewerber,
						landesliste, wahlkreis));

				totalCount++;

				if (wahlkreisStimmzettel.size() > maxCollectionSize) {
					commitWahlkreisCollection(wahlkreisStimmzettel);
					wahlkreisStimmzettel.clear();
				}
			}
		}

		int valid = Math.max(Integer.valueOf(row.get(6)),
				Integer.valueOf(row.get(7)));
		int anzahlStimmzettel = Integer.valueOf(row.get(3));
		totalCount += anzahlStimmzettel - valid;
		
		for (int count = 0; count < anzahlStimmzettel - valid; count++) {
			wahlkreisStimmzettel.add(new Stimmzettel(wahljahr, null, null,
					wahlkreis));
		}

		commitWahlkreisCollection(wahlkreisStimmzettel);
		
		System.out.println(totalCount + " rows inserted.");
	}

	private void commitWahlkreisCollection(EntityCollection wahlkreisStimmzettel) {
		try {
			wahlkreisStimmzettel.commitAll();
			wahlkreisStimmzettel.clear();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void commitCollections() {
		// Do nothing
	}
}
