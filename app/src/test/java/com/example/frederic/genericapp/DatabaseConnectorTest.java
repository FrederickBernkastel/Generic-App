package com.example.frederic.genericapp;

import com.example.frederic.genericapp.data.get.AsyncFetchResponse;
import com.example.frederic.genericapp.data.DatabaseConnector;
import com.example.frederic.genericapp.data.get.DurationResponse;
import com.example.frederic.genericapp.data.get.FetchedObject;
import com.example.frederic.genericapp.data.get.FoodStatus;
import com.example.frederic.genericapp.data.get.FoodStatuses;
import com.example.frederic.genericapp.data.get.MenuItem;
import com.example.frederic.genericapp.data.get.RestaurantMenu;
import com.example.frederic.genericapp.data.get.SessionInfo;
import com.example.frederic.genericapp.data.get.TableNumResponse;
import com.example.frederic.genericapp.data.post.FoodBatchOrder;
import com.example.frederic.genericapp.data.post.FoodOrder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.BatchUpdateException;
import java.util.Locale;

import static org.apache.tools.ant.dispatch.DispatchUtils.execute;
import static org.junit.Assert.*;


import org.robolectric.annotation.Config;

import javax.xml.datatype.Duration;

@Config(constants = BuildConfig.class, sdk=21)
@RunWith(RobolectricTestRunner.class)
public class DatabaseConnectorTest implements AsyncFetchResponse {
    @Test
    public void parseJSON() throws Exception {
        String s;
        // Create JSON object, and converts it to JSON string

        JSONObject json=new JSONObject();
        json.put("name","Ugandan Cuisine");
        json.put("imagehyperlink","http://a57.foxnews.com/media2.foxnews.com/2016/06/09/640/360/060916_chew_crispychicken_1280.jpg");

        // sort of like a dictionary. json object can only take in key and value
        JSONObject obj1 = new JSONObject();
        obj1.put("food_id",999);
        obj1.put("price","5");
        obj1.put("name","Baagaa");
        obj1.put("description","Meat so fresh, you can still hear it baa-ing");
        obj1.put("image_link","https://lh3.googleusercontent.com/alX3SlsbUt4ZBZ1ct6efz5wxIcjM6S3Gva_pstMNXGjlFAQRr6CbpwFyFNoixBgPOGXxQi7vqC3U0CDT8oGz4lu4IZWzifs40owj_jA=w600-l68");

        // json array can only take in objects
        JSONArray array = new JSONArray();
        array.put(obj1);
        json.put("menu",array);
        s = json.toString();


        RestaurantMenu menu = DatabaseConnector.parseJSONMenu(s);
        MenuItem item = menu.menu.get(0);
        assertEquals("Ugandan Cuisine",menu.name);
        assertEquals(new URL("https://www.link1.com"),menu.imageURL);
        assertEquals(999,item.id);
        assertEquals("5",item.price,.009);
        assertEquals("Fries",item.name);
        assertEquals("I am French",item.description);
        assertEquals(new URL("https://www.link2.com"),item.imageURL);
    }
    // TODO: Test this test case (EveryBranch WhiteBox Test)


