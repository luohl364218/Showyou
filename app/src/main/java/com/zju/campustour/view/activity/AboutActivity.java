package com.zju.campustour.view.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zju.campustour.R;

import cn.jpush.im.android.api.JMessageClient;

public class AboutActivity extends BaseActivity {

    private ImageButton mReturnBtn;
    private TextView mAboutTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mReturnBtn = (ImageButton) findViewById(R.id.return_btn);
        mAboutTv = (TextView) findViewById(R.id.about_tv);

        mReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        PackageManager manager = this.getPackageManager();
        try{
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            String demoVersionName = this.getString(R.string.demo_version_name);
            String sdkVersion = JMessageClient.getSdkVersionString();
            String aboutContent = String.format(demoVersionName, version)
                    + this.getString(R.string.sdk_version)
                    + sdkVersion + this.getString(R.string.about_date)
                    + this.getString(R.string.new_functions);
            mAboutTv.setText(aboutContent);
        }catch (PackageManager.NameNotFoundException e) {
            Log.d("AboutActivity", "Name not Found");
        }

    }
}
