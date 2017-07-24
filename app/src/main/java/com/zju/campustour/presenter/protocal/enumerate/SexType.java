package com.zju.campustour.presenter.protocal.enumerate;

/**
 * Created by HeyLink on 2017/4/24.
 */

public enum SexType {

    MALE("男",0),
    FEMALE("女",1);

    private String typeName;
    private int sexTypeId;

    SexType(String mTypeName, int mSexTypeId) {
        typeName = mTypeName;
        sexTypeId = mSexTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String mTypeName) {
        typeName = mTypeName;
    }

    public int getSexTypeId() {
        return sexTypeId;
    }

    public void setSexTypeId(int mSexTypeId) {
        sexTypeId = mSexTypeId;
    }

    public int getValue(){
        return this.ordinal();
    }
}
