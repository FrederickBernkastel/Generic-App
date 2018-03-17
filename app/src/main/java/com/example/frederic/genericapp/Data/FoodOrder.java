package com.example.frederic.genericapp.Data;

/**
 * Data class to store information on single pending food order
 */
public class FoodOrder{
    public int foodId;
    public String comment;
    public FoodOrder(int foodId,String comment){
        this.foodId = foodId;
        this.comment = comment;
    }
    public FoodOrder(int foodId){
        this.foodId = foodId;
        this.comment = null;
    }
}