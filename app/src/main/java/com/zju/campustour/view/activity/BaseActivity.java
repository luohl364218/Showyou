package com.zju.campustour.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.model.util.SharePreferenceManager;
import com.zju.campustour.model.chatting.utils.DialogCreator;
import com.zju.campustour.model.chatting.utils.FileHelper;
import com.zju.campustour.view.application.CampusTourApplication;
import com.zju.campustour.view.widget.ActivityCollector;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.model.UserInfo;


/**
 * Created by HeyLink on 2017/4/22.
 */

public class BaseActivity extends SwipeBackAppCompatActivity {

    private Toast mToast = null;
    protected   boolean isNetworkUseful = true;
    private static final String TAG = "BaseActivity";

    protected float mDensity;
    protected int mDensityDpi;
    protected int mAvatarSize;
    protected int mWidth;
    protected int mHeight;
    protected float mRatio;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isNetworkUseful = NetworkUtil.isNetworkAvailable(this);
        if (!isNetworkUseful)
            showToast("网络不可用，请稍后重试");
        Log.d("BaseActivity", getClass().getSimpleName());

        ActivityCollector.addActivity(this);
        mContext = this;
        //强制屏幕为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //订阅接收消息,子类只要重写onEvent就能收到
        JMessageClient.registerEventReceiver(this);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mDensityDpi = dm.densityDpi;
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;
        mRatio = Math.min((float) mWidth / 720, (float) mHeight / 1280);
        mAvatarSize = (int) (50 * mDensity);


    }

    public void startActivity(Intent intent, boolean isNeedLogin){

        ParseUser currentUser = ParseUser.getCurrentUser();
        if(isNeedLogin && currentUser ==null){

            CampusTourApplication.getInstance().putIntent(intent);
            Intent loginIntent = new Intent(this, LoginActivity.class);
            super.startActivity(loginIntent);

        }
        else{
            super.startActivity(intent);
        }


    }


    public void showToast(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

    public void showToast(int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(getApplicationContext(), resId,
                    Toast.LENGTH_SHORT);
        } else {
            mToast.setText(resId);
        }
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
        if (dialog != null){
            dialog.dismiss();
        }
        ActivityCollector.removeActivity(this);
    }

    private Dialog dialog;

    private UserInfo myInfo;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
            Intent intent = new Intent();
            if (null != myInfo) {
                intent.setClass(BaseActivity.this, ReloginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                BaseActivity.this.finish();
            } else {
                Log.d(TAG, "user info is null! Jump to Login activity");
                intent.setClass(BaseActivity.this, LoginActivity.class);
                startActivity(intent);
                BaseActivity.this.finish();
            }
        }
    };

    /**
     * 接收登录状态相关事件:登出事件,修改密码事件及被删除事件
     * @param event 登录状态相关事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onEventMainThread(LoginStateChangeEvent event) {
        LoginStateChangeEvent.Reason reason = event.getReason();
        myInfo = event.getMyInfo();
        if (null != myInfo) {
            String path;
            File avatar = myInfo.getAvatarFile();
            if (avatar != null && avatar.exists()) {
                path = avatar.getAbsolutePath();
            } else {
                path = FileHelper.getUserAvatarPath(myInfo.getUserName());
            }
            Log.i(TAG, "userName " + myInfo.getUserName());
            SharePreferenceManager.putString(mContext, Constants.DB_USERNAME, myInfo.getUserName());
            SharePreferenceManager.putString(mContext,Constants.DB_USERIMG, path);
            JMessageClient.logout();
        }
        switch (reason) {
            case user_password_change:
                String title = mContext.getString(R.string.change_password);
                String msg = mContext.getString(R.string.change_password_message);
                dialog = DialogCreator.createBaseCustomDialog(mContext, title, msg, onClickListener);
                break;
            case user_logout:
                title = mContext.getString(R.string.user_logout_dialog_title);
                msg = mContext.getString(R.string.user_logout_dialog_message);
                dialog = DialogCreator.createBaseCustomDialog(mContext, title, msg, onClickListener);
                break;
            case user_deleted:
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent = new Intent();
                        intent.setClass(BaseActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        BaseActivity.this.finish();
                    }
                };
                title = mContext.getString(R.string.user_logout_dialog_title);
                msg = mContext.getString(R.string.user_delete_hint_message);
                dialog = DialogCreator.createBaseCustomDialog(mContext, title, msg, listener);
                break;
        }
        dialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

}
