package entities;

import util.Wahljahr;
import database.DatabaseAttribute;
import database.DatabaseEntity;

public class Wahlberechtigte extends DatabaseEntity {

	private DatabaseAttribute id;
	private DatabaseAttribute wahlkreis;
	private DatabaseAttribute wahljahr;
	private DatabaseAttribute wahlberechtigte;

	public Wahlberechtigte() {
		super();
	}

	public Wahlberechtigte(Wahlkreis wahlkreis, Wahljahr wahljahr,
			int wahlberechtigte) {
		super();
		this.wahlkreis.setValue(wahlkreis);
		this.wahljahr.setValue(wahljahr.toInt());
		this.wahlberechtigte.setValue(wahlberechtigte);
	}

	public Wahlkreis getWahlkreis() {
		return (Wahlkreis) wahlkreis.getValue();
	}

	public Wahljahr getWahljahr() {
		return Wahljahr.valueOf((int) wahljahr.getValue());
	}

	public int getWahlberechtigte() {
		return (int) wahlberechtigte.getValue();
	}

	public int getId() {
		return id.getValue() == null ? -1 : (int) id.getValue();
	}

	@Override
	public void initDatabaseAttributes() {
		this.id = new DatabaseAttribute(this, "id", "serial unique")
				.setNullable(false).setPrimary(true);
		wahlkreis = new DatabaseAttribute(this, "wahlkreis", "int")
				.setNullable(false).setIsForeignKey(Wahlkreis.class, "nummer");
		wahljahr = new DatabaseAttribute(this, "wahljahr", "int")
				.setNullable(false);
		wahlberechtigte = new DatabaseAttribute(this, "wahlberechtigte", "int")
				.setNullable(false);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Wahlberechtigte
				&& ((Wahlberechtigte) o).getId() == getId()
				&& ((Wahlberechtigte) o).getWahljahr().equals(getWahljahr())
				&& ((Wahlberechtigte) o).getWahlkreis().equals(getWahlkreis())
				&& ((Wahlberechtigte) o).getWahlberechtigte() == getWahlberechtigte();
	}
}
