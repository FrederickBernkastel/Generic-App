package com.example.frederic.genericapp.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.frederic.genericapp.R;

/**
 * A fragment to display current orders
 * Created by: Frederick Bernkastel
 */
public class MyCurrentOrdersFragment extends Fragment {


    public MyCurrentOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_current_orders, container, false);
    }

}
