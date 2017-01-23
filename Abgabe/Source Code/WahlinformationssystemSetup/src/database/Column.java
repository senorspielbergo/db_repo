package database;

import util.Pair;

public class Column implements ISqlParseable {

	private String type;
	private String name;
	private boolean nullable;
	private boolean primary;
	private Pair<String, String> foreignKey;

	public Column(String name, String type) {
		this.name = name;
		this.type = type;
		this.nullable = true;
		this.primary = false;
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public Column setPrimary(boolean primary) {
		this.primary = primary;
		return this;
	}

	public Column setNullable(boolean nullable) {
		this.nullable = nullable;
		return this;
	}

	public Column setIsForeignKey(String table, String column) {
		if (table != null && column != null) {
			this.foreignKey = new Pair<String, String>(table, column);
		} else {
			this.foreignKey = null;
		}
		return this;
	}

	public boolean isForeignKey() {
		return foreignKey != null;
	}

	public boolean isPrimary() {
		return primary;
	}

	public boolean isNullable() {
		return nullable;
	}

	public StringBuilder getSqlDescription() {
		StringBuilder builder = new StringBuilder(getName()).append(" ")
				.append(getType());
		if (foreignKey != null) {
			builder.append(" references " + foreignKey.first + "("
					+ foreignKey.second + ")");
		} else if (!nullable && !primary) {
			builder.append(" not null");
		}
		return builder;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Column && ((Column) o).getName().equals(getName())
				&& ((Column) o).getType().equals(getType());
	}
}
