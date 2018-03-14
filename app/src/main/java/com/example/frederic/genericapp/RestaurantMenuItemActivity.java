package com.example.frederic.genericapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.util.Locale;

/**
 * Activity to display a single menu item, and allow user to order that item
 * Created by: Frederick Bernkastel
 */
public class RestaurantMenuItemActivity extends AppCompatActivity {
    MenuItem menuItem;
    int numOfSpecialRequestVisible = 0;
    int height,width;
    final int VIEWSPERSPECIALREQUESTROW = 4;

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
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        int button_size = (height/8<width/5)?height/8:width/5;

        // Get references to widgets
        TextView itemName = findViewById(R.id.restaurant_menu_item_name);
        TextView itemDescription = findViewById(R.id.restaurant_menu_item_description);
        ImageView itemImage = findViewById(R.id.restaurant_menu_item_image);
        ImageView plusButton = findViewById(R.id.restaurant_menu_item_plus);
        ImageView minusButton = findViewById(R.id.restaurant_menu_item_minus);

        // Set relevant text
        itemName.setText(menuItem.name);
        itemDescription.setText(menuItem.description);

        // Set / Resize images
        int itemImageSize = (width<height/8*3)?width:height/8*3;
        ImageResize.loadImageByUrl(RestaurantMenuItemActivity.this,menuItem.imageURL.toString(),itemImage,itemImageSize,itemImageSize);
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
            TextView priceTextView = findViewById(R.id.restaurant_menu_item_price);
            // Show description instead of price if needed
            if(itemQuantity==1){
                TextView descriptionTextView = findViewById(R.id.restaurant_menu_item_description);
                descriptionTextView.setVisibility(View.VISIBLE);
                priceTextView.setText("");
                deleteSpecialRequestRow(0);

            } else {
                String price = getItemPrice(menuItem,itemQuantity - 1);
                priceTextView.setText(price);
                // Check if need to delete extra requests
                if (itemQuantity<=numOfSpecialRequestVisible){
                    deleteSpecialRequestRow((numOfSpecialRequestVisible-1)*VIEWSPERSPECIALREQUESTROW);
                }

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
            TextView priceTextView = findViewById(R.id.restaurant_menu_item_price);
            priceTextView.setText(price);
            // Hide description if quantity is not 0
            if (itemQuantity==0) {
                TextView descriptionTextView = findViewById(R.id.restaurant_menu_item_description);
                descriptionTextView.setVisibility(View.GONE);
                createNewSpecialRequestRow();
            } else {
                // Check if last special request row is filled, and there is the maximum allowed number of special request rows
                EditText lastSpecialRequestEditText = findViewById((numOfSpecialRequestVisible-1)*VIEWSPERSPECIALREQUESTROW+2);

                if (! lastSpecialRequestEditText.getText().toString().equals("") && itemQuantity==numOfSpecialRequestVisible){
                    createNewSpecialRequestRow();
                }
            }
        }
    }

    private String getItemPrice(MenuItem menuItem,int itemQuantity){
        // Update price
        double itemPrice;
        String currency;
        try {
            itemPrice = Double.parseDouble(menuItem.price.split(" ")[1]);
            currency = menuItem.price.split(" ")[0];
        } catch (NumberFormatException e){
            itemPrice = Double.parseDouble(menuItem.price.split(" ")[0]);
            currency = menuItem.price.split(" ")[1];
        }
        itemPrice *= itemQuantity;
        return String.format(Locale.US,"%s%.2f",currency,itemPrice);
    }

    void onAddItemClick(View v){
        TextView itemQuantityTextview = findViewById(R.id.restaurant_menu_item_quantity);
        int itemQuantity = Integer.valueOf(String.valueOf(itemQuantityTextview.getText()));
        // Check if valid itemQuantity
        if (itemQuantity>0) {
            // Save Order Locally
            SharedPrefManager<FoodBatchOrder> prefManager = new SharedPrefManager<>();
            FoodBatchOrder batchOrder = prefManager.fetchObj(getString(R.string.key_batch_orders),RestaurantMenuItemActivity.this,FoodBatchOrder.class);
            if (batchOrder==null){
                batchOrder=new FoodBatchOrder();
            }
            int food_id = menuItem.id;
            for(int item_idx=0;item_idx<itemQuantity;item_idx++){
                if (item_idx<numOfSpecialRequestVisible){
                    EditText specialOrderEditText = findViewById(item_idx*VIEWSPERSPECIALREQUESTROW+2);
                    String description = specialOrderEditText.getText().toString();
                    if (description.equals("")) {
                        batchOrder.insertFoodOrder(food_id);
                    } else {
                        batchOrder.insertFoodOrder(food_id, description);
                    }
                } else {
                    batchOrder.insertFoodOrder(food_id);
                }
            }

            // Inform user that order has been added
            String updateUser = String.format(Locale.US,"%d %s has been added to pending orders",itemQuantity,menuItem.name);
            Toast.makeText(RestaurantMenuItemActivity.this,updateUser,Toast.LENGTH_LONG).show();

            // Go back to menu
            onBackPressed();
        }
    }

    private void deleteSpecialRequestRow(int deletedRowID){
        // Extract row id, if id belongs to view inside row
        deletedRowID = deletedRowID - deletedRowID%VIEWSPERSPECIALREQUESTROW;
        // Check if valid row id
        if (deletedRowID<0 || deletedRowID>=VIEWSPERSPECIALREQUESTROW*numOfSpecialRequestVisible){

            return;
        }


        // Delete row
        TableLayout table = findViewById(R.id.restaurant_menu_item_special_request_table);
        TableRow tRowDelete = findViewById(deletedRowID);
        table.removeView(tRowDelete);

        // Update IDs of other rows
        for (int rowID = deletedRowID+VIEWSPERSPECIALREQUESTROW;rowID<numOfSpecialRequestVisible*VIEWSPERSPECIALREQUESTROW;rowID+=VIEWSPERSPECIALREQUESTROW){
            // Get reference to views in row
            TableRow tRow = findViewById(rowID);
            TextView requestNumber = findViewById(rowID+1);
            EditText editText = findViewById(rowID+2);
            ImageView deleteButton = findViewById(rowID+3);

            // Set new ID
            tRow.setId(rowID-VIEWSPERSPECIALREQUESTROW);
            requestNumber.setId(rowID+1-VIEWSPERSPECIALREQUESTROW);
            editText.setId(rowID+2-VIEWSPERSPECIALREQUESTROW);
            deleteButton.setId(rowID+3-VIEWSPERSPECIALREQUESTROW);

            // Update requestNumber text
            requestNumber.setText(getString(R.string.add_special_request_header,String.valueOf(rowID/VIEWSPERSPECIALREQUESTROW)));
        }
        numOfSpecialRequestVisible -= 1;

        // Create new row if last row has text
        EditText lastRowEditText = findViewById((numOfSpecialRequestVisible-1)*VIEWSPERSPECIALREQUESTROW+2);
        if (lastRowEditText==null||!lastRowEditText.getText().toString().equals("")){
            TextView quantityTextView = findViewById(R.id.restaurant_menu_item_quantity);

            // Check if creating new row will not exceed current food quantity
            if (numOfSpecialRequestVisible<Integer.valueOf(quantityTextView.getText().toString())) {
                createNewSpecialRequestRow();
            }
        }
    }
    private void createNewSpecialRequestRow(){
        // Get reference to widgets, and instantiate new ones
        TableLayout table = findViewById(R.id.restaurant_menu_item_special_request_table);
        TableRow tRow = new TableRow(RestaurantMenuItemActivity.this);
        LinearLayout tRowLinearLayout = new LinearLayout(RestaurantMenuItemActivity.this);
        LinearLayout buttonLinearLayout = new LinearLayout(RestaurantMenuItemActivity.this);
        final ImageView deleteButton = new ImageView(RestaurantMenuItemActivity.this);
        TextView requestNumber = new TextView(RestaurantMenuItemActivity.this);
        EditText editText = new EditText(RestaurantMenuItemActivity.this);


        // Set relevant IDs
        int rowID = numOfSpecialRequestVisible*(VIEWSPERSPECIALREQUESTROW);
        tRow.setId(rowID);
        requestNumber.setId(rowID+1);
        editText.setId(rowID+2);
        deleteButton.setId(rowID+3);

        // Set image for deleteButton
        deleteButton.setImageBitmap(ImageResize.decodeSampledBitmapFromResource(
                getResources(),
                R.drawable.activity_restaurant_menu__item__delete,
                height/16,
                height/16
        ));

        // Set and format TextView / EditText
        requestNumber.setText(getString(R.string.add_special_request_header,String.valueOf(numOfSpecialRequestVisible+1)));
        requestNumber.setTextColor(Color.parseColor("#000000"));
        requestNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        editText.setHint(R.string.special_request);
        editText.setSingleLine(false);
        editText.setVerticalScrollBarEnabled(true);




        //Set LayoutParams
        LinearLayout.LayoutParams params1;

        params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        editText.setLayoutParams(params1);
        editText.setMaxWidth(width*2/3);
        editText.setPadding(0,0,10,30);

        params1.gravity = Gravity.CENTER_VERTICAL;
        requestNumber.setLayoutParams(params1);
        requestNumber.setPadding(20,0,10,0);
        deleteButton.setLayoutParams(params1);

        params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonLinearLayout.setGravity(Gravity.END);
        buttonLinearLayout.setLayoutParams(params1);




        //Set OnClick for deleteButton
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int deletedRowID = view.getId();

                deleteSpecialRequestRow(deletedRowID);


            }
        });

        // Set TextWatcher for editText
        editText.addTextChangedListener(new CustomTextWatcher(editText));

        // Add corresponding views to their parent
        tRowLinearLayout.addView(requestNumber);
        tRowLinearLayout.addView(editText);
        tRowLinearLayout.addView(deleteButton);
        tRow.addView(tRowLinearLayout);
        table.addView(tRow);


        // Update row numbers
        numOfSpecialRequestVisible += 1;
    }
    class CustomTextWatcher implements TextWatcher{
        private EditText mEditText;
        // Constructor
        CustomTextWatcher(EditText editText){
            this.mEditText = editText;
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Get reference to view
            TextView itemQuantityTextView = findViewById(R.id.restaurant_menu_item_quantity);

            int itemQuantity = Integer.valueOf(itemQuantityTextView.getText().toString());

            // Add editTextRow if needed
            boolean isNewEntry = charSequence.equals("");
            boolean doesntExceedQuantity = numOfSpecialRequestVisible < itemQuantity;

            if (isLastRow() && doesntExceedQuantity){
                createNewSpecialRequestRow();
            }
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Delete editTextRow if needed
            boolean isEmptyEntry = charSequence.equals("");
            if(isEmptyEntry && !isLastRow()){
                int rowId = mEditText.getId();
                deleteSpecialRequestRow(rowId);
                numOfSpecialRequestVisible -= 1;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }

        private boolean isLastRow(){
            return mEditText.getId()/VIEWSPERSPECIALREQUESTROW==numOfSpecialRequestVisible-1;
        }
    }
}
