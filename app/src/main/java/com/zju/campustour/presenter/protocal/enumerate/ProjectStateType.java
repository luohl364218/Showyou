package com.zju.campustour.presenter.protocal.enumerate;

/**
 * Created by HeyLink on 2017/4/24.
 */

public enum ProjectStateType {

    BOOK_ACCEPT,

    BOOK_STOP,

    PROJECT_RUNNING,

    PROJECT_STOP;


    /*BOOK_ACCEPT --->  BOOK_STOP --> PROJECT_RUNNING --> PROJECT_STOP
    *     \                \
    *     \                \
    *      <———————--
    *
    *
    *
    *
    *
    * */

    public int getValue(){
        return this.ordinal();
    }

}
