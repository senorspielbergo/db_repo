package csv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParsedCsvFile {

	private List<Row> rows;
	private int numRows, numCols;

	protected ParsedCsvFile(int rows, int cols) {
		this.numRows = rows;
		this.numCols = cols;
		this.rows = new ArrayList<ParsedCsvFile.Row>(rows);
	}

	protected void addRow(String... values) {
		if (values == null || rows.size() == numRows) {
			throw new RuntimeException("Invalid arguments!");
		}
		rows.add(new Row(numCols, values));
	}

	public Row getRow(int idx) {
		return rows.get(idx);
	}

	public int getRowCount() {
		return rows.size();
	}

	public int getColumnCount() {
		return numCols;
	}

	public class Row {
		private String[] row;

		protected Row(int length, String... values) {
			this.row = Arrays.copyOf(values, length);
			if (values.length < length) {
				Arrays.fill(this.row, values.length, length, "");
			}
		}

		public String get(int idx) {
			return row[idx];
		}

		public int length() {
			return numCols;
		}
	}
}
