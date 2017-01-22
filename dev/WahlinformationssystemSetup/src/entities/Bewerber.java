package entities;

import database.DatabaseAttribute;
import database.DatabaseEntity;

public class Bewerber extends DatabaseEntity {

	private DatabaseAttribute id;
	private DatabaseAttribute titel;
	private DatabaseAttribute vorname;
	private DatabaseAttribute nachname;
	private DatabaseAttribute partei;
	private DatabaseAttribute jahrgang;

	public Bewerber() {
		super();
	}

	public Bewerber(int id, String titel, String vorname, String nachname,
			int jahrgang, Partei partei) {
		super();
		this.id.setValue(id);
		this.titel.setValue(titel);
		this.vorname.setValue(vorname);
		this.nachname.setValue(nachname);
		this.partei.setValue(partei);
		this.jahrgang.setValue(jahrgang);
	}

	public String getTitel() {
		return (String) titel.getValue();
	}

	public String getVorname() {
		return (String) vorname.getValue();
	}

	public String getNachname() {
		return (String) nachname.getValue();
	}

	public Partei getPartei() {
		return (Partei) partei.getValue();
	}

	public int getJahrgang() {
		return (int) jahrgang.getValue();
	}

	public int getId() {
		return (int) id.getValue();
	}

	@Override
	public void initDatabaseAttributes() {
		this.id = new DatabaseAttribute(this, "id", "int").setNullable(false)
				.setPrimary(true);
		this.titel = new DatabaseAttribute(this, "titel", "varchar(30)");
		this.vorname = new DatabaseAttribute(this, "vorname", "varchar(50)")
				.setNullable(false);
		this.nachname = new DatabaseAttribute(this, "nachname", "varchar(60)")
				.setNullable(false);
		this.partei = new DatabaseAttribute(this, "partei", "varchar(40)")
				.setNullable(true).setIsForeignKey(Partei.class, "name");
		this.jahrgang = new DatabaseAttribute(this, "jahrgang", "int")
				.setNullable(false);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Bewerber
				&& ((Bewerber) o).getVorname().equals(getVorname())
				&& ((Bewerber) o).getTitel().equals(getTitel())
				&& ((Bewerber) o).getNachname().equals(getNachname())
				&& ((Bewerber) o).getJahrgang() == getJahrgang()
				&& (((Bewerber) o).getPartei() == null && getPartei() == null || (((Bewerber) o)
						.getPartei() != null && getPartei() != null && ((Bewerber) o)
						.getPartei().equals(getPartei())));
	}
}
