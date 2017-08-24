package com.zju.campustour.presenter.protocal.event;

import com.zju.campustour.model.bean.LabelInfo;

/**
 * Created by HeyLink on 2017/8/23.
 */

public class LabelSelectEvent {

    LabelInfo mLabelInfo;

    public LabelSelectEvent(LabelInfo mLabelInfo) {
        this.mLabelInfo = mLabelInfo;
    }

    public LabelInfo getLabelInfo() {
        return mLabelInfo;
    }
}
