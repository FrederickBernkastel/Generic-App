package com.example.frederic.genericapp.activities;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frederic.genericapp.data.AsyncFetchResponse;
import com.example.frederic.genericapp.data.DatabaseConnector;
import com.example.frederic.genericapp.data.FetchedObject;
import com.example.frederic.genericapp.data.FoodBatchOrder;
import com.example.frederic.genericapp.data.RestaurantMenu;
import com.example.frederic.genericapp.data.TableNumResponse;
import com.example.frederic.genericapp.ImageResize;
import com.example.frederic.genericapp.fragments.PeopleFragment;
import com.example.frederic.genericapp.R;
import com.example.frederic.genericapp.SharedPrefManager;
import com.example.frederic.genericapp.fragments.TableFragment;

import java.util.ArrayList;

import static android.media.CamcorderProfile.get;

public class RestaurantTableInputActivity extends AppCompatActivity implements AsyncFetchResponse {
    private ArrayList<TextView> textViewList;
    private int textViewListPtr=0;
    public static ArrayList<TextView> viewList;
    private int tableNumber;
    private int peopleNumber;
    private String randomPaylahId;

    enum FetchState{
        ISPEOPLE,
        ISTABLE,
        ISMENU
    }
    FetchState currState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_table_input);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        TableFragment tableFragment = new TableFragment();
        fragmentTransaction.add(R.id.fragment_container, tableFragment);
        fragmentTransaction.commit();
        currState = FetchState.ISTABLE;

        randomPaylahId = new SharedPrefManager<String>().fetchObj(getString(R.string.key_plid),RestaurantTableInputActivity.this,String.class);

        // Get screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

