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

import java.util.ArrayList;
import java.util.Arrays;

public class RestaurantMainActivity extends AppCompatActivity implements AsyncFetchResponse{
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
        Intent intent = getIntent();
        String tablenumber = intent.getStringExtra("tablenumber");
        DatabaseConnector.FetchTaskInput input = new DatabaseConnector.FetchTaskInput("1","/menu?tablenumber="+tablenumber,DatabaseConnector.FetchMode.MENU);
        new DatabaseConnector.FetchTask(this).execute(input);

    }
    // TODO: Backtrack to MainActivity, iff nothing ordered yet, or no time
    public void onRestaurantMainBackClick(View v){

    }
    // TODO: Implement
    public void onRestaurantMainHotDishClick(View v){

    }
    // TODO: Implement
    public void onRestaurantMainMenuClick(View v){

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

    @Override
    public void fetchFinish(FetchedObject output) {
        // Get screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        try {
            RestaurantMenu menu = (RestaurantMenu) output;
            this.menu = menu;
            // Display data from database in resized widgets
            TextView restaurantNameTextView = findViewById(R.id.restaurant_main_activity_name);
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
}
