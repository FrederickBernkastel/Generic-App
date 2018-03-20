/*
    Main Menu Activity
*/

package com.example.frederic.genericapp.Activities;

import android.Manifest;
import android.app.Activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;

import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.frederic.genericapp.Data.AsyncFetchResponse;
import com.example.frederic.genericapp.Data.DatabaseConnector;
import com.example.frederic.genericapp.Data.FetchedObject;
import com.example.frederic.genericapp.Data.FoodStatuses;
import com.example.frederic.genericapp.R;
import com.example.frederic.genericapp.SharedPrefManager;

/**
 * Main menu of activity
 * Created by: Frederick Bernkastel
 */
public class MainActivity extends Activity implements AsyncFetchResponse{

    final int PERMISSION_TO_READ_NUMBER = 1;
    String plid;
    TelephonyManager tMgr;

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

    @Override
    protected void onResume() {
        super.onResume();
        // Check if phone number exists
        this.plid = new SharedPrefManager<String>().fetchObj(getString(R.string.key_plid),MainActivity.this,String.class);
        // Extract phone number if it does not exists
        if(plid==null) {
            tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_PHONE_STATE},PERMISSION_TO_READ_NUMBER);
            }

        }

        // Check for unpaid bills
        DatabaseConnector.FetchTaskInput input;
        try {
            input = new DatabaseConnector.FetchTaskInput(plid, DatabaseConnector.FetchMode.EXISTINGORDERS);
        } catch (Exception e){
            System.out.println("Error parsing DatabaseConnector input in MainActivity. Was the correct mode used?");
            // Terminate this activity, and close entire app
            this.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            return;
        }

        new DatabaseConnector.FetchTask(this).execute(input);
    }

    public void onGuestButtonClick(View v){
        /*
        // DEBUG PORTION
        Intent intentDebug = new Intent(MainActivity.this, RestaurantMainActivity.class);
        startActivity(intentDebug);
        // END OF DEBUG PORTION
        */
        Intent intent = new Intent(MainActivity.this, RestaurantTableInputActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_TO_READ_NUMBER: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, yay!
                    try {
                        // Extract phone number and save locally
                        this.plid = tMgr.getLine1Number();
                        new SharedPrefManager<String>().saveObj(getString(R.string.key_plid),this.plid,MainActivity.this);

                    } catch (SecurityException e){
                        // TODO: Exception in getting phone number (no SIM card / encrypted SIM card?) Time for plan B
                    }


                } else {
                    // TODO: Permission denied, boo! Time for plan B
                }
                return;
            }
        }
    }

    @Override
    public void fetchFinish(FetchedObject output) {
        // Check if fetchObject is null
        if (output==null){
            // TODO: Unable to connect to server, link to ErrorActivity
            return;
        }
        FoodStatuses foodStatuses = (FoodStatuses) output;
        if(foodStatuses.statuses.size()>0){
            // Server indicates that there are unpaid bills, launch restaurant menu
            Intent intent = new Intent(MainActivity.this, RestaurantMainActivity.class);
            startActivity(intent);
        }
    }
}
