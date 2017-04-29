package com.zju.campustour.model.bean;

import java.io.Serializable;

/**
 * Created by HeyLink on 2017/4/24.
 */

public class ProviderUserItemInfo implements Serializable {

    private String studentId;
    private String studentImg;
    private String studentName;
    private String shortDescription;
    private String studentCollege;
    private String studentMajor;
    private String studentGrade;
    private int fansNum;


    public ProviderUserItemInfo(String mStudentId, String mStudentImg,
                                String mStudentName, String mShortDescription,
                                String mStudentCollege, String mStudentMajor,
                                String mStudentGrade, int mFansNum) {
        studentId = mStudentId;
        studentImg = mStudentImg;
        studentName = mStudentName;
        shortDescription = mShortDescription;
        studentCollege = mStudentCollege;
        studentMajor = mStudentMajor;
        studentGrade = mStudentGrade;
        fansNum = mFansNum;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String mStudentId) {
        studentId = mStudentId;
    }

    public String getStudentImg() {
        return studentImg;
    }

    public void setStudentImg(String mStudentImg) {
        studentImg = mStudentImg;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String mStudentName) {
        studentName = mStudentName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String mShortDescription) {
        shortDescription = mShortDescription;
    }

    public int getFansNum() {
        return fansNum;
    }

    public void setFansNum(int mFansNum) {
        fansNum = mFansNum;
    }

    public String getStudentCollege() {
        return studentCollege;
    }

    public void setStudentCollege(String mStudentCollege) {
        studentCollege = mStudentCollege;
    }

    public String getStudentMajor() {
        return studentMajor;
    }

    public void setStudentMajor(String mStudentMajor) {
        studentMajor = mStudentMajor;
    }

    public String getStudentGrade() {
        return studentGrade;
    }

    public void setStudentGrade(String mStudentGrade) {
        studentGrade = mStudentGrade;
    }
}
