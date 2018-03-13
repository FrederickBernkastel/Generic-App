package com.example.frederic.genericapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;



/**
 * Class to store information about a restaurant menu
 * Created by: Frederick Bernkastel
 */
class RestaurantMenu extends FetchedObject {
    String name;
    URL imageURL;
    ArrayList<MenuItem> menu;

    /**
     * Constructor
     * @param name      Name of restaurant
     * @param imageURL  URL of restaurant's image to be displayed on the app
     */
    RestaurantMenu(String name, String imageURL){
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
    void addItem(int id, String price, String name,String description,String imageURL){
        try {
            URL url = new URL(imageURL);
            MenuItem item = new MenuItem(id,price,name,description,url);
            menu.add(item);
        } catch (MalformedURLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     *  Function to help debugging process by printing menu items
     */
    void printMenu(){
        System.out.println(name);
        System.out.println(imageURL);
        for (int i =0;i<menu.size();i++){
            MenuItem item = menu.get(i);
            String s = String.format(Locale.US,"id %d\nprice %s\nname %s\ndescription %s",item.id,item.price,item.name,item.description);
            System.out.println(s);
        }
    }

}

/**
 * MenuItem is a data class to store information about each menu item
 */
class MenuItem implements Parcelable {
    int id;
    String price;
    String name;
    String description;
    URL imageURL;
    MenuItem(int id, String price, String name,String description,URL imageURL){
        this.id = id;
        this.price = price;
        this.name = name;
        this.description = description;
        this.imageURL = imageURL;
    }

    /**
     * Required function from Parcelable interface
     * @return
     */
    public int describeContents() {
        return 0;
    }
    // write your data to the passed-in Parcel

    /**
     * Method to write all data into a Parcel
     * @param out
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(price);
        out.writeString(name);
        out.writeString(description);
        out.writeString(imageURL.toString());
    }

    /**
     * Constructor to read data from a Parcel
     * @param in
     */
    private MenuItem(Parcel in) {
        id = in.readInt();
        price = in.readString();
        name = in.readString();
        description = in.readString();
        try {
            imageURL = new URL(in.readString());
        } catch (MalformedURLException e){
            System.out.println("Malformed URL in parcel, possible data corruption");
        }
    }
    public static final Parcelable.Creator<MenuItem> CREATOR
            = new Parcelable.Creator<MenuItem>() {
        public MenuItem createFromParcel(Parcel in) {
            return new MenuItem(in);
        }

        public MenuItem[] newArray(int size) {
            return new MenuItem[size];
        }
    };

}
