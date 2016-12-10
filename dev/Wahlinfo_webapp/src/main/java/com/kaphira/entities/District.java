package com.kaphira.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author theralph
 */
public class District implements Serializable {
    
    private int id;
    private String bundesland;
    private String name; 
    private double wahlbeteiligung;
    private int wahlberechtigte;
    private Politician direktKandidat;
    private List<Party> parties;

    private boolean isLoaded = false;
    
    public District(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBundesland() {
        return bundesland;
    }

    public void setBundesland(String bundesland) {
        this.bundesland = bundesland;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWahlbeteiligung() {
        return wahlbeteiligung;
    }

    public void setWahlbeteiligung(double wahlbeteiligung) {
        this.wahlbeteiligung = wahlbeteiligung;
    }

    public int getWahlberechtigte() {
        return wahlberechtigte;
    }

    public void setWahlberechtigte(int wahlberechtigte) {
        this.wahlberechtigte = wahlberechtigte;
    }

    public Politician getDirektKandidat() {
        return direktKandidat;
    }

    public void setDirektKandidat(Politician direktKandidat) {
        this.direktKandidat = direktKandidat;
    }

    public List<Party> getParties() {
        return parties;
    }

    public void setParties(List<Party> parties) {
        this.parties = parties;
    }
    
    

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setIsLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final District other = (District) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
    
    
}
