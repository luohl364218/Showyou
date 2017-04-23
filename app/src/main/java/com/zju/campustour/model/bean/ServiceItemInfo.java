package com.zju.campustour.model.bean;

/**
 * Created by HeyLink on 2017/4/24.
 */

public class ServiceItemInfo {

    private int studentId;
    private String studentImg;
    private String studentName;
    private String serviceName;
    private String studentShortInfo;
    private int fansNum;


    public ServiceItemInfo(int mStudentId, String mStudentImg, String mStudentName,
                            String mServiceName, String mStudentShortInfo, int mFansNum) {
        studentId = mStudentId;
        studentImg = mStudentImg;
        studentName = mStudentName;
        serviceName = mServiceName;
        studentShortInfo = mStudentShortInfo;
        fansNum = mFansNum;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int mStudentId) {
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

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String mServiceName) {
        serviceName = mServiceName;
    }

    public String getStudentShortInfo() {
        return studentShortInfo;
    }

    public void setStudentShortInfo(String mStudentShortInfo) {
        studentShortInfo = mStudentShortInfo;
    }

    public int getFansNum() {
        return fansNum;
    }

    public void setFansNum(int mFansNum) {
        fansNum = mFansNum;
    }
}
