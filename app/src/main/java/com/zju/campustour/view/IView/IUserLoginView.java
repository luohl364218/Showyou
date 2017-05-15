package com.zju.campustour.view.IView;

import com.zju.campustour.model.database.models.User;

/**
 * Created by HeyLink on 2017/5/12.
 */

public interface IUserLoginView extends IUserView {
    void loginSuccessful();

    void usernameOrPasswordIsInvalid();

    void loginError(Exception e);

}
