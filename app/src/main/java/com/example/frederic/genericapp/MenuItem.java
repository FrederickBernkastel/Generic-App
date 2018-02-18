package com.example.frederic.genericapp;

import java.net.URL;

/**
 * Project GenericApp
 * Created by Frederic
 * On 2/17/2018
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
