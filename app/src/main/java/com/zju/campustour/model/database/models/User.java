package com.zju.campustour.model.database.models;

import com.zju.campustour.presenter.protocal.enumerate.SexType;
import com.zju.campustour.presenter.protocal.enumerate.UserType;

import java.io.Serializable;

/**
 * Created by HeyLink on 2017/4/24.
 */

public class User implements Serializable {


    private String id;

    private String userName;

    private String realName;

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

    private int categoryId;

    private String province;
    private String city;
    private String district;
    private int    gradeId;
    private String collegeTag;


    private boolean isVerified;

  
    public User(){

    }

    public User(String mId, String mUserName, String mRealName, String mPassword,
                SexType mSex, String mSchool, String mMajor, String mGrade,
                int mFansNum, boolean mOnline, String mImgUrl, String mPhoneNum,
                String mEmailAddr, UserType mUserType, String mDescription, String mShortDescription, int mCategoryId,
                String mProvince,String mCity, String mDistrict, int mGradeId, String mCollegeTag, boolean mIsVerified) {
        id = mId;
        userName = mUserName;
        realName = mRealName;
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
        categoryId = mCategoryId;

        province = mProvince;
        city = mCity;
        district = mDistrict;
        gradeId = mGradeId;
        collegeTag = mCollegeTag;
        isVerified = mIsVerified;
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

    public String getRealName() {
        return realName;
    }

    public void setRealName(String mRealName) {
        realName = mRealName;
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


    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int mCategoryId) {
        categoryId = mCategoryId;
    }


    public String getProvince() {
        return province;
    }

    public void setProvince(String mProvince) {
        province = mProvince;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String mCity) {
        city = mCity;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String mDistrict) {
        district = mDistrict;
    }

    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int mGradeId) {
        gradeId = mGradeId;
    }

    public String getCollegeTag() {
        return collegeTag;
    }

    public void setCollegeTag(String mCollegeTag) {
        collegeTag = mCollegeTag;
    }


    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }


    public void update(User mUser){

        if (this == mUser)return;
        if (mUser == null || getClass() != mUser.getClass())return;

        if (getUserName() != null? !getUserName().equals(mUser.getUserName()) : mUser.getUserName() != null){
            setUserName(mUser.getUserName());
        }

        if (getRealName() != null? !getRealName().equals(mUser.getRealName()) : mUser.getRealName() != null){
            setRealName(mUser.getRealName());
        }

        if (getSchool() != null? !getSchool().equals(mUser.getSchool()) : mUser.getSchool() != null){
            setSchool(mUser.getSchool());
        }

        if (getMajor() != null? !getMajor().equals(mUser.getMajor()) : mUser.getMajor() != null){
            setMajor(mUser.getMajor());
        }

        if (getGrade() != null? !getGrade().equals(mUser.getGrade()) : mUser.getGrade() != null){
            setGrade(mUser.getGrade());
        }

        if (getFansNum() >= 0? !(getFansNum()== mUser.getFansNum()) : mUser.getFansNum() >= 0){
            setFansNum(mUser.getFansNum());
        }

        if (getImgUrl() != null? !getImgUrl().equals(mUser.getImgUrl()) : mUser.getImgUrl() != null){
            setImgUrl(mUser.getImgUrl());
        }



        if (getShortDescription() != null? !getShortDescription().equals(mUser.getShortDescription()) : mUser.getShortDescription() != null){
            setShortDescription(mUser.getShortDescription());
        }

        setVerified(mUser.isVerified());
    }
}
