package com.kaphira.entities;

/**
 *
 * @author theralph
 */
public class Party {
    
    private String name;
    private int seats;

    public Party(String name, int percentage){
        this.name = name;
        this.seats = percentage;
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

    public void setPercentage(int percentage) {
        this.seats = percentage;
    }
    
    
}
