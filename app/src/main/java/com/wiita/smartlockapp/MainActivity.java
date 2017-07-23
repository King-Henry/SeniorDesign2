package com.wiita.smartlockapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity implements
        SummaryFragment.OnSummaryFragmentInteractionListener,
        HistoryFragment.OnHistoryFragmentInteractionListener{

    MainActivityPagerAdapter adapter;
    ViewPager viewPager;
    TabLayout tabLayout;
    ActionBar actionBar;

    public final static String HISTORY_EVENT_DETAIL_TIME = "HISTORY_EVENT_DETAIL_TIME";
    public final static String HISTORY_EVENT_DETAIL_DATE = "HISTORY_EVENT_DETAIL_DATE";
    public final static String HISTORY_EVENT_DETAIL_HEADER = "HISTORY_EVENT_DETAIL_HEADER";
    public final static String HISTORY_EVENT_DETAIL_SUBHEADER = "HISTORY_EVENT_DETAIL_SUBHEADER";
    public final static String HISTORY_EVENT_DETAIL_IMAGE_URL = "HISTORY_EVENT_DETAIL_IMAGE_URL";

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.main_activity_settings_menu_item){
            goToSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHistoryListEventSelected(HistoryEventClickEvent event){
        Intent intent = new Intent(this,HistoryEventDetailActivity.class);
        intent.putExtra(HISTORY_EVENT_DETAIL_TIME, event.model.time);
        intent.putExtra(HISTORY_EVENT_DETAIL_DATE, event.model.date);
        intent.putExtra(HISTORY_EVENT_DETAIL_HEADER, event.model.header);
        intent.putExtra(HISTORY_EVENT_DETAIL_SUBHEADER, event.model.subHeader);
        intent.putExtra(HISTORY_EVENT_DETAIL_IMAGE_URL, event.model.url);
        startActivity(intent);
    }

    @Override
    public void onSummaryFragmentInteraction(Uri uri) {

    }

    @Override
    public void onHistoryEventSelected(int position) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void goToSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
