package database;

import util.Pair;

public class DatabaseAttribute {
	private String type;
	private String name;
	private DatabaseEntity entity;
	private boolean primary;
	private boolean nullable;
	private Pair<Class<? extends DatabaseEntity>, String> foreignKey;
	private Object value;

	public DatabaseAttribute(DatabaseEntity entity, String name, String type) {
		this.name = name;
		this.type = type;
		this.entity = entity;
		setIsForeignKey(null, null);
		if (entity != null) {
			entity.addAttribute(this);
		}
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public boolean isPrimary() {
		return primary;
	}

	public DatabaseAttribute setIsForeignKey(Class<? extends DatabaseEntity> c,
			String attr) {
		if (c != null && attr != null) {
			foreignKey = new Pair<Class<? extends DatabaseEntity>, String>(c,
					attr);
		} else {
			foreignKey = new Pair<Class<? extends DatabaseEntity>, String>(
					null, null);
		}
		return this;
	}

	public Pair<Class<? extends DatabaseEntity>, String> getIsForeignKey() {
		return foreignKey;
	}

	public DatabaseAttribute setNullable(boolean nullable) {
		this.nullable = nullable;
		return this;
	}

	public boolean isNullable() {
		return nullable;
	}

	public DatabaseAttribute setPrimary(boolean primary) {
		this.primary = primary;
		return this;
	}

	public DatabaseEntity getEntity() {
		return entity;
	}

	public Column toColumn() {
		Column column = new Column(getName(), getType()).setNullable(
				isNullable()).setPrimary(isPrimary());
		if (getIsForeignKey().second != null) {
			column.setIsForeignKey(getIsForeignKey().first.getSimpleName()
					.toLowerCase(), getIsForeignKey().second);
		}
		return column;
	}
}
