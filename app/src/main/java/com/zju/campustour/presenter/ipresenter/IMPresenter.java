package com.zju.campustour.presenter.ipresenter;

/**
 * Created by HeyLink on 2017/5/20.
 */

public interface IMPresenter {

    public void registerIMAccount(String userName, String password);

    public void loginIMAccount(String userName, String password);

    public void logout();
}
