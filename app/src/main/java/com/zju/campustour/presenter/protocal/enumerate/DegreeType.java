package com.zju.campustour.presenter.protocal.enumerate;

/**
 * Created by HeyLink on 2017/7/22.
 */

public enum DegreeType {

    UNDERGRADUATE_STUDENT("本科",18),
    POSTGRADUATE_STUDENT("硕士",22),
    DOCTOR_STUDENT("博士",26);

    String typeName;
    int typeId;

    DegreeType(String typeName,int typeId){
        this.typeName = typeName;
        this.typeId = typeId;
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
