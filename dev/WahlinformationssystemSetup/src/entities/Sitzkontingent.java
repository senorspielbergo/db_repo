package entities;

import util.Wahljahr;
import database.DatabaseAttribute;
import database.DatabaseEntity;

public class Sitzkontingent extends DatabaseEntity {

	private DatabaseAttribute id;
	private DatabaseAttribute bundesland;
	private DatabaseAttribute wahljahr;
	private DatabaseAttribute kontingent;

	public Sitzkontingent() {
		super();
	}

	public Sitzkontingent(Bundesland bundesland, Wahljahr wahljahr,
			int kontingent) {
		super();
		this.bundesland.setValue(bundesland);
		this.wahljahr.setValue(wahljahr.toInt());
		this.kontingent.setValue(kontingent);
	}

	public Bundesland getBundesland() {
		return (Bundesland) bundesland.getValue();
	}

	public Wahljahr getWahljahr() {
		return Wahljahr.valueOf((int) wahljahr.getValue());
	}

	public int getKontigent() {
		return (int) kontingent.getValue();
	}

	public int getId() {
		return id.getValue() == null ? -1 : (int) id.getValue();
	}

	@Override
	public void initDatabaseAttributes() {
		this.id = new DatabaseAttribute(this, "id", "serial unique")
				.setNullable(false).setPrimary(true);
		bundesland = new DatabaseAttribute(this, "bundesland", "varchar(30)")
				.setNullable(false).setIsForeignKey(Bundesland.class, "name");
		wahljahr = new DatabaseAttribute(this, "wahljahr", "int")
				.setNullable(false);
		kontingent = new DatabaseAttribute(this, "kontingent", "int")
				.setNullable(false);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Sitzkontingent
				&& ((Sitzkontingent) o).getId() == getId()
				&& ((Sitzkontingent) o).getWahljahr().equals(getWahljahr())
				&& ((Sitzkontingent) o).getKontigent() == getKontigent()
				&& ((Sitzkontingent) o).getBundesland().equals(getBundesland());
	}
}
