package com.example.android.quakereport;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by root on 1/14/17.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class EarthquakePreferenceFragment extends PreferenceFragment implements
            Preference.OnPreferenceChangeListener {



        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference minMagnitude = findPreference(getString(R.string.settings_min_magnitude_key));
            bindPreferenceSummaryToValue(minMagnitude);



//            minMagnitude.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    preference.setSummary(newValue.toString());
//
//                    return true;
//                }
//            });
//
//            You could do this, but it would be more efficient to have a helper method that binds
//            all of your preferences to listeners and summary values rather than create
//            anonymous inner local classes for each one.

        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
//            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
//            Either of these should work as the activities in the app actually reference the same static map class within Context class
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String preferenceString = sharedPreferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);



        }


        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            //See helper method bindPreferenceSummaryToValue(Preference preference)
            preference.setSummary(newValue.toString());

            return true;
        }
    }
}
