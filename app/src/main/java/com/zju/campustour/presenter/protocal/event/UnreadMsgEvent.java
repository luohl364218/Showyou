package com.zju.campustour.presenter.protocal.event;

/**
 * Created by HeyLink on 2017/6/6.
 */

public class UnreadMsgEvent {

    int unreadMsg;

    public UnreadMsgEvent(int mUnreadMsg) {
        unreadMsg = mUnreadMsg;
    }

    public int getUnreadMsg() {
        return unreadMsg;
    }
}
