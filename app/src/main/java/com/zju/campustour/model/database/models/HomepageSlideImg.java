package com.zju.campustour.model.database.models;

/**
 * Created by HeyLink on 2017/5/20.
 */

public class HomepageSlideImg {

    String imgUrl;
    String linkUrl;
    String description;

    public HomepageSlideImg() {
    }

    public HomepageSlideImg(String mImgUrl, String mLinkUrl, String mDescription) {
        imgUrl = mImgUrl;
        linkUrl = mLinkUrl;
        description = mDescription;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String mImgUrl) {
        imgUrl = mImgUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String mLinkUrl) {
        linkUrl = mLinkUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String mDescription) {
        description = mDescription;
    }
}
