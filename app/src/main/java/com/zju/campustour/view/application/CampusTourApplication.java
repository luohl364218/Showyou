package com.zju.campustour.view.application;

import android.app.Application;
import android.graphics.Bitmap;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.parse.Parse;
import com.squareup.leakcanary.LeakCanary;

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
                .setBitmapsConfig(Bitmap.Config.RGB_565)
                .build();
        Fresco.initialize(this,config);

        LeakCanary.install(this);
    }
}
