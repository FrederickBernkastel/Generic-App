package com.example.frederic.genericapp;

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
public class DatabaseConnectorTest {
    @Test
    public void parseJSON() throws Exception {
        String s;
        // Create JSON object, and converts it to JSON string INVESTIGATE ROBOELECTRIC

        JSONObject json=new JSONObject();
        json.put("name","Ugandan Cuisine");
        json.put("imagehyperlink","https://www.link1.com");
        JSONObject obj1 = new JSONObject();
        obj1.put("itemid",999);
        obj1.put("price",5.);
        obj1.put("name","Fries");
        obj1.put("description","I am French");
        obj1.put("imagehyperlink","https://www.link2.com");
        JSONArray array = new JSONArray();
        array.put(obj1);
        json.put("menu",array);
        s = json.toString();

        DatabaseConnector dc = new DatabaseConnector();
        RestaurantMenu menu = dc.parseJSON(s);
        MenuItem item = menu.menu.get(0);
        assertEquals("Ugandan Cuisine",menu.name);
        assertEquals(new URL("https://www.link1.com"),menu.imageURL);
        assertEquals(999,item.id);
        assertEquals(5,item.price,.009);
        assertEquals("Fries",item.name);
        assertEquals("I am French",item.description);
        assertEquals(new URL("https://www.link2.com"),item.imageURL);
    }
    // TODO: Test this test case
    @Test
    public void FetchTask() throws Exception{
        DatabaseConnector.FetchTaskInput input = new DatabaseConnector.FetchTaskInput("1");
        // Block thread until output is received
        RestaurantMenu output = (RestaurantMenu) new DatabaseConnector.FetchTask().execute(input).get();
        //new DatabaseConnector.FetchTask().execute(input).get();

        System.out.println(output.name);
        for (int i =0;i<output.menu.size();i++){
            System.out.println(output.menu.get(0).name);
        }

    }
}