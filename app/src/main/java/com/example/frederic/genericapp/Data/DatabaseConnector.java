/*
    Class to connect to database
*/
package com.example.frederic.genericapp.Data;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;


/**
 * Class to handle connections to the database PostgreSQL, should be used in a thread
 * Created by: Frederick Bernkastel
 */
public class DatabaseConnector {

    private static final String SERVERURLSTRING = "http://10.12.79.231:4995/api";
    private static final String CHARSET = "UTF-8";

    public enum FetchMode{
        MENU,
        TABLENO,
        PEOPLENO,
        EXISTINGORDERS
    }



    /**
     * Parse JSON file representing the menu, and output RestaurantMenu class, class should have an ID identifier
     * @param s     Json String to be parsed
     * @return      RestaurantMenu containing information in JSON string
     */
    public static RestaurantMenu parseJSONMenu(String s){
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
                        "currency":"S$*"
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
                if (currency.charAt(0)=='*' && currency.length()>1){
                    price = price + " " + currency.substring(1);
                } else if (currency.charAt(currency.length()-1)=='*' && currency.length()>1){
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
    public static TableNumResponse parseJSONTableNumResponse(String s){
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
        System.out.println(String.valueOf(response.isPeopleNumRequired));
        return response;
    }

    /**
     * Parse JSON file representing statuses of ordered items, and output FoodStatuses class
     * @param       s
     * @return      foodStatuses
     */
    public static FoodStatuses parseFoodStatusResponse(String s){
        /*  JSON FORMAT FOR INPUT FOOD STATUSES
            {
                "orders": [ {
                    "table_number": 3,
                    "start_time": "2018-03-14 01:56:48.426864",
                    "food_id":103,
                    "delivered":false
                    "comments":"comment"
                 }, {
                    ...
                 }]
            }

         */
        FoodStatuses foodStatuses = new FoodStatuses();
        try {

            JSONObject restaurantJSON = new JSONObject(s);
            JSONArray jsonStatuses = restaurantJSON.getJSONArray("orders");
            for (int i=0; i<jsonStatuses.length(); i++){
                JSONObject item = jsonStatuses.getJSONObject(i);
                int id = item.getInt("food_id");
                boolean delivered = item.getBoolean("delivered");
                foodStatuses.addStatus(id,delivered);
            }
            return foodStatuses;
        } catch (JSONException e){
            throw new RuntimeException(e);
        }
    }


    /**
     * Input information for fetching data from database using AsyncTask
     */
    public static class FetchTaskInput{
        String paylahID;
        String ServerURLString;
        FetchMode  fetchMode;
        public FetchTaskInput(String paylahID,int tableNumber,FetchMode fetchMode) throws Exception{
            this.paylahID = paylahID;
            this.fetchMode = fetchMode;
            String ServerURLTail;
            switch(fetchMode){
                case TABLENO:
                    ServerURLTail = String.format(Locale.US,"/register?table_number=%d&plid=%s",tableNumber,paylahID);
                    break;
                case MENU:
                    ServerURLTail = String.format(Locale.US,"/menu?table_number=%d",tableNumber);
                    break;
                default:
                    throw new Exception("Invalid FetchTaskInput parameters");
            }

            this.ServerURLString = SERVERURLSTRING + ServerURLTail;
        }
        public FetchTaskInput(String paylahID,int tableNumber,int peopleNumber, FetchMode fetchMode) throws Exception{
            this.paylahID = paylahID;
            this.fetchMode = fetchMode;
            String ServerURLTail;
            switch(fetchMode){
                case PEOPLENO:
                    ServerURLTail = String.format(Locale.US,"/register?table_number=%d&plid=%s&numpeople=%d",tableNumber,paylahID,peopleNumber);
                    break;
                default:
                    throw new Exception("Invalid FetchTaskInput parameters");
            }

            this.ServerURLString = SERVERURLSTRING + ServerURLTail;
        }
        public FetchTaskInput(String paylahID, FetchMode fetchMode) throws Exception {
            this.paylahID = paylahID;
            this.fetchMode = fetchMode;
            String ServerURLTail;
            switch (fetchMode){
                case EXISTINGORDERS:
                    ServerURLTail = String.format(Locale.US,"/existing_orders?plid=%s",paylahID);
                    break;
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
    public static class FetchTask extends AsyncTask<FetchTaskInput, Void, FetchedObject> {
        AsyncFetchResponse delegate=null;
        public FetchTask(AsyncFetchResponse delegate){
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
                myConnection.setRequestMethod("GET");
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
                        case EXISTINGORDERS:
                            fetchedObject = parseFoodStatusResponse(response);
                            break;

                    }
                    fetchedObject.fetchMode = fetchTaskInput.fetchMode;


                } else {
                    // Connection failed
                    throw new IOException("Response code error" + String.valueOf(myConnection.getResponseCode()));
                }

            } catch (IOException e){
                Log.e("Server Error","Error connecting to server in DatabaseConnector");
                Log.e("Server Error",e.getMessage());
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
     * Constructs JSON string representing food order, to be sent to the server
     * @param       batchOrder
     * @return      JSON String representing FoodBatchOrder
     */
    private static String constructJSON(FoodBatchOrder batchOrder){
        int counter = 0;
        int totalOrders = batchOrder.foodOrders.size();
        StringBuilder sBuilder = new StringBuilder("{\"orders\":[");
        for(FoodOrder order:batchOrder.foodOrders){
            String s = String.format(Locale.US,"{%d,\"%s\"}");
            sBuilder.append(s);
            if(++counter < totalOrders){
                sBuilder.append(',');
            }
        }
        sBuilder.append("]}");
        return sBuilder.toString();
    }

    /**
     * Output information for posting data using AsyncTask
     */
    public static class PostTaskOutput{
        String ServerURLString;
        byte[] JSONData;
        String paylahID;

        public PostTaskOutput(String paylahID, FoodBatchOrder batchOrder) throws UnsupportedEncodingException{
            this.paylahID = paylahID;
            String ServerURLTail =String.format(Locale.US,"/make_order?plid=%d",paylahID);
            this.ServerURLString = SERVERURLSTRING + ServerURLTail;
            this.JSONData = constructJSON(batchOrder).getBytes(CHARSET);

        }
    }

    /**
     * AsyncTask to post JSON code to database
     * Returns -1 on failure, 0 otherwise
     */
    public static class PostTask extends AsyncTask<PostTaskOutput,Void,Integer>{

        AsyncPostResponse delegate = null;

        public PostTask(AsyncPostResponse delegate){this.delegate = delegate;}

        @Override
        protected Integer doInBackground(PostTaskOutput... params) {
            PostTaskOutput postTaskOutput = params[0];
            String paylahID = postTaskOutput.paylahID;

            try {
                // Create URL
                URL serverURL = new URL(postTaskOutput.ServerURLString);

                // Create connection
                HttpURLConnection myConnection = (HttpURLConnection) serverURL.openConnection();

                // Set request Headers
                myConnection.setRequestMethod("POST");
                myConnection.setRequestProperty("User-Agent", paylahID);
                myConnection.setRequestProperty( "charset", "utf-8");

                // Write to server
                DataOutputStream wr = new DataOutputStream( myConnection.getOutputStream());
                wr.write(postTaskOutput.JSONData);


            } catch (IOException e){
                Log.e("Server Error","Error connecting to server in DatabaseConnector");
                Log.e("Server Error",e.getMessage());
                return -1;
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer response) {
            super.onPostExecute(response);
            if (delegate!=null) {
                delegate.postFinish(response);
            }
        }
    }
}
