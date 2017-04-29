package com.zju.campustour.presenter.ipresenter;

import com.zju.campustour.model.database.models.User;


/**
 * Created by HeyLink on 2017/4/24.
 */

public interface IUserInfoOpPresenter {

    public void initialUserInfo();

    public void addOrUpdateUser(User mUser);

    public void queryUserInfoWithId(String userId);

    public void userLogin(String loginName, String password);

    public void userLogout(String userId);

    public void userDelete(String userId);

    public void queryProviderUserWithConditions(String school, String major,int start, int area, int categoryId);

}
