package com.example.frederic.genericapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.frederic.genericapp.activities.RestaurantTableInputActivity;
import com.example.frederic.genericapp.R;

import java.util.ArrayList;

/**
 * Created by nixsterchan on 14/3/2018.
 */

public class TableFragment extends Fragment {

    private final int NUMOFDIGITS = 6;
    public static ArrayList<TextView> textViewList;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.table_fragment, container, false );


        // Get screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;


        // Dynamically create textviews, and store in textViewList
        textViewList = new ArrayList<>();
        LinearLayout linearLayout = v.findViewById(R.id.tableLinearLayout);
        for (int i=0;i<NUMOFDIGITS;i++){
            TextView textView = new TextView(getActivity().getApplicationContext());
            textView.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT)
            );
            textView.setText("-");
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, height/7);
            textViewList.add(textView);
            linearLayout.addView(textView);
        }
        RestaurantTableInputActivity.viewList=textViewList;
        return v;

    }

}
