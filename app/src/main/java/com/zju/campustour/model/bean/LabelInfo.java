package com.zju.campustour.model.bean;

/**
 * Created by HeyLink on 2017/8/23.
 */

public class LabelInfo {

    String labelId;
    User user;
    String content;
    int joinNum;

    public LabelInfo(String mLabelId, User mUser, String mContent, int mJoinNum) {
        labelId = mLabelId;
        user = mUser;
        content = mContent;
        joinNum = mJoinNum;
    }

    public String getLabelId() {
        return labelId;
    }

    public void setLabelId(String mLabelId) {
        labelId = mLabelId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User mUser) {
        user = mUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String mContent) {
        content = mContent;
    }

    public int getJoinNum() {
        return joinNum;
    }

    public void setJoinNum(int mJoinNum) {
        joinNum = mJoinNum;
    }
}
