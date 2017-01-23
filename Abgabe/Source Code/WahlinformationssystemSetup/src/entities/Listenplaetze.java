package entities;

import database.DatabaseAttribute;
import database.DatabaseEntity;

public class Listenplaetze extends DatabaseEntity {

	private DatabaseAttribute liste;
	private DatabaseAttribute bewerber;
	private DatabaseAttribute listenplatz;

	public Listenplaetze() {
		super();
	}

	public Listenplaetze(Landesliste liste, Bewerber bewerber, int listenplatz) {
		super();
		this.liste.setValue(liste);
		this.bewerber.setValue(bewerber);
		this.listenplatz.setValue(listenplatz);
	}

	public Landesliste getLandesliste() {
		return (Landesliste) liste.getValue();
	}

	public Bewerber getBewerber() {
		return (Bewerber) bewerber.getValue();
	}

	public int getListenplatz() {
		return (int) listenplatz.getValue();
	}

	@Override
	public void initDatabaseAttributes() {
		liste = new DatabaseAttribute(this, "listen_id", "int")
				.setNullable(false).setIsForeignKey(Landesliste.class, "id")
				.setPrimary(true);
		bewerber = new DatabaseAttribute(this, "bewerber_id", "int")
				.setNullable(false).setIsForeignKey(Bewerber.class, "id")
				.setPrimary(true);
		listenplatz = new DatabaseAttribute(this, "listenplatz", "int")
				.setNullable(false);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Listenplaetze
				&& ((Listenplaetze) o).getLandesliste()
						.equals(getLandesliste())
				&& ((Listenplaetze) o).getBewerber().equals(getBewerber())
				&& ((Listenplaetze) o).getListenplatz() == getListenplatz();
	}
}
