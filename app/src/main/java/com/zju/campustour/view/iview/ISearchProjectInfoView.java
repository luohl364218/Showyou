package com.zju.campustour.view.iview;


import java.util.List;

/**
 * Created by HeyLink on 2017/4/26.
 */

public interface ISearchProjectInfoView extends IProjectView{

    public void onGetProjectInfoDone(List<? extends Object> mProjects);

    public void onGetProjectInfoError(Exception e);
}
