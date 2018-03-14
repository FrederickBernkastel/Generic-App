package com.example.frederic.genericapp;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


/**
 * A fragment to display pending orders
 * Created by: Frederick Bernkastel
 */
public class MyPendingOrdersFragment extends Fragment {
    TableLayout table;
    TextView totalPriceTextView;
    FoodBatchOrder pendingOrders;

    public MyPendingOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Toast.makeText(getContext(),"IN",Toast.LENGTH_LONG).show();
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_pending_orders, container, false);

        // Instantiate and reference relevant widgets
        totalPriceTextView = v.findViewById(R.id.pending_orders_fragment_price);
        table = getActivity().findViewById(R.id.pending_orders_fragment_table);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Load pending orders
        pendingOrders = new SharedPrefManager<FoodBatchOrder>().fetchObj(
                getString(R.string.key_batch_orders),
                getContext(),
                FoodBatchOrder.class
        );
        // Load menu
        RestaurantMenu menu = new SharedPrefManager<RestaurantMenu>().fetchObj(
                getString(R.string.key_restaurant_menu),
                getContext(),
                RestaurantMenu.class
        );

        // Iterate through current orders and add to table
        double totalPrice = 0.;
        String currency = "$";
        ArrayList<Integer> displayedIDs = new ArrayList<>();
        for(FoodOrder foodOrder:pendingOrders.foodOrders){
            if (displayedIDs.contains(foodOrder.foodId)){
                continue;
            }
            MenuItem menuItem = menu.findItem(foodOrder.foodId);
            insertMenuItemTableEntry(menuItem);
            double itemPrice;
            try{
                itemPrice = Double.parseDouble(menuItem.price.split(" ")[1]);
                currency = menuItem.price.split(" ")[0];
            } catch (NumberFormatException e){
                itemPrice = Double.parseDouble(menuItem.price.split(" ")[0]);
                currency = menuItem.price.split(" ")[1];
            }
            totalPrice+=itemPrice*pendingOrders.getItemCount(foodOrder.foodId);
        }

        // Set total price
        totalPriceTextView.setText(String.format(Locale.US,"%s %.2f",currency,totalPrice));

    }

    private void insertMenuItemTableEntry(MenuItem menuItem){
        // Instantiate and reference relevant widgets
        RelativeLayout layout = new RelativeLayout(getContext());
        TextView quantityNameTextView = new TextView(getContext());
        TextView priceTextView = new TextView(getContext());

        // Calculate & format relevant string values
        int quantity = pendingOrders.getItemCount(menuItem.id);
        String quantityNameText = quantity + " " + menuItem.name;
        double itemPrice;
        String currency;
        try {
            itemPrice = Double.parseDouble(menuItem.price.split(" ")[1]);
            currency = menuItem.price.split(" ")[0];
        } catch (NumberFormatException e){
            itemPrice = Double.parseDouble(menuItem.price.split(" ")[0]);
            currency = menuItem.price.split(" ")[1];
        }
        itemPrice *= quantity;
        String price = String.format(Locale.US,"%s%.2f",currency,itemPrice);

        // Set relevant text
        quantityNameTextView.setText(quantityNameText);
        quantityNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        quantityNameTextView.setTextColor(Color.parseColor("#000000"));
        quantityNameTextView.setSingleLine(false);
        priceTextView.setText(price);
        priceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        priceTextView.setTextColor(Color.parseColor("#000000"));

        // Set View Ids
        layout.setId(menuItem.id);

        // Set layout params
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        quantityNameTextView.setLayoutParams(params1);
        quantityNameTextView.setGravity(Gravity.START);
        quantityNameTextView.setPadding(5,0,5,0);
        params1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        priceTextView.setLayoutParams(params1);
        priceTextView.setGravity(Gravity.END);
        priceTextView.setPadding(5,20,5,20);

        // Add child views
        layout.addView(quantityNameTextView);
        layout.addView(priceTextView);

        // Set onClick for layout
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
            }
        });

    }
    public void onOrderNowClick(View v){

    }
}
