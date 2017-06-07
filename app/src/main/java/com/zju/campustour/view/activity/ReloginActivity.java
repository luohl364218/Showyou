package com.zju.campustour.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseUser;
import com.zju.campustour.MainActivity;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.util.SharePreferenceManager;
import com.zju.campustour.presenter.implement.UserInfoOpPresenterImpl;
import com.zju.campustour.presenter.listener.MyTextWatch;
import com.zju.campustour.presenter.protocal.event.LoginDoneEvent;
import com.zju.campustour.view.application.CampusTourApplication;
import com.zju.campustour.view.chatting.widget.CircleImageView;
import com.zju.campustour.view.iview.IUserLoginView;
import com.zju.campustour.view.widget.ClearEditText;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zju.campustour.model.common.Constants.DB_USERNAME;

public class ReloginActivity extends BaseActivity implements IUserLoginView {

    @BindView(R.id.relogin_switch_user_btn)
    TextView switchAccount;

    @BindView(R.id.relogin_head_icon)
    CircleImageView userImg;

    @BindView(R.id.username_tv)
    TextView userName;

    @BindView(R.id.relogin_password)
    ClearEditText userPwd;

    @BindView(R.id.relogin_btn)
    Button reloginBtn;

    @BindView(R.id.register_btn)
    Button registerBtn;

    private String userNameStr;
    private String userImgPath;
    private UserInfoOpPresenterImpl userLoginImpl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relogin);
        userNameStr = SharePreferenceManager.getString(this, Constants.DB_USERNAME);
        if (userNameStr == null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        ButterKnife.bind(this);
        userLoginImpl = new UserInfoOpPresenterImpl(this,this);
        fillContent();
    }


    private void fillContent() {
        userName.setText(userNameStr);
        //强制用户设置了头像
        userImgPath = SharePreferenceManager.getString(this,Constants.DB_USERIMG);
        String userOnlineImg = SharePreferenceManager.getString(this,Constants.DB_USERIMG_ONLINE);
        if (userOnlineImg != null)
            Glide.with(this).load(Uri.parse(userOnlineImg)).into(userImg);
        else if (userImgPath != null)
            userImg.setImageURI(Uri.parse(userImgPath));
        else
            userImg.setImageResource(R.mipmap.take_photo_me);//以防万一

        userPwd.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                boolean isPwdNotNull = !TextUtils.isEmpty(s.toString());
                reloginBtn.setEnabled(isPwdNotNull);
            }
        });
    }

    @OnClick(R.id.relogin_btn)
    public void reLogin(){

        String pwd = userPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)){
            showToast("密码不能为空");
            return;
        }

        //隐藏软键盘
        InputMethodManager manager = ((InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode
                != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }

        userLoginImpl.userLogin(userNameStr,pwd);

    }

    @OnClick(R.id.register_btn)
    public void register(){
        Intent mIntent = new Intent(this,RegisterActivity.class);
        startActivity(mIntent);
        finish();
    }

    @OnClick(R.id.relogin_switch_user_btn)
    public void switchUser(){
        Intent mIntent = new Intent(this,LoginActivity.class);
        startActivity(mIntent);
        finish();
    }

    @Override
    public void loginSuccessful() {
        SharePreferenceManager.putString(this,DB_USERNAME, ParseUser.getCurrentUser().getUsername());
        CampusTourApplication application =  CampusTourApplication.getInstance();

        if(application.getIntent() == null){
            //todo 登录成功后通知左侧滑动栏头像加载
            startActivity(new Intent(this, MainActivity.class));
            EventBus.getDefault().post(new LoginDoneEvent(true));
            finish();
        }else{
            EventBus.getDefault().post(new LoginDoneEvent(true));
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
