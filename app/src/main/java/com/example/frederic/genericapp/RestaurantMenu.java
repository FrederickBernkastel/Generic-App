package com.example.frederic.genericapp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Project GenericApp
 * Created by Frederic
 * On 2/17/2018
 */

class RestaurantMenu extends FetchedObject {
    String name;
    URL imageURL;
    ArrayList<MenuItem> menu;

    RestaurantMenu(String name, String imageURL){
        this.name = name;
        this.menu = new ArrayList<>();
        try {
            this.imageURL = new URL(imageURL);
        } catch (MalformedURLException e){
            throw new RuntimeException(e);
        }
    }

    void addItem(int id, double price, String name,String description,String imageURL){
        try {
            URL url = new URL(imageURL);
            MenuItem item = new MenuItem(id,price,name,description,url);
            menu.add(item);
        } catch (MalformedURLException e){
            throw new RuntimeException(e);
        }
    }

    void printMenu(){
        System.out.println(name);
        System.out.println(imageURL);
        for (int i =0;i<menu.size();i++){
            MenuItem item = menu.get(i);
            String s = String.format(Locale.US,"id %d\nprice %.2f\nname %s\ndescription %s",item.id,item.price,item.name,item.description);
            System.out.println(s);
        }
    }

}
