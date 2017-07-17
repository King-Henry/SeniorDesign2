package com.wiita.smartlockapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements
        SummaryListFragment.OnSummaryFragmentInteractionListener{

    MainActivityPagerAdapter adapter;
    ViewPager viewPager;
    TabLayout tabLayout;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = (TabLayout)findViewById(R.id.main_activity_tab_layout);
        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setElevation(0);
        }
        adapter = new MainActivityPagerAdapter(getSupportFragmentManager(),this);
        viewPager = (ViewPager)findViewById(R.id.main_activity_viewpager);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onSummaryFragmentInteraction(Uri uri) {

    }

}
