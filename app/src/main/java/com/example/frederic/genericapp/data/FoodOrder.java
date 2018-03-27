package com.example.frederic.genericapp.data;

/**
 * Data class to store information on single pending food order
 */
public class FoodOrder{
    public int foodId;
    public String comment;
    FoodOrder(int foodId,String comment){
        this.foodId = foodId;
        this.comment = comment;
    }
    FoodOrder(int foodId){
        this.foodId = foodId;
        this.comment = null;
    }
}