    @Test
    public void parseFoodStatusResponseTest() throws Exception{
        // messed up string

        String s1 ="{\"orders\":[" +
                "{\"table_number\":2,\"start_time\":\"2018-03-27 18:39:51.940409\",\"price\":14.00,\"food_id\":43,\"delivered\":fkkkk,\"comments\":\"byeeeeeeeeee\",\"additional_price\":0.00}," +
                "{\"table_number\":12,\"start_time\":\"2018-03-27 18:39:51.940409\",\"price\":4.90,\"food_id\":203,\"delivered\":false,\"comments\":\"thereeeeeeeee\",\"additional_price\":0.00}," +
                "{\"table_number\":12,\"start_time\":\"2018-03-27 18:39:51.940409\",\"price\":4.50,\"food_id\":101,\"delivered\":false,\"comments\":\"fking briiiaaannnn did it\",\"additional_price\":0.00}]}";
        // proper string

        String s2 ="{\"orders\":[" +
                "{\"table_number\":12,\"start_time\":\"2018-03-27 18:39:51.940409\",\"price\":14.00,\"food_id\":43,\"delivered\":false,\"comments\":\"byeeeeeeeeee\",\"additional_price\":0.00}," +
                "{\"table_number\":12,\"start_time\":\"2018-03-27 18:39:51.940409\",\"price\":4.90,\"food_id\":203,\"delivered\":true,\"comments\":\"thereeeeeeeee\",\"additional_price\":0.00}," +
                "{\"table_number\":12,\"start_time\":\"2018-03-27 18:39:51.940409\",\"price\":4.90,\"food_id\":203,\"delivered\":true,\"comments\":\"thereeeeeeeee\",\"additional_price\":0.00}," +
                "{\"table_number\":12,\"start_time\":\"2018-03-27 18:39:51.940409\",\"price\":4.50,\"food_id\":101,\"delivered\":false,\"comments\":\"fking briiiaaannnn did it\",\"additional_price\":20}]}";

        /*
            For testing if we can break this method
         */
        try {
            FoodStatuses foodStatuses = DatabaseConnector.parseFoodStatusResponse(s1);
            assertTrue(false);

        } catch (Exception e) {
            assertTrue(true);
        }


        /*
        For testing if functionality works
         */
        FoodStatuses foodStatuses = DatabaseConnector.parseFoodStatusResponse(s1);
        FoodStatus food = foodStatuses.getStatus(43);
        FoodStatus food2 = foodStatuses.getStatus(203);
        FoodStatus food3 = foodStatuses.getStatus(101);
        assertNotEquals(null, foodStatuses.getStatus(43));
        assertEquals(0, food.delivered);
        assertEquals(1, food.pending);
        assertEquals(2, food2.delivered);
        assertEquals(26.50, food3.totalPrice,0.009);

        //TODO: confirm with B man
    }

/*
    Ignore for now
    @Test
    public static FetchedObject parseStripeTransactionResponseTest(String s){

    }*/

    @Test
    public void parseDurationResponseTest() throws Exception{

        // trying to break the code
        String s3 = "{\"time_price\":2,\"hours\":5,\"minutes\":58}";

        // proper input
        String s4 = "{\"time_price\":2,\"hours\":-1,\"minutes\":0}";

        // Try to break code
        try {
            FetchedObject restaurantJSON = DatabaseConnector.parseDurationResponse(s4);
            assertTrue(false);
        } catch (Exception e){
            assertTrue(true);
        }

        // Testing if function is working
        DurationResponse durationResponse = (DurationResponse) DatabaseConnector.parseDurationResponse(s3);
        assertEquals(2, durationResponse.price, 0.005);
        assertEquals(5, durationResponse.hours);
        assertEquals(58, durationResponse.minutes);

        }
        /*try {

            JSONObject restaurantJSON = new JSONObject(s);
            double price = restaurantJSON.getDouble("time_price");
            int hours = restaurantJSON.getInt("hours");
            int minutes = restaurantJSON.getInt("minutes");

            return new DurationResponse(hours,minutes,price);

        } catch (JSONException e){
            throw new RuntimeException(e);
        }
    }*/



