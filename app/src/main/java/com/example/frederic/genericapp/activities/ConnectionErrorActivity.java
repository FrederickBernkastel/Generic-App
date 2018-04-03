package com.example.frederic.genericapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.frederic.genericapp.R;
import com.example.frederic.genericapp.data.ConnectivityReceiver;
import com.example.frederic.genericapp.data.MyApplication;

public class ConnectionErrorActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private Button btnCheck;

    public enum ConnectType{
        NOCONNECT,
        ISCONNECT
    }
    ConnectType connectType = ConnectType.NOCONNECT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_error);

        btnCheck = (Button) findViewById(R.id.btn_check);

        // Manually checking internet connection
        checkConnection();

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Manually checking internet connection
                checkConnection();
                if (connectType == ConnectType.ISCONNECT){
                    onBackPressed();
            }
            }
        });
    }


    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected == true){
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
            connectType = ConnectType.ISCONNECT;
        }
        else{
            Toast.makeText(this, "Not connected you fool", Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        onBackPressed();
/*
        showSnack(isConnected);*/
    }


    @Override
    public void onBackPressed() {
        if (connectType == ConnectType.ISCONNECT) {
            super.onBackPressed();
        }
    }

}