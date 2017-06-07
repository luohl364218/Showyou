package com.zju.campustour.presenter.implement;

import android.util.Log;
import android.widget.Toast;

import com.zju.campustour.model.database.models.UserEntry;
import com.zju.campustour.presenter.ipresenter.IMPresenter;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by HeyLink on 2017/5/20.
 */

public class IMImplement implements IMPresenter {


    @Override
    public void registerIMAccount(String userName, String password) {

        JMessageClient.register(userName, password, new BasicCallback() {

            @Override
            public void gotResult(final int status, final String desc) {

                if (status == 0) {
                    JMessageClient.login(userName, password, new BasicCallback() {
                        @Override
                        public void gotResult(final int status, String desc) {
                            if (status == 0) {
                                String username = JMessageClient.getMyInfo().getUserName();
                                String appKey = JMessageClient.getMyInfo().getAppKey();
                                UserEntry user = UserEntry.getUser(username, appKey);
                                if (null == user) {
                                    user = new UserEntry(username, appKey);
                                    user.save();
                                }

                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void loginIMAccount(String userName, String password) {

        JMessageClient.login(userName, password, new BasicCallback() {
            @Override
            public void gotResult(final int status, final String desc) {
                if (status == 0) {
                    String username = JMessageClient.getMyInfo().getUserName();
                    String appKey = JMessageClient.getMyInfo().getAppKey();
                    UserEntry user = UserEntry.getUser(username, appKey);
                    if (null == user) {
                        user = new UserEntry(username, appKey);
                        user.save();
                    }
                    Log.i("Login","极光登陆成功");

                } else {
                    if (status == 801003){
                        //user not exist
                        registerIMAccount(userName, password);
                    }

                    Log.i("LoginController", "status = " + status);
                }
            }
        });
    }

    public void logout(){

    }
}
