package com.example.frederic.genericapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;

public class RestaurantMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_main);

        // Get screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
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



    }
    // TODO: Backtrack to MainActivity, iff nothing ordered yet
    public void onRestaurantMainBackClick(View v){

    }
    // TODO: Backtrack to MainActivity, iff nothing ordered yet
    public void onRestaurantMainHotDishClick(View v){

    }
    // TODO: Backtrack to MainActivity, iff nothing ordered yet
    public void onRestaurantMainMenuClick(View v){

    }
    // TODO: Backtrack to MainActivity, iff nothing ordered yet
    public void onRestaurantMainOrdersClick(View v){

    }
    // TODO: Backtrack to MainActivity, iff nothing ordered yet
    public void onRestaurantMainBillClick(View v){

    }

    @Override
    public void onBackPressed() {
        //TODO: Allow onBackPressed iff no orders sent to kitchen, or no time
        //super.onBackPressed();
    }
}
