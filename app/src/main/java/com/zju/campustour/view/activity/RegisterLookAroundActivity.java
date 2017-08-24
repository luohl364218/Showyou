package com.zju.campustour.view.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

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
import com.zju.campustour.model.chatting.utils.HandleResponseCode;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.util.SharePreferenceManager;
import com.zju.campustour.presenter.implement.ImageUploader;
import com.zju.campustour.presenter.listener.MyTextWatch;
import com.zju.campustour.presenter.protocal.enumerate.IdentityType;
import com.zju.campustour.presenter.protocal.enumerate.SexType;
import com.zju.campustour.presenter.protocal.enumerate.UploadImgType;
import com.zju.campustour.presenter.protocal.enumerate.UserType;
import com.zju.campustour.presenter.protocal.enumerate.VerifyStateType;
import com.zju.campustour.presenter.protocal.event.EditUserInfoDone;
import com.zju.campustour.presenter.protocal.event.UserPictureUploadDone;
import com.zju.campustour.view.iview.IImageUploadView;
import com.zju.campustour.view.widget.ClearEditText;
import com.zju.campustour.view.widget.GifSizeFilter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterLookAroundActivity extends BaseActivity implements IImageUploadView {

    @BindView(R.id.register_info_one_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.regist_user_img)
    CircleImageView userImg;

    @BindView(R.id.real_name)
    ClearEditText realName;

    @BindView(R.id.btn_finish)
    Button btnNext;

    ParseUser currentUser;
    private Context mContext = this;
    private boolean isRealNameNotNull = false;
    private boolean isImgSet = false;
    private ImageUploader mImageUploader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_look_around);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        currentUser = ParseUser.getCurrentUser();
        if (currentUser == null)
            return;

        initView();

        //请求照片
        mImageUploader = new ImageUploader(this, this);

    }


    private void initView() {

        //让按钮随着输入内容有效而使能
        realName.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isRealNameNotNull = !TextUtils.isEmpty(s.toString());
                btnNext.setEnabled(isRealNameNotNull);
            }
        });
    }


    @OnClick(R.id.btn_finish)
    protected void updateUserInfo() {
        if (!isImgSet) {
            showToast("请选择头像");
            return;
        }

        String userRealName = realName.getText().toString().trim();
        if (TextUtils.isEmpty(userRealName)) {
            showToast("请输入真实姓名");
            return;
        }

        currentUser.put("realname", userRealName);
        currentUser.put("sex", SexType.MALE.getSexTypeId());
        currentUser.put(Constants.User_userType, UserType.USER.getUserTypeId());
        currentUser.put(Constants.User_identityType, IdentityType.LOOK_AROUND_USER.getIdentityId());
        currentUser.put(Constants.User_isVerified, false);
        currentUser.put("shortDescription", "我是一名身份未知的围观群众");
        currentUser.put("description", "我是一名身份未知的围观群众");
        //上传极光 用户性别 个性签名
        try {
            UserInfo myUserInfo = JMessageClient.getMyInfo();
            myUserInfo.setGender(UserInfo.Gender.male);
            JMessageClient.updateMyInfo(UserInfo.Field.gender, myUserInfo, new BasicCallback() {
                @Override
                public void gotResult(final int status, final String desc) {
                    if (status != 0) {
                        HandleResponseCode.onHandle(mContext, status, false);
                    }
                }
            });

            myUserInfo.setNickname("校游围观群众 " + userRealName);
            JMessageClient.updateMyInfo(UserInfo.Field.nickname, myUserInfo, new BasicCallback() {
                @Override
                public void gotResult(final int status, final String desc) {
                    if (status != 0) {
                        HandleResponseCode.onHandle(mContext, status, false);
                    }
                }
            });

            myUserInfo.setSignature("我是一名身份未知的围观群众");
            JMessageClient.updateMyInfo(UserInfo.Field.signature, myUserInfo, new BasicCallback() {
                @Override
                public void gotResult(final int status, final String desc) {
                    if (status != 0) {
                        HandleResponseCode.onHandle(mContext, status, false);
                    }
                }
            });
        } catch (Exception e) {

        }

        currentUser.saveInBackground();

        EventBus.getDefault().post(new EditUserInfoDone(true));
        Intent mIntent = new Intent(this, MainActivity.class);

        showToast("完成注册，欢迎加入校游");
        startActivity(mIntent);
        finish();

    }


    @OnClick(R.id.regist_user_img)
    public void chooseUserImg() {
         showImgSelectDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void showImgSelectDialog() {

        final Dialog dialog = new Dialog(this, R.style.jmui_default_dialog_style);
        final LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_img_select, null);
        dialog.setContentView(view);
        dialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        RelativeLayout albumBtn = (RelativeLayout) view.findViewById(R.id.album_btn);
        RelativeLayout cameraBtn = (RelativeLayout) view.findViewById(R.id.camera_btn);

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
                Glide.with(getApplicationContext()).load(localPath).into(userImg);
            }
        });


    }

    @Override
    public void imageUploadFailed(Exception e) {
        showToast("图片上传失败");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

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
