package com.zju.campustour.presenter.protocal.event;

/**
 * Created by HeyLink on 2017/4/27.
 */

public class onNetworkChangeEvent {
    boolean valid;

    public onNetworkChangeEvent(boolean mValid) {
        valid = mValid;
    }

    public boolean isValid() {
        return valid;
    }
}
