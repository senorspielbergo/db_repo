package database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class DatabaseEntity {

	public static final class Any extends DatabaseEntity {
		@Override
		public void initDatabaseAttributes() {
		}
	}

	private List<DatabaseAttribute> attributes;

	public DatabaseEntity() {
		this.attributes = new ArrayList<DatabaseAttribute>();
		initDatabaseAttributes();
	}

	public void addAttribute(DatabaseAttribute attribute) {
		if (attributes.contains(attribute)) {
			throw new RuntimeException();
		}
		attributes.add(attribute);
	}

	public DatabaseAttribute removeAttribute(String name) {
		for (int idx = 0; idx < attributes.size(); idx++) {
			if (attributes.get(idx).getName().equals(name)) {
				return attributes.remove(idx);
			}
		}
		return null;
	}

	public DatabaseAttribute getAttribute(String name) {
		for (DatabaseAttribute attribute : attributes) {
			if (attribute.getName().equals(name)) {
				return attribute;
			}
		}
		return null;
	}

	public List<DatabaseAttribute> getPrimaryKey() {
		List<DatabaseAttribute> result = new ArrayList<DatabaseAttribute>();
		for (DatabaseAttribute attribute : attributes) {
			if (attribute.isPrimary()) {
				result.add(attribute);
			}
		}
		return result;
	}

	public List<DatabaseAttribute> getAttributes() {
		return new ArrayList<DatabaseAttribute>(attributes);
	}

	public final void commit() {
		try {
			PostgreSQLDatabase.getCurrent().insertEntities(this.getClass(),
					this);
		} catch (SQLException e) {
			throw new RuntimeException("Couldn't persist entity instance!");
		}
	}

	protected String toSqlValues() {
		StringBuilder builder = new StringBuilder("(");
		for (int idx = 0; idx < attributes.size();) {
			DatabaseAttribute attribute = attributes.get(idx);

			Object value = attribute.getValue();

			if (value != null && attribute.getIsForeignKey().second != null) {
				value = ((DatabaseEntity) value).getAttribute(
						attribute.getIsForeignKey().second).getValue();
			}

			if (value == null) {
				if (attribute.isPrimary()
						&& attribute.getType().toLowerCase().contains("serial")) {
					value = "default";
				} else if (!attribute.isNullable()) {
					throw new RuntimeException(
							"Entity attribute not null constraint violated!");
				}
				builder.append(String.valueOf(value));
			} else {
				builder.append("'" + String.valueOf(value) + "'");
			}

			if (++idx < attributes.size()) {
				builder.append(", ");
			}
		}

		builder.append(")");
		return builder.toString();
	}

	public abstract void initDatabaseAttributes();
}
