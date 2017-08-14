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

public class ModifyEmailActivity extends BaseActivity {


    private ImageButton mReturnBtn;
    private TextView mTitle;
    private Button mCommitBtn;
    private EditText mEditText;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_email);
        mContext = this;
        mReturnBtn = (ImageButton) findViewById(R.id.return_btn);
        mTitle = (TextView) findViewById(R.id.jmui_title_tv);
        mCommitBtn = (Button) findViewById(R.id.jmui_commit_btn);
        mEditText = (EditText) findViewById(R.id.edit_area_et);
        mEditText.setHint(getIntent().getStringExtra("email"));

        mTitle.setText(this.getString(R.string.input_email_title));
        mReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mCommitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEditText.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    showToast( mContext.getString(R.string.input_email_error));
                } else {
                    final ProgressDialog dialog = new ProgressDialog(mContext);
                    dialog.setMessage(mContext.getString(R.string.modifying_hint));
                    dialog.show();
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    currentUser.put("emailAddr",email);
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                showToast(mContext.getString(R.string.modify_success_toast));
                                Intent intent = new Intent();
                                intent.putExtra("email", email);
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
