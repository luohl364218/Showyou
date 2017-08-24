package com.zju.campustour.view.iview;

import com.zju.campustour.model.bean.StatusInfoModel;

import java.util.List;

/**
 * Created by HeyLink on 2017/8/14.
 */

public interface IStatusInfoView {

    void onStatusInfoGotSuccess(List<StatusInfoModel> mStatusInfoModels);

    void onStatusInfoGotError(Exception e);

    void onStatusInfoCommitSuccess();

    void onStatusInfoCommitError(Exception e);


}
