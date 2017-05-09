package com.zju.campustour.presenter.protocal.event;

/**
 * Created by HeyLink on 2017/5/5.
 */

public class onRecycleViewRefreshEvent {

    public onRecycleViewRefreshEvent(int mPosition) {
        position = mPosition;
    }

    public int getPosition() {
        return position;
    }

    int position;

}
