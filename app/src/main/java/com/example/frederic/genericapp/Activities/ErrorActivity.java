package com.example.frederic.genericapp.Activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.frederic.genericapp.Fragments.InvalidPhoneNumberErrorFragment;
import com.example.frederic.genericapp.Fragments.NoConnectionErrorFragment;
import com.example.frederic.genericapp.R;

public class ErrorActivity extends AppCompatActivity {

    public enum ErrorType{

        INVALIDPHONENUMBER,
        NOCONNECTION,
        NOERROR
    }
    public static ErrorType errorType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        try {
            switch (errorType) {
                case INVALIDPHONENUMBER:
                    InvalidPhoneNumberErrorFragment invalidPhoneNumberErrorFragment = new InvalidPhoneNumberErrorFragment();
                    fragmentTransaction.add(R.id.fragment_container_2, invalidPhoneNumberErrorFragment);
                    fragmentTransaction.commit();

                    break;

                case NOCONNECTION:
                    NoConnectionErrorFragment noConnectionErrorFragment = new NoConnectionErrorFragment();
                    fragmentTransaction.add(R.id.fragment_container_2, noConnectionErrorFragment);
                    fragmentTransaction.commit();

                    break;
                case NOERROR:
                    break;
            }

            if (errorType == ErrorType.NOERROR) {
                //onBackPressed();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onBackPressed() {

        if (errorType == ErrorType.NOERROR){
            super.onBackPressed();
        }
    }


}