    @Test
    public void parseTableNumResponseTest()throws Exception{

        // wrong string
        String s5 ="{\"orders\":[" +
                "{\"table_number\":2,\"start_time\":\"2018-03-27 18:39:51.940409\",\"price\":14.00,\"food_id\":43,\"delivered\":fkkkk,\"comments\":\"byeeeeeeeeee\",\"additional_price\":0.00}," +
                "{\"table_number\":12,\"start_time\":\"2018-03-27 18:39:51.940409\",\"price\":4.90,\"food_id\":203,\"delivered\":false,\"comments\":\"thereeeeeeeee\",\"additional_price\":0.00}," +
                "{\"table_number\":12,\"start_time\":\"2018-03-27 18:39:51.940409\",\"price\":4.50,\"food_id\":101,\"delivered\":false,\"comments\":\"fking briiiaaannnn did it\",\"additional_price\":0.00}]}";
        // proper string

        String s6 ="{\"orders\":[" +
                "{\"table_number\":12,\"start_time\":\"2018-03-27 18:39:51.940409\",\"price\":14.00,\"food_id\":43,\"delivered\":false,\"comments\":\"byeeeeeeeeee\",\"additional_price\":0.00}," +
                "{\"table_number\":12,\"start_time\":\"2018-03-27 18:39:51.940409\",\"price\":4.90,\"food_id\":203,\"delivered\":true,\"comments\":\"thereeeeeeeee\",\"additional_price\":0.00}," +
                "{\"table_number\":12,\"start_time\":\"2018-03-27 18:39:51.940409\",\"price\":4.90,\"food_id\":203,\"delivered\":true,\"comments\":\"thereeeeeeeee\",\"additional_price\":0.00}," +
                "{\"table_number\":12,\"start_time\":\"2018-03-27 18:39:51.940409\",\"price\":4.50,\"food_id\":101,\"delivered\":false,\"comments\":\"fking briiiaaannnn did it\",\"additional_price\":20}]}";
        try{
            FetchedObject info = DatabaseConnector.parseJSONTableNumResponse(s5);
            assertTrue(false);
        } catch (Exception e){
            assertTrue(true);
        }


        // Test function
        SessionInfo sessionInfo = (SessionInfo) DatabaseConnector.parseTableNumResponse(s6);
        assertEquals(12, sessionInfo.tableNo);

        /*SessionInfo info = new SessionInfo();
        try {

            JSONObject restaurantJSON = new JSONObject(s);
            JSONArray jsonStatuses = restaurantJSON.getJSONArray("orders");
            JSONObject item = jsonStatuses.getJSONObject(0);
            int tableNo = item.getInt("table_number");
            info.tableNo = tableNo;
            return info;
        } catch (JSONException e){
            throw new RuntimeException(e);
        }*/

    }
/*
    @Test
    public void onPostExecute(FetchedObject fetchedObject){

    }*/
/*
    @Test
    public static String constructJSON(FoodBatchOrder batchOrder){


        *//*
        int counter = 0;
        int totalOrders = batchOrder.foodOrders.size();
        StringBuilder sBuilder = new StringBuilder("{\"orders\":[");
        for(FoodOrder order:batchOrder.foodOrders){
            String s = String.format(Locale.US,"{\"food_id\":%d,\"comment\":\"%s\"}",order.foodId,order.comment);
            sBuilder.append(s);
            if(++counter < totalOrders){
                sBuilder.append(',');
            }
        }
        sBuilder.append("]}");
        return sBuilder.toString();*//*
    }*/
/*
    @Test
    public Integer doInBackground(DatabaseConnector.PostTaskOutput... params){

    }

    @Test
    public static String readConnectionInput(HttpURLConnection myConnection) throws IOException{

    }*/
    @Test
    public void FetchTask() throws Exception{
        DatabaseConnector.FetchTaskInput input;

        // Test FetchMode.TABLENO
        input = new DatabaseConnector.FetchTaskInput("1",1,DatabaseConnector.FetchMode.TABLENO);
        System.out.println(new DatabaseConnector.FetchTask(this).execute(input).get().response);

        // Test FetchMode.PEOPLENO
        input = new DatabaseConnector.FetchTaskInput("1",1,DatabaseConnector.FetchMode.PEOPLENO);
        assertNull(new DatabaseConnector.FetchTask(null).execute(input).get().response);

        // Test FetchMode.MENU
        input = new DatabaseConnector.FetchTaskInput("1",1,DatabaseConnector.FetchMode.MENU);
        RestaurantMenu output = (RestaurantMenu) new DatabaseConnector.FetchTask(null).execute(input).get();
        System.out.println(output.name);
        for (int i =0;i<output.menu.size();i++){
            System.out.println(output.menu.get(i).name);
        }


    }



