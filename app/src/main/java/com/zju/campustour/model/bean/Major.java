package com.zju.campustour.model.bean;

/**
 * Created by Administrator on 2016/7/9.
 */
public class Major {

    //专业配图
    private String majorImg;
    //专业所属类别
    private int majorClass;
    //专业名称
    private String majorName;
    //专业代码
    private String majorCode;
    //专业简介
    private String majorAbstract;
    //感兴趣人数，点赞该专业人数
    private int interests;

    public Major(String majorAbstract, int majorClass, String majorCode, String majorName) {
        this.majorAbstract = majorAbstract;
        this.majorClass = majorClass;
        this.majorCode = majorCode;
        this.majorName = majorName;
    }

    public String getMajorImg() {
        return majorImg;
    }

    public void setMajorImg(String mMajorImg) {
        majorImg = mMajorImg;
    }

    public String getMajorAbstract() {
        return majorAbstract;
    }

    public void setMajorAbstract(String majorAbstract) {
        this.majorAbstract = majorAbstract;
    }

    public int getMajorClass() {
        return majorClass;
    }

    public void setMajorClass(int majorClass) {
        this.majorClass = majorClass;
    }

    public String getMajorCode() {
        return majorCode;
    }

    public void setMajorCode(String majorCode) {
        this.majorCode = majorCode;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }
}
