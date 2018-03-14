package com.example.frederic.genericapp;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Class with tabs to display pending / sent orders
 * Created by: Frederick Bernkastel
 */
public class MyOrdersActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        // Get reference to widgets
        mTabLayout = findViewById(R.id.my_orders_activity_tabs);
        mViewPager = findViewById(R.id.my_orders_activity_pager);

        // Add tabs to tabLayout
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.pending_orders)));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Creating our pager adapter
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());

    }
}


class PagerAdapter extends FragmentStatePagerAdapter{
    int mNumOfTabs;
    public PagerAdapter(FragmentManager fm,int NumOfTabs){
        super(fm);
        mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                MyPendingOrdersFragment tab0 = new MyPendingOrdersFragment();
                return tab0;
            default:
                return null;
        }

    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
