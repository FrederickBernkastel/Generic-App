package com.example.frederic.genericapp.data;

import java.util.ArrayList;

/**
 * Data class to hold data on pending orders
 */
public class FoodBatchOrder extends PostObject{
    public ArrayList<FoodOrder> foodOrders;
    public FoodBatchOrder(){
        foodOrders = new ArrayList<>();
    }
    public void insertFoodOrder(int foodId,String comment){
        foodOrders.add(new FoodOrder(foodId,comment));
    }
    public void insertFoodOrder(int foodId){
        foodOrders.add(new FoodOrder(foodId));
    }

    /**
     * Returns quantity of an item purchased
     * @param       foodId      Identifier of food item
     * @return
     */
    public int getItemCount(int foodId){
        int count = 0;
        for(FoodOrder foodOrder:foodOrders){
            if (foodOrder.foodId==foodId){
                count++;
            }
        }
        return count;
    }

    public void deleteAll(int foodId){
        ArrayList<FoodOrder> removeOrder = new ArrayList<>();
        for(FoodOrder order : foodOrders){
            if(order.foodId==foodId){
                removeOrder.add(order);
            }
        }
        foodOrders.removeAll(removeOrder);
    }

    /**
     * Debuging Function to print all items
     */
    public void printItems(){
        for (FoodOrder f:foodOrders){
            System.out.println(f.foodId);
        }
    }
}
