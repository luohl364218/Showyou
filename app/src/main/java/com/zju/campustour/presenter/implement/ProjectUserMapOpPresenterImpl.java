package com.zju.campustour.presenter.implement;

import com.zju.campustour.presenter.ipresenter.IProjectUserMapOpPresenter;
import com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType;

/**
 * Created by HeyLink on 2017/4/24.
 */

public class ProjectUserMapOpPresenterImpl implements IProjectUserMapOpPresenter {
    @Override
    public int getBookedNum(int projectId) {
        return 0;
    }

    @Override
    public UserProjectStateType getUserProjectState(int userId, int projectId) {
        return null;
    }
}
