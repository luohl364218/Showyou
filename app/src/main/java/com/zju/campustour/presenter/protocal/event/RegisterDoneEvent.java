package com.zju.campustour.presenter.protocal.event;

/**
 * Created by HeyLink on 2017/5/13.
 */

public class RegisterDoneEvent {

    boolean success;

    public RegisterDoneEvent(boolean mSuccess) {
        success = mSuccess;
    }

    public boolean isSuccess() {
        return success;
    }
}
