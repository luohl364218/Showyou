package com.zju.campustour.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.chatting.entity.Event;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.util.CountTimerView;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.model.util.SharePreferenceManager;
import com.zju.campustour.presenter.implement.UserInfoOpPresenterImpl;
import com.zju.campustour.presenter.listener.MyTextWatch;
import com.zju.campustour.presenter.protocal.event.LoginDoneEvent;
import com.zju.campustour.view.iview.IUserRegisterView;
import com.zju.campustour.view.widget.ClearEditText;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.IdentifyNumPage;
import cn.smssdk.gui.RegisterPage;
import cn.smssdk.gui.SmartVerifyPage;
import cn.smssdk.utils.SMSLog;

import static com.zju.campustour.model.common.Constants.DB_USERNAME;

public class RegisterActivity extends BaseActivity implements IUserRegisterView {

    @BindView(R.id.register_name)
    ClearEditText registerName;

    @BindView(R.id.txtCountry)
    TextView countryTxt;

    @BindView(R.id.txtCountryCode)
    TextView countryCode;

    @BindView(R.id.register_password)
    ClearEditText registerPwd;

    @BindView(R.id.register_verification)
    ClearEditText verifyTxt;

    @BindView(R.id.verify_btn)
    Button verifyBtn;

    @BindView(R.id.btn_register)
    Button registerBtn;

    @BindView(R.id.txtTip)
    TextView mTxtTip;

    // 默认使用中国区号
    private static final String DEFAULT_COUNTRY_ID = "42";

    /**
     * 帐号名密码是否为空
     **/
    private boolean isRegisterNameNull = false;
    private boolean isPwdNull = false;
    private boolean isPwdConfirmNull = false;

    private Context mContext;

    //获取验证码倒计时
    private CountTimerView countTimerView;

    private UserInfoOpPresenterImpl userRegisteImpl;

    private  SMSEvenHanlder evenHanlder;

    private String registerPhone;
    private String registerPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        SMSSDK.initSDK(this, Constants.bmobAppKey, Constants.bmobAppPwd);


        evenHanlder = new SMSEvenHanlder();
        SMSSDK.registerEventHandler(evenHanlder);
        userRegisteImpl = new UserInfoOpPresenterImpl(this,this);


        countryCode.setText("+86");
        countryTxt.setText("中国");

