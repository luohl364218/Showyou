package com.zju.campustour.model.bean;

import com.zju.campustour.model.database.models.User;

import java.util.Date;

/**
 * 这个是用户发布的状态表
 * Created by HeyLink on 2017/8/14.
 */

public class StatusInfoModel {

    String objectId;
    String imgUrl;
    String content;
    String userId;
    User user;
    Date createdTime;
    int favourCount;
    int commentCount;

    boolean isDeleted;

    public StatusInfoModel(String mObjectId,
                           String mImgUrl,
                           String mContent,
                           String mUserId,
                           User mUser,
                           Date mCreatedTime,
                           int mFavourCount,
                           int mCommentCount,
                           boolean mIsDeleted) {
        objectId = mObjectId;
        imgUrl = mImgUrl;
        content = mContent;
        userId = mUserId;
        user = mUser;
        createdTime = mCreatedTime;
        favourCount = mFavourCount;
        commentCount = mCommentCount;
        isDeleted = mIsDeleted;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String mObjectId) {
        objectId = mObjectId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String mImgUrl) {
        imgUrl = mImgUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String mContent) {
        content = mContent;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String mUserId) {
        userId = mUserId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User mUser) {
        user = mUser;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date mCreatedTime) {
        createdTime = mCreatedTime;
    }

    public int getFavourCount() {
        return favourCount;
    }

    public void setFavourCount(int mFavourCount) {
        favourCount = mFavourCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int mCommentCount) {
        commentCount = mCommentCount;
    }


    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean mDeleted) {
        isDeleted = mDeleted;
    }

}
