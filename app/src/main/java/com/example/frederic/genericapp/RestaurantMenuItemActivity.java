package com.example.frederic.genericapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Locale;

public class RestaurantMenuItemActivity extends AppCompatActivity {
    MenuItem menuItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_menu_item);

        // Get menuItem from previous activity's intent
        Bundle data = getIntent().getExtras();
        menuItem = data.getParcelable(getString(R.string.key_menu_item));

        // Get screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        int button_size = (height/8<width/5)?height/8:width/5;

        // Get references to widgets
        TextView itemName = findViewById(R.id.restaurant_menu_item_name);
        TextView itemDescriptionPrice = findViewById(R.id.restaurant_menu_item_description_price);
        ImageView itemImage = findViewById(R.id.restaurant_menu_item_image);
        ImageView plusButton = findViewById(R.id.restaurant_menu_item_plus);
        ImageView minusButton = findViewById(R.id.restaurant_menu_item_minus);
        Button addButton = findViewById(R.id.restaurant_menu_item_add_button);

        // Set relevant text
        itemName.setText(menuItem.name);
        itemDescriptionPrice.setText(menuItem.description);

        // Set / Resize images
        ImageResize.loadImageByUrl(RestaurantMenuItemActivity.this,menuItem.imageURL.toString(),itemImage,width,height/8*3);
        plusButton.setImageBitmap(ImageResize.decodeSampledBitmapFromResource(
                getResources(),
                R.drawable.activity_restaurant_menu__item_plus,
                button_size,
                button_size
        ));
        minusButton.setImageBitmap(ImageResize.decodeSampledBitmapFromResource(
                getResources(),
                R.drawable.activity_restaurant_menu__item_minus,
                button_size,
                button_size
        ));

        // This will disable the Soft Keyboard from appearing by default
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }


    void onMinusButtonClick(View v){
        // Reduce item quantity if > 0
        TextView itemQuantityTextview = findViewById(R.id.restaurant_menu_item_quantity);
        int itemQuantity = Integer.valueOf(String.valueOf(itemQuantityTextview.getText()));
        if (itemQuantity>0){
            itemQuantityTextview.setText(String.valueOf(itemQuantity-1));
            // Show description instead of price if needed
            if(itemQuantity==1){
                TextView descriptionPriceTextView = findViewById(R.id.restaurant_menu_item_description_price);
                descriptionPriceTextView.setText(menuItem.description);
                descriptionPriceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            } else {
                String price = getItemPrice(menuItem,itemQuantity - 1);
                TextView descriptionPriceTextView = findViewById(R.id.restaurant_menu_item_description_price);
                descriptionPriceTextView.setText(price);
                descriptionPriceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,40);
            }
        }
    }
    void onPlusButtonClick(View v){
        // Increase item quantity if < 10
        TextView itemQuantityTextview = findViewById(R.id.restaurant_menu_item_quantity);
        int itemQuantity = Integer.valueOf(String.valueOf(itemQuantityTextview.getText()));
        if (itemQuantity<9){
            itemQuantityTextview.setText(String.valueOf(itemQuantity + 1));

            String price = getItemPrice(menuItem,itemQuantity + 1);
            TextView descriptionPriceTextView = findViewById(R.id.restaurant_menu_item_description_price);
            descriptionPriceTextView.setText(price);
            descriptionPriceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,40);

        }
    }
    private String getItemPrice(MenuItem menuItem,int itemQuantity){
        // Update price
        double itemPrice;
        String currency = "$";
        try {
            itemPrice = Double.parseDouble(menuItem.price.split(" ")[1]);
            currency = menuItem.price.split(" ")[0];
        } catch (IndexOutOfBoundsException e){
            itemPrice = Double.parseDouble(menuItem.price.split(" ")[0]);
        }
        itemPrice *= itemQuantity;
        return String.format(Locale.US,"%s%.2f",currency,itemPrice);
    }

    void onAddItemClick(View v){
        TextView itemQuantityTextview = findViewById(R.id.restaurant_menu_item_quantity);
        String specialRequests = ((EditText)findViewById(R.id.restaurant_menu_item_requests)).getText().toString();
        int itemQuantity = Integer.valueOf(String.valueOf(itemQuantityTextview.getText()));
        // Check if valid itemQuantity
        if (itemQuantity>0) {
            // Save Order Locally


            onBackPressed();
        }
    }
}
