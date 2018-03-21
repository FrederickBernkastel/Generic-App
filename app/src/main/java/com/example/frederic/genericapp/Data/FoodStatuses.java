package com.example.frederic.genericapp.Data;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 * Project GenericApp
 * Created by Frederic
 * On 3/17/2018
 */

public class FoodStatuses extends FetchedObject{
    public ArrayList<Integer> foodIDs;
    public ArrayList<FoodStatus> statuses;
    private SparseArray<FoodStatus> statusesSparse;
    public boolean allFulfilled;

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
    public void addStatus(int id,boolean delivered){
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
}