        initView();


    }

    private void initView(){
/*
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

        verifyTxt.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isPwdConfirmNull = !TextUtils.isEmpty(s.toString());
                registerBtn.setEnabled((isRegisterNameNull && isPwdNull && isPwdConfirmNull));

            }
        });*/

        mContext = this;



    }

    @OnClick(R.id.btn_register)
    protected void register(){

        String phone = registerName.getText().toString().trim().replaceAll("\\s*", "");
        String code = countryCode.getText().toString().trim();

        if (!checkPhoneNum(phone,code)){
            return;
        }

        String pwd = registerPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            showToast("请输入密码");
            return;
        }
        else if (pwd.length() > 16 || pwd.length() < 6){
            showToast("密码长度不符合要求");
            return;
        }

        String verifyNum = verifyTxt.getText().toString().trim();
        if (TextUtils.isEmpty(verifyNum)){
            showToast("请输入验证码");
            return;
        }

        registerPhone = phone;
        registerPassword = pwd;

        SMSSDK.submitVerificationCode(code,phone,verifyNum);


    }

    @OnClick(R.id.login_btn)
    public void onLoginBtnClick(){
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }

    @Override
    public void onUserSignedUpSuccessfully(String userName, String password) {
        //todo 注册成功后通知左侧滑动栏头像加载默认头像
        EventBus.getDefault().post(new LoginDoneEvent(true));
        ParseUser currentUser = ParseUser.getCurrentUser();
        SharePreferenceManager.putString(this,DB_USERNAME,currentUser.getUsername());
        Intent mIntent = new Intent(this, IdentityConfirmActivity.class);
        mIntent.putExtra("userName",userName);
        mIntent.putExtra("password",password);
        mIntent.putExtra("phone",registerPhone);
        mIntent.putExtra("isEditMode",false);

        startActivity(mIntent);
        finish();

    }

    @Override
    public void onUserSignUpDidNotSucceed(Exception e) {

        //showToast("注册失败，该用户名已经存在");
        registerName.setText("");
        registerPwd.setText("");

    }


    private String[] getCurrentCountry() {
        String mcc = getMCC();
        String[] country = null;
        if (!TextUtils.isEmpty(mcc)) {
            country = SMSSDK.getCountryByMCC(mcc);
        }

        if (country == null) {
            SMSLog.getInstance().d("no country found by MCC: " + mcc);
            country = SMSSDK.getCountry(DEFAULT_COUNTRY_ID);
        }
        return country;
    }

    private String getMCC() {
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        // 返回当前手机注册的网络运营商所在国家的MCC+MNC. 如果没注册到网络就为空.
        String networkOperator = tm.getNetworkOperator();
        if (!TextUtils.isEmpty(networkOperator)) {
            return networkOperator;
        }

        // 返回SIM卡运营商所在国家的MCC+MNC. 5位或6位. 如果没有SIM卡返回空
        return tm.getSimOperator();
    }

    @OnClick(R.id.verify_btn)
    public void getCode(){
        if (!NetworkUtil.isNetworkAvailable(this)){
            showToast("请先连接网络");
        }

        String phone = registerName.getText().toString().trim().replaceAll("\\s*", "");
        String code = countryCode.getText().toString().trim();

        if (!checkPhoneNum(phone,code)){
            return;
        }

        //not 86   +86
        SMSSDK.getVerificationCode(code,phone);

    }

    private boolean checkPhoneNum(String phone, String code) {
        if (code.startsWith("+")) {
            code = code.substring(1);
        }

        if (TextUtils.isEmpty(phone)) {
            showToast("请输入手机号码");
            return false;
        }

        if ("86".equals(code)) {
            if(phone.length() != 11) {
                 showToast("手机号码长度不对");
                return false;
            }

        }

        String rule = "^1(3|5|7|8|4)\\d{9}";
        Pattern p = Pattern.compile(rule);
        Matcher m = p.matcher(phone);

        if (!m.matches()) {
            showToast("您输入的手机号码格式不正确");
            return false;
        }

        return true;

    }

    class SMSEvenHanlder extends EventHandler {
        @Override
        public void afterEvent(final int event, final int result,
                               final Object data) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (result == SMSSDK.RESULT_COMPLETE) {
                        if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {

                        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                            // 请求验证码后，跳转到验证码填写页面
                            afterVerificationCodeRequested((Boolean) data);

                        } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                            //验证码通过验证了
                            doReg();

                        }
                    } else {

                        // 根据服务器返回的网络错误，给toast提示
                        try {
                            ((Throwable) data).printStackTrace();
                            Throwable throwable = (Throwable) data;

                            JSONObject object = new JSONObject(
                                    throwable.getMessage());
                            String des = object.optString("detail");
                            if (!TextUtils.isEmpty(des)) {
                                showToast(des);
                                return;
                            }
                        } catch (Exception e) {
                            SMSLog.getInstance().w(e);
                        }

                    }


                }
            });
        }
    }

    private void doReg(){

         /*如果当前已经有登录用户，那么令当前用户下线*/
        ParseUser currentLoginUser = ParseUser.getCurrentUser();
        if (currentLoginUser != null){
            currentLoginUser.put("online",false);
            currentLoginUser.saveInBackground();
        }

        userRegisteImpl.registerUser(registerPhone,registerPassword);


    }

    /** 请求验证码后，跳转到验证码填写页面 */
    private void afterVerificationCodeRequested(boolean smart) {
        String phone = registerName.getText().toString().trim().replaceAll("\\s*", "");
        String code = countryCode.getText().toString().trim();
        if (code.startsWith("+")) {
            code = code.substring(1);
        }

        String formatedPhone = "+" + code + " " + splitPhoneNum(phone);

        String text = getString(R.string.smssdk_send_mobile_detail)+formatedPhone;
        mTxtTip.setText(Html.fromHtml(text));

        CountTimerView timerView = new CountTimerView(verifyBtn);
        timerView.start();

    }

    /** 分割电话号码 */
    private String splitPhoneNum(String phone) {
        StringBuilder builder = new StringBuilder(phone);
        builder.reverse();
        for (int i = 4, len = builder.length(); i < len; i += 5) {
            builder.insert(i, ' ');
        }
        builder.reverse();
        return builder.toString();
    }


    //点击两次返回才退出程序
    private long lastPressTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if (new Date().getTime() - lastPressTime < 2000) {
                /*如果当前已经有登录用户，那么令当前用户下线*/
                ParseUser currentLoginUser = ParseUser.getCurrentUser();
                if (currentLoginUser != null){
                    currentLoginUser.put("online",false);
                    currentLoginUser.saveInBackground();
                }
                this.finish();
            }

            else{
                lastPressTime = new Date().getTime();
                showToast("再按一次返回键退出");
            }
            return true;

        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SMSSDK.unregisterEventHandler(evenHanlder);

    }

}
