package com.example.frederic.genericapp.data.get;

import com.example.frederic.genericapp.data.get.FetchedObject;

/**
 * TableNumResponse is a data class to store information about server's response to table num query
 */
public class TableNumResponse extends FetchedObject {
    public boolean isPeopleNumRequired;
    public boolean isInvalidTableNum;
}