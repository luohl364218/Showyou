package com.zju.campustour.model.database.models;

import com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType;


import java.io.Serializable;

/**
 * Created by HeyLink on 2017/4/24.
 */

public class ProjectUserMap implements Serializable{


    private String projectId;

    private String userId;

    private UserProjectStateType userProjectState;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String mProjectId) {
        projectId = mProjectId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String mUserId) {
        userId = mUserId;
    }

    public UserProjectStateType getUserProjectState() {
        return userProjectState;
    }

    public void setUserProjectState(UserProjectStateType mUserProjectState) {
        userProjectState = mUserProjectState;
    }
}
