package com.example.frederic.genericapp.data.get;

/**
 * Class to store information about the time a customer spent in the restaurant, and its associated cost
 * Should not be used, if the restaurant does not have time-based charges
 * Created by: Frederick Bernkastel
 */
public class DurationResponse extends FetchedObject {
    public final int hours;
    public final int minutes;
    public final double price;
    public DurationResponse(int hours, int minutes, double price){
        this.hours = hours;
        this.minutes = minutes;
        this.price = price;
    }
}

