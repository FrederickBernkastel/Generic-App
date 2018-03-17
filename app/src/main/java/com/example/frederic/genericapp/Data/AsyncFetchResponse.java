package com.example.frederic.genericapp.Data;

/**
 * Project GenericApp
 * Created by Frederic
 * On 3/9/2018
 */

import com.example.frederic.genericapp.Data.FetchedObject;

/**
 * Interface to allow processing of fetch output asynchronously
 * Implement in activity, then override method to handle output
 * Created by: Frederick Bernkastel
 */
public interface AsyncFetchResponse {
    void fetchFinish(FetchedObject output);
}
