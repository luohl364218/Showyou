package com.zju.campustour.presenter.protocal.event;

/**
 * Created by HeyLink on 2017/5/15.
 */

public class LogoutEvent {
    boolean isLogout;

    public LogoutEvent(boolean mIsLogout) {
        isLogout = mIsLogout;
    }

    public boolean isLogout() {
        return isLogout;
    }
}
