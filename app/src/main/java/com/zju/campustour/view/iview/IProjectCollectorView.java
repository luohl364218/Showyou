package com.zju.campustour.view.iview;

import com.zju.campustour.model.database.models.ProjectUserMap;
import com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType;

import java.util.List;

/**
 * Created by HeyLink on 2017/5/4.
 */

public interface IProjectCollectorView extends IProjectView{

    public void onQueryProjectCollectorStateDone(boolean state, List<ProjectUserMap> mProjectUserMapList);

    public void onChangeCollectStateError(boolean isFavor, UserProjectStateType type);

    public void onGetDealNumDone(int deal);
}
