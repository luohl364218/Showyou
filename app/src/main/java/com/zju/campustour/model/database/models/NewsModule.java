package com.zju.campustour.model.database.models;

/**
 * Created by HeyLink on 2017/5/20.
 */

public class NewsModule {

    String imgUrl;
    String text;

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String mLinkUrl) {
        linkUrl = mLinkUrl;
    }

    String linkUrl;

    public NewsModule() {
    }

    public NewsModule(String mImgUrl, String mText, String link) {
        imgUrl = mImgUrl;
        text = mText;
        linkUrl =link;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String mImgUrl) {
        imgUrl = mImgUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String mText) {
        text = mText;
    }
}
