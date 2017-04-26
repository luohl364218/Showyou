package com.zju.campustour.presenter.protocal.enumerate;

/**
 * Created by HeyLink on 2017/4/24.
 */

public enum UserProjectStateType {

    BOOK_ACCEPT,

    WAIT_TO_PAY,

    CANCEL_SUCCESS,

    BOOK_SUCCESS,

    WAIT_CANCEL_CONFIRM;

    public int getValue(){
        return this.ordinal();
    }
}
