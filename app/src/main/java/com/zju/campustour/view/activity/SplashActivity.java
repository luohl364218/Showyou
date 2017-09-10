package com.zju.campustour.view.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.parse.ParseUser;
import com.zju.campustour.MainActivity;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.util.SharePreferenceManager;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //无title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  //全屏
        super.onCreate(savedInstanceState);

        // 判断是否是第一次开启应用
        boolean notFirstOpen = SharePreferenceManager.getBoolean(this, Constants.First_Open);

        // 如果是第一次启动，则先进入功能引导页
        if (!notFirstOpen) {

            Intent intent = new Intent(this, WelcomeGuideActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // 如果不是第一次启动app，则正常显示启动屏
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                enterMainActivity();
            }
        }, 1000);

    }

    private void enterMainActivity() {
        ParseUser user = ParseUser.getCurrentUser();
        UserInfo myInfo = JMessageClient.getMyInfo();
        if (user == null || myInfo == null){
            Intent intent = new Intent();
            if (null != SharePreferenceManager.getString(this,Constants.DB_USERNAME)) {
                intent.setClass(this, ReloginActivity.class);
            } else {
                intent.setClass(this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }


    }
}
