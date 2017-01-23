package entities;

import database.DatabaseAttribute;
import database.DatabaseEntity;

public class Partei extends DatabaseEntity {

	private DatabaseAttribute name;

	public Partei() {
		super();
	}

	public Partei(String name) {
		super();
		this.name.setValue(name);
	}

	public String getName() {
		return (String) name.getValue();
	}

	@Override
	public void initDatabaseAttributes() {
		name = new DatabaseAttribute(this, "name", "varchar(40)").setNullable(
				false).setPrimary(true);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Partei && ((Partei) o).getName().equals(getName());
	}
}
