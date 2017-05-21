package com.zju.campustour.view.IView;

import com.zju.campustour.model.database.models.User;

import java.util.List;

/**
 * Created by HeyLink on 2017/5/18.
 */

public interface IProjectUserInfoView extends IProjectView {

    public void onGetProjectUserInfoDone(List<User> userList);
}
