package com.zju.campustour.view.application;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.parse.Parse;

import org.litepal.LitePal;

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
                .server("http://123.206.208.68:1337/parse/")
                .clientKey("5681e7f54529b5a41b2woyou")
                .build()
        );

        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(this);

    }
}
