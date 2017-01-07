package com.example.android.quakereport;

/**
 * Created by root on 1/5/17.
 */

public class Earthquake {

    private double mMagnitude;
    private String mPlace;
    private long mTimeInMilli;
    private String mUrl;


    public Earthquake(double magnitude, String place, long time, String url) {
        mMagnitude = magnitude;
        mPlace = place;
        mTimeInMilli = time;
        mUrl = url;

    }

    public void setMagnitude(double magnitude) {
        mMagnitude = magnitude;
    }

    public double getMagnitude() {
        return mMagnitude;
    }

    public void setPlace(String place) {
        mPlace = place;
    }

    public String getPlace() {
        return mPlace;
    }

    public void setTime(long time) {
        mTimeInMilli = time;
    }

    public long getTime() {
        return mTimeInMilli;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

}
