package csv;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

public class CsvParser {

	public static ParsedCsvFile parse(File file, String separator)
			throws IOException {
		List<String> wahlbewerberLines = Files.readAllLines(file.toPath(),
				Charset.forName("UTF-8"));
		String[] columnNames = wahlbewerberLines.get(0).split(separator);
		ParsedCsvFile result = new ParsedCsvFile(wahlbewerberLines.size(),
				columnNames.length);
		for (String line : wahlbewerberLines) {
			result.addRow(line.split(separator));
		}
		return result;
	}
}
