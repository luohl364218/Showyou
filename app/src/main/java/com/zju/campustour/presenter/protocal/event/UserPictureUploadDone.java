package com.zju.campustour.presenter.protocal.event;

/**
 * Created by HeyLink on 2017/5/18.
 */

public class UserPictureUploadDone {

    String imgUrl;

    public UserPictureUploadDone(String mImgUrl) {
        imgUrl = mImgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
