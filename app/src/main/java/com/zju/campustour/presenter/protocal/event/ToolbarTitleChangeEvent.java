package com.zju.campustour.presenter.protocal.event;

/**
 * Created by HeyLink on 2017/4/27.
 */

public class ToolbarTitleChangeEvent {

    String title;

    public ToolbarTitleChangeEvent(String mTitle) {
        title = mTitle;
    }

    public String getTitle() {
        return title;
    }
}
