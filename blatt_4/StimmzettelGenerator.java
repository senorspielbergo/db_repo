import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StimmzettelGenerator {

	private Map<String, Map<Integer, Map<String, List<BewerberTuple>>>> direktKandidaten;
	private List<String[]> wahlkreisValues, landeslisteValues;
	private int stimmzettelCount;

	public StimmzettelGenerator(File wahlbewerber2009, File wahlbewerber2013,
			List<String[]> bewerberValues, List<String[]> wahlkreisValues,
			List<String[]> landeslisteValues) {
		this.direktKandidaten = new HashMap<String, Map<Integer, Map<String, List<BewerberTuple>>>>();
		fillDirektKandidaten(wahlbewerber2009, bewerberValues, wahlkreisValues,
				2009);
		fillDirektKandidaten(wahlbewerber2013, bewerberValues, wahlkreisValues,
				2013);
		this.wahlkreisValues = wahlkreisValues;
		this.landeslisteValues = landeslisteValues;
		this.stimmzettelCount = 0;
	}

	private void fillDirektKandidaten(File wahlbewerber,
			List<String[]> bewerberValues, List<String[]> wahlkreisValues,
			int wahljahr) {
		try {
			List<String> wahlbewerberLines = Files.readAllLines(wahlbewerber
					.toPath(), Charset.forName("UTF-8"));
			wahlbewerberLines.remove(0);
			int idx = (wahljahr == 2009) ? 8 : 9;
			String separator = (wahljahr == 2009) ? "," : ";";
			wahlkreisValues
					.stream()
					.forEach(
							kreis -> {
								Map<String, List<BewerberTuple>> direktKandidaten = new HashMap<String, List<BewerberTuple>>();
								wahlbewerberLines
										.stream()
										.filter(w -> w.split(separator)[idx - 2]
												.equals(kreis[0]))
										.collect(
												Collectors.groupingBy(w -> (w
														.split(separator)[idx - 3])))
										.forEach(
												(p, l) -> l
														.stream()
														.forEach(
																w -> {
																	String partei = p
																			.isEmpty() ? "�brige"
																			: p;
																	String[] s = w
																			.split(separator);
																	if (s.length < idx
																			|| !direktKandidaten
																					.containsKey(partei)
																			|| (direktKandidaten
																					.get(partei)
																					.stream()
																					.anyMatch(v -> v.value.length >= idx
																							&& Integer
																									.valueOf(v.value[idx]) < Integer
																									.valueOf(s[idx])))) {
																		String bewerberId = "";
																		try {
																			bewerberId = bewerberValues
																					.stream()
																					.filter(b -> {
																						if (wahljahr == 2009) {
																							return b[1]
																									.equals(s[1])
																									&& b[2].equals(s[2])
																									&& b[3].equals(s[3])
																									&& (b[4].equals(s[5]) || b[4]
																											.equals("#NULL#")
																											&& s[5].isEmpty());
																						}
																						return b[1]
																								.equals(s[2])
																								&& b[2].equals(s[4])
																								&& b[3].equals(s[3])
																								&& (b[4].equals(s[6]) || b[4]
																										.equals("#NULL#")
																										&& s[6].isEmpty());
																					})
																					.findFirst()
																					.get()[0];
																		} catch (Exception e) {
																			System.out
																					.println();
																		}
																		if (!direktKandidaten
																				.containsKey(partei)) {
																			direktKandidaten
																					.put(partei,
																							new ArrayList<BewerberTuple>());
																		}
																		if (!p.isEmpty()) {
																			direktKandidaten
																					.get(partei)
																					.clear();
																		}
																		direktKandidaten
																				.get(partei)
																				.add(new BewerberTuple(
																						bewerberId,
																						s));
																	}
																}));
								if (!this.direktKandidaten
										.containsKey(kreis[0])) {
									this.direktKandidaten
											.put(kreis[0],
													new HashMap<Integer, Map<String, List<BewerberTuple>>>());
								}
								this.direktKandidaten.get(kreis[0]).put(
										wahljahr, direktKandidaten);
							});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<String[]> generateForWahlkreis(int wahljahr, String header,
			String[] kreisErgebnis) {
		List<String[]> stimmzettelResult = new ArrayList<String[]>();
		try {
			List<String> headerList = Arrays.asList(header.split(","));
			Map<String, Integer[]> parteiStimmen = new HashMap<String, Integer[]>();
			headerList
					.subList(8, headerList.size())
					.stream()
					.map(p -> p.substring(0, p.lastIndexOf("_S")))
					.distinct()
					.forEach(
							partei -> {
								if (!parteiStimmen.containsKey(partei)) {
									Integer s1 = 0;
									if (headerList.contains(partei + "_S1")
											&& headerList.indexOf(partei
													+ "_S1") < kreisErgebnis.length) {
										s1 = Integer
												.valueOf(kreisErgebnis[headerList
														.indexOf(partei + "_S1")]
														.isEmpty() ? "0"
														: kreisErgebnis[headerList
																.indexOf(partei
																		+ "_S1")]);
									}
									Integer s2 = 0;
									if (headerList.contains(partei + "_S2")
											&& headerList.indexOf(partei
													+ "_S2") < kreisErgebnis.length) {
										s2 = Integer
												.valueOf(kreisErgebnis[headerList
														.indexOf(partei + "_S2")]
														.isEmpty() ? "0"
														: kreisErgebnis[headerList
																.indexOf(partei
																		+ "_S2")]);
									}
									Integer[] values = new Integer[] { s1, s2 };
									parteiStimmen.put(partei, values);
								}
							});

			String bundesland = wahlkreisValues.stream()
					.filter(w -> w[0].equals(kreisErgebnis[0])).findFirst()
					.get()[3];

			int anzahlStimmzettel = Integer.valueOf(kreisErgebnis[3]);

			int[] sumNotVotedStimmen = new int[2];
			sumNotVotedStimmen[0] = (int) parteiStimmen.values().stream()
					.collect(Collectors.summarizingInt(s -> s[0])).getSum();
			sumNotVotedStimmen[1] = (int) parteiStimmen.values().stream()
					.collect(Collectors.summarizingInt(s -> s[1])).getSum();
			List<String> parteien = new ArrayList<String>(
					parteiStimmen.keySet());

			int pidx = 0, pdidx = 0, plidx = 0;
			while (sumNotVotedStimmen[0] > 0 || sumNotVotedStimmen[1] > 0) {
				String parteiDirekt = parteien.get(pdidx), parteiListe = parteien
						.get(plidx);
				while (parteiStimmen.get(parteiDirekt)[0] == 0) {
					parteiDirekt = parteien.get(pdidx = (pdidx + 1)
							% parteien.size());
					if (pdidx == pidx) {
						break;
					}
				}
				while (parteiStimmen.get(parteiListe)[1] == 0) {
					parteiListe = parteien.get(plidx = (plidx + 1)
							% parteien.size());
					if (plidx == pidx) {
						break;
					}
				}
				if (plidx > pidx && pdidx > pidx) {
					pidx = (pidx + 1) % parteien.size();
				}
				String parteiL = parteiListe;
				String direktId = "#NULL#";

				if (parteiStimmen.get(parteiDirekt)[0] > 0) {
					try {
						direktId = direktKandidaten.get(kreisErgebnis[0])
								.get(wahljahr).get(parteiDirekt).stream()
								.findAny().get().key;
					} catch (Exception e) {
						System.out.println();
					}
					parteiStimmen.get(parteiDirekt)[0]--;
					sumNotVotedStimmen[0]--;
				}
				String landeslisteId = "#NULL#";

				if (parteiStimmen.get(parteiListe)[1] > 0) {
					landeslisteId = landeslisteValues
							.stream()
							.filter(l -> l[1].equals(String.valueOf(wahljahr))
									&& l[2].equals(parteiL)
									&& l[3].equals(bundesland)).findFirst()
							.get()[0];
					sumNotVotedStimmen[1]--;
					parteiStimmen.get(parteiListe)[1]--;
				}

				stimmzettelResult.add(new String[] {
						String.valueOf(stimmzettelCount++),
						String.valueOf(wahljahr), direktId, landeslisteId,
						kreisErgebnis[0] });
			}

			int valid = Math.max(Integer.valueOf(kreisErgebnis[6]),
					Integer.valueOf(kreisErgebnis[7]));
			for (int count = 0; count < anzahlStimmzettel - valid; count++) {
				stimmzettelResult.add(new String[] {
						String.valueOf(stimmzettelCount++),
						String.valueOf(wahljahr), "#NULL#", "#NULL#",
						kreisErgebnis[0] });
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return stimmzettelResult;
	}

	private static class BewerberTuple {
		public String key;
		public String[] value;

		public BewerberTuple(String key, String[] value) {
			this.key = key;
			this.value = value;
		}
	}
}
