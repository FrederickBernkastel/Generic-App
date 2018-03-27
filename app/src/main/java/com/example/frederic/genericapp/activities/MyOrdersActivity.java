package com.example.frederic.genericapp.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.frederic.genericapp.data.AsyncFetchResponse;
import com.example.frederic.genericapp.data.AsyncPostResponse;
import com.example.frederic.genericapp.data.DatabaseConnector;
import com.example.frederic.genericapp.data.FetchedObject;
import com.example.frederic.genericapp.data.FoodBatchOrder;
import com.example.frederic.genericapp.fragments.ConfirmOrderDialogFragment;
import com.example.frederic.genericapp.fragments.MyCurrentOrdersFragment;
import com.example.frederic.genericapp.fragments.MyPendingOrdersFragment;
import com.example.frederic.genericapp.R;
import com.example.frederic.genericapp.SharedPrefManager;

/**
 * Class with tabs to display pending / sent orders
 * Created by: Frederick Bernkastel
 */
public class MyOrdersActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, MyPendingOrdersFragment.ConfirmOrderListener, ConfirmOrderDialogFragment.ConfirmOrderDialogListener, AsyncPostResponse,AsyncFetchResponse{
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private PagerAdapter adapter;
    private FoodBatchOrder pendingOrders;

    private boolean layoutCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        // Extract plid
        String plid = new SharedPrefManager<String>().fetchObj(getString(R.string.key_plid),MyOrdersActivity.this,String.class);

        layoutCreated = false;

        // Get server data
        DatabaseConnector.FetchTaskInput input;
        try {
            input = new DatabaseConnector.FetchTaskInput(plid, DatabaseConnector.FetchMode.EXISTINGORDERS);
        } catch(Exception e){
            System.out.println("Error constructing DatabaseConnector input, was the wrong mode used?");
            System.out.println(e.getMessage());
            return;
        }

        // Starts AsyncTask
        new DatabaseConnector.FetchTask(MyOrdersActivity.this).execute(input);

        // Get reference to widgets
        mTabLayout = findViewById(R.id.my_orders_activity_tabs);
        mViewPager = findViewById(R.id.my_orders_activity_pager);

        // Add tabs to tabLayout
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.pending_orders)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.sent_orders)));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Creating our pager adapter
        adapter = new PagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());

        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        // Set listener to tab's layout onClick
        mTabLayout.addOnTabSelectedListener(MyOrdersActivity.this);

        layoutCreated = true;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onConfirmSelected(MyPendingOrdersFragment fragment) {
        // Recording orders
        pendingOrders = fragment.pendingOrders;

        // Launch Dialog Fragment
        new ConfirmOrderDialogFragment().show(getFragmentManager(),"ConfirmOrderDialogFragment");
    }

    @Override
    public void onDialogConfirm(android.app.Fragment fragment) {
        // Post to server
        String plid = new SharedPrefManager<String>().fetchObj(getString(R.string.key_plid),MyOrdersActivity.this,String.class);
        DatabaseConnector.PostTaskOutput output;
        try {
            output = new DatabaseConnector.PostTaskOutput(plid, pendingOrders);
        } catch (Exception e){

            System.out.println("Error parsing DatabaseConnector input in MyOrdersActivity.");
            System.out.println(e.getMessage());
            // Terminate this activity, and close entire app
            this.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            return;
        }
        new DatabaseConnector.PostTask(MyOrdersActivity.this).execute(output);



    }

    @Override
    public void postFinish(int response) {
        if (response<=-1){
            // Error posting to server, launch ErrorActivity
            Intent intent = new Intent(MyOrdersActivity.this, ErrorActivity.class);
            startActivity(intent);
            ErrorActivity.errorType = ErrorActivity.ErrorType.NOCONNECTION;
            return;
        }
        // Delete all pending orders if POST success
        SharedPrefManager<FoodBatchOrder> prefManager = new SharedPrefManager<>();
        pendingOrders = null;
        prefManager.saveObj(getString(R.string.key_batch_orders),pendingOrders,MyOrdersActivity.this);

        // Go back
        onBackPressed();

        // Inform user of success
        Toast.makeText(MyOrdersActivity.this,getString(R.string.toast_order_sent_success),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void fetchFinish(FetchedObject output) {
        // Unable to connect to server, launch ErrorActivity
        if (output==null){
            Intent intent = new Intent(MyOrdersActivity.this, ErrorActivity.class);
            startActivity(intent);
            ErrorActivity.errorType = ErrorActivity.ErrorType.NOCONNECTION;
            return;
        }
        // Waits while layout fragments have not been created
        while(!layoutCreated){
            try {
                wait();
            } catch (InterruptedException e){
                // This should not occur as threads are not notifying each other
                System.out.println("An interrupted exception has occurred in MyOrdersActivity");
                return;
            }
        }

        adapter.mCurrentOrdersFragment.fetchFinish(output);
    }
}


class PagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
    MyCurrentOrdersFragment mCurrentOrdersFragment;
    PagerAdapter(FragmentManager fm, int NumOfTabs){
        super(fm);
        mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return new MyPendingOrdersFragment();
            case 1:
                mCurrentOrdersFragment = new MyCurrentOrdersFragment();
                return mCurrentOrdersFragment;
            default:
                return null;
        }

    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