    @Test
    public void testRegistrationFlow () throws Exception {
        String fixedPaylahID = "81232135";
        int fixedTableNumber = 12;
        int fixedPeopleNumber = 4;
        FoodBatchOrder newBatchOrder = new FoodBatchOrder();




        try{
            /////// EXISTING ORDER CHECK ///////

            // Create new fetchtaskinput to get RESPONSE
            DatabaseConnector.FetchTaskInput fetchTaskInputEO =
                    new DatabaseConnector.FetchTaskInput(fixedPaylahID, DatabaseConnector.FetchMode.EXISTINGORDERS);

            // Creates fetchtask
            DatabaseConnector.FetchTask fetchTaskEO= new DatabaseConnector.FetchTask(null);

            // Use fetchtask to pull object from database, INPUTS THE RESPONSE FROM EARLIER into method
            FetchedObject fEO = fetchTaskEO.execute(fetchTaskInputEO).get();

            // create foodstatuses
            FoodStatuses foodStatuses = (FoodStatuses) fEO;

            // check if its empty
            assertEquals(null,(foodStatuses.statuses));


            /////// TABLE NUMBER CHECK ///////
            DatabaseConnector.FetchTaskInput fetchTaskInputTNC =
                    new DatabaseConnector.FetchTaskInput(fixedPaylahID, fixedTableNumber, DatabaseConnector.FetchMode.TABLENO);
            DatabaseConnector.FetchTask fetchTaskTNC= new DatabaseConnector.FetchTask(null);

            FetchedObject fTNC = fetchTaskTNC.execute(fetchTaskInputTNC).get();

            // create tablenum response
            TableNumResponse tableNumResponse = (TableNumResponse) fTNC;

            // test
            assertTrue(tableNumResponse.isPeopleNumRequired);
            assertFalse(tableNumResponse.isInvalidTableNum);



            /////// PEOPLE NUMBER CHECK ///////


            DatabaseConnector.FetchTaskInput fetchTaskInputPNC =
                    new DatabaseConnector.FetchTaskInput(fixedPaylahID, fixedTableNumber, fixedPeopleNumber, DatabaseConnector.FetchMode.PEOPLENO);
            DatabaseConnector.FetchTask fetchTaskPNC= new DatabaseConnector.FetchTask(null);
            fetchTaskPNC.execute(fetchTaskInputPNC).get();



            // repeat people number check
            DatabaseConnector.FetchTaskInput fetchTaskInputPN2 =
                    new DatabaseConnector.FetchTaskInput(fixedPaylahID, fixedTableNumber, fixedPeopleNumber, DatabaseConnector.FetchMode.PEOPLENO);
            DatabaseConnector.FetchTask fetchTaskPN2= new DatabaseConnector.FetchTask(null);
            fetchTaskPN2.execute(fetchTaskInputPN2);

            // menu check
            DatabaseConnector.FetchTaskInput fetchTaskInputMenu =
                    new DatabaseConnector.FetchTaskInput(fixedPaylahID, fixedTableNumber, DatabaseConnector.FetchMode.MENU);
            DatabaseConnector.FetchTask fetchTaskMenu= new DatabaseConnector.FetchTask(null);

            FetchedObject fMenu = fetchTaskMenu.execute(fetchTaskInputMenu).get();

            RestaurantMenu restaurantMenu = (RestaurantMenu) fMenu;

            //test
            assertFalse(((RestaurantMenu) fMenu).menu.isEmpty());
            assertNotEquals(0,((RestaurantMenu) fMenu).name.length());
            // TODO: assertNotEquals(0,((RestaurantMenu) fMenu).imageURL.

        } catch (Exception e){
            e.printStackTrace();
        }
        // Check if we can break the code
/*
        try{
            // Number exceeds 8 digit limit
            DatabaseConnector.FetchTaskInput fetchTaskInput1 =
                    new DatabaseConnector.FetchTaskInput("882281041", DatabaseConnector.FetchMode.EXISTINGORDERS);

            // Number below 8 digit limit
            DatabaseConnector.FetchTaskInput fetchTaskInput2 =
                    new DatabaseConnector.FetchTaskInput("8281041", DatabaseConnector.FetchMode.EXISTINGORDERS);

            assertTrue(false);
        } catch (Exception e){
            assertTrue(true);
        }
*/






        /////// PEOPLE NUMBER CHECK AGAIN (should give error) ///////

        /////// MENU CHECK ///////

        /////// POST TASK CHECK (sending to database) ///////

        /////// INFO SENT TO DATABASE SUCCESSFULLY CHECK ///////

        /////// STRIPE CHECK ///////

    }

    public void fetchFinish(FetchedObject output){

        System.out.println("Fetch Successful");
    }
}