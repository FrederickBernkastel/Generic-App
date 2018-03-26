package com.example.frederic.genericapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
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

import org.json.JSONArray;
import org.json.JSONObject;


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

        // TODO: INSERT RESTAURANT MENU FOR DEBUGGING, DELETE WHEN DONE
        JSONArray array = new JSONArray();
        JSONObject json=new JSONObject();
        FoodStatuses foodStatuses = new FoodStatuses();
        try {
            json.put("name", "Ugandan Cuisine");
            json.put("imagehyperlink", "http://a57.foxnews.com/media2.foxnews.com/2016/06/09/640/360/060916_chew_crispychicken_1280.jpg");
            JSONObject obj1 = new JSONObject();
            obj1.put("food_id", 999);
            obj1.put("price", "5.00");
            obj1.put("currency", "S$*");
            obj1.put("name", "Baagaa");
            obj1.put("description", "Meat so fresh, you can still hear it baa-ing");
            obj1.put("image_link", "https://lh3.googleusercontent.com/alX3SlsbUt4ZBZ1ct6efz5wxIcjM6S3Gva_pstMNXGjlFAQRr6CbpwFyFNoixBgPOGXxQi7vqC3U0CDT8oGz4lu4IZWzifs40owj_jA=w600-l68");
            array.put(obj1);
            obj1 = new JSONObject();
            obj1.put("food_id", 1000);
            obj1.put("price", "1.99");
            obj1.put("currency", "S$*");
            obj1.put("name", "Fries");
            obj1.put("description", "The straightest thing you'll put in your mouth every year");
            obj1.put("image_link", "https://chiosrotisserie.com/wp-content/uploads/2017/07/fries.jpg");
            array.put(obj1);
            obj1 = new JSONObject();
            obj1.put("food_id", 1001);
            obj1.put("price", "1.49");
            obj1.put("currency", "S$*");
            obj1.put("name", "Coca-cola");
            obj1.put("description", "Is pepsi OK?");
            obj1.put("image_link", "https://i5.walmartimages.com/asr/791c580c-9a80-4d53-b972-50c78a935d72_1.8a4d4ced51a177d1c4dbbfb823d696f5.jpeg");
            array.put(obj1);
            obj1 = new JSONObject();
            obj1.put("food_id", 1002);
            obj1.put("price", "0.99");
            obj1.put("currency", "S$*");
            obj1.put("name", "Chocolate Soft-serve Ice-cream");
            obj1.put("description", "‎💩");
            obj1.put("image_link", "http://dlitesshoppe.com/wp-content/uploads/2015/03/Dlites_chocolate.png");
            array.put(obj1);
            obj1 = new JSONObject();
            obj1.put("food_id", 1003);
            obj1.put("price", "450.00");
            obj1.put("currency", "S$*");
            obj1.put("name", "Duluxe Pizza Pie (Foie grass, truffles, cavier)");
            obj1.put("description", "Can't decide between junk food and fine dining? Look no further, for our Duluxe Pizza Pie is sure to satisfy your cravings. Topped with 24-karat gold flakes, Foie grass, truffles, cavier, and baked with the finest cheese fresh from a bull's udder, this meal is sure to help you leave your toilet shinier than before you used it.\n\n\nNote: Price per slice, not pizza");
            obj1.put("image_link", "http://finedininglovers.cdn.crosscast-system.com/BlogPost/l_7853_expensive.pizza-2.jpg");
            array.put(obj1);
            json.put("menu", array);
            String s = json.toString();
            RestaurantMenu savedMenu = DatabaseConnector.parseJSONMenu(s);
            new SharedPrefManager<RestaurantMenu>().saveObj(getString(R.string.key_restaurant_menu), savedMenu, MyBillActivity.this);
            foodStatuses = new FoodStatuses();
            foodStatuses.addStatus(999, true);
            foodStatuses.addStatus(999, true);
            foodStatuses.addStatus(1002, true);

        } catch(Exception e){
            System.out.println("Debug failure");
        }
        refreshExistingOrders(foodStatuses);
        // END OF DEBUG
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
        // Extract restaurant menu
        RestaurantMenu menu = new SharedPrefManager<RestaurantMenu>().fetchObj(getString(R.string.key_restaurant_menu),MyBillActivity.this,RestaurantMenu.class);

        // Reset views
        GridAdapter gridAdapter = new GridAdapter(MyBillActivity.this,foodStatuses,menu);
        gridView.setAdapter(gridAdapter);
        totalPrice.setText(gridAdapter.getTotalPrice());
    }

    @Override
    public void fetchFinish(FetchedObject output) {
        if (output==null){
            // TODO: Error connecting to server, send to ErrorActivity
            return;
        }
        switch (output.fetchMode){
            case EXISTINGORDERS:
                FoodStatuses foodStatuses = (FoodStatuses) output;
                // TODO: Inform user that not all orders have been fulfilled with a DialogFragment
                if (! foodStatuses.isAllFulfilled()){
                    onBackPressed();
                }

                // Refresh gridView and update price
                refreshExistingOrders(foodStatuses);
                break;
        }
    }

    // TODO: Handle billing
    public void onBillingRequestPress(View v){

    }
}

class GridAdapter extends BaseAdapter{
    private Context context;
    private String[] gridValues;
    private static final String[] GRID_HEADERS = new String[]{
            "Item",
            "Qty",
            "Price"
    };
    private Double totalPrice;
    // Stores last MenuItem in order to access its Price Formatting methods
    private MenuItem lastMenuItem;

    // TODO: REDO ADAPTER TO INFLATE A XML ROW
    GridAdapter(Context context, FoodStatuses foodStatuses,RestaurantMenu menu){
        // Init
        gridValues = new String[3+foodStatuses.statuses.size()*3];
        int i = 0;
        totalPrice = 0.;
        this.context = context;

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
            totalPrice += item.priceVal*foodStatus.delivered;

            lastMenuItem = item;

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

        // Check if view already exists
        if (view == null) {
            TextView textView = new TextView(context);
            textView.setText(gridValues[i]);
            textView.setPadding(20,0,20,0);

            // Check if headers
            if (i<3){
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
            } else {
                // Check if item name
                if (i%3==0) {
                    textView.setSingleLine(false);

                }
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
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

    public String getTotalPrice(){
        return lastMenuItem.formatPrice(totalPrice);
    }
}
