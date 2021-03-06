package kaphira.wahlinfo.entities;

import java.io.Serializable;
import java.util.List;

/**
 * This entity represents a party with all the information used in Q1, Q2 and Q4 queries
 * @author theralph
 */
public class Party implements Serializable{
    
    private String name;
    private int seats;
    private double percentage;
    private int totalVotes;

    private List<Decision> closestDecisions;
    
    public Party(String name){
        this.name = name;
    }
    
    public Party(){
        
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }

    public List<Decision> getClosestDecisions() {
        return closestDecisions;
    }

    public void setClosestDecisions(List<Decision> closestDecisions) {
        this.closestDecisions = closestDecisions;
    }
    
    
    
}
