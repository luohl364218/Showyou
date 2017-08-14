package com.zju.campustour.presenter.ipresenter;

import com.zju.campustour.model.bean.StatusInfoModel;

/**
 * Created by HeyLink on 2017/8/14.
 */

public interface IStatusInfoPresenter {

    void publishStatusInfo(StatusInfoModel mStatusInfoModel);

    void getHotStatusInfo(int start, int count);

    void getMyFocusStatusInfo(int start, int count);

    void getSpecifiedStatusInfo(int start,int count, String userId);
}
