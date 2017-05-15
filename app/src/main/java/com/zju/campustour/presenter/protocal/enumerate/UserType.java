package com.zju.campustour.presenter.protocal.enumerate;

/**
 * Created by HeyLink on 2017/4/24.
 */

public enum UserType {

    USER("普通用户",0),

    PROVIDER("专业用户",1);

    private String name;
    private int index;

    UserType(String mName, int mIndex) {
        name = mName;
        index = mIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String mName) {
        name = mName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int mIndex) {
        index = mIndex;
    }

    public int getValue(){
        return this.ordinal();
    }
}
