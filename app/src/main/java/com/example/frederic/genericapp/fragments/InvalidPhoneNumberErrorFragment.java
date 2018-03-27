package com.example.frederic.genericapp.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frederic.genericapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nixsterchan on 22/3/2018.
 */

public class InvalidPhoneNumberErrorFragment extends Fragment {

    private View v;
    public static final int REQUEST_ID_FOR_MULTI_PERMISSIONS = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.invalid_phone_number_error_fragment, container, false );

        if (checkRequestPermission()){
            Toast.makeText(getActivity().getApplicationContext(), "SUP BITCH", Toast.LENGTH_SHORT).show();

            /*Intent intent =
            new Intent(getActivity().getApplicationContext(), RestaurantMainActivity.class);
            startActivity(intent);*/
        }
        this.v=v;


        return v;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");

                TextView tv = v.findViewById(R.id.textViewContainer);
                tv.setText(message);
            }
        }
    };
    private boolean checkRequestPermission(){
        int permissionSendMessage = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.SEND_SMS);

        int receiveSMS = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.RECEIVE_SMS);

        int readSMS = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.READ_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_MMS);
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(),
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_FOR_MULTI_PERMISSIONS);
            return false;
        }
        return true;
    }



    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).
                registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(receiver);
    }

}
