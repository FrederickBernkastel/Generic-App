package com.example.frederic.genericapp.Data;

/**
 * Project GenericApp
 * Created by Frederic
 * On 3/17/2018
 */

public class FoodStatus {
    public int food_id;
    public int delivered;
    public int pending;

    public FoodStatus(int food_id){
        this.food_id = food_id;
        this.delivered = 0;
        this.pending = 0;
    }
    public void appendStatus(boolean delivered){
        if(delivered) {
            this.delivered++;
        }else {
            this.pending++;
        }
    }
}
