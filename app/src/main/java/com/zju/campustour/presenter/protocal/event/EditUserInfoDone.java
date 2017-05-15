package com.zju.campustour.presenter.protocal.event;

/**
 * Created by HeyLink on 2017/5/15.
 */

public class EditUserInfoDone {
    boolean done;

    public EditUserInfoDone(boolean done) {
        this.done = done;
    }

    public boolean isDone() {
        return done;
    }
}
