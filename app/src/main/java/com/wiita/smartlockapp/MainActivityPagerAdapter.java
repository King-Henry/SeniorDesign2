package com.wiita.smartlockapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Wiita on 7/15/2017.
 */

public class MainActivityPagerAdapter extends FragmentPagerAdapter {

    Context context;

    public MainActivityPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return SummaryListFragment.newInstance(null,null);
            default:
                return HistoryFragment.newInstance(null,null);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return(context.getString(R.string.main_activity_view_pager_summary_title));
            case 1:
                return(context.getString(R.string.main_activity_view_pager_history_title));
            default:
                return "";
        }
    }
}
