package com.zju.campustour.view.IView;

import com.zju.campustour.model.database.models.User;

import java.util.List;

/**
 * Created by HeyLink on 2017/4/25.
 */

public interface ISearchUserInfoView {

    public void onGetProviderUserDone(List<User> mUsers);
}
