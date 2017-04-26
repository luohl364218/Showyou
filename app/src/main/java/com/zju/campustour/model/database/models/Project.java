package com.zju.campustour.model.database.models;

import com.zju.campustour.presenter.protocal.enumerate.ProjectStateType;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.util.Date;
import java.util.List;

/**
 * Created by HeyLink on 2017/4/24.
 */

public class Project extends DataSupport {


    private String id;

    private User provider;

    private String title;

    private Date startTime;

    private String imgUrl;

    private long price;

    private String description;

    private int acceptNum;

    private ProjectStateType projectState;

    private List<User> favorites;

    public Project(String mId, User mProvider, String mTitle,
                    Date mStartTime, String mImgUrl, long mPrice,
                    String mDescription, int mAcceptNum,
                    ProjectStateType mProjectState, List<User> mFavorites) {
        id = mId;
        provider = mProvider;
        title = mTitle;
        startTime = mStartTime;
        imgUrl = mImgUrl;
        price = mPrice;
        description = mDescription;
        acceptNum = mAcceptNum;
        projectState = mProjectState;
        favorites = mFavorites;
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

    public List<User> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<User> mFavorites) {
        favorites = mFavorites;
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

}
