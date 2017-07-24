package com.zju.campustour.presenter.protocal.enumerate;

/**
 * Created by HeyLink on 2017/4/24.
 */

public enum UserType {

    USER("普通用户",0),

    PROVIDER("专业用户",1);

    private String name;
    private int userTypeId;

    UserType(String mName, int mUserTypeId) {
        name = mName;
        userTypeId = mUserTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String mName) {
        name = mName;
    }

    public int getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(int mUserTypeId) {
        userTypeId = mUserTypeId;
    }

    public int getValue(){
        return this.ordinal();
    }
}
