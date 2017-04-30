package com.zju.campustour.presenter.ipresenter;

import com.zju.campustour.model.database.models.Project;
import com.zju.campustour.presenter.protocal.enumerate.ProjectStateType;

/**
 * Created by HeyLink on 2017/4/24.
 */

public interface IProjectInfoOpPresenter {

    public void addOrUpdateProject(Project mProject);

    public void queryProjectWithUserId(String userId);

    public void setProjectState(int projectId, ProjectStateType state);

    public void getLimitProjectInfo(int start, int count);

}