package com.example.frederic.genericapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

/**
 * MenuItem is a data class to store information about each menu item
 */
public class MenuItem implements Parcelable {
    public int id;
    public String price;
    public String name;
    public String description;
    public URL imageURL;
    public double priceVal;
    public String currency;
    private boolean currencyAtStart;

    public MenuItem(int id, String price, String name,String description,URL imageURL){
        this.id = id;
        this.price = price;
        this.name = name;
        this.description = description;
        this.imageURL = imageURL;
        try {
            this.priceVal = Double.valueOf(price.split(" ")[1]);
            this.currency = price.split(" ")[0];
            this.currencyAtStart = true;
        } catch (NumberFormatException e){
            this.priceVal = Double.valueOf(price.split(" ")[0]);
            this.currency = price.split(" ")[1];
            this.currencyAtStart=false;
        }
    }

    /**
     * Formats a number into the relevant currency held by this item
     * @param priceVal      Number to format
     * @return              String with value, and currency
     */
    public String formatPrice(double priceVal){
        if(currencyAtStart){
            return String.format(Locale.US,"%s %.2f",currency,priceVal);
        } else {
            return String.format(Locale.US,"%.2f %s",priceVal,currency);
        }
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
