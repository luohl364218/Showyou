package com.zju.campustour.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.ParseUser;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yalantis.ucrop.UCrop;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zju.campustour.MainActivity;
import com.zju.campustour.R;
import com.zju.campustour.model.chatting.utils.BitmapLoader;
import com.zju.campustour.model.chatting.utils.DialogCreator;
import com.zju.campustour.model.chatting.utils.FileHelper;
import com.zju.campustour.model.chatting.utils.HandleResponseCode;
import com.zju.campustour.model.chatting.utils.IdHelper;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.util.SharePreferenceManager;
import com.zju.campustour.presenter.implement.ImageUploader;
import com.zju.campustour.presenter.protocal.enumerate.UploadImgType;
import com.zju.campustour.presenter.protocal.event.UserPictureUploadDone;
import com.zju.campustour.view.chatting.widget.CircleImageView;
import com.zju.campustour.view.iview.IImageUploadView;
import com.zju.campustour.view.widget.GifSizeFilter;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FixProfileActivity extends BaseActivity implements IImageUploadView {

    private static final String TAG = "FixProfileActivity";

    private Button mFinishBtn;
    private EditText mNickNameEt;
    private ImageView mAvatarIv;
    private String mPath;
    private ProgressDialog mDialog;
    private Dialog mSetAvatarDialog;
    private Context mContext;
    private ImageUploader mImageUploader;
    // 裁剪后图片的宽(X)和高(Y), 720 X 720的正方形。
    private static int OUTPUT_X = 720;
    private static int OUTPUT_Y = 720;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != savedInstanceState) {
            String nickName = savedInstanceState.getString("savedNickName");
            mNickNameEt.setText(nickName);
        }
        setContentView(R.layout.activity_fix_profile);
        mContext = this;
        mNickNameEt = (EditText) findViewById(R.id.nick_name_et);
        mAvatarIv = (CircleImageView) findViewById(R.id.jmui_avatar_iv);
        mFinishBtn = (Button) findViewById(R.id.finish_btn);
        mAvatarIv.setOnClickListener(listener);
        mFinishBtn.setOnClickListener(listener);
        JMessageClient.getUserInfo(JMessageClient.getMyInfo().getUserName(), null);
        SharePreferenceManager.setCachedFixProfileFlag(mContext,true);
        mNickNameEt.requestFocus();

        //请求照片
        mImageUploader = new ImageUploader(this, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstancedState) {
        savedInstancedState.putString("savedNickName", mNickNameEt.getText().toString());
        super.onSaveInstanceState(savedInstancedState);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.jmui_avatar_iv:
                    showSetAvatarDialog();
                    break;
                case R.id.finish_btn:
                    String nickName = mNickNameEt.getText().toString().trim();
                    if (nickName != null && !nickName.equals("")) {
                        final ProgressDialog dialog = new ProgressDialog(mContext);
                        dialog.setMessage(mContext.getString(R.string.saving_hint));
                        dialog.show();
                        dialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
                        UserInfo myUserInfo = JMessageClient.getMyInfo();
                        myUserInfo.setNickname(nickName);
                        JMessageClient.updateMyInfo(UserInfo.Field.nickname, myUserInfo, new BasicCallback() {
                            @Override
                            public void gotResult(final int status, String desc) {
                                //更新跳转标志
                                SharePreferenceManager.setCachedFixProfileFlag(mContext,false);
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                if (status != 0) {
                                    showToast(mContext.getString(R.string.nickname_save_failed));
                                }
                                startMainActivity();
                            }
                        });
                    }else {
                        showToast(FixProfileActivity.this.getString(R.string.nickname_not_null_toast));
                        return;
                    }
                    break;
            }
        }
    };

    public void showSetAvatarDialog() {

        final Dialog dialog = new Dialog(mContext, R.style.jmui_default_dialog_style);
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_img_select, null);
        dialog.setContentView(view);
        dialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        RelativeLayout albumBtn = (RelativeLayout) view.findViewById(R.id.album_btn);
        RelativeLayout cameraBtn = (RelativeLayout) view.findViewById(R.id.camera_btn);
        RelativeLayout cancelBtn = (RelativeLayout) view.findViewById(R.id.cancel_btn);
        View.OnClickListener listener = new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                switch (v.getId()){

                    case R.id.album_btn:

                        mImageUploader.chooseUserImg(UploadImgType.IMG_AVATAR);
                        dialog.dismiss();

                        break;

                    case R.id.camera_btn:
                        mImageUploader.takePhoto(UploadImgType.IMG_AVATAR);
                        dialog.dismiss();
                        break;

                    case R.id.cancel_btn:
                        dialog.dismiss();
                        break;

                }
            }
        };


        albumBtn.setOnClickListener(listener);
        cameraBtn.setOnClickListener(listener);
        cancelBtn.setOnClickListener(listener);
    }

    private void startMainActivity() {
        Intent intent = new Intent();
        intent.setClass(FixProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void imagePermissionRefused() {
        showToast("照片获取请求被拒绝，请手动开启");
    }

    @Override
    public void imageUploadSuccess(String imgUrl, Uri localPath) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(getApplicationContext()).load(localPath).into(mAvatarIv);
            }
        });


    }

    @Override
    public void imageUploadFailed(Exception e) {
        showToast("图片上传失败");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case Constants.REQUEST_CODE_CHOOSE:
                if (data == null)
                    break;
                mImageUploader.startCrop(data);
                break;
            case UCrop.REQUEST_CROP:
                if (data == null)
                    break;
                mImageUploader.imageUpLoad(data);
                break;
            case Constants.REQUEST_CODE_TAKE_PHOTO:
                mImageUploader.startCrop();
                break;
            default:
        }


    }
}
