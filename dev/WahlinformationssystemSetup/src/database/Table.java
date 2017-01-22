package database;

import java.util.ArrayList;
import java.util.List;

public class Table implements ISqlParseable {

	private String name;
	private List<Column> columns;

	public Table(String name, Column... columns) {
		this.name = name;
		this.columns = new ArrayList<Column>();
		for (Column column : columns) {
			this.columns.add(column);
		}
	}

	public String getName() {
		return name;
	}

	public List<Column> getColumns() {
		return new ArrayList<Column>(columns);
	}

	public List<Column> getPrimaryKey() {
		List<Column> result = new ArrayList<Column>();
		for (Column column : getColumns()) {
			if (column.isPrimary()) {
				result.add(column);
			}
		}
		return result;
	}

	public StringBuilder getSqlDescription() {
		StringBuilder builder = new StringBuilder(getName() + " (");

		List<Column> columns = getColumns();
		List<Column> primary = getPrimaryKey();

		for (int idx = 0; idx < columns.size();) {
			Column column = columns.get(idx);
			builder.append(column.getSqlDescription());
			if (primary.contains(column) && primary.size() == 1) {
				builder.append(" ").append("primary key");
			}
			if (++idx < columns.size() || primary.size() != 1) {
				builder.append(", ");
			}
		}

		if (primary.size() > 1) {
			builder.append("primary key (");
			for (int idx = 0; idx < primary.size();) {
				builder.append(primary.get(idx).getName());
				builder.append(++idx < primary.size() ? ", " : ")");
			}
		}

		return builder.append(");");
	}
}
