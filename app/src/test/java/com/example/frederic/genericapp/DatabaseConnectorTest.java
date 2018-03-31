package com.example.frederic.genericapp;

import com.example.frederic.genericapp.data.get.AsyncFetchResponse;
import com.example.frederic.genericapp.data.DatabaseConnector;
import com.example.frederic.genericapp.data.get.FetchedObject;
import com.example.frederic.genericapp.data.get.MenuItem;
import com.example.frederic.genericapp.data.get.RestaurantMenu;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.net.URL;

import static org.junit.Assert.*;


import org.robolectric.annotation.Config;

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
        JSONObject obj1 = new JSONObject();
        obj1.put("food_id",999);
        obj1.put("price","5");
        obj1.put("name","Baagaa");
        obj1.put("description","Meat so fresh, you can still hear it baa-ing");
        obj1.put("image_link","https://lh3.googleusercontent.com/alX3SlsbUt4ZBZ1ct6efz5wxIcjM6S3Gva_pstMNXGjlFAQRr6CbpwFyFNoixBgPOGXxQi7vqC3U0CDT8oGz4lu4IZWzifs40owj_jA=w600-l68");
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
    public void FetchTask() throws Exception{
        DatabaseConnector.FetchTaskInput input;

        // Test FetchMode.TABLENO
        input = new DatabaseConnector.FetchTaskInput("1",1,DatabaseConnector.FetchMode.TABLENO);
        System.out.println(new DatabaseConnector.FetchTask(this).execute(input).get().response);

        // Test FetchMode.PEOPLENO
        input = new DatabaseConnector.FetchTaskInput("1",1,DatabaseConnector.FetchMode.PEOPLENO);
        assertNull(new DatabaseConnector.FetchTask(this).execute(input).get().response);

        // Test FetchMode.MENU
        input = new DatabaseConnector.FetchTaskInput("1",1,DatabaseConnector.FetchMode.MENU);
        RestaurantMenu output = (RestaurantMenu) new DatabaseConnector.FetchTask(this).execute(input).get();
        System.out.println(output.name);
        for (int i =0;i<output.menu.size();i++){
            System.out.println(output.menu.get(i).name);
        }


    }


    public void fetchFinish(FetchedObject output){
        System.out.println("Fetch Successful");
    }
}