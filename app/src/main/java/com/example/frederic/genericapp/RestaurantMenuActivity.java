package com.example.frederic.genericapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class RestaurantMenuActivity extends AppCompatActivity{
    int height;
    int width;
    int image_size;
    static int num_of_rows = 0;
    static HashMap<Integer,MenuItem> menuHashMap;

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

        // Fetch restaurant menu from sharedPreferences
        RestaurantMenu menu = new SharedPrefManager<RestaurantMenu>().fetchObj(getString(R.string.key_restaurant_menu),RestaurantMenuActivity.this,RestaurantMenu.class);
        // Instantiate menuHashMap
        menuHashMap = new HashMap<>();
        // Insert menu items into table row
        for (int i =0;i<menu.menu.size();i++){
            insertTableEntry(menu.menu.get(i));
        }
    }
    private void insertTableEntry(MenuItem item){

        // Create new widgets, and get reference to existing widgets
        TableLayout tableLayout = findViewById(R.id.restaurant_menu_table_layout);
        TableRow tableRow = new TableRow(this);
        ImageView itemImageView = new ImageView(this);
        LinearLayout linearLayoutVertical = new LinearLayout(this);
        TextView itemTextView = new TextView(this);
        TextView priceTextView = new TextView(this);
        TextView headerTextView = findViewById(R.id.header_title);

        // Set text / images
        itemTextView.setText(item.name);
        itemTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
        itemTextView.setTextColor(Color.parseColor("#000000"));
        priceTextView.setText(item.price);
        priceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        headerTextView.setText("Menu");
        headerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,40);
        try {
            ImageResize.loadImageByUrl(
                    this,
                    item.imageURL.toString(),
                    itemImageView,
                    image_size,
                    image_size
            );
        } catch (Exception e){
            System.out.println("Load form URL failed");
        }


        // Set layout params
        linearLayoutVertical.setOrientation(LinearLayout.VERTICAL);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT
        );
        if (num_of_rows==0) {
            params1.setMargins(0, 0, this.width / 20, 0);
        } else {
            params1.setMargins(0, this.height / 40, this.width / 20, 0);
        }
        itemImageView.setLayoutParams(params1);
        itemTextView.setMaxWidth(this.width-this.image_size-this.width/10);
        itemTextView.setGravity(Gravity.START);
        linearLayoutVertical.setLayoutParams(params1);
        priceTextView.setGravity(Gravity.END|Gravity.BOTTOM);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        priceTextView.setLayoutParams(params2);



        // Add new widgets to layout
        linearLayoutVertical.addView(itemTextView);
        linearLayoutVertical.addView(priceTextView);
        tableRow.addView(itemImageView);
        tableRow.addView(linearLayoutVertical);
        tableLayout.addView(tableRow);

        // Configure onClick to tableRow
        tableRow.setId(num_of_rows);
        menuHashMap.put(num_of_rows,item);
        tableRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MenuItem item = menuHashMap.get(view.getId());

                //TODO: Handle selected menu item
                Toast.makeText(RestaurantMenuActivity.this,item.name,Toast.LENGTH_LONG).show();
            }
        });
        num_of_rows+=1;
    }



}
