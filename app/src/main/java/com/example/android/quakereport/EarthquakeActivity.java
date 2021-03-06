/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    List<Earthquake> mEarthquakes = new ArrayList<>();
    EarthquakeArrayAdapter mAdapter;
    TextView emptyListTextView;
    ProgressBar progressBar;
    Button mTryAgainButton;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Find a reference to the {@link ListView} in the layout
        final ListView earthquakeListView = (ListView) findViewById(R.id.list);
        emptyListTextView = (TextView) findViewById(R.id.textView_empty_list);

        // Create a new {@link ArrayAdapter} of mEarthquakes
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mTryAgainButton = (Button) findViewById(R.id.button_tryAgain);

        mAdapter = new EarthquakeArrayAdapter(getApplicationContext(), mEarthquakes);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);
        earthquakeListView.setEmptyView(emptyListTextView);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Earthquake earthquake = (Earthquake) mAdapter.getItem(position);
                Uri uri = Uri.parse(earthquake.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });

            startLoader();
    }

    private void startLoader() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable()) {
            getLoaderManager().initLoader(0, null, this);

        } else {
            progressBar.setVisibility(View.GONE);
            emptyListTextView.setText("No Internet Connection!!!");
            mTryAgainButton.setVisibility(View.VISIBLE);
            mTryAgainButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoader();
                }
            });
        }
    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPreferences.getString(getString(R.string.settings_min_magnitude_key),
                                    getString(R.string.settings_min_magnitude_default));

        String orderByPref = sharedPreferences.getString(getString(R.string.settings_order_by_key),
                                                        getString(R.string.settings_order_by_default));


        return new JsonAsyncTaskLoader(this, minMagnitude, orderByPref);
    }


    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {

        progressBar.setVisibility(View.GONE);
        mTryAgainButton.setVisibility(View.GONE);

        mAdapter.clear();
        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
        }
        emptyListTextView.setText("No Data D:");
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {

        Log.i(LOG_TAG, "onLoaderReset()");

    }

    private static class JsonAsyncTaskLoader extends AsyncTaskLoader<List<Earthquake>> {

        private String mMinMagnitude;
        private String mOrderByPref;

        public JsonAsyncTaskLoader(Context context, String minMagnitude, String orderByPref) {
            super(context);
            mMinMagnitude = minMagnitude;
            mOrderByPref = orderByPref;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();//done hear so not chained method after initLoader()
        }

        @Override
        public List<Earthquake> loadInBackground() {
            URL url = QueryUtils.buidURL(mMinMagnitude, mOrderByPref);
            return QueryUtils.makeHttpRequest(url);

        }
    }

}
