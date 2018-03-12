package com.example.frederic.genericapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;



/**
 * Class to handle saving / fetching objects locally
 * @param <T>       Type of object to save
 * Created by: Frederick Bernkastel
 */
class SharedPrefManager <T>{

    static private final String PREFERENCEFILEKEY = "com.example.frederic.genericapp.preferencefilekey";

    /**
     * Function to save an object as a string in a SharedPreferences file
     * @param key       Key to save object under
     * @param obj       Object to be saved
     * @param context   Activity context ( = getActivity() or ActivityClass.this)
     * @return true
     */
    boolean saveObj(String key, T obj,Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCEFILEKEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String value = new Gson().toJson(obj);
        editor.putString(key,value);
        editor.apply();
        return true;
    }

    /**
     *
     * @param key       Key to save object under
     * @param context   Activity context ( = getActivity()  or ActivityClass.this)
     * @param tClass    Class type to fetch ( Menu.class )
     * @return
     */
    T fetchObj(String key,Context context,Class<T> tClass){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCEFILEKEY, Context.MODE_PRIVATE);
        String value = sharedPref.getString(key,null);
        if (value==null){
            return null;
        }
        return new Gson().fromJson(value,tClass);
    }
}
