package com.example.frederic.genericapp.activities;





import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.TextView;

import com.example.frederic.genericapp.R;
import com.example.frederic.genericapp.fragments.RestaurantMenuFragment;

public class RestaurantMenuActivity extends FragmentActivity{
    int height;
    int width;
    int image_size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_menu);
        // Get screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.height = displayMetrics.heightPixels;
        this.width = displayMetrics.widthPixels;
        this.image_size = (this.height/6 < this.width/3) ? this.height/6 : this.width/3;

        // Set widget size
        TextView headerTextView = findViewById(R.id.header_title);
        headerTextView.setText("Menu");
        headerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,40);

        // Add fragment
        FragmentManager fm = getSupportFragmentManager();

        // add
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(R.id.restaurant_menu_fragment_holder, new RestaurantMenuFragment());
        //ft.addToBackStack(null);
        ft.commit();


    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
