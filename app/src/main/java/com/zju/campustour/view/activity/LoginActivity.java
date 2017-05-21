package com.zju.campustour.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.util.PreferenceUtils;
import com.zju.campustour.presenter.implement.IMImplement;
import com.zju.campustour.presenter.implement.UserInfoOpPresenterImpl;
import com.zju.campustour.presenter.listener.MyTextWatch;
import com.zju.campustour.presenter.protocal.event.LoginDoneEvent;
import com.zju.campustour.view.IView.IUserLoginView;
import com.zju.campustour.view.application.CampusTourApplication;
import com.zju.campustour.view.widget.ClearEditText;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements IUserLoginView {

    @BindView(R.id.login_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.login_user_name)
    ClearEditText userName;

    @BindView(R.id.login_user_pwd)
    ClearEditText password;

    @BindView(R.id.btn_login)
    Button loginBtn;

    //注册的用户名和密码
    String currentUserName;

    /**
     * 帐号名密码是否为空
     **/
    private boolean isUsernameNotNull = false;
    private boolean isPwdNotNull = false;

    private UserInfoOpPresenterImpl userLoginImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        userLoginImpl = new UserInfoOpPresenterImpl(this,this);

        initView();

    }

    private void initView(){

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.finish();
            }
        });


        currentUserName = PreferenceUtils.getString(this,"currentUserName","");
        if (!"".equals(currentUserName)){
            userName.setText(currentUserName);
            isUsernameNotNull = true;
        }


        //让按钮随着输入内容有效而使能
        userName.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isUsernameNotNull = !TextUtils.isEmpty(s.toString());
                loginBtn.setEnabled((isUsernameNotNull && isPwdNotNull));
            }
        });

        password.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isPwdNotNull = !TextUtils.isEmpty(s.toString());
                loginBtn.setEnabled((isUsernameNotNull && isPwdNotNull));
            }
        });

    }

    @OnClick(R.id.btn_login)
    protected void login(View view) {

        String loginName = userName.getText().toString().trim();
        if (TextUtils.isEmpty(loginName)) {
            showToast("请输入用户名");
            return;
        }

        String pwd = password.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            showToast("请输入密码");
            return;
        }

        userLoginImpl.userLogin(loginName,pwd);
        PreferenceUtils.putString(this,"userName",loginName);
        PreferenceUtils.putString(this,"password",pwd);
        IMImplement mIMImplement = new IMImplement();
        mIMImplement.registerIMAccount(loginName,pwd);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.toolbar_register:
                Intent mIntent = new Intent(this,RegisterActivity.class);
                startActivity(mIntent);
                finish();
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    public void loginSuccessful() {

        PreferenceUtils.putString(this,"currentUserName",ParseUser.getCurrentUser().getUsername());
        CampusTourApplication application =  CampusTourApplication.getInstance();

        if(application.getIntent() == null){
            //todo 登录成功后通知左侧滑动栏头像加载
            EventBus.getDefault().post(new LoginDoneEvent(true));
            finish();
        }else{
            EventBus.getDefault().post(new LoginDoneEvent(true));
            application.jumpToTargetActivity(LoginActivity.this);
            finish();

        }
    }

    @Override
    public void usernameOrPasswordIsInvalid() {
        showToast("抱歉，用户不存在或密码错误");
    }

    @Override
    public void loginError(Exception e) {
        showToast("登录失败，请检查网络连接");
    }
}
