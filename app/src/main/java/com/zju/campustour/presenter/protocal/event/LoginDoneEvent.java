package com.zju.campustour.presenter.protocal.event;

/**
 * Created by HeyLink on 2017/5/13.
 */

public class LoginDoneEvent {
    boolean login;

    public LoginDoneEvent(boolean mLogin) {
        login = mLogin;
    }

    public boolean isLogin() {
        return login;
    }
}
