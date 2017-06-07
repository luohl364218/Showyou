package com.zju.campustour.view.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zju.campustour.R;
import com.zju.campustour.model.chatting.utils.HandleResponseCode;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class SelectAreaActivity extends BaseActivity {


    private ImageButton mReturnBtn;
    private TextView mTitle;
    private Button mCommitBtn;
    private EditText mEditAreaEt;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_area);
        mContext = this;
        mReturnBtn = (ImageButton) findViewById(R.id.return_btn);
        mTitle = (TextView) findViewById(R.id.jmui_title_tv);
        mCommitBtn = (Button) findViewById(R.id.jmui_commit_btn);
        mEditAreaEt = (EditText) findViewById(R.id.edit_area_et);
        mEditAreaEt.setHint(getIntent().getStringExtra("OldRegion"));
        mTitle.setText(this.getString(R.string.input_location_title));
        mReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mCommitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String region = mEditAreaEt.getText().toString().trim();
                if (TextUtils.isEmpty(region)) {
                    showToast( mContext.getString(R.string.input_area_error));
                } else {
                    final ProgressDialog dialog = new ProgressDialog(mContext);
                    dialog.setMessage(mContext.getString(R.string.modifying_hint));
                    dialog.show();
                    UserInfo myUserInfo = JMessageClient.getMyInfo();
                    myUserInfo.setRegion(region);
                    JMessageClient.updateMyInfo(UserInfo.Field.region, myUserInfo, new BasicCallback() {
                        @Override
                        public void gotResult(final int status, final String desc) {
                            dialog.dismiss();
                            if (status == 0) {
                                showToast(mContext.getString(R.string.modify_success_toast));
                                Intent intent = new Intent();
                                intent.putExtra("region", region);
                                setResult(1, intent);
                                finish();
                            } else {
                                dismissSoftInput();
                                HandleResponseCode.onHandle(mContext, status, false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void dismissSoftInput() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //隐藏软键盘
        InputMethodManager imm = ((InputMethodManager) mContext
                .getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
