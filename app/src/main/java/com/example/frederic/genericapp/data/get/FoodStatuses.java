package com.example.frederic.genericapp.data.get;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Project GenericApp
 * Created by Frederic
 * On 3/17/2018
 */

public class FoodStatuses extends FetchedObject{
    private final ArrayList<Integer> foodIDs;
    public final ArrayList<FoodStatus> statuses;
    private final SparseArray<FoodStatus> statusesSparse;
    private boolean allFulfilled;


    public FoodStatuses(){
        statuses = new ArrayList<>();
        foodIDs = new ArrayList<>();
        statusesSparse = new SparseArray<>();
        allFulfilled = true;
    }

    /**
     * Add new food status to list, and changes allFulfilled if necessary
     * @param id            food_id of new status
     * @param delivered     New status of single food item
     */
    public void addStatus(int id,boolean delivered, double price){
        FoodStatus foodStatus = statusesSparse.get(id);

        if (foodStatus == null){
            foodStatus = new FoodStatus(id);
            foodIDs.add(id);
            statuses.add(foodStatus);
        }
        if(!delivered){
            allFulfilled = false;
        }
        foodStatus.appendStatus(delivered);
        foodStatus.addPrice(price);
        statusesSparse.append(id,foodStatus);
    }

    /**
     * Sort statuses, such that items with more unfulfilled quantities appear first, followed by items with higher quantities
     */
    public void sortStatuses(){
        Collections.sort(statuses);
    }

    public FoodStatus getStatus(int foodid){
        return statusesSparse.get(foodid);
    }

    public boolean isAllFulfilled(){
        return allFulfilled;
    }

    // TODO: Implement function to record total price of individual orders when server team catches up
}
