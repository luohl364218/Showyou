package com.zju.campustour.model.database.models;

import com.zju.campustour.presenter.protocal.enumerate.SexType;
import com.zju.campustour.presenter.protocal.enumerate.UserType;

import org.litepal.crud.DataSupport;

/**
 * Created by HeyLink on 2017/4/24.
 */

public class User extends DataSupport {


    private String id;

    private String userName;

    private String loginName;

    private String password;

    private SexType sex;

    private String school;

    private String major;

    private String grade;

    private int fansNum;

    private boolean online;

    private String imgUrl;

    private String phoneNum;

    private String emailAddr;

    private UserType userType;

    private String description;

    private String shortDescription;

    public User(String mId, String mUserName, String mLoginName, String mPassword,
                SexType mSex, String mSchool, String mMajor, String mGrade,
                int mFansNum, boolean mOnline, String mImgUrl, String mPhoneNum,
                String mEmailAddr, UserType mUserType, String mDescription, String mShortDescription) {
        id = mId;
        userName = mUserName;
        loginName = mLoginName;
        password = mPassword;
        sex = mSex;
        school = mSchool;
        major = mMajor;
        grade = mGrade;
        fansNum = mFansNum;
        online = mOnline;
        imgUrl = mImgUrl;
        phoneNum = mPhoneNum;
        emailAddr = mEmailAddr;
        userType = mUserType;
        description = mDescription;
        shortDescription = mShortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String mDescription) {
        description = mDescription;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String mShortDescription) {
        shortDescription = mShortDescription;
    }

    public String getId() {
        return id;
    }

    public void setId(String mId) {
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
        return imgUrl;
    }

    public void setImgUrl(String mImgUrl) {
        imgUrl = mImgUrl;
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

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType mUserType) {
        userType = mUserType;
    }
}
