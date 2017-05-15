package com.zju.campustour.presenter.protocal.event;

/**
 * Created by HeyLink on 2017/4/27.
 */

public class NetworkChangeEvent {
    boolean valid;

    public NetworkChangeEvent(boolean mValid) {
        valid = mValid;
    }

    public boolean isValid() {
        return valid;
    }
}
