package com.example.frederic.genericapp.data.post;

/**
 * Data class to store information on single pending food order
 */
public class FoodOrder{
    public int foodId;
    public String comment;
    FoodOrder(int foodId,String comment){
        this.foodId = foodId;
        // Check if comment is valid
        if (comment.matches("[a-zA-Z0-9 \n!@#$%^&*(){}:;\"',./<>?\\[\\]]*")) {
            this.comment = comment;
        } else {
            this.comment = "";
        }
    }
    FoodOrder(int foodId){
        this.foodId = foodId;
        this.comment = null;
    }
}