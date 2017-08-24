package com.zju.campustour.view.iview;

import com.zju.campustour.model.bean.MajorModel;

/**
 * Created by HeyLink on 2017/8/5.
 */

public interface IMajorInfoInterestView extends IMajorInfoView {

    void onCurrentMajorGotSuccess(MajorModel major);

    void onCurrentMajorGotError(Exception e);

}
