package com.zju.campustour.view.iview;

/**
 * Created by HeyLink on 2017/5/13.
 */

public interface IUserRegisterView extends IUserView {

    void onUserSignedUpSuccessfully(String userName, String password);

    void onUserSignUpDidNotSucceed(Exception e);

}
