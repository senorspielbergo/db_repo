package entities;

import util.Wahljahr;
import database.DatabaseAttribute;
import database.DatabaseEntity;

public class Stimmzettel extends DatabaseEntity {

	private DatabaseAttribute id;
	private DatabaseAttribute wahljahr;
	private DatabaseAttribute direktkandidat;
	private DatabaseAttribute landesliste;
	private DatabaseAttribute wahlkreis;

	public Stimmzettel() {
		super();
	}

	public Stimmzettel(Wahljahr wahljahr, Bewerber direktkandidat,
			Landesliste landesliste, Wahlkreis wahlkreis) {
		super();
		this.wahljahr.setValue(wahljahr.toInt());
		this.direktkandidat.setValue(direktkandidat);
		this.landesliste.setValue(landesliste);
		this.wahlkreis.setValue(wahlkreis);
	}

	public Wahljahr getWahljahr() {
		return Wahljahr.valueOf((int) wahljahr.getValue());
	}

	public Bewerber getDirektkandidat() {
		return (Bewerber) direktkandidat.getValue();
	}

	public Landesliste getLandesliste() {
		return (Landesliste) landesliste.getValue();
	}

	public Wahlkreis getWahlkreis() {
		return (Wahlkreis) wahlkreis.getValue();
	}

	public int getId() {
		return (int) id.getValue();
	}

	@Override
	public void initDatabaseAttributes() {
		this.id = new DatabaseAttribute(this, "id", "serial unique")
				.setNullable(false).setPrimary(true);
		wahljahr = new DatabaseAttribute(this, "wahljahr", "int")
				.setNullable(false);
		direktkandidat = new DatabaseAttribute(this, "direktkandidat", "int")
				.setNullable(true).setIsForeignKey(Bewerber.class, "id");
		landesliste = new DatabaseAttribute(this, "landesliste", "int")
				.setNullable(true).setIsForeignKey(Landesliste.class, "id");
		wahlkreis = new DatabaseAttribute(this, "wahlkreis", "int")
				.setNullable(false).setIsForeignKey(Wahlkreis.class, "nummer");
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Stimmzettel
				&& ((Stimmzettel) o).getId() == getId()
				&& ((Stimmzettel) o).getWahljahr().equals(getWahljahr())
				&& ((Stimmzettel) o).getLandesliste().equals(getLandesliste())
				&& ((Stimmzettel) o).getDirektkandidat().equals(
						getDirektkandidat())
				&& ((Stimmzettel) o).getWahlkreis().equals(getWahlkreis());
	}
}
