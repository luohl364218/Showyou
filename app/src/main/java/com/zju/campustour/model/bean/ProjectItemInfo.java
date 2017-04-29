package com.zju.campustour.model.bean;

import java.io.Serializable;

/**
 * Created by HeyLink on 2017/4/26.
 */

public class ProjectItemInfo implements Serializable {


    private String providerId;
    private String projectId;
    private String providerImg;
    private String projectTitle;
    private String projectTime;
    private int projectAcceptNum;
    private String projectInfo;
    private String projectImg;
    private int favoritesNum;
    private long projectPrice;
    private int projectEnrollNum;


    public ProjectItemInfo(String mProviderId,String mProjectId,String mProviderImg, String mProjectTitle,
                           String mProjectTime, int mProjectAcceptNum, 
                           String mProjectInfo, String mProjectImg, 
                           int mFavoritesNum, long mProjectPrice, 
                           int mProjectEnrollNum) {
        providerId = mProviderId;
        projectId = mProjectId;
        providerImg = mProviderImg;
        projectTitle = mProjectTitle;
        projectTime = mProjectTime;
        projectAcceptNum = mProjectAcceptNum;
        projectInfo = mProjectInfo;
        projectImg = mProjectImg;
        favoritesNum = mFavoritesNum;
        projectPrice = mProjectPrice;
        projectEnrollNum = mProjectEnrollNum;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String mProviderId) {
        providerId = mProviderId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String mProjectId) {
        projectId = mProjectId;
    }


    public String getProviderImg() {
        return providerImg;
    }

    public void setProviderImg(String mProviderImg) {
        providerImg = mProviderImg;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String mProjectTitle) {
        projectTitle = mProjectTitle;
    }

    public String getProjectTime() {
        return projectTime;
    }

    public void setProjectTime(String mProjectTime) {
        projectTime = mProjectTime;
    }

    public int getProjectAcceptNum() {
        return projectAcceptNum;
    }

    public void setProjectAcceptNum(int mProjectAcceptNum) {
        projectAcceptNum = mProjectAcceptNum;
    }

    public String getProjectInfo() {
        return projectInfo;
    }

    public void setProjectInfo(String mProjectInfo) {
        projectInfo = mProjectInfo;
    }

    public String getProjectImg() {
        return projectImg;
    }

    public void setProjectImg(String mProjectImg) {
        projectImg = mProjectImg;
    }

    public int getFavoritesNum() {
        return favoritesNum;
    }

    public void setFavoritesNum(int mFavoritesNum) {
        favoritesNum = mFavoritesNum;
    }

    public long getProjectPrice() {
        return projectPrice;
    }

    public void setProjectPrice(long mProjectPrice) {
        projectPrice = mProjectPrice;
    }

    public int getProjectEnrollNum() {
        return projectEnrollNum;
    }

    public void setProjectEnrollNum(int mProjectEnrollNum) {
        projectEnrollNum = mProjectEnrollNum;
    }
}
