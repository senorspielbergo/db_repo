package entities;

import util.Wahljahr;
import database.DatabaseAttribute;
import database.DatabaseEntity;

public class Landesliste extends DatabaseEntity {

	private DatabaseAttribute id;
	private DatabaseAttribute wahljahr;
	private DatabaseAttribute partei;
	private DatabaseAttribute bundesland;

	public Landesliste() {
		super();
	}

	public Landesliste(int id, Wahljahr wahljahr, Partei partei,
			Bundesland bundesland) {
		super();
		this.id.setValue(id);
		this.wahljahr.setValue(wahljahr.toInt());
		this.partei.setValue(partei);
		this.bundesland.setValue(bundesland);
	}

	public Wahljahr getWahljahr() {
		return Wahljahr.valueOf((int) wahljahr.getValue());
	}

	public Partei getPartei() {
		return (Partei) partei.getValue();
	}

	public Bundesland getBundesland() {
		return (Bundesland) bundesland.getValue();
	}

	public int getId() {
		return (int) id.getValue();
	}

	@Override
	public void initDatabaseAttributes() {
		this.id = new DatabaseAttribute(this, "id", "int").setNullable(false)
				.setPrimary(true);
		wahljahr = new DatabaseAttribute(this, "wahljahr", "int");
		partei = new DatabaseAttribute(this, "partei", "varchar(40)")
				.setNullable(false).setIsForeignKey(Partei.class, "name");
		bundesland = new DatabaseAttribute(this, "bundesland", "varchar(30)")
				.setNullable(false).setIsForeignKey(Bundesland.class, "name");
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Landesliste
				&& ((Landesliste) o).getWahljahr().equals(getWahljahr())
				&& ((Landesliste) o).getPartei().equals(getPartei())
				&& ((Landesliste) o).getBundesland().equals(getBundesland());
	}
}
