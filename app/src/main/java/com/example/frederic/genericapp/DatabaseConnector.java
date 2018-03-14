/*
    Class to connect to database
*/
package com.example.frederic.genericapp;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;


/**
 * Class to handle connections to the database PostgreSQL, should be used in a thread
 * Created by: Frederick Bernkastel
 */
class DatabaseConnector {

    private static final String SERVERURLSTRING = "http://10.12.184.102:4995/api";
    private static final String CHARSET = "UTF-8";

    public enum FetchMode{
        MENU,
        TABLENO,
        PEOPLENO
    }


    /**
     * Parse JSON file representing the menu, and output RestaurantMenu class, class should have an ID identifier
     * @param s     Json String to be parsed
     * @return      RestaurantMenu containing information in JSON string
     */
    static RestaurantMenu parseJSONMenu(String s){
        /*
            The below JSON format is updated to v1.0
            JSON FORMAT FOR INPUT REQUESTS
             {
                "name": "Ugandan Cuisine",
                "imagehyperlink":"www.link.com",
                "menu": [
                    {
                        "food_id": 999,
                        "price": 5.20,
                        "currency":"S$_"
                        "name":"Fries",
                        "description":"I am French",
                        "image_link":"www.link.com",
                        "food_category":"Main"
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
                        "food_id":999,
                        "quantity":1,
                        "specialrequest":""
                    } ,
                    {
                        "food_id":1000,
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
                int id = item.getInt("food_id");
                String price = item.getString("price");
                String name = item.getString("name");
                String description = item.getString("description");
                String currency = item.getString("currency");
                if (currency.charAt(0)=='_' && currency.length()>1){
                    price = price + " " + currency.substring(1);
                } else if (currency.charAt(currency.length()-1)=='_' && currency.length()>1){
                    price = currency.substring(0,currency.length()-1) + " " + price;
                } else {
                    price = "S$ " + price;
                }
                url = item.getString("image_link");
                menu.addItem(id,price,name,description,url);
            }
            return menu;
        } catch (JSONException e){
            throw new RuntimeException(e);
        }

    }

    /**
     * Parse string from server, and return it in a standard format independent of server side changes
     * @param s         Json String to be parsed
     * @return          TableNumResponse containing formatted information about response
     */
    static TableNumResponse parseJSONTableNumResponse(String s){
        // Possible server responses to GET request
        final String NUMPEOPLETRUE = "True";
        final String NUMPEOPLEFALSE = "False";
        final String INVALIDTABLENUMTRUE = "NIL";

        // Interpret server response and modify relevant parameters
        TableNumResponse response = new TableNumResponse();
        if(s.equals(INVALIDTABLENUMTRUE)){
            response.isInvalidTableNum = true;
        } else if(s.equals(NUMPEOPLETRUE)) {
            response.isInvalidTableNum = false;
            response.isPeopleNumRequired = true;
        } else if (s.equals(NUMPEOPLEFALSE)){
            response.isInvalidTableNum = false;
            response.isPeopleNumRequired = false;
        }
        return response;
    }


    /**
     * Input information for fetching data from database using AsyncTask
     */
    static class FetchTaskInput{
        String paylahID;
        String ServerURLString;
        FetchMode  fetchMode;
        FetchTaskInput(String paylahID,int number,FetchMode fetchMode) throws Exception{
            this.paylahID = paylahID;
            this.fetchMode = fetchMode;
            String ServerURLTail;
            switch(fetchMode){
                case TABLENO:
                    ServerURLTail = String.format(Locale.US,"/tableno?tablenum=%d&plid=%s",number,paylahID);
                    break;
                case PEOPLENO:
                    ServerURLTail = String.format(Locale.US,"/numpeople?tablenum=%d&plid=%s",number,paylahID);
                    break;
                case MENU:
                    ServerURLTail = String.format(Locale.US,"/menu?tablenum=%d",number);
                default:
                    throw new Exception("Invalid FetchTaskInput parameters");
            }

            this.ServerURLString = SERVERURLSTRING + ServerURLTail;
        }

    }


    /**
     * AsyncTask to fetch a restautant's JSON code from database
     * Returns null on error, else a FetchedObject
     */
    static class FetchTask extends AsyncTask<FetchTaskInput, Void, FetchedObject> {
        public AsyncFetchResponse delegate=null;
        FetchTask(AsyncFetchResponse delegate){
            this.delegate = delegate;
        }
        @Override
        protected FetchedObject doInBackground(FetchTaskInput... params) {
            FetchTaskInput fetchTaskInput = params[0];
            String paylahID = fetchTaskInput.paylahID;
            FetchedObject fetchedObject = new FetchedObject();
            try {
                // Create URL
                URL serverURL = new URL(fetchTaskInput.ServerURLString);

                // Create connection
                HttpURLConnection myConnection = (HttpURLConnection) serverURL.openConnection();

                // Set request Headers
                myConnection.setRequestProperty("User-Agent", paylahID);

                if (myConnection.getResponseCode() == 200) {
                    // Connection success, read string
                    InputStreamReader responseBodyReader = new InputStreamReader(myConnection.getInputStream(), CHARSET);
                    StringBuilder stringBuilder = new StringBuilder();
                    String response;

                    BufferedReader bufferedReader = new BufferedReader(responseBodyReader);
                    while ((response = bufferedReader.readLine()) != null) {
                        stringBuilder.append(response);
                    }
                    response = stringBuilder.toString();

                    // Handle different GET requests
                    switch(fetchTaskInput.fetchMode){
                        case MENU:
                            fetchedObject = parseJSONMenu(response);
                            break;
                        case TABLENO:
                            fetchedObject = parseJSONTableNumResponse(response);
                            break;
                        case PEOPLENO:
                            break;

                    }


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

        @Override
        protected void onPostExecute(FetchedObject fetchedObject) {
            super.onPostExecute(fetchedObject);
            if (delegate!=null) {
                delegate.fetchFinish(fetchedObject);
            }
        }
    }



    /**
     * Output information for posting data using AsyncTask
     */
    static class PostTaskOutput{
        String ServerURLString;
        String JSONData;
        String paylahID;
        PostTaskOutput(String paylahID,String ServerURLTail,String JSONData){
            this.paylahID = paylahID;
            this.ServerURLString = SERVERURLSTRING + ServerURLTail;
            this.JSONData = JSONData;
        }
    }
}
