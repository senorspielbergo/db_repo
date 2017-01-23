package entities;

import database.DatabaseAttribute;
import database.DatabaseEntity;

public class Wahlkreis extends DatabaseEntity {

	private DatabaseAttribute nummer;
	private DatabaseAttribute name;
	private DatabaseAttribute bundesland;

	public Wahlkreis() {
		super();
	}

	public Wahlkreis(int nummer, String name, Bundesland bundesland) {
		super();
		this.nummer.setValue(nummer);
		this.name.setValue(name);
		this.bundesland.setValue(bundesland);
	}

	public int getNummer() {
		return (int) nummer.getValue();
	}

	public String getName() {
		return (String) name.getValue();
	}

	public Bundesland getBundesland() {
		return (Bundesland) bundesland.getValue();
	}

	@Override
	public void initDatabaseAttributes() {
		nummer = new DatabaseAttribute(this, "nummer", "int")
				.setNullable(false).setPrimary(true);
		name = new DatabaseAttribute(this, "name", "varchar(100)")
				.setNullable(false);
		bundesland = new DatabaseAttribute(this, "bundesland", "varchar(30)")
				.setNullable(false).setIsForeignKey(Bundesland.class, "name");
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Wahlkreis
				&& ((Wahlkreis) o).getNummer() == getNummer()
				&& ((Wahlkreis) o).getName().equals(getName())
				&& ((Wahlkreis) o).getBundesland().equals(getBundesland());
	}
}
