package com.zju.campustour.presenter.ipresenter;

import com.zju.campustour.model.bean.Project;
import com.zju.campustour.presenter.protocal.enumerate.ProjectStateType;
import com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType;

/**
 * Created by HeyLink on 2017/4/24.
 */

public interface IProjectInfoOpPresenter {

    public void addOrUpdateProject(Project mProject, boolean isEditMode);

    public void queryProjectWithUserId(String userId, int startIndex, int count);

    public void queryProjectWithUserIdAndState(String userId, UserProjectStateType type, int startIndex, int count);

    public void setProjectState(String projectId, ProjectStateType state);

    public void getLimitProjectInfo(int start, int count ,boolean isLatest, boolean isHotest);

    public void getLimitProjectInfo(int start, int count ,boolean isLatest, boolean isHotest,boolean isRecommend);

    public void getLimitProjectInfo(int start, int count ,boolean isLatest, boolean isHotest,boolean isRecommend, boolean isOffline);

    public void queryProjectWithId(String projectId);

    public void queryProjectSaleInfoWithId(String projectId);

    public void deleteProjectWithId(String projectId);

}
