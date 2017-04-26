package com.zju.campustour.presenter.protocal.http;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by HeyLink on 2017/4/19.
 */

public class SweetAlertDialogCallback<T> extends BaseHttpCallback<T>{

    private SweetAlertDialog mDialog;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Handler sweetAlertDialogHandler;
    private static final int CANCLE_DIALOD = 1;

    private Context mContext;
    private String TAG = "SweetAlertDialog:";

    public SweetAlertDialogCallback(Context context){
        this.mContext = context;
        mDialog = new SweetAlertDialog(context);

        sweetAlertDialogHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case CANCLE_DIALOD:
                        mDialog.dismissWithAnimation();
                        Log.d(TAG, "dialog cancle:"+System.currentTimeMillis());
                        if (mTimerTask!=null)
                            mTimerTask.cancel();
                        mTimer.cancel();
                        break;
                    default:
                        break;

                }
            }
        };

    }


    public void showProgressDialog(){

        mDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        mDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mDialog.setTitleText("Loading");
        mDialog.show();

    }

    public void showSuccessDialog(){

        mDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        mDialog.getProgressHelper().setBarColor(Color.parseColor("#eb4f38"));
        mDialog.setTitleText("Success");
        Log.d(TAG, "Success:"+System.currentTimeMillis());
        showTimer();

    }

    private void showTimer() {
        mTimer = new Timer(true);
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = CANCLE_DIALOD;
                sweetAlertDialogHandler.sendMessage(message);

            }
        };

        mTimer.schedule(mTimerTask, 3000);
    }

    public void showFailureDialog(){

        mDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
        mDialog.getProgressHelper().setBarColor(Color.parseColor("#dc143c"));
        mDialog.setTitleText("图片获取失败，请检查网络是否连接！");

        showTimer();
    }

    public void showErrorDialog(){

        mDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
        mDialog.getProgressHelper().setBarColor(Color.parseColor("#4169e1"));
        mDialog.setTitleText("图片加载出错");
        showTimer();

    }


    @Override
    public void onRequestBefore(Request request) {
        showProgressDialog();
    }

    @Override
    public void onFailure(Request request, Exception e) {
        showFailureDialog();
    }

    @Override
    public void onSuccess(Response response, T mT) {
        if (mDialog != null)
        mDialog.dismissWithAnimation();
    }

    @Override
    public void onError(Response response, int code, Exception e) {
        showErrorDialog();
    }
}
