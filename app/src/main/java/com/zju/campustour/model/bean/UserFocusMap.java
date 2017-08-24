package com.zju.campustour.model.bean;


import com.zju.campustour.presenter.protocal.enumerate.FocusStateType;

import java.io.Serializable;

/**
 * Created by HeyLink on 2017/4/24.
 */

public class UserFocusMap implements Serializable {

    private String providerId;

    private String fansId;

    private FocusStateType focusState;

    public FocusStateType getFocusState() {
        return focusState;
    }

    public void setFocusState(FocusStateType mFocusState) {
        focusState = mFocusState;
    }

    public UserFocusMap(String mProviderId, String mFansId) {
        providerId = mProviderId;
        fansId = mFansId;
    }

    public UserFocusMap() {
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String mProviderId) {
        providerId = mProviderId;
    }

    public String getFansId() {
        return fansId;
    }

    public void setFansId(String mFansId) {
        fansId = mFansId;
    }
}
