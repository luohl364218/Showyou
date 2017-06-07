package com.zju.campustour.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zju.campustour.R;
import com.zju.campustour.model.chatting.utils.HandleResponseCode;
import com.zju.campustour.model.common.Constants;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditNoteNameActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.edit_note_name_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.edit_note_name_et)
    EditText mNoteNameEt;
    @BindView(R.id.edit_friend_info_et)
    EditText mFriendInfoEt;
    @BindView(R.id.delete_iv)
    ImageButton mResetBtn;
    @BindView(R.id.text_count_tv)
    TextView mCountTv;
    Context mContext;
    SweetAlertDialog mDialog;
    private static final int UPDATE_NOTE_NAME = 0x1100;
    private String mNotename;
    private MyHandler myHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note_name);
        ButterKnife.bind(this);
        this.mContext = this;

        initModule();

    }

    private void initModule() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mNoteNameEt.setHint(getIntent().getStringExtra("noteName"));
        mFriendInfoEt.setHint(getIntent().getStringExtra("friendDescription"));
        mFriendInfoEt.addTextChangedListener(watcher);

        mResetBtn.setOnClickListener(this);
    }

    TextWatcher watcher = new TextWatcher() {
        private CharSequence temp;
        private int editStart;
        private int editEnd;
        private String note;
        @Override
        public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            temp = s;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            editStart = mFriendInfoEt.getSelectionStart();
            editEnd = mFriendInfoEt.getSelectionEnd();
            if (temp.length() >= 0) {
                note = "" + (temp.length()) + "/" + (200 - temp.length()) + "";
                mCountTv.setText(note);
            }
            if (temp.length() > 200) {
                editable.delete(editStart - 1, editEnd);
                int tempSelection = editStart;
                mFriendInfoEt.setText(editable);
                mFriendInfoEt.setSelection(tempSelection);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_save:
                mNotename = mNoteNameEt.getText().toString();
                final String noteText = mFriendInfoEt.getText().toString();
                String targetId = getIntent().getStringExtra(Constants.TARGET_ID);
                String appKey = getIntent().getStringExtra(Constants.TARGET_APP_KEY);
                if (!TextUtils.isEmpty(mNotename)) {
                    mDialog = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
                    mDialog.setTitleText("正在保存");
                    mDialog.show();
                    JMessageClient.getUserInfo(targetId, appKey, new GetUserInfoCallback() {
                        @Override
                        public void gotResult(final int status, final String desc, final UserInfo userInfo) {
                            if (status == 0) {
                                userInfo.updateNoteName(mNotename, new BasicCallback() {
                                    @Override
                                    public void gotResult(int status, String s) {
                                        if (status == 0) {
                                            if (!TextUtils.isEmpty(noteText)) {
                                                userInfo.updateNoteText(noteText, new BasicCallback() {
                                                    @Override
                                                    public void gotResult(int status, String s) {
                                                        myHandler.sendEmptyMessage(UPDATE_NOTE_NAME);
                                                        if (status != 0) {
                                                            HandleResponseCode.onHandle(mContext, status, false);
                                                        }
                                                    }
                                                });
                                            } else {
                                                myHandler.sendEmptyMessage(UPDATE_NOTE_NAME);
                                            }
                                        } else {
                                            HandleResponseCode.onHandle(mContext, status, false);
                                        }
                                    }
                                });
                            }
                        }
                    });
                } else {
                    showToast(mContext.getString(R.string.note_name_is_empty_hint));
                }
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.delete_iv:
                mFriendInfoEt.setText("");
                break;
            default:
                break;
        }
    }

    private static class MyHandler extends Handler {

        private WeakReference<EditNoteNameActivity> mActivity;

        public MyHandler(EditNoteNameActivity activity) {
            mActivity = new WeakReference<EditNoteNameActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            EditNoteNameActivity activity = mActivity.get();
            if (null != activity) {
                switch (msg.what) {
                    case UPDATE_NOTE_NAME:
                        if (null != activity.mDialog) {
                            activity.mDialog.dismissWithAnimation();
                            Intent intent = new Intent();
                            intent.putExtra(Constants.NOTENAME, activity.mNotename);
                            activity.setResult(Constants.RESULT_CODE_EDIT_NOTENAME, intent);
                            activity.finish();
                        }
                        break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mDialog) {
            mDialog.dismissWithAnimation();
        }
    }
}
