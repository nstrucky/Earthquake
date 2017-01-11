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
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    List<Earthquake> mEarthquakes = new ArrayList<>();
    EarthquakeArrayAdapter mAdapter;


    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {
        return new JsonAsyncTaskLoader(this);
    }


    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {
        mAdapter.clear();
        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Find a reference to the {@link ListView} in the layout
        final ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new {@link ArrayAdapter} of mEarthquakes

        mAdapter = new EarthquakeArrayAdapter(getApplicationContext(), mEarthquakes);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Earthquake earthquake = (Earthquake) mAdapter.getItem(position);
                Uri uri = Uri.parse(earthquake.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });

        getLoaderManager().initLoader(0, null, this);
//        getLoaderManager().initLoader(0, null, this).forceLoad();
//          forceLoad is needed to start the loadInBackground method

    }

    private static class JsonAsyncTaskLoader extends AsyncTaskLoader<List<Earthquake>> {

        public JsonAsyncTaskLoader(Context context) {
            super(context);
        }


        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();//done hear so not chained method after initLoader()
        }

        @Override
        protected void onForceLoad() {
            super.onForceLoad();
        }

        @Override
        public List<Earthquake> loadInBackground() {

            URL url = QueryUtils.buidURL();
            return QueryUtils.makeHttpRequest(url);

        }
    }

}
