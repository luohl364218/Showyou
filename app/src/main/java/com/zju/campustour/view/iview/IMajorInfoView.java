package com.zju.campustour.view.iview;

import com.zju.campustour.model.database.data.MajorModel;

import java.util.List;

/**
 * Created by HeyLink on 2017/6/24.
 */

public interface IMajorInfoView {

    void onAllMajorInfoGot(List<MajorModel> mMajorModelList);

    void onUpdateMajorInfoGot(List<MajorModel> mMajorModelList);

    void onGetMajorInfoError(Exception e);
}