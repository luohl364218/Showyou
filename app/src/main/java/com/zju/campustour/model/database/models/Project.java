package com.zju.campustour.model.database.models;

import com.zju.campustour.presenter.protocal.enumerate.ProjectStateType;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by HeyLink on 2017/4/24.
 */

public class Project implements Serializable {


    private String id;

    private User provider;

    private String title;

    private Date startTime;

    private String imgUrl;

    private long price;

    public long getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(long mSalePrice) {
        salePrice = mSalePrice;
    }

    private long salePrice;

    private String description;


    public String getTips() {
        return tips;
    }

    public void setTips(String mTips) {
        tips = mTips;
    }

    private String tips;

    private int acceptNum;

    private ProjectStateType projectState;

    private int collectorNum;

    private int bookedNum;

    public Project() {
    }

    public Project(String mId, User mProvider, String mTitle,
                   Date mStartTime, String mImgUrl, long mPrice, long mSalePrice,
                   String mDescription, int mAcceptNum,
                   ProjectStateType mProjectState, int mCollectorNum, int mBookedNum, String mTips) {
        id = mId;
        provider = mProvider;
        title = mTitle;
        startTime = mStartTime;
        imgUrl = mImgUrl;
        price = mPrice;
        salePrice = mSalePrice;
        description = mDescription;
        acceptNum = mAcceptNum;
        projectState = mProjectState;
        collectorNum = mCollectorNum;
        bookedNum = mBookedNum;
        tips = mTips;
    }

    public String getId() {
        return id;
    }

    public void setId(String mId) {
        id = mId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String mTitle) {
        title = mTitle;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date mStartTime) {
        startTime = mStartTime;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long mPrice) {
        price = mPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String mDescription) {
        description = mDescription;
    }

    public int getAcceptNum() {
        return acceptNum;
    }

    public void setAcceptNum(int mAcceptNum) {
        acceptNum = mAcceptNum;
    }

    public ProjectStateType getProjectState() {
        return projectState;
    }

    public void setProjectState(ProjectStateType mProjectState) {
        projectState = mProjectState;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String mImgUrl) {
        imgUrl = mImgUrl;
    }


    public User getProvider() {
        return provider;
    }

    public void setProvider(User mProvider) {
        provider = mProvider;
    }


    public int getCollectorNum() {
        return collectorNum;
    }

    public void setCollectorNum(int mCollectorNum) {
        collectorNum = mCollectorNum;
    }


    public void update(Project mProject){

        if (this == mProject)return;
        if (mProject == null || getClass() != mProject.getClass())return;

        if (getTitle() != null? !getTitle().equals(mProject.getTitle()) : mProject.getTitle() != null){
            setTitle(mProject.getTitle());
        }

        if (getStartTime() != null? !getStartTime().equals(mProject.getStartTime()) : mProject.getStartTime() != null){
            setStartTime(mProject.getStartTime());
        }

        if (getImgUrl() != null? !getImgUrl().equals(mProject.getImgUrl()) : mProject.getImgUrl() != null){
            setImgUrl(mProject.getImgUrl());
        }

        if (getPrice() >= 0? !(getPrice()== mProject.getPrice()) : mProject.getPrice() >= 0){
            setPrice(mProject.getPrice());
        }

        if (getDescription() != null? !getDescription().equals(mProject.getDescription()) : mProject.getDescription() != null){
            setDescription(mProject.getDescription());
        }

        if (getAcceptNum() >= 0? !(getAcceptNum()== mProject.getAcceptNum()) : mProject.getAcceptNum() >= 0){
            setAcceptNum(mProject.getAcceptNum());
        }

        if (getProjectState() != null? !getProjectState().equals(mProject.getProjectState()) : mProject.getProjectState() != null){
            setProjectState(mProject.getProjectState());
        }

        if (getCollectorNum() >= 0? !(getCollectorNum()== mProject.getCollectorNum()) : mProject.getCollectorNum() >= 0){
            setCollectorNum(mProject.getCollectorNum());
        }

        if (getBookedNum() >= 0? !(getBookedNum()== mProject.getBookedNum()) : mProject.getBookedNum() >= 0){
            setBookedNum(mProject.getBookedNum());
        }
    }


    public int getBookedNum() {
        return bookedNum;
    }

    public void setBookedNum(int mBookedNum) {
        bookedNum = mBookedNum;
    }
}
