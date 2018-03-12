package com.example.frederic.genericapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class RestaurantMainActivity extends AppCompatActivity{
    RestaurantMenu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_main);

        // Get screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        // Set icon sizes
        int size = width/5;
        ArrayList<Integer> bitmap_ids = new ArrayList<>(Arrays.asList(
                R.id.restaurant_main_activity_chef_image,
                R.id.restaurant_main_activity_menu_image,
                R.id.restaurant_main_activity_orders_image,
                R.id.restaurant_main_activity_bill_image
        ));
        ArrayList<Integer> bitmap_drawables = new ArrayList<>(Arrays.asList(
                R.drawable.activity_restautant_main__icon_chef,
                R.drawable.activity_restautant_main__icon_menu,
                R.drawable.activity_restautant_main__icon_orders,
                R.drawable.activity_restautant_main__icon_bill
        ));
        for (int i=0;i<bitmap_drawables.size();i++){
            int bitmap_id = bitmap_ids.get(i);
            int bitmap_drawable = bitmap_drawables.get(i);
            ImageView imageView =findViewById(bitmap_id);
            Bitmap d = ImageResize.decodeSampledBitmapFromResource(getResources(),bitmap_drawable,size,size);
            imageView.setImageBitmap(d);
        }

        //Test
        try {
            // INSERT RESTAURANT MENU FOR DEBUGGING, DELETE WHEN DONE
            JSONArray array = new JSONArray();
            JSONObject json=new JSONObject();
            json.put("name","Ugandan Cuisine");
            json.put("imagehyperlink","http://a57.foxnews.com/media2.foxnews.com/2016/06/09/640/360/060916_chew_crispychicken_1280.jpg");
            JSONObject obj1 = new JSONObject();
            obj1.put("food_id",999);
            obj1.put("price","$ 5.00");
            obj1.put("name","Baagaa");
            obj1.put("description","Meat so fresh, you can still hear it baa-ing");
            obj1.put("image_link","https://lh3.googleusercontent.com/alX3SlsbUt4ZBZ1ct6efz5wxIcjM6S3Gva_pstMNXGjlFAQRr6CbpwFyFNoixBgPOGXxQi7vqC3U0CDT8oGz4lu4IZWzifs40owj_jA=w600-l68");
            array.put(obj1);
            obj1 = new JSONObject();
            obj1.put("food_id",1000);
            obj1.put("price","$ 1.99");
            obj1.put("name","Fries");
            obj1.put("description","The straightest thing you'll put in your mouth every year");
            obj1.put("image_link","https://chiosrotisserie.com/wp-content/uploads/2017/07/fries.jpg");
            array.put(obj1);
            obj1 = new JSONObject();
            obj1.put("food_id",1001);
            obj1.put("price","$ 1.49");
            obj1.put("name","Coca-cola");
            obj1.put("description","Is pepsi OK?");
            obj1.put("image_link","https://i5.walmartimages.com/asr/791c580c-9a80-4d53-b972-50c78a935d72_1.8a4d4ced51a177d1c4dbbfb823d696f5.jpeg");
            array.put(obj1);
            obj1 = new JSONObject();
            obj1.put("food_id",1002);
            obj1.put("price","$ 0.99");
            obj1.put("name","Chocolate Soft-serve Ice-cream");
            obj1.put("description","‎💩");
            obj1.put("image_link","http://dlitesshoppe.com/wp-content/uploads/2015/03/Dlites_chocolate.png");
            array.put(obj1);
            obj1 = new JSONObject();
            obj1.put("food_id",1003);
            obj1.put("price","$ 450.00");
            obj1.put("name","Jumbo Whale Meat with Cavier Pizza");
            obj1.put("description","When you can't decide between junk food and fine dining");
            obj1.put("image_link","http://finedininglovers.cdn.crosscast-system.com/BlogPost/l_7853_expensive.pizza-2.jpg");
            array.put(obj1);
            json.put("menu",array);
            String s = json.toString();
            RestaurantMenu savedMenu = DatabaseConnector.parseJSONMenu(s);
            new SharedPrefManager<RestaurantMenu>().saveObj(getString(R.string.key_restaurant_menu),savedMenu,RestaurantMainActivity.this);
            // END OF DELETE PORTION

            // Fetch restaurant menu from sharedPreferences
            RestaurantMenu menu = new SharedPrefManager<RestaurantMenu>().fetchObj(getString(R.string.key_restaurant_menu),RestaurantMainActivity.this,RestaurantMenu.class);
            this.menu = menu;
            // Display data from database in resized widgets
            TextView restaurantNameTextView = findViewById(R.id.header_title);
            restaurantNameTextView.setText(menu.name);
            restaurantNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,(menu.name.length()<20)? Math.round(75-menu.name.length()*2.5): 25);
            ImageResize.loadImageByUrl(
                    this,
                    menu.imageURL.toString(),
                    ((ImageView) findViewById(R.id.restaurant_main_activity_image)),
                    width,
                    height/8*3
            );
        } catch (Exception e){
            System.out.println("FAIL");
        }

    }
    // TODO: Backtrack to MainActivity, iff nothing ordered yet, or no time
    public void onRestaurantMainBackClick(View v){

    }
    // TODO: Implement
    public void onRestaurantMainHotDishClick(View v){

    }
    // TODO: Implement
    public void onRestaurantMainMenuClick(View v){
        Intent intent = new Intent(RestaurantMainActivity.this,RestaurantMenuActivity.class);
        startActivity(intent);
    }
    // TODO: Implement
    public void onRestaurantMainOrdersClick(View v){

    }
    // TODO: Implement
    public void onRestaurantMainBillClick(View v){

    }

    @Override
    public void onBackPressed() {
        //TODO: Allow onBackPressed iff no orders sent to kitchen, or no time
        //super.onBackPressed();
    }



}
