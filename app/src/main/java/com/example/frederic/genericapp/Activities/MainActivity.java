/*
    Main Menu Activity
*/

package com.example.frederic.genericapp.Activities;

import android.app.Activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.frederic.genericapp.R;

/**
 * Main menu of activity
 * Created by: Frederick Bernkastel
 */
public class MainActivity extends Activity {
    SharedPreferences prefs = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        // Set Guest Button params dynamically
        Button guestButton = findViewById(R.id.guest_button);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, height/10, 0, height/10);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        guestButton.setLayoutParams(params);

    }



    public void onGuestButtonClick(View v){
        // DEBUG PORTION
        Intent intentDebug = new Intent(MainActivity.this, RestaurantMainActivity.class);
        startActivity(intentDebug);
        // END OF DEBUG PORTION
        Intent intent = new Intent(MainActivity.this, RestaurantTableInputActivity.class);
        startActivity(intent);
    }

}