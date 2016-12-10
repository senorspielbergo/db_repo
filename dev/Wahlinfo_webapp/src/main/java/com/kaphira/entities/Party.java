package com.kaphira.entities;

import java.util.List;

/**
 *
 * @author theralph
 */
public class Party {
    
    private String name;
    private int seats;
    private double percentage;
    private int totalVotes;

    private List<Decision> closestDecisions;
    
    public Party(String name){
        this.name = name;
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
