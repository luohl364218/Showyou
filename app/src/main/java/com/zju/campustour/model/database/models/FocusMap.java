package com.zju.campustour.model.database.models;

import org.litepal.crud.DataSupport;

/**
 * Created by HeyLink on 2017/4/24.
 */

public class FocusMap extends DataSupport {

    private String starsId;

    private String fansId;

    public FocusMap(String mStarsId, String mFansId) {
        starsId = mStarsId;
        fansId = mFansId;
    }

    public String getStarsId() {
        return starsId;
    }

    public void setStarsId(String mStarsId) {
        starsId = mStarsId;
    }

    public String getFansId() {
        return fansId;
    }

    public void setFansId(String mFansId) {
        fansId = mFansId;
    }
}
