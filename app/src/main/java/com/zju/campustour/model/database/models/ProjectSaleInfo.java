package com.zju.campustour.model.database.models;

import java.io.Serializable;

/**
 * Created by HeyLink on 2017/5/3.
 */

public class ProjectSaleInfo implements Serializable {

    private boolean refundable;
    private boolean identified;

    public boolean isOfficial() {
        return official;
    }

    public void setOfficial(boolean mOfficial) {
        official = mOfficial;
    }

    private boolean official;
    private int totalScore;
    private int commentNum;
    private Comment comment;

    public ProjectSaleInfo(boolean mRefundable, boolean mIdentified,boolean mOfficial,
                           int mTotalScore, int mCommentNum, Comment mComment) {
        refundable = mRefundable;
        identified = mIdentified;
        official = mOfficial;
        totalScore = mTotalScore;
        commentNum = mCommentNum;
        comment = mComment;
    }

    public boolean isRefundable() {
        return refundable;
    }

    public void setRefundable(boolean mRefundable) {
        refundable = mRefundable;
    }

    public boolean isIdentified() {
        return identified;
    }

    public void setIdentified(boolean mIdentified) {
        identified = mIdentified;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int mTotalScore) {
        totalScore = mTotalScore;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int mCommentNum) {
        commentNum = mCommentNum;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment mComment) {
        comment = mComment;
    }
}
