/*
    Class to connect to database
*/
package com.example.frederic.genericapp;


import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;




/*
    Class to handle connections to the database PostgreSQL, should be used in a thread
    Created by: Frederick Bernkastel
    TODO: AWAIT RESTful WEB SERVICE IMPLEMENTATION
*/
class DatabaseConnector {
    //TODO: change url
    private static final String SERVERURLSTRING = "www.INSERTURL.com";
    private static final String CHARSET = "UTF-8";
    
    

    // Parse JSON file, and output relevant class, class should have an ID identifier
    static RestaurantMenu parseJSON(String s){
        /*
            The below JSON format is updated to v1.0
            JSON FORMAT FOR INPUT REQUESTS
             {
                "name": "Ugandan Cuisine",
                "imagehyperlink":"www.link.com",
                "menu": [
                    {
                        "itemid": 999,
                        "price": 5,
                        "name":"Fries",
                        "description":"I am French",
                        "imagehyperlink":"www.link.com"
                    },
                    {
                        ...
                    }
                ]
             }
             JSON FORMAT FOR OUTPUT ORDERS
             {
                "tableno":111,
                "orders": [
                    {
                        "itemid":999,
                        "quantity":1,
                        "specialrequest":""
                    } ,
                    {
                        "itemid":1000,
                        "quantity":0,
                        "specialrequest:"No fish"
                    }
                ]
             }
        */
        try {

            JSONObject restaurantJSON = new JSONObject(s);
                    String restaurantName = restaurantJSON.getString("name");
            String url = restaurantJSON.getString("imagehyperlink");
            RestaurantMenu menu = new RestaurantMenu(restaurantName,url);
            JSONArray menuItems = restaurantJSON.getJSONArray("menu");
            for (int i=0; i<menuItems.length(); i++){
                JSONObject item = menuItems.getJSONObject(i);
                int id = item.getInt("itemid");
                int price = item.getInt("price");
                String name = item.getString("name");
                String description = item.getString("description");
                url = item.getString("imagehyperlink");
                menu.addItem(id,price,name,description,url);
            }
            return menu;
        } catch (JSONException e){
            throw new RuntimeException(e);
        }

    }

    private class FetchTaskInput{
        String paylahID;

    }

    // AsyncTask to fetch JSON code from database
    private static class FetchTask extends AsyncTask<FetchTaskInput, Void, FetchedObject> {
        @Override
        protected FetchedObject doInBackground(FetchTaskInput... params) {
            FetchTaskInput FetchTaskInput = params[0];
            String paylahID = FetchTaskInput.paylahID;
            FetchedObject fetchedObject;
            try {
                // Create URL
                URL serverURL = new URL(SERVERURLSTRING);
                // Create connection
                HttpsURLConnection myConnection = (HttpsURLConnection) serverURL.openConnection();

                // TODO: Set request Headers
                myConnection.setRequestProperty("User-Agent", paylahID);

                if (myConnection.getResponseCode() == 200) {
                    // Connection success, read json string
                    InputStreamReader responseBodyReader = new InputStreamReader(myConnection.getInputStream(), CHARSET);
                    StringBuilder stringBuilder = new StringBuilder();
                    String response;

                    BufferedReader bufferedReader = new BufferedReader(responseBodyReader);
                    while ((response = bufferedReader.readLine()) != null) {
                        stringBuilder.append(response);
                    }
                    response = stringBuilder.toString();
                    fetchedObject=parseJSON(response);

                } else {
                    // Connection failed
                    throw new IOException();
                }

            } catch (IOException e){
                Log.e("Server Error","Error connecting to server in DatabaseConnector");
                return null;
            }
            return fetchedObject;
        }


    }

}