/*        // Dynamically create textviews, and store in textViewList
        textViewList = new ArrayList<>();
        LinearLayout linearLayout = findViewById(R.id.displayLinearLayout);
        for (int i=0;i<numOfDigits;i++){
            TextView textView = new TextView(this);
            textView.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            );
            textView.setText("-");
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, height/7);
            textViewList.add(textView);
            linearLayout.addView(textView);
        }*/

        // Dynamically create buttons 1-9
        GridLayout grid = findViewById(R.id.grid);

        int gridSize = (height/10 < width/6)? height/10: width/7;


        //Drawable newDrawing = ImageResize.pullImageFromDatabase("https://images-na.ssl-images-amazon.com/images/I/71m2NvJyIVL.png");
        // Resizing of image and putting it into imageview
        ImageView mTextView = new ImageView(this);
        BitmapDrawable drawable;
        BitmapDrawable[] drawList = new BitmapDrawable[3]; // to store resized drawables
        int[] resourceList = new int[]{R.drawable.activity_restaurant_table_input__cancel,
                R.drawable.activity_restaurant_table_input__spiral_circle,
                R.drawable.activity_restaurant_table_input__delete};

        for ( int i = 0; i < 3; i++){
            mTextView.setImageBitmap(
                    ImageResize.decodeSampledBitmapFromResource(getResources(), resourceList[i], gridSize, gridSize));

            // extracts drawable from imageview
            drawable = (BitmapDrawable) mTextView.getDrawable();
            drawList[i] = drawable;
        }
        // Bitmap bitmap = drawable.getBitmap();

        for(int i =1;i<10;i++){
            // Set dimensions and text of the button
            Button button = new Button(this);
            button.setId(i);
            button.setText(String.valueOf(i));
            button.setHeight(gridSize);
            button.setBackground(drawList[1]);
            button.setWidth(gridSize);
            button.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    onKeypadButtonClick(v);
                }
            });
            grid.addView(button);
        }
        // IDs for button X,0,del
        int[] buttonId = new int[]{10,0,11};
        //Drawable[] drawableId = new Drawable[]{drawList[1], drawList[0], drawList[2]};
        // Dynamically create buttons X,0,del
        for(int i =0;i<3;i++){
            Button button = new Button(this);
            button.setId(buttonId[i]);
            if(buttonId[i] == 0){
                button.setText("0");
            }
            button.setHeight(gridSize);
            button.setBackground(drawList[i]);
            button.setWidth(gridSize);
            button.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    onKeypadButtonClick(v);
                }
            });
            grid.addView(button);
        }



    }
    public void onKeypadButtonClick(View v){
        int viewId = v.getId();
        TextView textView;

        int numOfDigits = viewList.size();
        if(viewId<10){
            if (textViewListPtr>=numOfDigits){
                return;
            }

            textView = viewList.get(textViewListPtr);
            textView.setText(String.valueOf(viewId));
            textViewListPtr++;
            // Check not OOB
            if (textViewListPtr>=numOfDigits){
                Button button = findViewById(R.id.backConfirmButton);
                button.setText(R.string.button_confirm);
            }
        } else{
            // Change button's text from confirm to back if fully filled
            if(textViewListPtr==numOfDigits) {
                ((Button) findViewById(R.id.backConfirmButton)).setText(R.string.button_back);
            }
            if(viewId==10){
                for(;textViewListPtr>0;){
                    textView = viewList.get(--textViewListPtr);
                    textView.setText("-");
                }
                textViewListPtr=0;
            } else{
                if (textViewListPtr>0) {
                    textView = viewList.get(--textViewListPtr);
                    textView.setText("-");
                }
            }
        }
    }

    public void onBackConfirmButtonClick(View v){
        TextView textView = (TextView) v;
        String text = String.valueOf(textView.getText());
        String checkValid = "";

        if(text.equals(getResources().getString(R.string.button_back))) {
            finish();
        } else {

            for ( int i = 0; i < viewList.size(); i++){
                checkValid += viewList.get(i).getText().toString();
            }
            int numbah = Integer.parseInt(checkValid);

            if ( numbah == 0 && currState == FetchState.ISPEOPLE){
                // TODO: add toast
                Toast.makeText(this, "Please type in a valid number", Toast.LENGTH_LONG);
                return;
            }
            // TODO: Use DatabaseConnector.FetchTask with FetchTaskInput.FetchMode = TABLENO and PEOPLENO
            switch(currState){
                case ISTABLE:
                    tableNumber = numbah;
                    try{
                        // To store data class
                        DatabaseConnector.FetchTaskInput fetchTaskInput =
                                new DatabaseConnector.FetchTaskInput(randomPaylahId, tableNumber, DatabaseConnector.FetchMode.TABLENO);
                        DatabaseConnector.FetchTask fetchTask= new DatabaseConnector.FetchTask(this);
                        fetchTask.execute(fetchTaskInput);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case ISPEOPLE:
                    peopleNumber = numbah;
                    try{
                        // To store data class
                        DatabaseConnector.FetchTaskInput fetchTaskInput =
                                new DatabaseConnector.FetchTaskInput(randomPaylahId, tableNumber, peopleNumber, DatabaseConnector.FetchMode.PEOPLENO);
                        DatabaseConnector.FetchTask fetchTask= new DatabaseConnector.FetchTask(this);
                        fetchTask.execute(fetchTaskInput);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    break;

                case ISMENU:
                    break;
            }

            // TODO: When starting RestaurantMainActivity, put String tablenumber into intent and Boolean istimed into Intent Extra


            /*Intent intent = new Intent(RestaurantTableInputActivity.this, RestaurantMainActivity.class);
            // TESTING CODE ONLY
            intent.putExtra("tablenumber",49);
            startActivity(intent);*/
        }
    }

    public void instantiateMenu(){

        currState = FetchState.ISMENU;
        try{
            DatabaseConnector.FetchTaskInput fetchTaskInput =
                    new DatabaseConnector.FetchTaskInput(randomPaylahId, tableNumber, DatabaseConnector.FetchMode.MENU);
            DatabaseConnector.FetchTask fetchTask= new DatabaseConnector.FetchTask(this);
            fetchTask.execute(fetchTaskInput);
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    // TODO: Handle output of DatabaseConnector.FetchTask
    @Override
    public void fetchFinish(FetchedObject output) {
        if (output==null){
            // TODO: Server failed to respond, launch ErrorActivity
            return;
        }
        switch(output.fetchMode) {
            case TABLENO:
                TableNumResponse tableNumResponse = (TableNumResponse) output;
                if (tableNumResponse.isInvalidTableNum) {
                    // TODO: add toast (to be checked)
                    Toast.makeText(this, "Invalid Table Number", Toast.LENGTH_LONG);
                    return;
                }
                // Save valid table number
                new SharedPrefManager<Integer>().saveObj(getString(R.string.key_table_no),tableNumber,RestaurantTableInputActivity.this);

                // Wipe previous pending_orders
                new SharedPrefManager<FoodBatchOrder>().saveObj(getString(R.string.key_batch_orders),new FoodBatchOrder(),RestaurantTableInputActivity.this);

                if (tableNumResponse.isPeopleNumRequired) {
                    TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.fragment_container));
                    TextView textView = findViewById(R.id.backConfirmButton);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    PeopleFragment peopleFragment = new PeopleFragment();
                    fragmentTransaction.replace(R.id.fragment_container, peopleFragment);
                    fragmentTransaction.commit();
                    currState = FetchState.ISPEOPLE;
                    textViewListPtr = 0;
                    textView.setText("Back");
                    return;

                }
                instantiateMenu();
                break;

            case PEOPLENO:
                instantiateMenu();
                break;

            case MENU:
                RestaurantMenu restaurantMenu = (RestaurantMenu) output;
                SharedPrefManager<RestaurantMenu> saveMenu = new SharedPrefManager<>();
                saveMenu.saveObj(getResources().getString(R.string.key_restaurant_menu), restaurantMenu, this);

                Intent intent = new Intent(RestaurantTableInputActivity.this, RestaurantMainActivity.class);
                startActivity(intent);
                currState = FetchState.ISTABLE;
                break;
        }

    }
}


