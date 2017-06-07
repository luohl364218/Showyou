package com.zju.campustour.presenter.protocal.event;

/**
 * Created by HeyLink on 2017/6/6.
 */

public class UnreadFriendVerifyNum {

    int friendVerifyNum;

    public UnreadFriendVerifyNum(int mFriendVerifyNum) {
        friendVerifyNum = mFriendVerifyNum;
    }

    public int getFriendVerifyNum() {
        return friendVerifyNum;
    }
}
