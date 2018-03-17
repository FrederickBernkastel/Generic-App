package com.example.frederic.genericapp.Data;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Class to store information about a restaurant menu
 * Created by: Frederick Bernkastel
 */
public class RestaurantMenu extends FetchedObject {
    public String name;
    public URL imageURL;
    public ArrayList<MenuItem> menu;

    /**
     * Constructor
     * @param name      Name of restaurant
     * @param imageURL  URL of restaurant's image to be displayed on the app
     */
    public RestaurantMenu(String name, String imageURL){
        this.name = name;
        this.menu = new ArrayList<>();
        try {
            this.imageURL = new URL(imageURL);
        } catch (MalformedURLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Function to save menu item
     * @param id            Unique id for menu item
     * @param price         Price of item
     * @param name          Name of item
     * @param description   Description of item
     * @param imageURL      Image of item
     */
    public void addItem(int id, String price, String name,String description,String imageURL){
        try {
            URL url = new URL(imageURL);
            MenuItem item = new MenuItem(id,price,name,description,url);
            menu.add(item);
        } catch (MalformedURLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Function to find MenuItem in O(n), returns null if not found
     * @param id        Unique id of item to fetch
     * @return          MenuItem object
     */
    public MenuItem findItem(int id){
        for (MenuItem item:menu){
            if (item.id==id){
                return item;
            }
        }
        return null;
    }
    /**
     *  Function to help debugging process by printing menu items
     */
    public void printMenu(){
        System.out.println(name);
        System.out.println(imageURL);
        for (int i =0;i<menu.size();i++){
            MenuItem item = menu.get(i);
            String s = String.format(Locale.US,"id %d\nprice %s\nname %s\ndescription %s",item.id,item.price,item.name,item.description);
            System.out.println(s);
        }
    }

}
