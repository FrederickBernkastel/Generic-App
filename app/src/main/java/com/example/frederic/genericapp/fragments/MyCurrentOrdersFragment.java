package com.example.frederic.genericapp.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.frederic.genericapp.activities.ErrorActivity;
import com.example.frederic.genericapp.data.get.AsyncFetchResponse;
import com.example.frederic.genericapp.data.get.FetchedObject;
import com.example.frederic.genericapp.data.get.FoodStatus;
import com.example.frederic.genericapp.data.get.FoodStatuses;
import com.example.frederic.genericapp.data.get.MenuItem;
import com.example.frederic.genericapp.data.get.RestaurantMenu;
import com.example.frederic.genericapp.R;
import com.example.frederic.genericapp.SharedPrefManager;


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
    RefreshOrderListener mListener;

    // Interface for fragment to communicate to activity, to request refreshing orders
    public interface RefreshOrderListener{
        void onRefreshSelected(MyCurrentOrdersFragment fragment);
    }

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

        // Extract plid
        plid = new SharedPrefManager<String>().fetchObj(getString(R.string.key_plid),getContext(),String.class);

        v.findViewById(R.id.current_orders_fragment_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onRefreshSelected(MyCurrentOrdersFragment.this);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        mListener.onRefreshSelected(this);
    }

    private void repopulate_table(){
        // Delete all previously loaded items
        table.removeAllViews();

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
        double itemPrice;
        int foodId;
        MenuItem menuItem = null;

        for(FoodStatus foodStatus:foodStatuses.statuses){
            foodId = foodStatus.food_id;
            System.out.println(foodId);
            menuItem = menu.findItem(foodId);
            insertMenuItemTableEntry(menuItem, foodStatus);
            itemPrice = foodStatus.totalPrice;

            // Sums up total price
            totalPrice += itemPrice * (foodStatus.delivered + foodStatus.pending);
        }

        // Set total price
        if (menuItem != null) {
            totalPriceTextView.setText(menuItem.formatPrice(totalPrice));
        }

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
        itemFulfilledTextView.setText(String.format(Locale.US,"%d / %d",foodStatus.delivered,foodStatus.pending+foodStatus.delivered));
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

        // Launch ErrorActivity
        if (output==null){
            System.out.println("Error connecting to server in CurrentOrdersFragment");
            Intent intent = new Intent(getContext(), ErrorActivity.class);
            startActivity(intent);

            return;
        }
        // Store data
        foodStatuses = (FoodStatuses) output;
        repopulate_table();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (RefreshOrderListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement ConfirmOrderListener");
        }
    }

}
