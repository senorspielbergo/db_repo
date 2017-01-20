package kaphira.wahlinfo.entities;

/**
 * This entity represents a close decision displayed in the Q6 query
 * @author theralph
 */
public class Decision {
    
    private String district;
    private String candidate;
    private int difference;

    public Decision(String district, String candidate, int difference) {
        this.district = district;
        this.candidate = candidate;
        this.difference = difference;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public int getDifference() {
        return difference;
    }

    public void setDifference(int difference) {
        this.difference = difference;
    }
    
    
}
