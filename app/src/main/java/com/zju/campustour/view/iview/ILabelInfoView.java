package com.zju.campustour.view.iview;

import com.zju.campustour.model.bean.LabelInfo;

import java.util.List;

/**
 * Created by HeyLink on 2017/8/23.
 */

public interface ILabelInfoView {

    void onLabelInfoGotSuccess(List<LabelInfo> mLabelInfos);

    void onLabelInfoGotFailed(Exception e);
}
