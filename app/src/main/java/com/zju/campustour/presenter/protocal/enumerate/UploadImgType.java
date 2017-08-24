package com.zju.campustour.presenter.protocal.enumerate;

/**
 * Created by HeyLink on 2017/8/23.
 */

public enum UploadImgType {
    
    IMG_AVATAR("avatar/",0),
    IMG_STATUS("status/",1),
    IMG_PROJECT("project/",2),
    IMG_IDENTITY("identity/",3);
    
    String filePrefix;
    int typeId;

    UploadImgType(String mFilePrefix, int mTypeId) {
        filePrefix = mFilePrefix;
        typeId = mTypeId;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    public void setFilePrefix(String mFilePrefix) {
        filePrefix = mFilePrefix;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int mTypeId) {
        typeId = mTypeId;
    }
}
