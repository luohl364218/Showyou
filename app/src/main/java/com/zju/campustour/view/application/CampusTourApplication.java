package com.zju.campustour.view.application;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.parse.Parse;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.zju.campustour.model.bean.UserEntry;
import com.zju.campustour.presenter.receiver.NotificationClickEventReceiver;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;


/**
 * Created by HeyLink on 2017/4/23.
 */

public class CampusTourApplication extends com.activeandroid.app.Application {

    public static boolean isNeedAtMsg = true;
    public static String PICTURE_DIR = "sdcard/showyou/pictures/";
    private boolean flag = true;

    @Override
    public void onCreate() {
        super.onCreate();
        //LitePal.initialize(this);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("CampusTour")
                .server("http://119.23.248.205:1337/parse/")
                .clientKey("5681e7f54529b5a41b2woyou")
                .build()
        );

        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .setBitmapsConfig(Bitmap.Config.RGB_565)
                .build();
        Fresco.initialize(this,config);

        ZXingLibrary.initDisplayOpinion(this);
        //初始化极光推送
        JPushInterface.init(this);
        //初始化JMessage-sdk，第二个参数表示开启漫游
        JMessageClient.init(getApplicationContext(), true);
        //设置Notification的模式
        JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_DEFAULT);
        //注册Notification点击的接收器
        new NotificationClickEventReceiver(getApplicationContext());
        mInstance = this;
        //自动更新和手动更新功能初始化
        Bmob.initialize(this,"13298107f78d5a596be774c30df169d1");

        if (flag){
            BmobUpdateAgent.initAppVersion();
            flag = false;
        }


    }




    private static CampusTourApplication mInstance;

    public static CampusTourApplication getInstance(){
        return mInstance;
    }

    private Intent intent;
    public void putIntent(Intent intent){
        this.intent = intent;
    }

    public Intent getIntent() {
        return this.intent;
    }

    public void jumpToTargetActivity(Context context){

        context.startActivity(intent);
        this.intent =null;
    }



    public static UserEntry getUserEntry() {
        return UserEntry.getUser(JMessageClient.getMyInfo().getUserName(), JMessageClient.getMyInfo().getAppKey());
    }

}
