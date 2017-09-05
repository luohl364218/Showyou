package com.zju.campustour.presenter.protocal.enumerate;

/**
 * Created by HeyLink on 2017/8/25.
 */

public enum StatusType {

    HOT_STATUS("热门",0),
    FOCUS_STATUS("关注",1),
    NEARBY_STATUS("附近",2);

    String typeName;
    int typeId;

    StatusType(String mTypeName, int mTypeId) {
        typeName = mTypeName;
        typeId = mTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String mTypeName) {
        typeName = mTypeName;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int mTypeId) {
        typeId = mTypeId;
    }
}
