package com.zju.campustour.view.activity;

import android.os.Bundle;

import com.zju.campustour.R;

public class SendLocationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_location);
        showToast("暂未开通发送位置功能，敬请期待");
        finish();
    }
}
