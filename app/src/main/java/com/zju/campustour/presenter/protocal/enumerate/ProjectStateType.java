package com.zju.campustour.presenter.protocal.enumerate;

/**
 * Created by HeyLink on 2017/4/24.
 */

public enum ProjectStateType {

    BOOK_ACCEPT("立即预约",0),

    BOOK_STOP("结束报名",1),

    PROJECT_RUNNING("活动进行中",2),

    PROJECT_STOP("活动结束",3);


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
    ProjectStateType(String name, int index){
        stateName = name;
        stateId = index;
    }

    private String stateName;
    private int stateId;

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String mStateName) {
        stateName = mStateName;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int mStateId) {
        stateId = mStateId;
    }

    public int getValue(){
        return this.ordinal();
    }

}
