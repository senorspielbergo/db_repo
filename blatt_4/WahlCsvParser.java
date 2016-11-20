import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WahlCsvParser {

	private static final String SEPARATOR = ",";

	public static void main(String[] args) {
		String wahlkreisId = null, wahljahr = null;
		try {
			if (args.length != 1) {
				if (args.length == 4 && args[1] == "-gs") {
					wahlkreisId = args[2];
					wahljahr = args[3];
				} else {
					throw new Exception();
				}
			}

			File wahlkreise = new File(args[0].concat(File.separator
					+ "Wahlkreise.csv"));
			File kerg = new File(args[0].concat(File.separator
					+ "kerg_modified_unicode.csv"));
			File wahlbewerber2009 = new File(args[0].concat(File.separator
					+ "wahlbewerber2009_mod.csv"));
			File wahlbewerber2013 = new File(args[0].concat(File.separator
					+ "wahlbewerber2013_mit_platz.csv"));
			File wkumrechnung2013 = new File(args[0].concat(File.separator
					+ "wkumrechnung2013_modified_unicode.csv"));
			File sql = new File(
					args[0].concat(File.separator + "sql_statement"));

			List<String[]> bewerberValues = getBewerberValues(wahlbewerber2009,
					wahlbewerber2013);
			List<String[]> landeslisteValues = getLandeslisteValues(
					wahlbewerber2009, wahlbewerber2013);
			List<String[]> wahlkreisValues = getWahlkreisValues(wahlkreise,
					kerg);

			sql.createNewFile();
			PrintWriter writer = new PrintWriter(sql);

			List<String[]> parteiValues = null, bundeslandValues = null, listenplaetzeValues = null, stimmzettelValues = null;

			if (wahlkreisId == null) {
				InputStream createStatement = WahlCsvParser.class
						.getClassLoader().getResourceAsStream("createTable");

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(createStatement));
				reader.lines().forEach(l -> {
					try {
						writer.println(l);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				reader.close();

				parteiValues = getParteiValues(kerg, wkumrechnung2013);
				bundeslandValues = getBundeslandValues(wahlkreisValues);
				listenplaetzeValues = getListenplaetzeValues(landeslisteValues,
						bewerberValues, wahlbewerber2009, wahlbewerber2013);

				writeValues(writer, "partei", parteiValues,
						new String[] { "varchar" });
				writeValues(writer, "bundesland", bundeslandValues,
						new String[] { "varchar", "int" });
				writeValues(writer, "bewerber", bewerberValues, new String[] {
						"int", "varchar", "varchar", "varchar", "varchar" });
				writeValues(writer, "wahlkreis", wahlkreisValues, new String[] {
						"int", "varchar", "int", "varchar" });
				writeValues(writer, "landesliste", landeslisteValues,
						new String[] { "int", "int", "varchar", "varchar" });
				writeValues(writer, "listenplaetze", listenplaetzeValues,
						new String[] { "int", "int", "int" });

				List<String[]> stimmzettelResult = new ArrayList<String[]>();
				List<String> ergebnisLines = Files.readAllLines(kerg.toPath());
				String header13 = ergebnisLines.remove(0);
				ergebnisLines
						.stream()
						.forEach(
								l -> {
									String[] s = l.split(SEPARATOR);
									stimmzettelResult
											.addAll(StimmzettelGenerator
													.generateForWahlkreis(
															wahlbewerber2013,
															bewerberValues,
															landeslisteValues,
															wahlkreisValues,
															2013, header13, s));
									writeValues(writer, "stimmzettel",
											stimmzettelResult, new String[] {
													"int", "int", "int", "int",
													"int" });
									stimmzettelResult.clear();
								});
				ergebnisLines = Files.readAllLines(wkumrechnung2013.toPath());
				String header09 = ergebnisLines.remove(0);
				ergebnisLines
						.stream()
						.forEach(
								l -> {
									String[] s = l.split(SEPARATOR);
									stimmzettelResult
											.addAll(StimmzettelGenerator
													.generateForWahlkreis(
															wahlbewerber2009,
															bewerberValues,
															landeslisteValues,
															wahlkreisValues,
															2009, header09, s));
									writeValues(writer, "stimmzettel",
											stimmzettelResult, new String[] {
													"int", "int", "int", "int",
													"int" });
									stimmzettelResult.clear();
								});
			} else {
				File wahlbewerber = (wahljahr.equals("2009") ? wahlbewerber2009
						: wahlbewerber2013);
				File wahlResult = (wahljahr.equals("2009") ? wkumrechnung2013
						: kerg);
				List<String> lines = Files.readAllLines(wahlResult.toPath());
				String header = lines.remove(0);
				String wkId = wahlkreisId;
				String line = lines.stream()
						.filter(l -> l.split(SEPARATOR)[0].equals(wkId))
						.findAny().get();
				stimmzettelValues = StimmzettelGenerator.generateForWahlkreis(
						wahlbewerber, bewerberValues, landeslisteValues,
						wahlkreisValues, Integer.valueOf(wahljahr), header,
						line.split(SEPARATOR));

				writeValues(writer, "stimmzettel", stimmzettelValues,
						new String[] { "int", "int", "int", "int", "int" });
			}

			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void writeValues(PrintWriter writer, String table,
			List<String[]> values, String[] types) {
		writer.println();
		writer.print("insert into " + table + " values(");
		for (int pidx = 0; pidx < values.size(); pidx++) {
			writer.print("(");
			String[] s = values.get(pidx);
			for (int sidx = 0; sidx < s.length - 1; sidx++) {
				if (types[sidx].equals("varchar")) {
					writer.print("'" + s[sidx] + "', ");
				} else {
					writer.print(s[sidx] + ", ");
				}
			}
			if (types[s.length - 1].equals("varchar")) {
				writer.print("'" + s[s.length - 1] + "')");
			} else {
				writer.print(s[s.length - 1] + ")");
			}
			if (pidx < values.size() - 1) {
				writer.print(",");
			}
		}
		writer.print(");");
	}

	private static List<String[]> getListenplaetzeValues(
			List<String[]> landeslisteValues, List<String[]> bewerberValues,
			File wahlbewerber2009, File wahlbewerber2013) {
		List<String[]> listenplaetzeResult = new ArrayList<String[]>();
		try {
			List<String> wahlbewerberLines = Files
					.readAllLines(wahlbewerber2009.toPath());
			wahlbewerberLines.remove(0);
			wahlbewerberLines
					.stream()
					.forEach(
							w -> {
								String[] a = w.split(SEPARATOR);
								String listId = null, bewerberId = null;
								if (a.length > 8) {
									listId = landeslisteValues
											.stream()
											.filter(l -> ((String[]) l)[1]
													.equals("2009")
													&& ((String[]) l)[2]
															.equals(a[5])
													&& ((String[]) l)[3]
															.equals(convertAbkuerzungToName(a[7])))
											.findFirst().get()[0];
									bewerberId = bewerberValues
											.stream()
											.filter(b -> ((String[]) b)[1]
													.equals(a[1])
													&& ((String[]) b)[2]
															.equals(a[2])
													&& ((String[]) b)[3]
															.equals(a[3])
													&& ((String[]) b)[4]
															.equals(a[5]))
											.findFirst().get()[0];
									if (listId != null && bewerberId != null) {
										listenplaetzeResult.add(new String[] {
												listId, bewerberId, a[8] });
									}
								}
							});
			wahlbewerberLines = Files.readAllLines(wahlbewerber2013.toPath());
			wahlbewerberLines.remove(0);
			wahlbewerberLines
					.stream()
					.forEach(
							w -> {
								String[] a = w.split(";");
								String listId = null, bewerberId = null;
								if (a.length > 9) {
									listId = landeslisteValues
											.stream()
											.filter(l -> ((String[]) l)[1]
													.equals("2013")
													&& ((String[]) l)[2]
															.equals(a[6])
													&& ((String[]) l)[3]
															.equals(convertAbkuerzungToName(a[8])))
											.findFirst().get()[0];
									bewerberId = bewerberValues
											.stream()
											.filter(b -> ((String[]) b)[1]
													.equals(a[2])
													&& ((String[]) b)[2]
															.equals(a[4])
													&& ((String[]) b)[3]
															.equals(a[3])
													&& ((String[]) b)[4]
															.equals(a[6]))
											.findFirst().get()[0];
									if (listId != null && bewerberId != null) {
										listenplaetzeResult.add(new String[] {
												listId, bewerberId, a[9] });
									}
								}
							});
		} catch (IOException e) {
			e.printStackTrace();
			System.out
					.println("WahlCsvParser \"path\" [-gs \"wahlkreisNr\" \"wahljahr\"]");
		}
		return listenplaetzeResult;
	}

	private static List<String[]> getLandeslisteValues(File wahlbewerber2009,
			File wahlbewerber2013) {
		List<String[]> landeslisteResult = new ArrayList<String[]>();
		try {
			List<String> wahlbewerberLines = Files
					.readAllLines(wahlbewerber2009.toPath());
			wahlbewerberLines.remove(0);
			Map<String, String[]> map = new HashMap<String, String[]>();
			wahlbewerberLines.stream().forEach(
					s -> {
						String[] e = s.split(SEPARATOR);
						if (e.length > 7) {
							map.put("2009" + e[5] + e[7],
									new String[] { "", "2009", e[5],
											convertAbkuerzungToName(e[7]) });
						}
					});
			wahlbewerberLines = Files.readAllLines(wahlbewerber2013.toPath());
			wahlbewerberLines.remove(0);
			wahlbewerberLines.stream().forEach(
					s -> {
						String[] e = s.split(";");
						if (e.length > 8) {
							map.put("2013" + e[6] + e[8],
									new String[] { "", "2013", e[6],
											convertAbkuerzungToName(e[8]) });
						}
					});
			map.values().stream().forEach(s -> {
				s[0] = String.valueOf(landeslisteResult.size());
				landeslisteResult.add(s);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return landeslisteResult;
	}

	private static String convertAbkuerzungToName(String bundesland) {
		switch (bundesland) {
		case "BB":
			return "Brandenburg";
		case "BE":
			return "Berlin";
		case "BW":
			return "Baden-Württemberg";
		case "BY":
			return "Bayern";
		case "HB":
			return "Bremen";
		case "HE":
			return "Hessen";
		case "HH":
			return "Hamburg";
		case "MV":
			return "Mecklenburg-Vorpommern";
		case "NI":
			return "Niedersachsen";
		case "NW":
			return "Nordrhein-Westfalen";
		case "RP":
			return "Rheinland-Pfalz";
		case "SH":
			return "Schleswig-Holstein";
		case "SL":
			return "Saarland";
		case "SN":
			return "Sachsen";
		case "ST":
			return "Sachsen-Anhalt";
		case "TH":
			return "Thüringen";
		}
		return null;
	}

	private static List<String[]> getBewerberValues(File wahlbewerber2009,
			File wahlbewerber2013) {
		List<String[]> wahlbewerberResult = new ArrayList<String[]>();
		try {
			List<String> wahlbewerberLines = Files
					.readAllLines(wahlbewerber2009.toPath());
			wahlbewerberLines.remove(0);
			Map<String, String[]> map = new HashMap<String, String[]>();
			wahlbewerberLines.stream().forEach(
					s -> {
						String[] e = s.split(SEPARATOR);
						map.put(e[1] + e[2] + e[3] + e[4] + e[5], new String[] {
								"", e[1], e[2], e[3], e[5] });
					});
			wahlbewerberLines = Files.readAllLines(wahlbewerber2013.toPath());
			wahlbewerberLines.remove(0);
			wahlbewerberLines.stream().forEach(
					s -> {
						String[] e = s.split(";");
						map.put(e[2] + e[4] + e[3] + e[5] + e[6], new String[] {
								"", e[2], e[4], e[3], e[6] });
					});
			map.values().stream().forEach(s -> {
				s[0] = String.valueOf(wahlbewerberResult.size());
				wahlbewerberResult.add(s);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return wahlbewerberResult;
	}

	private static List<String[]> getBundeslandValues(
			List<String[]> wahlkreisValues) {
		List<String[]> wahlkreisResult = new ArrayList<String[]>();
		wahlkreisValues
				.stream()
				.collect(
						Collectors.groupingBy(wv -> ((String[]) wv)[3],
								Collectors.summingInt(wv -> Integer
										.parseInt(((String[]) wv)[2]))))
				.forEach(
						(k, v) -> wahlkreisResult
								.add(new String[] { k, "" + v }));
		return wahlkreisResult;
	}

	private static List<String[]> getParteiValues(File kerg,
			File wkumrechnung2013) {
		List<String[]> result = new ArrayList<String[]>();
		try {
			List<String> kergLines = Files.readAllLines(kerg.toPath());
			List<String> parties = Arrays.asList(kergLines.remove(0).split(
					SEPARATOR));
			parties.subList(8, parties.size())
					.stream()
					.forEach(
							p -> result.add(new String[] { p.substring(0,
									p.lastIndexOf("_S")) }));
			List<String> wkumrechnung2013Lines = Files
					.readAllLines(wkumrechnung2013.toPath());
			List<String> parties2 = Arrays.asList(wkumrechnung2013Lines.remove(
					0).split(SEPARATOR));
			parties2.subList(8, parties2.size())
					.stream()
					.filter(p -> !parties.contains(p))
					.forEach(
							p -> result.add(new String[] { p.substring(0,
									p.lastIndexOf("_S")) }));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static List<String[]> getWahlkreisValues(File wahlkreise, File kerg) {
		List<String[]> wahlkreisResult = new ArrayList<String[]>();
		try {
			List<String> wahlkreisLines = Files.readAllLines(wahlkreise
					.toPath());
			List<String> kergLines = Files.readAllLines(kerg.toPath());
			wahlkreisLines.remove(0);
			kergLines.remove(0);
			Map<Object, String> kergs = kergLines
					.stream()
					.collect(
							Collectors.groupingBy(l -> ((String) l)
									.split(SEPARATOR)[0], Collectors.reducing(
									"",
									(i, l) -> ((String) l).split(SEPARATOR)[2])));
			wahlkreisLines.stream().forEach(
					line -> {
						String[] wk = line.split(SEPARATOR);
						wahlkreisResult.add(new String[] { wk[0], wk[1],
								kergs.get(wk[0]), wk[2] });
					});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return wahlkreisResult;
	}
}
