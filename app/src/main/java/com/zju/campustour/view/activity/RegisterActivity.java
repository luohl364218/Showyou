package com.zju.campustour.view.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.util.PreferenceUtils;
import com.zju.campustour.presenter.implement.IMImplement;
import com.zju.campustour.presenter.implement.UserInfoOpPresenterImpl;
import com.zju.campustour.presenter.listener.MyTextWatch;
import com.zju.campustour.presenter.protocal.event.LoginDoneEvent;
import com.zju.campustour.presenter.protocal.event.RegisterDoneEvent;
import com.zju.campustour.view.IView.IUserRegisterView;
import com.zju.campustour.view.widget.ClearEditText;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity implements IUserRegisterView {

    @BindView(R.id.register_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.register_name)
    ClearEditText registerName;

    @BindView(R.id.register_password)
    ClearEditText registerPwd;

    @BindView(R.id.register_pwd_again)
    EditText registerPwdConf;

    @BindView(R.id.btn_register)
    Button registerBtn;

    /**
     * 帐号名密码是否为空
     **/
    private boolean isRegisterNameNull = false;
    private boolean isPwdNull = false;
    private boolean isPwdConfirmNull = false;

    private UserInfoOpPresenterImpl userRegisteImpl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        userRegisteImpl = new UserInfoOpPresenterImpl(this,this);
        initView();

    }

    private void initView(){

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //让按钮随着输入内容有效而使能
        registerName.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isRegisterNameNull = !TextUtils.isEmpty(s.toString());
                registerBtn.setEnabled((isRegisterNameNull && isPwdNull && isPwdConfirmNull));
            }
        });

        registerPwd.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isPwdNull = !TextUtils.isEmpty(s.toString());
                registerBtn.setEnabled((isRegisterNameNull && isPwdNull && isPwdConfirmNull));

            }
        });

        registerPwdConf.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isPwdConfirmNull = !TextUtils.isEmpty(s.toString());
                registerBtn.setEnabled((isRegisterNameNull && isPwdNull && isPwdConfirmNull));
            }
        });

        registerPwdConf.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(hasFocus){
                    //获得焦点处理
                }
                else {
                    //失去焦点处理
                    String pwd = registerPwd.getText().toString().trim();
                    String pwdConf = registerPwdConf.getText().toString().trim();
                    if (!pwd.equals(pwdConf)){
                        showToast("两次密码不一致");
                    }
                }
            }
        });

    }

    @OnClick(R.id.btn_register)
    protected void register(){
        String userName = registerName.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            showToast("请输入用户名");
            return;
        }

        String pwd = registerPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            showToast("请输入密码");
            return;
        }

        String pwdConf = registerPwdConf.getText().toString().trim();
        if (TextUtils.isEmpty(pwdConf)) {
            showToast("请再次输入密码");
            return;
        }
        else if (!pwd.equals(pwdConf)){
            showToast("两次密码不一致");
            return;
        }

        ParseUser currentLoginUser = ParseUser.getCurrentUser();
        if (currentLoginUser != null){
            currentLoginUser.put("online",false);
            currentLoginUser.saveEventually();
        }

        IMImplement mIMImplement = new IMImplement();
        mIMImplement.registerIMAccount(userName,pwd);

        userRegisteImpl.registerUser(userName,pwd);

        PreferenceUtils.putString(this,"userName",userName);
        PreferenceUtils.putString(this,"password",pwd);
    }

    @Override
    public void userSignedUpSuccessfully(String userName, String password) {
        //todo 注册成功后通知左侧滑动栏头像加载默认头像
        EventBus.getDefault().post(new LoginDoneEvent(true));
        ParseUser currentUser = ParseUser.getCurrentUser();
        PreferenceUtils.putString(this,"currentUserName",currentUser.getUsername());
        Intent mIntent = new Intent(this, RegisterInfoOneActivity.class);
        mIntent.putExtra("userName",userName);
        mIntent.putExtra("password",password);
        mIntent.putExtra("isEditMode",false);

        startActivity(mIntent);
        finish();

    }

    @Override
    public void userSignUpDidNotSucceed(Exception e) {

        //showToast("注册失败，该用户名已经存在");
        registerName.setText("");
        registerPwd.setText("");
        registerPwdConf.setText("");
        registerBtn.setEnabled(false);
    }
}
