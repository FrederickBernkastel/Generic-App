package com.example.frederic.genericapp;

import java.net.URL;


/**
 * Class for each menu item
 * Created by: Frederick Bernkastel
 */
public class MenuItem {
    int id;
    double price;
    String name;
    String description;
    URL imageURL;
    MenuItem(int id, double price, String name,String description,URL imageURL){
        this.id = id;
        this.price = price;
        this.name = name;
        this.description = description;
        this.imageURL = imageURL;
    }

}
