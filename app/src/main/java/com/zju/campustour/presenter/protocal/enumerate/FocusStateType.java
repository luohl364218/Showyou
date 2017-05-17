package com.zju.campustour.presenter.protocal.enumerate;

/**
 * Created by HeyLink on 2017/5/16.
 */

public enum  FocusStateType {

    FOCUS("关注",0),
    ALUMI("校友",1);

    private String stateName;
    private int stateId;

    FocusStateType(String name, int id){
        stateName = name;
        stateId = id;
    }

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
