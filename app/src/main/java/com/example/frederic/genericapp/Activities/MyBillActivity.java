package com.example.frederic.genericapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.example.frederic.genericapp.Data.AsyncFetchResponse;
import com.example.frederic.genericapp.Data.DatabaseConnector;
import com.example.frederic.genericapp.Data.FetchedObject;
import com.example.frederic.genericapp.Data.FoodStatus;
import com.example.frederic.genericapp.Data.FoodStatuses;
import com.example.frederic.genericapp.Data.MenuItem;
import com.example.frederic.genericapp.Data.RestaurantMenu;
import com.example.frederic.genericapp.R;
import com.example.frederic.genericapp.SharedPrefManager;


public class MyBillActivity extends Activity implements AsyncFetchResponse{

    GridView gridView;
    TextView totalPrice;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bill);



        gridView = findViewById(R.id.my_bill_activity_grid);
        totalPrice = findViewById(R.id.my_bill_activity_price);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fetch order status from server
        String plid = new SharedPrefManager<String>().fetchObj(getString(R.string.key_plid),MyBillActivity.this,String.class);
        try {
            DatabaseConnector.FetchTaskInput input = new DatabaseConnector.FetchTaskInput(plid, DatabaseConnector.FetchMode.EXISTINGORDERS);
            new DatabaseConnector.FetchTask(MyBillActivity.this).execute(input);
        } catch (Exception e){
            System.out.println("Error parsing DatabaseConnector input in MyBillActivity. Was the correct mode used?");
            // Terminate this activity
            this.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            return;
        }
    }

    private void refreshExistingOrders(FoodStatuses foodStatuses){

        for (FoodStatus foodStatus : foodStatuses.statuses){

        }
    }

    @Override
    public void fetchFinish(FetchedObject output) {
        if (output==null){
            // TODO: Error connecting to server, send to ErrorActivity
            return;
        }
        switch (output.fetchMode){
            case EXISTINGORDERS:
                // Check if all orders fulfilled
                // Refresh gridView and update price
                break;
        }
    }
}

class GridAdapter extends BaseAdapter{
    private Context context;
    private String[] gridValues;
    private static final String[] GRID_HEADERS = new String[]{
            "Item",
            "Quantity",
            "Price"
    };

    GridAdapter(Context context, FoodStatuses foodStatuses,RestaurantMenu menu){
        gridValues = new String[3+foodStatuses.statuses.size()];
        int i = 0;
        // Add headers
        for (;i<GRID_HEADERS.length;i++){
            gridValues[i] = GRID_HEADERS[i];
        }

        // Add Food name / quantity / price
        for (FoodStatus foodStatus: foodStatuses.statuses){
            MenuItem item = menu.findItem(foodStatus.food_id);
            gridValues[i++] = item.name;
            gridValues[i++] = String.valueOf(foodStatus.delivered);

            // TODO: Simplification of price used as substitution for actual
            gridValues[i++] = item.formatPrice(item.priceVal*foodStatus.delivered);

        }
    }

    /**
     *
     * @param i             Position of grid cell, starts from 0 in upper left
     * @param view          View in grid, which may already exist
     * @param viewGroup     Parent view
     * @return              View to be in this cell
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        if (view == null) {
            TextView textView = new TextView(context);
            textView.setText(gridValues[i]);
            if (i<3){
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            } else {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
                if(i%3==0){
                    textView.setTextColor(Color.parseColor("#000000"));
                }
            }

            return textView;


        } else {
            return view;
        }


    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getCount() {
        return gridValues.length;
    }
}
