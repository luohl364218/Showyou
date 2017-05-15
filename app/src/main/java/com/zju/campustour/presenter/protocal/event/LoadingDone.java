package com.zju.campustour.presenter.protocal.event;

/**
 * Created by HeyLink on 2017/4/28.
 */

public class LoadingDone {
    boolean done;

    public LoadingDone(boolean mDone) {
        done = mDone;
    }

    public boolean isDone() {
        return done;
    }
}
