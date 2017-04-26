package com.zju.campustour.presenter.ipresenter;

import com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType;

/**
 * Created by HeyLink on 2017/4/24.
 */

public interface IProjectUserMapOpPresenter {

    public int getBookedNum(int projectId);

    public UserProjectStateType getUserProjectState(int userId, int projectId);

}
