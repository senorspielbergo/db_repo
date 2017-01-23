package util;

public class BundeslandUtil {

	private static final String[] BUNDESLAENDER = new String[] {
			"Baden-W\u00fcrttemberg", "Bayern", "Berlin", "Brandenburg",
			"Bremen", "Hamburg", "Hessen", "Mecklenburg-Vorpommern",
			"Niedersachsen", "Nordrhein-Westfalen", "Rheinland-Pfalz",
			"Saarland", "Sachsen", "Sachsen-Anhalt", "Schleswig-Holstein",
			"Th\u00fcringen" };

	public static String[] getBundeslandNames() {
		return BUNDESLAENDER;
	}

	public static String convertAbkuerzungToName(String abkuerzung) {
		switch (abkuerzung) {
		case "BB":
			return "Brandenburg";
		case "BE":
			return "Berlin";
		case "BW":
			return "Baden-W\u00fcrttemberg";
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
			return "Th\u00fcringen";
		}
		throw new RuntimeException("No valid Bundesland shortkey!");
	}

}
