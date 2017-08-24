package com.zju.campustour.model.bean;

import java.util.Date;

/**
 * Created by HeyLink on 2017/5/20.
 */

public class NewsModule {

    String imgUrl;
    String text;
    String linkUrl;
    Date newsTime;


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

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String mLinkUrl) {
        linkUrl = mLinkUrl;
    }


    public Date getNewsTime() {
        return newsTime;
    }

    public void setNewsTime(Date mNewsTime) {
        newsTime = mNewsTime;
    }



}
