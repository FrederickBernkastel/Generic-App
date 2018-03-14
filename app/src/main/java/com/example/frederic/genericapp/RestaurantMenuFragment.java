package com.example.frederic.genericapp;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import java.util.HashMap;


/**
 * Fragment of RestaurantMenuActivity to hold menu categories / items
 * Created by: Frederick Bernkastel
 */
public class RestaurantMenuFragment extends Fragment {
    private int height;
    private int width;
    private int image_size;
    private int num_of_rows = 0;
    private static HashMap<Integer,MenuItem> menuHashMap;
    private TableLayout tableLayout;
    private MenuMode menuMode=MenuMode.FOODITEMS;
    public enum MenuMode{
        CATEGORIES,
        FOODITEMS
    }

    public RestaurantMenuFragment() {
        // Required empty public constructor
    }
    public void setMenuMode(MenuMode menuMode){
        this.menuMode=menuMode;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_restaurant_menu, container, false);

        // Save reference to root view
        tableLayout = v.findViewById(R.id.restaurant_menu_table_layout);

        // Get screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.height = displayMetrics.heightPixels;
        this.width = displayMetrics.widthPixels;
        this.image_size = (this.height/6 < this.width/3) ? this.height/6 : this.width/3;

        // Add different items depending on mode
        switch(menuMode) {
            case FOODITEMS:
                // Fetch restaurant menu from sharedPreferences
                RestaurantMenu menu = new SharedPrefManager<RestaurantMenu>().fetchObj(getString(R.string.key_restaurant_menu), getActivity(), RestaurantMenu.class);
                // Instantiate menuHashMap
                menuHashMap = new HashMap<>();

                // Insert menu items into table row
                for (int i = 0; i < menu.menu.size(); i++) {
                    insertMenuItemTableEntry(menu.menu.get(i));
                }
                break;
            case CATEGORIES:
                //TODO: Implement support for food categories
                break;
        }


        return v;

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void insertMenuItemTableEntry(MenuItem item){
        // Create new widgets, and get reference to existing widgets
        TableRow tableRow = new TableRow(getContext());
        ImageView itemImageView = new ImageView(getContext());
        LinearLayout linearLayoutVertical = new LinearLayout(getContext());
        TextView itemTextView = new TextView(getContext());
        TextView priceTextView = new TextView(getContext());


        // Set text / images
        itemTextView.setText(item.name);
        itemTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
        itemTextView.setTextColor(Color.parseColor("#000000"));
        priceTextView.setText(item.price);
        priceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);

        try {
            ImageResize.loadImageByUrl(
                    getContext(),
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



        // Add widgets to layout
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

                // Start RestaurantMenuItemActivity
                Intent intent = new Intent(getContext(),RestaurantMenuItemActivity.class);
                intent.putExtra(getString(R.string.key_menu_item),item);
                startActivity(intent);
            }
        });
        num_of_rows+=1;

    }

}
