package kaphira.wahlinfo.entities;

/**
 * This entity represents a so called "Ueberhangmandat"
 * @author theralph
 */
public class Mandat {
    
    private String bundesland;
    private String partei;
    private int ueberhang;
    private int wahljahr;

    public Mandat(String bundesland, String partei, int ueberhang, int wahljahr) {
        this.bundesland = bundesland;
        this.partei = partei;
        this.ueberhang = ueberhang;
        this.wahljahr = wahljahr;
    }
    
    public String getBundesland() {
        return bundesland;
    }

    public void setBundesland(String bundesland) {
        this.bundesland = bundesland;
    }

    public String getPartei() {
        return partei;
    }

    public void setPartei(String partei) {
        this.partei = partei;
    }

    public int getUeberhang() {
        return ueberhang;
    }

    public void setUeberhang(int ueberhang) {
        this.ueberhang = ueberhang;
    }

    public int getWahljahr() {
        return wahljahr;
    }

    public void setWahljahr(int wahljahr) {
        this.wahljahr = wahljahr;
    }
    
    
    
}
