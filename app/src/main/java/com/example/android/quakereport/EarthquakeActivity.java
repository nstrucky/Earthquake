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

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    List<Earthquake> earthquakes;


    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);


        JsonAsyncTask task = new JsonAsyncTask();
        task.execute();


    }

    //    http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=15




    private class JsonAsyncTask extends AsyncTask<Void, Void, String> {



        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            StringBuilder stringBuilder = new StringBuilder();
            String jsonString = null;
            URL url = buidURL();

            try {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                inputStream = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                BufferedReader buffer = new BufferedReader(reader);

                String line = null;
                while ((line = buffer.readLine()) != null) {

                    stringBuilder.append(line + "\n");
                }

                jsonString = stringBuilder.toString();

                Log.i(LOG_TAG, jsonString);

            } catch (IOException e) {
                Log.e(LOG_TAG, "IOException", e);
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }

                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return jsonString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            earthquakes = QueryUtils.extractEarthquakes(s);
            setListData();


        }

        private URL buidURL() {
            URL url = null;
            final String BASE_URL = "http://earthquake.usgs.gov/fdsnws/event/1/query?";
            final String PARAM_FORMAT = "format";
            final String PARAM_LIMIT = "limit";
            String limit = "15";
            String format = "geojson";

            Uri uri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(PARAM_FORMAT, format)
                    .appendQueryParameter(PARAM_LIMIT, limit)
                    .build();

            try {
                url = new URL(uri.toString());

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "MalformedURL", e);
            }

            return url;

        }
    }



    protected void setListData() {
        // Find a reference to the {@link ListView} in the layout
        final ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new {@link ArrayAdapter} of earthquakes

        final EarthquakeArrayAdapter adapter =
                new EarthquakeArrayAdapter(getApplicationContext(), earthquakes);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Earthquake earthquake = (Earthquake) adapter.getItem(position);
                Uri uri = Uri.parse(earthquake.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });
    }



}
