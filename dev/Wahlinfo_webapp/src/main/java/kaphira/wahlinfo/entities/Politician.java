package kaphira.wahlinfo.entities;

/**
 *
 * @author theralph
 */
public class Politician {
    
    private String title;
    private String name;
    private String firstName;
    private String lastName;
    private String party;
    private int votes;

    public Politician(String name, String party) {
        this.name = name; 
        this.party = party;
        this.title = "";
        this.votes = 0;
    }
    
    public Politician(String firstName, String lastName, String party) {
        this.firstName = firstName; 
        this.lastName = lastName; 
        this.party = party;
        this.title = "";
        this.votes = 0;
    }
    
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    
}
