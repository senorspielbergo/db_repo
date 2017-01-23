package entities;

import database.DatabaseAttribute;
import database.DatabaseEntity;

public class Bundesland extends DatabaseEntity {

	private DatabaseAttribute name;

	public Bundesland() {
		super();
	}

	public Bundesland(String name) {
		super();
		this.name.setValue(name);
	}

	public String getName() {
		return (String) name.getValue();
	}

	@Override
	public void initDatabaseAttributes() {
		name = new DatabaseAttribute(this, "name", "varchar(30)").setNullable(
				false).setPrimary(true);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Bundesland
				&& ((Bundesland) o).getName().equals(getName());
	}
}
