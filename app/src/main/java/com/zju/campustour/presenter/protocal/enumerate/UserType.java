package com.zju.campustour.presenter.protocal.enumerate;

/**
 * Created by HeyLink on 2017/4/24.
 */

public enum UserType {

    USER,

    PROVIDER;

    public int getValue(){
        return this.ordinal();
    }
}
