package com.zju.campustour.presenter.protocal.event;

/**
 * Created by HeyLink on 2017/5/9.
 */

public class ToolbarItemClickEvent {
    int itemId;

    public ToolbarItemClickEvent(int mItemId) {
        itemId = mItemId;
    }

    public int getItemId() {
        return itemId;
    }
}
