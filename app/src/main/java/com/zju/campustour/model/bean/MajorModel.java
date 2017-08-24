package com.zju.campustour.model.bean;


import java.util.Date;

/**
 * Created by HeyLink on 2017/5/16.
 */

public class MajorModel {

    private String name;
    private String tag;
    private int majorClass;
    private String majorCode;
    private String majorAbstract;
    private boolean isRecommend;
    private String majorType;
    private boolean isUpdate;
    private Date updateAt;
    private String imgUrl;
    private int interests;

    public MajorModel() {
        super();
    }

    public MajorModel(String name, String tag) {
        this.name = name;
        this.tag = tag;
    }

    public MajorModel(String mName, int mMajorClass,
                       String mMajorCode, String mMajorAbstract,
                       boolean mIsRecommend, String mMajorType,
                       boolean mIsUpdate, Date mUpdateAt,
                       String mImgUrl, int mInterests) {
        name = mName;
        majorClass = mMajorClass;
        majorCode = mMajorCode;
        majorAbstract = mMajorAbstract;
        isRecommend = mIsRecommend;
        majorType = mMajorType;
        isUpdate = mIsUpdate;
        updateAt = mUpdateAt;
        imgUrl = mImgUrl;
        interests = mInterests;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getMajorClass() {
        return majorClass;
    }

    public void setMajorClass(int mMajorClass) {
        majorClass = mMajorClass;
    }

    public String getMajorCode() {
        return majorCode;
    }

    public void setMajorCode(String mMajorCode) {
        majorCode = mMajorCode;
    }

    public String getMajorAbstract() {
        return majorAbstract;
    }

    public void setMajorAbstract(String mMajorAbstract) {
        majorAbstract = mMajorAbstract;
    }

    public boolean isRecommend() {
        return isRecommend;
    }

    public void setRecommend(boolean mRecommend) {
        isRecommend = mRecommend;
    }

    public String getMajorType() {
        return majorType;
    }

    public void setMajorType(String mMajorType) {
        majorType = mMajorType;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean mUpdate) {
        isUpdate = mUpdate;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date mUpdateAt) {
        updateAt = mUpdateAt;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String mImgUrl) {
        imgUrl = mImgUrl;
    }

    public int getInterests() {
        return interests;
    }

    public void setInterests(int mInterests) {
        interests = mInterests;
    }
}
