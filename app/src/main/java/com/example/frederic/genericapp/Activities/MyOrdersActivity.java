package com.example.frederic.genericapp.Activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.frederic.genericapp.Fragments.MyPendingOrdersFragment;
import com.example.frederic.genericapp.R;

/**
 * Class with tabs to display pending / sent orders
 * Created by: Frederick Bernkastel
 */
public class MyOrdersActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener{
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

        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));


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
}


class PagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
    PagerAdapter(FragmentManager fm,int NumOfTabs){
        super(fm);
        mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return new MyPendingOrdersFragment();
            case 1:
                return null;
            default:
                return null;
        }

    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
