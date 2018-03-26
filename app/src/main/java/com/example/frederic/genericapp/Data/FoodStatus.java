package com.example.frederic.genericapp.Data;

import android.support.annotation.NonNull;

/**
 * Project GenericApp
 * Created by Frederic
 * On 3/17/2018
 */

public class FoodStatus implements Comparable<FoodStatus>{
    public final int food_id;
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

    @Override
    public int compareTo(@NonNull FoodStatus foodStatus) {
        // sort by pending orders in descending order
        if (this.pending - foodStatus.pending!=0){
            return foodStatus.pending - this.pending;
        } else {
            // sort by delivered items in descending order if pending orders are the same
            return foodStatus.delivered - this.delivered;
        }

    }
}
