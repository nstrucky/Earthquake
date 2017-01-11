package com.example.android.quakereport;

import android.net.Uri;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by root on 1/7/17.
 */

public final class QueryUtils {



    /** Sample JSON response for a USGS query */


    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Earthquake> extractEarthquakes(String jsonToParse) {

        Log.i("QueryUtils", "extractEarthquakes()");
        // Create an empty ArrayList that we can start adding mEarthquakes to
        ArrayList<Earthquake> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject jsonObject = new JSONObject(jsonToParse);
            JSONArray earthquakesAsFeatures = jsonObject.getJSONArray("features");

            for (int i = 0; i < earthquakesAsFeatures.length(); i++) {

                JSONObject earthquakeFeature = earthquakesAsFeatures.getJSONObject(i);
                JSONObject properties = earthquakeFeature.getJSONObject("properties");
                double mag = properties.getDouble("mag");
                String place = properties.getString("place");
                long timeInMilli = properties.getLong("time");
                String url = properties.getString("url");

                Earthquake earthquake = new Earthquake(mag, place, timeInMilli, url);
                earthquakes.add(earthquake);

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of mEarthquakes
        return earthquakes;
    }

    public static URL buidURL() {
        URL url = null;
        final String BASE_URL = "http://earthquake.usgs.gov/fdsnws/event/1/query?";
        final String PARAM_FORMAT = "format";
        final String PARAM_LIMIT = "limit";
        String limit = "1000";
        String format = "geojson";

        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_FORMAT, format)
                .appendQueryParameter(PARAM_LIMIT, limit)
                .build();

        try {
            url = new URL(uri.toString());

        } catch (MalformedURLException e) {
            Log.e("TAG", "MalformedURL", e);
        }

        return url;

    }
    public static List<Earthquake> makeHttpRequest(URL url) {
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        String jsonString = null;


        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                jsonString = readFromStream(inputStream);
            }

        } catch (IOException e) {
            Log.e("TAG", "IOException", e);
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

        return extractEarthquakes(jsonString);
    }
    public static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String jsonResponse = "";
        if (inputStream == null) {
            return jsonResponse;
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));

        String line = null;

        try {
            while ((line = buffer.readLine()) != null) {

                stringBuilder.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        jsonResponse = stringBuilder.toString();
        Log.i("TAG", jsonResponse);

        return jsonResponse;
    }

}
