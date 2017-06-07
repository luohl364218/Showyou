package com.zju.campustour.presenter.protocal.event;

/**
 * Created by HeyLink on 2017/5/18.
 */

public class UserPictureUploadDone {

    String cloudImgUrl;
    String localImgUrl;

    public UserPictureUploadDone(String mCloudImgUrl, String mLocalImgUrl) {
        cloudImgUrl = mCloudImgUrl;
        localImgUrl = mLocalImgUrl;
    }

    public String getCloudImgUrl() {
        return cloudImgUrl;
    }

    public String getLocalImgUrl() {
        return localImgUrl;
    }
}
