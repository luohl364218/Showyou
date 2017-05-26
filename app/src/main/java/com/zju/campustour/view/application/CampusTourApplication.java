package com.zju.campustour.view.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.parse.Parse;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;


/**
 * Created by HeyLink on 2017/4/23.
 */

public class CampusTourApplication extends Application {

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

        mInstance = this;

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

}
