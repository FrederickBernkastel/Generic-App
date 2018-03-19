package com.example.frederic.genericapp.Fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.frederic.genericapp.Data.AsyncFetchResponse;
import com.example.frederic.genericapp.Data.DatabaseConnector;
import com.example.frederic.genericapp.Data.FetchedObject;
import com.example.frederic.genericapp.Data.FoodBatchOrder;
import com.example.frederic.genericapp.Data.FoodOrder;
import com.example.frederic.genericapp.Data.FoodStatus;
import com.example.frederic.genericapp.Data.FoodStatuses;
import com.example.frederic.genericapp.Data.MenuItem;
import com.example.frederic.genericapp.Data.RestaurantMenu;
import com.example.frederic.genericapp.R;
import com.example.frederic.genericapp.SharedPrefManager;


import java.util.ArrayList;
import java.util.Locale;

/**
 * A fragment to display current orders
 * Created by: Frederick Bernkastel
 */
public class MyCurrentOrdersFragment extends Fragment implements AsyncFetchResponse{
    TableLayout table;
    TextView totalPriceTextView;
    TextView priceLabelTextView;
    int uniqueItemsOrdered = 0;
    int width;

    String plid="";
    FoodStatuses foodStatuses;

    public MyCurrentOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_my_current_orders, container, false);

        // Get screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;

        // Instantiate and reference relevant widgets
        totalPriceTextView = v.findViewById(R.id.current_orders_fragment_price);
        priceLabelTextView = v.findViewById(R.id.current_orders_fragment_price_label);
        table = v.findViewById(R.id.current_orders_fragment_table);

        // TODO: Extract plid

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Delete all previously loaded items
        table.removeAllViews();

        retrieveCurrentOrders();

        repopulate_table();
    }
    private void retrieveCurrentOrders(){

        // Get server data
        DatabaseConnector.FetchTaskInput input;
        try {
            input = new DatabaseConnector.FetchTaskInput(plid, DatabaseConnector.FetchMode.EXISTINGORDERS);
        } catch(Exception e){
            System.out.println("Error constructing DatabaseConnector input, was the wrong mode used?");
            System.out.println(e.getMessage());
            return;
        }
        // Starts AsyncTask
        new DatabaseConnector.FetchTask(MyCurrentOrdersFragment.this).execute(input);
    }
    private void repopulate_table(){

        double totalPrice=0;
        // Load menu
        RestaurantMenu menu = new SharedPrefManager<RestaurantMenu>().fetchObj(
                getString(R.string.key_restaurant_menu),
                getContext(),
                RestaurantMenu.class
        );

        // Sort foodStatuses
        foodStatuses.sortStatuses();

        // Create rows for unfulfilled items first
        String currency = "$";

        for(FoodStatus foodStatus:foodStatuses.statuses){
            int foodId = foodStatus.food_id;

            MenuItem menuItem = menu.findItem(foodId);
            insertMenuItemTableEntry(menuItem, foodStatus);
            double itemPrice;

            try {
                itemPrice = Double.parseDouble(menuItem.price.split(" ")[1]);
                currency = menuItem.price.split(" ")[0];
            } catch (NumberFormatException e) {
                itemPrice = Double.parseDouble(menuItem.price.split(" ")[0]);
                currency = menuItem.price.split(" ")[1];
            }

            totalPrice += itemPrice * (foodStatus.delivered + foodStatus.pending);
        }

        // Set total price
        totalPriceTextView.setText(String.format(Locale.US,"%s %.2f",currency,totalPrice));

    }
    private void insertMenuItemTableEntry(MenuItem menuItem,FoodStatus foodStatus){
        // Create relevant views
        RelativeLayout layout = new RelativeLayout(getContext());
        TextView itemNameTextView = new TextView(getContext());
        TextView itemFulfilledTextView = new TextView(getContext());

        layout.setId(uniqueItemsOrdered);

        // Set text
        itemNameTextView.setText(menuItem.name);
        itemNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        itemNameTextView.setTextColor(Color.parseColor("#000000"));
        itemFulfilledTextView.setText(String.format(Locale.US,"%d / %d",foodStatus.pending,foodStatus.pending+foodStatus.delivered));
        itemFulfilledTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        itemFulfilledTextView.setTextColor(Color.parseColor("#000000"));

        // Set layout params
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        itemNameTextView.setLayoutParams(params1);
        itemNameTextView.setGravity(Gravity.START);
        itemNameTextView.setPadding(20,0,5,10);
        itemNameTextView.setMaxWidth(width*5/8);

        params1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        itemFulfilledTextView.setLayoutParams(params1);
        itemFulfilledTextView.setPadding(5,0,20,10);


        // Add views to layout
        layout.addView(itemNameTextView);
        layout.addView(itemFulfilledTextView);
        table.addView(layout);

        uniqueItemsOrdered++;
    }

    @Override
    public void fetchFinish(FetchedObject output) {
        // Store data
        foodStatuses = (FoodStatuses) output;
        repopulate_table();
    }
}
