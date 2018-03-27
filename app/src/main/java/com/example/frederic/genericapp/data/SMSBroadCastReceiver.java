package com.example.frederic.genericapp.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;

/**
 * Created by nixsterchan on 23/3/2018.
 */

public class SMSBroadCastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Get the bundle object contained within SMS intent passed in

        Bundle bundleSMS = intent.getExtras();
        SmsMessage[] smsMessages = null;
        String sms_string = "";

        if (bundleSMS != null){

            // Receive SMS

            Object[] pdus =(Object[]) bundleSMS.get("pdus");
            smsMessages = new SmsMessage[pdus.length];
            for (int i = 0 ; i < smsMessages.length; i++){
                smsMessages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);

                sms_string += "\r\nMessage: ";
                sms_string += smsMessages[i].getMessageBody().toString();
                sms_string += "\r\n";

                String Sender = smsMessages[i].getOriginatingAddress();

                // Check if sender is yours
                Intent smsIntent = new Intent("otp");
                smsIntent.putExtra("message", sms_string);

                LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent);
            }

        }
    }



}
