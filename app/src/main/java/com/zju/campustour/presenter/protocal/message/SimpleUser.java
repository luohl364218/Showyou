package com.zju.campustour.presenter.protocal.message;

import com.zju.campustour.presenter.protocal.enumerate.SexType;

/**
 * Created by HeyLink on 2017/4/24.
 */

public class SimpleUser {

    private int id;

    private String userName;

    private String loginName;

    private String password;

    private SexType sex;

    private String school;

    private String major;

    private String grade;

    private int fansNum;

    private boolean online;

    private String ImgUrl;

    private String phoneNum;

    private String emailAddr;


    public int getId() {
        return id;
    }

    public void setId(int mId) {
        id = mId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String mUserName) {
        userName = mUserName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String mLoginName) {
        loginName = mLoginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String mPassword) {
        password = mPassword;
    }

    public SexType getSex() {
        return sex;
    }

    public void setSex(SexType mSex) {
        sex = mSex;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String mSchool) {
        school = mSchool;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String mMajor) {
        major = mMajor;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String mGrade) {
        grade = mGrade;
    }

    public int getFansNum() {
        return fansNum;
    }

    public void setFansNum(int mFansNum) {
        fansNum = mFansNum;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean mOnline) {
        online = mOnline;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public void setImgUrl(String mImgUrl) {
        ImgUrl = mImgUrl;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String mPhoneNum) {
        phoneNum = mPhoneNum;
    }

    public String getEmailAddr() {
        return emailAddr;
    }

    public void setEmailAddr(String mEmailAddr) {
        emailAddr = mEmailAddr;
    }

}
