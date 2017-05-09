package com.zju.campustour.view.IView;

import com.zju.campustour.model.database.models.Project;

import java.util.List;

/**
 * Created by HeyLink on 2017/4/26.
 */

public interface ISearchProjectInfoView {

    public void onGetProjectInfoDone(List<? extends Object> mProjects);

    public void onGetProjectInfoError(Exception e);
}
