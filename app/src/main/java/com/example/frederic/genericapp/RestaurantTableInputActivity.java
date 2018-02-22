package com.example.frederic.genericapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RestaurantTableInputActivity extends AppCompatActivity {
    private final int NUMOFDIGITS = 6;
    private ArrayList<TextView> textViewList;
    private int textViewListPtr=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_table_input);

        // Get screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        // Dynamically create textviews, and store in textViewList
        textViewList = new ArrayList<>();
        LinearLayout linearLayout = findViewById(R.id.displayLinearLayout);
        for (int i=0;i<NUMOFDIGITS;i++){
            TextView textView = new TextView(this);
            textView.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            );
            textView.setText("-");
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, height/7);
            textViewList.add(textView);
            linearLayout.addView(textView);
        }

        // Dynamically create buttons 1-9
        GridLayout grid = findViewById(R.id.grid);

        int gridSize = (height/7 < width/4)? height/7: width/4;
        for(int i =1;i<10;i++){
            // Set dimensions and text of the button
            Button button = new Button(this);
            button.setId(i);
            button.setText(String.valueOf(i));
            button.setHeight(gridSize);
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
        String[] drawableId = new String[]{"|X|", "0", "<--"
        };
        // Dynamically create buttons X,0,del
        for(int i =0;i<3;i++){
            Button button = new Button(this);
            button.setId(buttonId[i]);
            button.setText(drawableId[i]);
            button.setHeight(gridSize);
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


        if(viewId<10){
            if (textViewListPtr>=NUMOFDIGITS){
                return;
            }
            textView = textViewList.get(textViewListPtr);
            textView.setText(String.valueOf(viewId));
            textViewListPtr++;
            // Check not OOB
            if (textViewListPtr>=NUMOFDIGITS){
                Button button = findViewById(R.id.backConfirmButton);
                button.setText(R.string.button_confirm);
            }
        } else{
            // Change button's text from confirm to back if fully filled
            if(textViewListPtr==NUMOFDIGITS) {
                ((Button) findViewById(R.id.backConfirmButton)).setText(R.string.button_back);
            }
            if(viewId==10){
                for(;textViewListPtr>0;){
                    textView = textViewList.get(--textViewListPtr);
                    textView.setText("-");
                }
                textViewListPtr=0;
            } else{
                if (textViewListPtr>0) {
                    textView = textViewList.get(--textViewListPtr);
                    textView.setText("-");
                }
            }
        }
    }

    public void onBackConfirmButtonClick(View v){
        TextView textView = (TextView) v;
        String text = String.valueOf(textView.getText());

        if(text.equals(getResources().getString(R.string.button_back))) {
            finish();
        } else {
            Intent intent = new Intent(RestaurantTableInputActivity.this, TrollActivity.class);
            startActivity(intent);
        }
    }

}
