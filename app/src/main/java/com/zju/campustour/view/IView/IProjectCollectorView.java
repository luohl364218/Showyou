package com.zju.campustour.view.IView;

import com.zju.campustour.model.database.models.ProjectUserMap;

import java.util.List;

/**
 * Created by HeyLink on 2017/5/4.
 */

public interface IProjectCollectorView {

    public void onQueryProjectCollectorStateDone(boolean state, List<ProjectUserMap> mProjectUserMapList);

    public void onChangeCollectStateError(boolean isFavor);
}
