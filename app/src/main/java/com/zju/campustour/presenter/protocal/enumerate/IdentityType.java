package com.zju.campustour.presenter.protocal.enumerate;

/**
 * Created by HeyLink on 2017/7/12.
 */

public enum IdentityType {

    COLLEGE_STUDENT("在校大学生",0),
    SCHOOL_STUDENT("中小学生",1),
    GRADUATE_STUDENT("毕业大学生",2),
    SCHOOL_TEACHER("在校老师",3),
    STUDENT_PARENT("学生家长",4),
    LOOK_AROUND_USER("围观群众",5);

    private String identityName;
    private int identityId;

    IdentityType(String mIdentityName, int mIdentityId) {
        identityName = mIdentityName;
        identityId = mIdentityId;
    }

    public String getIdentityName() {
        return identityName;
    }

    public void setIdentityName(String mIdentityName) {
        identityName = mIdentityName;
    }

    public int getIdentityId() {
        return identityId;
    }

    public void setIdentityId(int mIdentityId) {
        identityId = mIdentityId;
    }
}
