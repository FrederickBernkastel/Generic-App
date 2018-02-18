/*
    Class to connect to database
*/
package com.example.frederic.genericapp;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


/**
 * Created by Frederic on 2/15/2018.
 */

/*
    Class to handle connections to the database PostgreSQL, should be used in a thread
<<<<<<< HEAD
    Factory Design pattern used to output relevant class
=======
>>>>>>> 2d946dd69eb7a7431ae5748bcd7e2827522647b4
    TODO: AWAIT RESTful WEB SERVICE IMPLEMENTATION
*/
public class DatabaseConnector {
    private static final String SERVERIP = "8.8.8.8";
    private static final int PORTNO = 5432;


    // TODO: Sync with RESTful Web Service

    public boolean checkConnection(){
        // TCP/HTTP/DNS (depending on the port, 53=DNS, 80=HTTP, etc.)
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress(SERVERIP,PORTNO);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) { return false; }

    }

    // TODO: Parse JSON file, and output relevant class, class should have an ID identifier
    public RestaurantMenu parseJSON(String s){
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


}
