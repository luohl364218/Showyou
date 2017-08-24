package com.zju.campustour.model.bean;

import com.zju.campustour.model.bean.User;
import com.zju.campustour.presenter.protocal.enumerate.IdentityType;
import com.zju.campustour.presenter.protocal.enumerate.VerifyStateType;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by WuyuShan on 2017/7/25.
 */

public class VerifyInfo implements Serializable{



    String objectId;
    User submitUser;
    String submitUserId;
    String submitImgUrl;
    String submitDescription;
    VerifyStateType submitVerifyStateType;
    Date submitTime;
    Date verifiedTime;
    String replyComment;
    String managerId;


    IdentityType identityType;

    public VerifyInfo(){

    }

    public VerifyInfo(String objectId,
                      String submitUserId,
                      String submitImgUrl,
                      String submitDescription,
                      VerifyStateType submitVerifyStateType,
                      Date submitTime,
                      Date verifiedTime,
                      String replyComment) {
        this.objectId = objectId;
        this.submitUserId = submitUserId;
        this.submitImgUrl = submitImgUrl;
        this.submitDescription = submitDescription;
        this.submitVerifyStateType = submitVerifyStateType;
        this.submitTime = submitTime;
        this.verifiedTime = verifiedTime;
        this.replyComment = replyComment;
    }


    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public User getSubmitUser() {
        return submitUser;
    }

    public void setSubmitUser(User submitUser) {
        this.submitUser = submitUser;
    }

    public String getSubmitUserId() {
        return submitUserId;
    }

    public void setSubmitUserId(String submitUserId) {
        this.submitUserId = submitUserId;
    }

    public String getSubmitImgUrl() {
        return submitImgUrl;
    }

    public void setSubmitImgUrl(String submitImgUrl) {
        this.submitImgUrl = submitImgUrl;
    }

    public String getSubmitDescription() {
        return submitDescription;
    }

    public void setSubmitDescription(String submitDescription) {
        this.submitDescription = submitDescription;
    }

    public VerifyStateType getSubmitVerifyStateType() {
        return submitVerifyStateType;
    }

    public void setSubmitVerifyStateType(VerifyStateType submitVerifyStateType) {
        this.submitVerifyStateType = submitVerifyStateType;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Date getVerifiedTime() {
        return verifiedTime;
    }

    public void setVerifiedTime(Date verifiedTime) {
        this.verifiedTime = verifiedTime;
    }

    public String getReplyComment() {
        return replyComment;
    }

    public void setReplyComment(String replyComment) {
        this.replyComment = replyComment;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }


    public IdentityType getIdentityType() {
        return identityType;
    }

    public void setIdentityType(IdentityType identityType) {
        this.identityType = identityType;
    }
}
