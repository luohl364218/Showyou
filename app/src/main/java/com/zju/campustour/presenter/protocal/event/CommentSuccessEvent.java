package com.zju.campustour.presenter.protocal.event;

/**
 * Created by HeyLink on 2017/5/22.
 */

public class CommentSuccessEvent {
    boolean isCommentSuccess;

    public CommentSuccessEvent(boolean mIsCommentSuccess) {
        isCommentSuccess = mIsCommentSuccess;
    }

    public boolean isCommentSuccess() {
        return isCommentSuccess;
    }
}
