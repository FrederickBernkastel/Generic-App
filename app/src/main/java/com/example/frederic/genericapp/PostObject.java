package com.example.frederic.genericapp;

import java.util.ArrayList;

/**
 * Parent object of everything fetched from database
 * Created by: Frederick Bernkastel
 */
public class PostObject {

}

/**
 * Data class to hold data on pending orders
 */
class FoodBatchOrder extends PostObject{
    private ArrayList<FoodOrder> foodOrders;
    FoodBatchOrder(){
        foodOrders = new ArrayList<>();
    }
    void insertFoodOrder(int foodId,String comment){
        foodOrders.add(new FoodOrder(foodId,comment));
    }
    void insertFoodOrder(int foodId){
        foodOrders.add(new FoodOrder(foodId));
    }
}

/**
 * Data class to store information on single pending food order
 */
class FoodOrder{
    int foodId;
    String comment;
    FoodOrder(int foodId,String comment){
        this.foodId = foodId;
        this.comment = comment;
    }
    FoodOrder(int foodId){
        this.foodId = foodId;
        this.comment = "None";
    }
}