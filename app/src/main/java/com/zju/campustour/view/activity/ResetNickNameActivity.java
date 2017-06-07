package com.zju.campustour.view.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zju.campustour.R;
import com.zju.campustour.model.chatting.utils.DialogCreator;
import com.zju.campustour.model.chatting.utils.HandleResponseCode;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class ResetNickNameActivity extends BaseActivity {

    private ImageButton mReturnBtn;
    private TextView mTitleTv;
    private Button mCommitBtn;
    private EditText mNickNameEt;
    private Dialog mDialog;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_nick_name);
        mContext = this;
        mReturnBtn = (ImageButton) findViewById(R.id.return_btn);
        mTitleTv = (TextView) findViewById(R.id.jmui_title_tv);
        mCommitBtn = (Button) findViewById(R.id.jmui_commit_btn);
        mNickNameEt = (EditText) findViewById(R.id.nick_name_et);

        mTitleTv.setText(this.getString(R.string.setting_username_big_hit));
        final String oldNickName = getIntent().getStringExtra("nickName");
        mNickNameEt.setHint(oldNickName);
        mReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mCommitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nickName = mNickNameEt.getText().toString().trim();
                if (TextUtils.isEmpty(nickName)) {
                    showToast(mContext.getString(R.string.nickname_not_null_toast));
                    return;
                }
                if (!oldNickName.equals(nickName)) {
                    mDialog = DialogCreator.createLoadingDialog(mContext, mContext.getString(R.string.modifying_hint));
                    mDialog.show();
                    UserInfo myUserInfo = JMessageClient.getMyInfo();
                    myUserInfo.setNickname(nickName);
                    JMessageClient.updateMyInfo(UserInfo.Field.nickname, myUserInfo, new BasicCallback() {
                        @Override
                        public void gotResult(final int status, final String desc) {
                            mDialog.dismiss();
                            if (status == 0) {
                                showToast(ResetNickNameActivity.this.getString(R.string.modify_success_toast));
                                Intent intent = new Intent();
                                intent.putExtra("nickName", nickName);
                                setResult(0, intent);
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

    public void dismissSoftInput() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //隐藏软键盘
        InputMethodManager imm = ((InputMethodManager) mContext
                .getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.RESULT_HIDDEN);
            }
        }
    }
}
