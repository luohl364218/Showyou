package com.zju.campustour.presenter.protocal.event;

/**
 * Created by HeyLink on 2017/6/6.
 */

public class UserTypeChangeEvent {
    boolean isCommonUser;

    public UserTypeChangeEvent(boolean mIsCommonUser) {
        isCommonUser = mIsCommonUser;
    }

    public boolean isCommonUser() {
        return isCommonUser;
    }
}
