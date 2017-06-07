package com.zju.campustour.view.iview;

/**
 * Created by HeyLink on 2017/5/13.
 */

public interface IUserRegisterView extends IUserView {

    void userSignedUpSuccessfully(String userName, String password);

    void userSignUpDidNotSucceed(Exception e);

}
