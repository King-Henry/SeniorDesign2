package com.wiita.smartlockapp;


import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.wiita.smartlockapp.SettingsActivity.bindPreferenceSummaryToValue;

/**
 * A simple {@link Fragment} subclass.
 */
public class GeneralSettingsFragment extends PreferenceFragment {

    public GeneralSettingsFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        setHasOptionsMenu(true);

        bindPreferenceSummaryToValue(findPreference("sign_in_method"));
        bindPreferenceSummaryToValue(findPreference("preferred_pin"));
        bindPreferenceSummaryToValue(findPreference("live_feed_type"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
