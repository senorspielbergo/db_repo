package kaphira.wahlinfo.entities;

import java.io.Serializable;

/**
 * This entity represents a politician with its information used throughout the application
 * @author theralph
 */
public class Politician implements Serializable{
    
    private String title;
    private String name;
    private String firstName;
    private String lastName;
    private String party;
    private int votes;
    private String formattedName;

    public Politician(String title,String firstName, String lastName, String party) {
        this.firstName = firstName; 
        this.lastName = lastName; 
        this.party = party;
        this.title = title;
        this.votes = 0;
        this.formattedName = title + " " + firstName + " " + lastName;
    }
    
    public Politician(){
        
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return lastName;
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

    public String getFormattedName() {
        return formattedName;
    }

    public void setFormattedName(String formattedName) {
        this.formattedName = formattedName;
    }
    
}
