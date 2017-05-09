package com.zju.campustour.model.database.models;

import java.io.Serializable;

/**
 * Created by HeyLink on 2017/5/3.
 */

public class ProjectSaleInfo implements Serializable {

    private boolean refundable;
    private boolean identified;
    private int totalScore;
    private int commentNum;
    private int originalPrice;
    private Comment comment;

    public ProjectSaleInfo(boolean mRefundable, boolean mIdentified,
                           int mTotalScore, int mCommentNum,
                           int mOriginalPrice, Comment mComment) {
        refundable = mRefundable;
        identified = mIdentified;
        totalScore = mTotalScore;
        commentNum = mCommentNum;
        originalPrice = mOriginalPrice;
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

    public int getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(int mOriginalPrice) {
        originalPrice = mOriginalPrice;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment mComment) {
        comment = mComment;
    }
}
