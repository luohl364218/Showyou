package com.zju.campustour.presenter.implement;

import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.zju.campustour.presenter.ipresenter.IMinterface;

/**
 * Created by HeyLink on 2017/5/20.
 */

public class IMImplement implements IMinterface {


    @Override
    public void registerIMAccount(final String userName, final String password) {
        //huanxin
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(userName, password);//同步方法
                    loginIMAccount(userName,password);
                } catch (HyphenateException mE) {
                    mE.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void loginIMAccount(String userName, String password) {

        EMClient.getInstance().login(userName,password,new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.d("main", "登录聊天服务器成功！");
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("main", "登录聊天服务器失败！");
            }
        });
    }

    public void logout(){
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub

            }
        });
    }
}
