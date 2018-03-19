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

import com.example.frederic.genericapp.R;
import com.example.frederic.genericapp.SharedPrefManager;

/**
 * Main menu of activity
 * Created by: Frederick Bernkastel
 */
public class MainActivity extends Activity {

    final int PERMISSION_TO_READ_NUMBER = 1;
    String plid;
    TelephonyManager tMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if phone number exists
        this.plid = new SharedPrefManager<String>().fetchObj(getString(R.string.key_plid),MainActivity.this,String.class);
        // Extract phone number if it does not exists
        if(plid==null) {
            tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_PHONE_STATE},PERMISSION_TO_READ_NUMBER);
            }

        }

        // TODO: Check for unpaid bills

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
                        // TODO: Exception in getting phone number (no SIM card?) Time for plan B
                    }


                } else {
                    // TODO: Permission denied, boo! Time for plan B
                }
                return;
            }
        }
    }


}
