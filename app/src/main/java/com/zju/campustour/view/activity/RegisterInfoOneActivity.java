package com.zju.campustour.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.util.PreferenceUtils;
import com.zju.campustour.presenter.implement.UserInfoOpPresenterImpl;
import com.zju.campustour.presenter.listener.MyTextWatch;
import com.zju.campustour.presenter.protocal.event.LoadingDone;
import com.zju.campustour.presenter.protocal.event.LoginDoneEvent;
import com.zju.campustour.view.IView.IUserLoginView;
import com.zju.campustour.view.widget.ClearEditText;
import com.zju.campustour.view.widget.GradeSelectDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterInfoOneActivity extends BaseActivity{

    @BindView(R.id.register_info_one_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.real_name)
    ClearEditText realName;

    @BindView(R.id.user_grade)
    TextView userGrade;

    @BindView(R.id.user_phone_num)
    ClearEditText userPhone;

    @BindView(R.id.user_email_addr)
    ClearEditText userEmail;

    @BindView(R.id.user_sex_type)
    RadioGroup userSexType;

    @BindView(R.id.user_short_desc)
    ClearEditText userShortDesc;

    @BindView(R.id.user_grade_select)
    LinearLayout userGradeSelect;

    @BindView(R.id.btn_one_next)
    Button btnNext;

    @BindView(R.id.user_info_update_title_1)
    TextView title;

    private boolean isRealNameNotNull = false;
    private boolean isUserPhoneNotNull = false;
    private boolean isUserShortDesc = false;
    private Context mContext = this;

    //注册以后要将当前用户自动登录
    //private UserInfoOpPresenterImpl userLoginImpl;
    //拿到用户的年级层次
    private int gradeId;

    //当前用户
    String userName;
    String password;
    ParseUser currentUser;

    //是否是编辑模式
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_info_one);
        ButterKnife.bind(this);

        Intent mIntent = getIntent();
        userName = mIntent.getStringExtra("userName");
        password = mIntent.getStringExtra("password");
        isEditMode = mIntent.getBooleanExtra("isEditMode",false);
        currentUser = ParseUser.getCurrentUser();
        initOriginalView();

        initView();
    }

    private void initOriginalView() {
        try{

            if (currentUser != null && isEditMode){
                title.setText("信息修改 1/2");
                realName.setText(currentUser.getString("realname"));
                userPhone.setText(currentUser.getString("phoneNum"));
                userEmail.setText(currentUser.getString("emailAddr"));
                userGrade.setText(currentUser.getString("grade"));
                userSexType.check(currentUser.getInt("sex") == 0 ? R.id.select_male : R.id.select_female);
                userShortDesc.setText(currentUser.getString("shortDescription"));
                gradeId = currentUser.getInt("gradeId");
                btnNext.setEnabled(true);
            }
            else {
                userSexType.check(R.id.select_male);
            }

        }catch (Exception e){
        }
    }

    private void initView(){

        //让按钮随着输入内容有效而使能
        realName.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isRealNameNotNull = !TextUtils.isEmpty(s.toString());
                btnNext.setEnabled((isRealNameNotNull && isUserPhoneNotNull && isUserShortDesc));
            }
        });

        userPhone.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isUserPhoneNotNull = !TextUtils.isEmpty(s.toString());
                btnNext.setEnabled((isRealNameNotNull && isUserPhoneNotNull && isUserShortDesc));

            }
        });

        userShortDesc.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isUserShortDesc = !TextUtils.isEmpty(s.toString());
                btnNext.setEnabled((isRealNameNotNull && isUserPhoneNotNull && isUserShortDesc));
            }
        });

        userGradeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GradeSelectDialog.Builder mBuilder = new GradeSelectDialog.Builder(mContext);
                mBuilder.setPositiveButtonListener(new GradeSelectDialog.Builder.OnGradeSelectClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, String grade, int gradeIndex) {
                        dialog.dismiss();
                        userGrade.setText(grade);
                        gradeId = gradeIndex ;
                    }
                });
                GradeSelectDialog dialog =  mBuilder.create();
                dialog.setCancelable(false);
                dialog.show();

            }
        });
    }


    @OnClick(R.id.btn_one_next)
    protected void updateUserInfo(){
        String userRealName = realName.getText().toString().trim();
        if (TextUtils.isEmpty(userRealName)) {
            showToast("请输入真实姓名");
            return;
        }

        String phone = userPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            showToast("请输入手机号码");
            return;
        }

        String grade = userGrade.getText().toString().trim();
        if ("点击选择".equals(grade)){
            showToast("请选择你的学历");
            return;
        }

        String email = userEmail.getText().toString().trim();

        int userSex = userSexType.getCheckedRadioButtonId() == R.id.select_male ? 0:1;

        String shortDesc = userShortDesc.getText().toString().trim();
        if (TextUtils.isEmpty(shortDesc)) {
            showToast("请用一句话完成自我介绍");
            return;
        }

        currentUser.put("realname",userRealName);
        currentUser.put("phoneNum",phone);
        currentUser.put("emailAddr",email);
        currentUser.put("grade",grade);
        currentUser.put("sex",userSex);
        currentUser.put("imgUrl",userSex == 1 ? Constants.URL_DEFAULT_WOMAN_IMG: Constants.URL_DEFAULT_MAN_IMG);
        currentUser.put("shortDescription",shortDesc);
        currentUser.put("gradeId",gradeId);

        currentUser.saveEventually();
        Intent mIntent = new Intent(this, RegisterInfoTwoActivity.class);
        mIntent.putExtra("gradeId",gradeId);
        mIntent.putExtra("isEditMode",isEditMode);
        startActivity(mIntent);
    }

}
