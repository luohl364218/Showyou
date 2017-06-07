package com.zju.campustour.presenter.protocal.enumerate;

/**
 * Created by HeyLink on 2017/6/7.
 */

public enum NewsType {

    RECOMMEND("推荐",0),
    STUDY_METHOD("学习",1),
    SCIENCE("科学",2),
    NEWS("时事",3);


    private String typeName;
    private int typeId;

    NewsType(String mTypeName, int mTypeId) {
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

    public int getValue(){
        return this.ordinal();
    }
}
