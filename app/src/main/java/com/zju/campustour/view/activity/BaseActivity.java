package com.zju.campustour.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseUser;
import com.zju.campustour.model.database.data.MajorData;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.view.application.CampusTourApplication;
import com.zju.campustour.view.widget.ActivityCollector;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by HeyLink on 2017/4/22.
 */

public class BaseActivity extends AppCompatActivity {

    private Toast mToast = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("BaseActivity", getClass().getSimpleName());

        ActivityCollector.addActivity(this);


    }

    public void startActivity(Intent intent, boolean isNeedLogin){


        if(isNeedLogin){

            ParseUser currentUser = ParseUser.getCurrentUser();;
            if(currentUser ==null){

                CampusTourApplication.getInstance().putIntent(intent);
                Intent loginIntent = new Intent(this
                        , LoginActivity.class);
                super.startActivity(intent);
            }

        }
        else{
            super.startActivity(intent);
        }

    }


    public void showToast(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

    public void showToast(int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(getApplicationContext(), resId,
                    Toast.LENGTH_SHORT);
        } else {
            mToast.setText(resId);
        }
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
