package com.zju.campustour.model.bean;

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
    String labelId;
    String labelContent;
    String province;
    String city;
    String district;
    String street;
    String detailLocation;
    PositionModel statusPosition;
    String diyLocation;
    boolean hidePosition;

    public StatusInfoModel(){}

    public StatusInfoModel(String mObjectId,
                            String mImgUrl,
                            String mContent,
                            String mUserId,
                            User mUser,
                            Date mCreatedTime,
                            int mFavourCount,
                            int mCommentCount,
                            boolean mIsDeleted,
                            String mLabelId,
                            String mLabelContent,
                            String mProvince,
                            String mCity, String mDistrict,
                            String mStreet, String mDetailLocation,
                            String mDiyLocation,
                            boolean mHidePosition) {
        objectId = mObjectId;
        imgUrl = mImgUrl;
        content = mContent;
        userId = mUserId;
        user = mUser;
        createdTime = mCreatedTime;
        favourCount = mFavourCount;
        commentCount = mCommentCount;
        isDeleted = mIsDeleted;
        labelId = mLabelId;
        labelContent = mLabelContent;
        province = mProvince;
        city = mCity;
        district = mDistrict;
        street = mStreet;
        detailLocation = mDetailLocation;
        diyLocation = mDiyLocation;
        hidePosition = mHidePosition;
    }


    public StatusInfoModel(String mObjectId,
                           String mImgUrl,
                           String mContent,
                           String mUserId,
                           User mUser,
                           Date mCreatedTime,
                           int mFavourCount,
                           int mCommentCount,
                           boolean mIsDeleted,
                           String mLabelId,
                           String mCity,
                           String mDistrict,
                           String mStreet,
                           String mDiyLocation) {
        objectId = mObjectId;
        imgUrl = mImgUrl;
        content = mContent;
        userId = mUserId;
        user = mUser;
        createdTime = mCreatedTime;
        favourCount = mFavourCount;
        commentCount = mCommentCount;
        isDeleted = mIsDeleted;
        labelId = mLabelId;
        city = mCity;
        district = mDistrict;
        street = mStreet;
        diyLocation = mDiyLocation;
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

    public String getLabelId() {
        return labelId;
    }

    public void setLabelId(String mLabelId) {
        labelId = mLabelId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String mCity) {
        city = mCity;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String mDistrict) {
        district = mDistrict;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String mStreet) {
        street = mStreet;
    }

    public String getDiyLocation() {
        return diyLocation;
    }

    public void setDiyLocation(String mDiyLocation) {
        diyLocation = mDiyLocation;
    }


    public String getLabelContent() {
        return labelContent;
    }

    public void setLabelContent(String mLabelContent) {
        labelContent = mLabelContent;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String mProvince) {
        province = mProvince;
    }

    public String getDetailLocation() {
        return detailLocation;
    }

    public void setDetailLocation(String mDetailLocation) {
        detailLocation = mDetailLocation;
    }

    public PositionModel getStatusPosition() {
        return statusPosition;
    }

    public void setStatusPosition(PositionModel mStatusPosition) {
        statusPosition = mStatusPosition;
    }

    public boolean isHidePosition() {
        return hidePosition;
    }

    public void setHidePosition(boolean mHidePosition) {
        hidePosition = mHidePosition;
    }
}
