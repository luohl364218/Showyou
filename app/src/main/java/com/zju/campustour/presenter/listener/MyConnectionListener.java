package com.zju.campustour.presenter.listener;

import android.content.Context;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.util.NetUtils;

/**
 * Created by HeyLink on 2017/5/20.
 */

public class MyConnectionListener implements EMConnectionListener {

    private Context mContext;

    public MyConnectionListener(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onConnected() {
    }
    @Override
    public void onDisconnected(final int error) {

        if(error == EMError.USER_REMOVED){
            // 显示帐号已经被移除
        }else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
            // 显示帐号在其他设备登录
        } else {
            if (NetUtils.hasNetwork(mContext)){

            }
            else{
                //连接不到聊天服务器
            }
            //当前网络不可用，请检查网络设置
        }
    }

}
