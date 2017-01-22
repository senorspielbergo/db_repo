package entities;

import util.Wahljahr;
import database.DatabaseAttribute;
import database.DatabaseEntity;

public class Direktkandidat extends DatabaseEntity {

	private DatabaseAttribute bewerber;
	private DatabaseAttribute wahlkreis;
	private DatabaseAttribute wahljahr;
	private DatabaseAttribute partei;

	public Direktkandidat() {
		super();
	}
	
	public Direktkandidat(Bewerber bewerber, Wahlkreis wahlkreis,
			Wahljahr wahljahr, Partei partei) {
		this.bewerber.setValue(bewerber);
		this.wahljahr.setValue(wahljahr.toInt());
		this.wahlkreis.setValue(wahlkreis);
		this.partei.setValue(partei);
	}

	@Override
	public void initDatabaseAttributes() {
		this.wahljahr = new DatabaseAttribute(this, "wahljahr", "int")
				.setNullable(false).setPrimary(true);
		this.wahlkreis = new DatabaseAttribute(this, "wahlkreis", "int")
				.setNullable(false).setIsForeignKey(Wahlkreis.class, "nummer").setPrimary(true);
		this.bewerber = new DatabaseAttribute(this, "bewerber", "int")
				.setNullable(false).setIsForeignKey(Bewerber.class, "id");
		this.partei = new DatabaseAttribute(this, "partei", "varchar(40)")
				.setNullable(false).setIsForeignKey(Partei.class, "name").setPrimary(true);
	}

	public Wahljahr getWahljahr() {
		return Wahljahr.valueOf((int) wahljahr.getValue());
	}

	public Wahlkreis getWahlkreis() {
		return (Wahlkreis) wahlkreis.getValue();
	}

	public Bewerber getBewerber() {
		return (Bewerber) bewerber.getValue();
	}

	public Partei getPartei() {
		return (Partei) partei.getValue();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Direktkandidat
				&& ((Direktkandidat) o).getWahljahr().equals(getWahljahr())
				&& ((Direktkandidat) o).getWahlkreis().equals(getWahlkreis())
				&& ((Direktkandidat) o).getPartei().equals(getPartei());
	}
}
