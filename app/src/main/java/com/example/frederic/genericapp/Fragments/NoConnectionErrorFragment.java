package com.example.frederic.genericapp.Fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.frederic.genericapp.Activities.ErrorActivity;
import com.example.frederic.genericapp.Data.ConnectivityReceiver;
import com.example.frederic.genericapp.Data.MyApplication;
import com.example.frederic.genericapp.R;

/**
 * Created by nixsterchan on 22/3/2018.
 */

public class NoConnectionErrorFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {
    private Button btnCheck;

    public enum ConnectType{
        NOCONNECT,
        ISCONNECT
    }
     ConnectType connectType = ConnectType.NOCONNECT;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.no_connection_error_fragment, container, false );

        btnCheck = (Button) v.findViewById(R.id.btn_check);

        // Manually checking internet connection
        checkConnection();

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Manually checking internet connection
                checkConnection();
            }
        });



        return v;
    }

    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected == true){
            Toast.makeText(getActivity().getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
            connectType = ConnectType.ISCONNECT;
        }
        else{
            Toast.makeText(getActivity().getApplicationContext(), "Not connected you fool", Toast.LENGTH_SHORT).show();
        }
    }

    // Showing the status in Snackbar
/*
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
            connectType = ConnectType.ISCONNECT;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar
                .make(getView().findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }
*/

/*    @Override
    public void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }*/

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
/*
        showSnack(isConnected);*/
    }
}