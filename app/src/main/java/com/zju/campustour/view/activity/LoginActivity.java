package com.zju.campustour.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;
import com.zju.campustour.MainActivity;
import com.zju.campustour.R;
import com.zju.campustour.model.util.SharePreferenceManager;
import com.zju.campustour.presenter.implement.UserInfoOpPresenterImpl;
import com.zju.campustour.presenter.listener.MyTextWatch;
import com.zju.campustour.presenter.protocal.event.LoginDoneEvent;
import com.zju.campustour.view.iview.IUserLoginView;
import com.zju.campustour.view.application.CampusTourApplication;
import com.zju.campustour.view.widget.ClearEditText;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zju.campustour.model.common.Constants.DB_USERNAME;

public class LoginActivity extends BaseActivity implements IUserLoginView {


    @BindView(R.id.login_user_name)
    ClearEditText userName;

    @BindView(R.id.login_user_pwd)
    ClearEditText password;

    @BindView(R.id.btn_login)
    Button loginBtn;

    @BindView(R.id.forget_pwd)
    TextView forgetPwd;

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
        userLoginImpl = new UserInfoOpPresenterImpl(this,this);

        initView();

    }

    private void initView(){

        currentUserName = SharePreferenceManager.getString(this,DB_USERNAME,"");
        if (!"".equals(currentUserName)){
            userName.setText(currentUserName);
            isUsernameNotNull = true;
        }


     /*   //让按钮随着输入内容有效而使能
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
        });*/

    }

    @OnClick(R.id.btn_login)
    protected void login(View view) {
        //隐藏软键盘
        InputMethodManager manager = ((InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode
                != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }

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
    }

    @OnClick(R.id.login_register_btn)
    public void register(){
        Intent mIntent = new Intent(this,RegisterActivity.class);
        startActivity(mIntent);
        finish();
    }

    //忘记密码
    @OnClick(R.id.forget_pwd)
    public void forgetPassword(){
        Intent mIntent = new Intent(this, InfoWebActivity.class);
        mIntent.putExtra("web","http://www.jianshu.com/p/677a0aaf760e");
        mIntent.putExtra("title","重设密码");
        startActivity(mIntent);
    }

    @OnClick(R.id.register_btn)
    public void onRegisterBtnClick(){
        startActivity(new Intent(this,RegisterActivity.class));
        finish();
    }


    @Override
    public void loginSuccessful() {
        EventBus.getDefault().post(new LoginDoneEvent(true));
        SharePreferenceManager.putString(this,DB_USERNAME,ParseUser.getCurrentUser().getUsername());

        CampusTourApplication application =  CampusTourApplication.getInstance();

        if(application == null || application.getIntent() == null){
            //todo 登录成功后通知左侧滑动栏头像加载
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else{
            application.jumpToTargetActivity(this);
            finish();
        }


    }

    @Override
    public void usernameOrPasswordIsInvalid(String error) {
        if (TextUtils.isEmpty(error))
            showToast("抱歉，用户不存在或密码错误");
        else
            showToast(error);
    }

    @Override
    public void loginError(Exception e) {
        showToast("登录失败，请检查网络连接");
    }
}
