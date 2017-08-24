package com.zju.campustour.view.iview;

import com.zju.campustour.model.bean.MajorModel;

import java.util.List;

/**
 * Created by HeyLink on 2017/8/5.
 */

public interface IMajorInfoUpdateView extends IMajorInfoView{


    void onAllMajorInfoGot(List<MajorModel> mMajorModelList);

    void onUpdateMajorInfoGot(List<MajorModel> mMajorModelList);

    void onGetMajorInfoError(Exception e);
}
