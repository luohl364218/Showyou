package com.zju.campustour.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseUser;
import com.yalantis.ucrop.UCrop;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.bean.VerifyInfo;
import com.zju.campustour.presenter.implement.ImageUploader;
import com.zju.campustour.presenter.implement.UserVerifyInfoImpl;
import com.zju.campustour.presenter.protocal.enumerate.IdentityType;
import com.zju.campustour.presenter.protocal.enumerate.UploadImgType;
import com.zju.campustour.presenter.protocal.enumerate.VerifyStateType;
import com.zju.campustour.view.iview.IImageUploadView;
import com.zju.campustour.view.iview.IUserVerifyInfoView;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zju.campustour.model.common.Constants.URL_VERIFIED_ID_BG;

public class VerifyIdentityActivity extends BaseActivity implements IUserVerifyInfoView, IImageUploadView {

    @BindView(R.id.publish_background_img)
    ImageView backgroundPic;
    @BindView(R.id.project_desc)
    EditText projectDesc;
    @BindView(R.id.btn_project_new)
    Button verifyBtn;

    @BindView(R.id.return_btn)
    ImageButton mReturnBtn;

    @BindView(R.id.title)
    TextView mTitle;

    @BindView(R.id.right_btn)
    ImageButton mMenuBtn;


    ParseUser currentUser;
    boolean isImgSet = false;
    private Context mContext = this;
    String imgUrl = "";
    boolean isRefresh = false;
    VerifyInfo verifyInfo;
    private ImageUploader mImageUploader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_identity);
        ButterKnife.bind(this);
        currentUser = ParseUser.getCurrentUser();
        if (currentUser == null)
            finish();


        initView();
        //请求照片
        mImageUploader = new ImageUploader(this,this);

        verifyInfo = (VerifyInfo)getIntent().getSerializableExtra("VerifyInfo");
        if(verifyInfo == null || verifyInfo.getSubmitVerifyStateType() == VerifyStateType.VERIFY_NOT_YET) {
            //进入首次提交模式
            //加载默认背景图片
            verifyInfo = new VerifyInfo();
            Glide.with(this).load(URL_VERIFIED_ID_BG).into(backgroundPic);
        }
        else{
            //进入修改模式
            isRefresh = true;
            imgUrl = verifyInfo.getSubmitImgUrl();

            Glide.with(mContext).load(imgUrl).into(backgroundPic);

            projectDesc.setText(verifyInfo.getSubmitDescription());

            verifyBtn.setEnabled(true);
        }

    }

    private void initView(){

        mTitle.setText("身份认证");
        mMenuBtn.setVisibility(View.GONE);
        mReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }



    @OnClick(R.id.publish_background_img)
    public void chooseUserImg(){
        showImgSelectDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.btn_project_new)
    public void onVerifyIdBtnClicked(){
        String description = projectDesc.getText().toString();

        if(TextUtils.isEmpty(description))
            description = "未填写";

        UserVerifyInfoImpl userVerifyInfo = new UserVerifyInfoImpl(this,this);

        verifyInfo.setSubmitUserId(currentUser.getObjectId());
        verifyInfo.setSubmitImgUrl(imgUrl);
        verifyInfo.setSubmitDescription(description);
        verifyInfo.setSubmitVerifyStateType(VerifyStateType.VERIFY_ING);
        verifyInfo.setSubmitTime(new Date());
        verifyInfo.setIdentityType(IdentityType.values()[currentUser.getInt(Constants.User_identityType)]);

        if (!isRefresh)
            userVerifyInfo.submitVerifyInfo(verifyInfo);
        else
            userVerifyInfo.refreshVerifyInfo(verifyInfo);
    }


    @Override
    public void onSubmitVerifyInfoSuccess(boolean isRefresh) {
        if(!isRefresh){
            showToast("提交成功");
            finish();
        }
        else{
            showToast("更新成功");
            finish();
        }

    }

    @Override
    public void onSubmitVerifyInfoFailed(Exception e) {
        Log.e(this.getClass().getSimpleName(),e.getStackTrace().toString());
        showToast("提交失败，请稍后再试");
    }

    @Override
    public void onQueryVerifyInfoDone(VerifyInfo verifyInfo) {
        if (verifyInfo == null)
            showToast("提交失败，请确认网络连接");
    }

    private void showImgSelectDialog() {

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

                        mImageUploader.chooseUserImg(UploadImgType.IMG_IDENTITY);
                        dialog.dismiss();

                        break;

                    case R.id.camera_btn:
                        mImageUploader.takePhoto(UploadImgType.IMG_IDENTITY);
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


    @Override
    public void imagePermissionRefused() {
        showToast("照片获取请求被拒绝，请手动开启");
    }

    @Override
    public void imageUploadSuccess(String imgUrl, Uri localPath) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(getApplicationContext()).load(localPath).into(backgroundPic);
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
