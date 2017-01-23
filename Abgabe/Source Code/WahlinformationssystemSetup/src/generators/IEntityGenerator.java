package generators;

import csv.ParsedCsvFile;

public interface IEntityGenerator {

	IEntityGenerator generateFrom(ParsedCsvFile file);
	void commitCollections();
}
