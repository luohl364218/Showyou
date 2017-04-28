package com.zju.campustour.presenter.protocal.event;

/**
 * Created by HeyLink on 2017/4/27.
 */

public class SearchProviderEvent {

    String mSearchArea;
    String mSearchSchool;
    String mSearchMajor;

    public SearchProviderEvent(String mSearchArea, String mSearchSchool, String mSearchMajor) {
        this.mSearchArea = mSearchArea;
        this.mSearchSchool = mSearchSchool;
        this.mSearchMajor = mSearchMajor;
    }

    public String getSearchArea() {
        return mSearchArea;
    }

    public void setSearchArea(String mSearchArea) {
        this.mSearchArea = mSearchArea;
    }

    public String getSearchSchool() {
        return mSearchSchool;
    }

    public void setSearchSchool(String mSearchSchool) {
        this.mSearchSchool = mSearchSchool;
    }

    public String getSearchMajor() {
        return mSearchMajor;
    }

    public void setSearchMajor(String mSearchMajor) {
        this.mSearchMajor = mSearchMajor;
    }
}
