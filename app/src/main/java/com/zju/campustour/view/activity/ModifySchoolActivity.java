package com.zju.campustour.view.activity;

import android.app.Activity;
import android.app.ProgressDialog;
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

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.zju.campustour.R;
import com.zju.campustour.model.chatting.utils.HandleResponseCode;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.presenter.protocal.enumerate.UserType;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class ModifySchoolActivity extends BaseActivity {


    private ImageButton mReturnBtn;
    private TextView mTitle;
    private Button mCommitBtn;
    private EditText mEditAreaEt;
    private Context mContext;
    private int gradeIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_school);
        mContext = this;
        mReturnBtn = (ImageButton) findViewById(R.id.return_btn);
        mTitle = (TextView) findViewById(R.id.jmui_title_tv);
        mCommitBtn = (Button) findViewById(R.id.jmui_commit_btn);
        mEditAreaEt = (EditText) findViewById(R.id.edit_area_et);
        mEditAreaEt.setHint(getIntent().getStringExtra("school"));
        gradeIndex = getIntent().getIntExtra("grade",0);

        mTitle.setText(this.getString(R.string.input_school_title));
        mReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mCommitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String school = mEditAreaEt.getText().toString().trim();
                if (TextUtils.isEmpty(school)) {
                    showToast( mContext.getString(R.string.input_school_error));
                } else {
                    final ProgressDialog dialog = new ProgressDialog(mContext);
                    dialog.setMessage(mContext.getString(R.string.modifying_hint));
                    dialog.show();
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    //1. 大学生+
                    if (gradeIndex > Constants.GRADE_HIGH_SCHOOL){
                        currentUser.put("school",school);

                    }
                    else if (gradeIndex > Constants.GRADE_JUNIOR_HIGH_SCHOOL){
                        currentUser.put("highSchool",school);
                    }
                    else if (gradeIndex > Constants.GRADE_PRIMARY_SCHOOL){
                        currentUser.put("juniorHighSchool",school);
                    }
                    else {
                        currentUser.put("primarySchool",school);
                    }

                    currentUser.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                showToast(mContext.getString(R.string.modify_success_toast));
                                Intent intent = new Intent();
                                intent.putExtra("school", school);
                                setResult(1, intent);
                                finish();
                            } else {
                                dismissSoftInput();
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
