package com.zju.campustour.presenter.protocal.event;

/**
 * Created by HeyLink on 2017/4/27.
 */

public class onAreaAndSchoolSelectedEvent {
    int area;
    String school;

    public onAreaAndSchoolSelectedEvent(int mArea, String mSchool) {
        area = mArea;
        school = mSchool;
    }

    public int getArea() {
        return area;
    }

    public String getSchool() {
        return school;
    }
}
