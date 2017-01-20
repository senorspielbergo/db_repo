package kaphira.wahlinfo.entities;

/**
 * This entity represents a history entry in with the years 2013 and 2009 are compared to each other
 * @author theralph
 */
public class History {
    
    private String party;
    private int diffFirstVote;
    private int diffSecondVote;
    
    private double diffFirstVotePc;
    private double diffSecondVotePc;

    public History(String party, int diffFirstVote, int diffSecondVote, double diffFirstVotePc, double diffSecondVotePc) {
        this.party = party;
        this.diffFirstVote = diffFirstVote;
        this.diffSecondVote = diffSecondVote;
        this.diffFirstVotePc = diffFirstVotePc;
        this.diffSecondVotePc = diffSecondVotePc;
    }

    
    
    
    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public int getDiffFirstVote() {
        return diffFirstVote;
    }

    public void setDiffFirstVote(int diffFirstVote) {
        this.diffFirstVote = diffFirstVote;
    }

    public int getDiffSecondVote() {
        return diffSecondVote;
    }

    public void setDiffSecondVote(int diffSecondVote) {
        this.diffSecondVote = diffSecondVote;
    }

    public double getDiffFirstVotePc() {
        return diffFirstVotePc;
    }

    public void setDiffFirstVotePc(double diffFirstVotePc) {
        this.diffFirstVotePc = diffFirstVotePc;
    }

    public double getDiffSecondVotePc() {
        return diffSecondVotePc;
    }

    public void setDiffSecondVotePc(double diffSecondVotePc) {
        this.diffSecondVotePc = diffSecondVotePc;
    }


    
}
