package com.zju.campustour.view.iview;

/**
 * Created by HeyLink on 2017/5/12.
 */

public interface IUserLoginView extends IUserView {
    void loginSuccessful();

    void usernameOrPasswordIsInvalid(String error);

    void loginError(Exception e);

}
