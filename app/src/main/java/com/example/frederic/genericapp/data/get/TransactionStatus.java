package com.example.frederic.genericapp.data.get;


import com.example.frederic.genericapp.data.get.FetchedObject;

/**
 * Project GenericApp
 * Created by Frederic
 * On 3/29/2018
 */

public class TransactionStatus extends FetchedObject {
    public final boolean success;
    public TransactionStatus(boolean success){
        this.success = success;
    }
}
