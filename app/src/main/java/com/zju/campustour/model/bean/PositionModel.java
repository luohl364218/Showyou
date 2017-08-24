package com.zju.campustour.model.bean;

/**
 * Created by HeyLink on 2017/8/22.
 */

public class PositionModel {

    double latitude;
    double longitude;

    public PositionModel(double mLatitude, double mLongitude) {
        latitude = mLatitude;
        longitude = mLongitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double mLatitude) {
        latitude = mLatitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double mLongitude) {
        longitude = mLongitude;
    }
}
