package com.zju.campustour.view.activity;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.chatting.utils.DialogCreator;
import com.zju.campustour.model.chatting.utils.FileHelper;
import com.zju.campustour.model.chatting.utils.HandleResponseCode;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.util.DataCleanManager;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.model.util.SharePreferenceManager;
import com.zju.campustour.presenter.chatting.tools.NativeImageLoader;
import com.zju.campustour.presenter.protocal.event.LogoutEvent;
import com.zju.campustour.view.chatting.widget.SlipButton;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import cn.bmob.v3.update.BmobUpdateAgent;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.IntegerCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class SettingActivity extends BaseActivity implements View.OnClickListener, SlipButton.OnChangedListener{

    private ImageButton mReturnBtn;
    private ImageButton mMenuBtn;
    private TextView mTitle;
    private RelativeLayout mNotificationLl;
    private RelativeLayout mResetPwdRl;
    private RelativeLayout mAboutRl;
    private RelativeLayout mCheckUpdateRl;
    private RelativeLayout mLogoutRl;
    private RelativeLayout mCleanDataRl;
    private TextView cacheSizeTv;
    private SlipButton mNoDisturbBtn;
    private Context mContext;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mContext = this;
        mReturnBtn = (ImageButton) findViewById(R.id.return_btn);
        mMenuBtn = (ImageButton) findViewById(R.id.right_btn);
        mTitle = (TextView) findViewById(R.id.title);
        mNotificationLl = (RelativeLayout) findViewById(R.id.notification_rl);
        mResetPwdRl = (RelativeLayout) findViewById(R.id.change_password_rl);
        mNoDisturbBtn = (SlipButton) findViewById(R.id.global_no_disturb_setting);
        mAboutRl = (RelativeLayout) findViewById(R.id.about_rl);
        mCheckUpdateRl = (RelativeLayout) findViewById(R.id.check_update_rl);
        mLogoutRl = (RelativeLayout)findViewById(R.id.logout_rl);
        mCleanDataRl = (RelativeLayout) findViewById(R.id.clean_data_rl);
        cacheSizeTv = (TextView) findViewById(R.id.cache_size);

        mMenuBtn.setVisibility(View.GONE);
        mTitle.setText(this.getString(R.string.setting));
        mReturnBtn.setOnClickListener(this);
        mNotificationLl.setOnClickListener(this);
        mResetPwdRl.setOnClickListener(this);
        mAboutRl.setOnClickListener(this);
        mNoDisturbBtn.setOnChangedListener(R.id.global_no_disturb_setting, this);
        mCheckUpdateRl.setOnClickListener(this);
        mLogoutRl.setOnClickListener(this);
        mCleanDataRl.setOnClickListener(this);

        String cacheSize = null;
        try {
            cacheSize = DataCleanManager.getTotalCacheSize(this);
            cacheSizeTv.setText(cacheSize);
        } catch (Exception mE) {
            mE.printStackTrace();
        }

        final Dialog dialog = DialogCreator.createLoadingDialog(mContext, getString(R.string.jmui_loading));
        dialog.show();
        JMessageClient.getNoDisturbGlobal(new IntegerCallback() {
            @Override
            public void gotResult(int status, String desc, Integer integer) {
                dialog.dismiss();
                if (status == 0) {
                    mNoDisturbBtn.setChecked(1 == integer);
                } else {
                    HandleResponseCode.onHandle(mContext, status, false);
                }
            }
        });
    }



    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.return_btn:
                finish();
                break;
            case R.id.notification_rl:
                intent = new Intent();
                intent.setClass(mContext, NotificationSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.change_password_rl:
                Dialog dialog = DialogCreator.createResetPwdDialog(this);
                dialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.show();
                break;
            case R.id.about_rl:
               /* intent = new Intent();
                intent.setClass(mContext, AboutActivity.class);
                //
                startActivity(intent);*/
                Intent mIntent = new Intent(mContext, InfoWebActivity.class);
                mIntent.putExtra("web","http://www.jianshu.com/p/57f5767af536");
                mIntent.putExtra("title","关于校游");
                startActivity(mIntent);
                break;

            case R.id.check_update_rl:
                //// TODO: 2017/9/10
                break;
            case R.id.clean_data_rl:
                DataCleanManager.clearAllCache(this);
                showToast("缓存清除完毕");
                cacheSizeTv.setText("0KB");
                break;

            case R.id.logout_rl:
                //退出登录
                ParseUser currentLoginUser = ParseUser.getCurrentUser();
            /*退出登录也要网络可用*/
                if (currentLoginUser != null){

                    View.OnClickListener listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            switch (view.getId()) {
                                case R.id.jmui_cancel_btn:
                                    mDialog.cancel();
                                    break;
                                case R.id.jmui_commit_btn:
                                    currentLoginUser.put("online",false);
                                    currentLoginUser.saveInBackground();

                                    Logout();
                                    cancelNotification();
                                    NativeImageLoader.getInstance().releaseCache();
                                    finish();
                                    mDialog.cancel();
                                    break;
                            }
                        }
                    };
                    mDialog = DialogCreator.createLogoutDialog(mContext, listener);
                    mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
                    mDialog.show();


                }
                break;


        }
    }

    @Override
    public void onChanged(int id, final boolean checkState) {
        switch (id) {
            case R.id.global_no_disturb_setting:
                final Dialog loadingDialog = DialogCreator.createLoadingDialog(mContext,
                        mContext.getString(R.string.jmui_loading));
                loadingDialog.show();
                JMessageClient.setNoDisturbGlobal(checkState ? 1 : 0, new BasicCallback() {
                    @Override
                    public void gotResult(int status, String desc) {
                        loadingDialog.dismiss();
                        if (status == 0) {
                            if (checkState) {
                                showToast(mContext.getString(R.string.set_no_disturb_global_succeed));

                            } else {
                                showToast(mContext.getString(R.string.remove_no_disturb_global_succeed));
                            }
                        } else {
                            if (checkState) {
                                mNoDisturbBtn.setChecked(false);
                            } else {
                                mNoDisturbBtn.setChecked(true);
                            }
                            HandleResponseCode.onHandle(mContext, status, false);
                        }
                    }
                });
                break;
        }
    }


    //退出登录
    private void Logout() {

        EventBus.getDefault().post(new LogoutEvent(true));
        // TODO Auto-generated method stub
        final Intent intent = new Intent();
        UserInfo info = JMessageClient.getMyInfo();
        if (null != info) {
            intent.putExtra("userName", info.getUserName());
            File file = info.getAvatarFile();
            if (file != null && file.isFile()) {
                intent.putExtra("avatarFilePath", file.getAbsolutePath());
            } else {
                String path = FileHelper.getUserAvatarPath(info.getUserName());
                file = new File(path);
                if (file.exists()) {
                    intent.putExtra("avatarFilePath", file.getAbsolutePath());
                }
            }
            SharePreferenceManager.putString(this, Constants.DB_USERNAME,info.getUserName());
            SharePreferenceManager.putString(this,Constants.DB_USERIMG,file.getAbsolutePath());

            if (NetworkUtil.isNetworkAvailable(mContext)){
                JMessageClient.logout();
                ParseUser.logOut();
            }

            intent.setClass(mContext, ReloginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Log.d("SettingActivtiy", "user info is null!");
        }
    }

    public void cancelNotification() {
        NotificationManager manager = (NotificationManager) this.getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }
}
