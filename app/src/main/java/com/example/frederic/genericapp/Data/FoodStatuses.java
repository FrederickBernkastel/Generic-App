package com.example.frederic.genericapp.Data;

import android.util.SparseArray;

import java.util.ArrayList;


/**
 * Project GenericApp
 * Created by Frederic
 * On 3/17/2018
 */

public class FoodStatuses extends FetchedObject{
    public ArrayList<Integer> foodIDs;
    public SparseArray<FoodStatus> statuses;
    public FoodStatuses(){
        statuses = new SparseArray<>();
        foodIDs = new ArrayList<>();
    }
    public void addStatus(int id,boolean delivered){
        FoodStatus foodStatus;
        if ((foodStatus = statuses.get(id)) == null){
            foodStatus = new FoodStatus(id);
            foodIDs.add(id);
        }
        foodStatus.appendStatus(delivered);
        statuses.append(id,foodStatus);
    }
}
