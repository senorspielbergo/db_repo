package csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {

	public static ParsedCsvFile parse(InputStream stream, String separator)
			throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				stream, Charset.forName("UTF-8")));
		String line = null;
		List<String> wahlbewerberLines = new ArrayList<String>();
		while ((line = reader.readLine()) != null) {
			wahlbewerberLines.add(line);
		}
		String[] columnNames = wahlbewerberLines.get(0).split(separator);
		ParsedCsvFile result = new ParsedCsvFile(wahlbewerberLines.size(),
				columnNames.length);
		for (String l : wahlbewerberLines) {
			result.addRow(l.split(separator));
		}
		return result;
	}
}
