package com.example.frederic.genericapp.Fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.example.frederic.genericapp.Activities.RestaurantMenuItemActivity;
import com.example.frederic.genericapp.Data.FoodBatchOrder;
import com.example.frederic.genericapp.Data.FoodOrder;
import com.example.frederic.genericapp.Data.MenuItem;
import com.example.frederic.genericapp.Data.RestaurantMenu;
import com.example.frederic.genericapp.R;
import com.example.frederic.genericapp.SharedPrefManager;

import java.util.ArrayList;
import java.util.Locale;


/**
 * A fragment to display pending orders
 * Created by: Frederick Bernkastel
 */
public class MyPendingOrdersFragment extends Fragment {
    TableLayout table;
    TextView totalPriceTextView;
    TextView priceLabelTextView;
    public FoodBatchOrder pendingOrders;
    Button orderButton;
    int uniqueItemsOrdered = 0;
    int width;

    ConfirmOrderListener mListener;

    public interface ConfirmOrderListener{
        public void onConfirmSelected(MyPendingOrdersFragment fragment);
    }
    public MyPendingOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_pending_orders, container, false);

        // Get screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;

        // Instantiate and reference relevant widgets
        totalPriceTextView = v.findViewById(R.id.pending_orders_fragment_price);
        priceLabelTextView = v.findViewById(R.id.pending_orders_fragment_price_label);
        orderButton = v.findViewById(R.id.pending_orders_fragment_button);
        table = v.findViewById(R.id.pending_orders_fragment_table);

        // Attach onClick to button
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOrderNowClick(view);
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Delete previously loaded items
        deleteAllMenuItemTableEntry();

        // Load pending orders
        pendingOrders = new SharedPrefManager<FoodBatchOrder>().fetchObj(
                getString(R.string.key_batch_orders),
                getContext(),
                FoodBatchOrder.class
        );
        // Hide buttons/text if no orders
        if (pendingOrders==null){
            Toast.makeText(getContext(),"No pending orders",Toast.LENGTH_SHORT).show();
            totalPriceTextView.setVisibility(View.GONE);
            priceLabelTextView.setVisibility(View.GONE);
            orderButton.setVisibility(View.GONE);

            return;
        }

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
            displayedIDs.add(foodOrder.foodId);
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

        // Set layout params for item name/quantity
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        quantityNameTextView.setLayoutParams(params1);
        quantityNameTextView.setGravity(Gravity.START);
        quantityNameTextView.setPadding(20,0,5,10);
        quantityNameTextView.setSingleLine(false);
        quantityNameTextView.setVerticalScrollBarEnabled(true);
        quantityNameTextView.setMaxWidth(width*5/8);

        // Set layout params for price
        params1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        priceTextView.setLayoutParams(params1);
        priceTextView.setGravity(Gravity.END);
        priceTextView.setPadding(5,20,20,20);




        // Add child views
        layout.addView(quantityNameTextView);
        layout.addView(priceTextView);

        // Set onClick for layout
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                RestaurantMenu menu = new SharedPrefManager<RestaurantMenu>().fetchObj(getString(R.string.key_restaurant_menu),getContext(),RestaurantMenu.class);
                MenuItem item = menu.findItem(id);

                // Start RestaurantMenuItemActivity
                Intent intent = new Intent(getContext(),RestaurantMenuItemActivity.class);
                intent.putExtra(getString(R.string.key_menu_item),item);
                startActivity(intent);
            }
        });

        // Add row to table
        table.addView(layout);

        uniqueItemsOrdered++;
    }

    private void deleteAllMenuItemTableEntry(){
        table.removeAllViews();
        uniqueItemsOrdered = 0;
    }

    public void onOrderNowClick(View v){
        // Ask for user confirmation in activity
        mListener.onConfirmSelected(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ConfirmOrderListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement ConfirmOrderListener");
        }
    }
}

