package com.zju.campustour.model.database.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by HeyLink on 2017/5/3.
 */

public class Comment implements Serializable {

    private String projectId;
    private String commentUserId;
    private String commentContent;
    private int commentScore;
    private Date commentTime;

    public Comment() {
    }

    public Comment(String mProjectId, String mCommentUserId, String mCommentContent, int mCommentScore) {
        projectId = mProjectId;
        commentUserId = mCommentUserId;
        commentContent = mCommentContent;
        commentScore = mCommentScore;
        commentTime = new Date();
    }

    public Comment(String mProjectId, String mCommentUserId, String mCommentContent, int mCommentScore,Date mCommentTime) {
        projectId = mProjectId;
        commentUserId = mCommentUserId;
        commentContent = mCommentContent;
        commentScore = mCommentScore;
        commentTime = mCommentTime;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String mProjectId) {
        projectId = mProjectId;
    }

    public String getCommentUserId() {
        return commentUserId;
    }

    public void setCommentUserId(String mCommentUserId) {
        commentUserId = mCommentUserId;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String mCommentContent) {
        commentContent = mCommentContent;
    }

    public int getCommentScore() {
        return commentScore;
    }

    public void setCommentScore(int mCommentScore) {
        commentScore = mCommentScore;
    }


    public Date getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Date mCommentTime) {
        commentTime = mCommentTime;
    }

}
