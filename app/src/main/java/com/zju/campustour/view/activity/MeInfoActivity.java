package com.zju.campustour.view.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yalantis.ucrop.UCrop;
import com.zju.campustour.R;
import com.zju.campustour.model.chatting.entity.Event;
import com.zju.campustour.model.chatting.utils.HandleResponseCode;
import com.zju.campustour.model.chatting.utils.IdHelper;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.presenter.chatting.controller.MeInfoController;
import com.zju.campustour.presenter.implement.ImageUploader;
import com.zju.campustour.presenter.protocal.enumerate.SexType;
import com.zju.campustour.presenter.protocal.enumerate.UploadImgType;
import com.zju.campustour.presenter.protocal.enumerate.UserType;
import com.zju.campustour.presenter.protocal.event.UserPictureUploadDone;
import com.zju.campustour.view.chatting.MeInfoView;
import com.zju.campustour.view.iview.IImageUploadView;
import com.zju.campustour.view.widget.AreaSelectDialog;
import com.zju.campustour.view.widget.CollegeSelectDialog;
import com.zju.campustour.view.widget.GradeSelectDialog;
import com.zju.campustour.view.widget.MajorSelectDialog;

import org.greenrobot.eventbus.EventBus;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class MeInfoActivity extends BaseActivity implements IImageUploadView {

    private MeInfoView mMeInfoView;
    private MeInfoController mMeInfoController;
    private final static int MODIFY_NICKNAME_REQUEST_CODE = 1;
    private final static int SELECT_AREA_REQUEST_CODE = 3;
    private final static int MODIFY_SCHOOL_REQUEST_CODE = 2;
    private final static int MODIFY_EMAIL_REQUEST_CODE = 5;
    private final static int MODIFY_SIGNATURE_REQUEST_CODE = 7;
    private final static int MODIFY_INTRODUCE_REQUEST_CODE = 6;
    private String mModifiedName;
    private Context mContext;
    private Dialog mDialog;
    private ImageUploader mImageUploader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_info);
        mContext = this;
        mMeInfoView = (MeInfoView) findViewById(R.id.me_info_view);
        mMeInfoView.initModule();
        mMeInfoController = new MeInfoController(mMeInfoView, this);
        mMeInfoView.setListeners(mMeInfoController);
        UserInfo userInfo = JMessageClient.getMyInfo();
        mMeInfoView.refreshUserInfo(userInfo);

        //准备好负责图片上传的工具
        mImageUploader = new ImageUploader(this,this);
    }

    public void startModifyNickNameActivity() {
        String nickname = JMessageClient.getMyInfo().getNickname();
        Intent intent = new Intent();
        intent.putExtra("nickName", nickname);
        intent.setClass(this, ResetNickNameActivity.class);
        startActivityForResult(intent, MODIFY_NICKNAME_REQUEST_CODE);
    }


    public void showTypeDialog(UserType type){
        final Dialog dialog = new Dialog(this, R.style.jmui_default_dialog_style);
        final LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_set_type, null);
        dialog.setContentView(view);
        dialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        RelativeLayout manRl = (RelativeLayout) view.findViewById(R.id.man_rl);
        RelativeLayout womanRl = (RelativeLayout) view.findViewById(R.id.woman_rl);
        ImageView manSelectedIv = (ImageView) view.findViewById(R.id.man_selected_iv);
        ImageView womanSelectedIv = (ImageView) view.findViewById(R.id.woman_selected_iv);
        if (type == UserType.USER) {
            manSelectedIv.setVisibility(View.VISIBLE);
            womanSelectedIv.setVisibility(View.GONE);
        } else if (type == UserType.PROVIDER) {
            manSelectedIv.setVisibility(View.GONE);
            womanSelectedIv.setVisibility(View.VISIBLE);
        } else {
            manSelectedIv.setVisibility(View.GONE);
            womanSelectedIv.setVisibility(View.GONE);
        }
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.man_rl:
                        if (type != UserType.USER) {
                            ParseUser currentUser = ParseUser.getCurrentUser();
                            currentUser.put("userType",UserType.USER.getValue());
                            currentUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null){
                                        mMeInfoView.setType(true);
                                        showToast(MeInfoActivity.this.getString(R.string.modify_success_toast));
                                    }
                                }
                            });

                        }
                        dialog.cancel();
                        break;
                    case R.id.woman_rl:
                        if (type != UserType.PROVIDER) {
                            ParseUser currentUser = ParseUser.getCurrentUser();
                            currentUser.put("userType",UserType.PROVIDER.getValue());
                            currentUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null){
                                        mMeInfoView.setType(false);
                                        showToast(MeInfoActivity.this.getString(R.string.modify_success_toast));
                                    }
                                }
                            });

                        }
                        dialog.cancel();
                        break;
                }
            }
        };
        manRl.setOnClickListener(listener);
        womanRl.setOnClickListener(listener);

    }

    public void showSexDialog(final UserInfo.Gender gender) {
        final Dialog dialog = new Dialog(this, R.style.jmui_default_dialog_style);
        final LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_set_sex, null);
        dialog.setContentView(view);
        dialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        RelativeLayout manRl = (RelativeLayout) view.findViewById(R.id.man_rl);
        RelativeLayout womanRl = (RelativeLayout) view.findViewById(R.id.woman_rl);
        ImageView manSelectedIv = (ImageView) view.findViewById(R.id.man_selected_iv);
        ImageView womanSelectedIv = (ImageView) view.findViewById(R.id.woman_selected_iv);
        if (gender == UserInfo.Gender.male) {
            manSelectedIv.setVisibility(View.VISIBLE);
            womanSelectedIv.setVisibility(View.GONE);
        } else if (gender == UserInfo.Gender.female) {
            manSelectedIv.setVisibility(View.GONE);
            womanSelectedIv.setVisibility(View.VISIBLE);
        } else {
            manSelectedIv.setVisibility(View.GONE);
            womanSelectedIv.setVisibility(View.GONE);
        }
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.man_rl:
                        if (gender != UserInfo.Gender.male) {
                            UserInfo myUserInfo = JMessageClient.getMyInfo();
                            myUserInfo.setGender(UserInfo.Gender.male);
                            JMessageClient.updateMyInfo(UserInfo.Field.gender, myUserInfo, new BasicCallback() {
                                @Override
                                public void gotResult(final int status, final String desc) {
                                    if (status == 0) {
                                        mMeInfoView.setGender(true);
                                        showToast(MeInfoActivity.this.getString(R.string.modify_success_toast));

                                        ParseUser currentUser = ParseUser.getCurrentUser();
                                        currentUser.put("sex", SexType.MALE.getValue());
                                        currentUser.saveInBackground();
                                    } else {
                                        ParseUser currentUser = ParseUser.getCurrentUser();
                                        currentUser.put("sex", SexType.FEMALE.getValue());
                                        currentUser.saveInBackground();
                                        HandleResponseCode.onHandle(MeInfoActivity.this, status, false);
                                    }
                                }
                            });

                        }
                        dialog.cancel();
                        break;
                    case R.id.woman_rl:
                        if (gender != UserInfo.Gender.female) {
                            UserInfo myUserInfo = JMessageClient.getMyInfo();
                            myUserInfo.setGender(UserInfo.Gender.female);
                            JMessageClient.updateMyInfo(UserInfo.Field.gender, myUserInfo, new BasicCallback() {
                                @Override
                                public void gotResult(final int status, final String desc) {
                                    if (status == 0) {
                                        mMeInfoView.setGender(false);
                                        showToast(MeInfoActivity.this.getString(R.string.modify_success_toast));
                                        ParseUser currentUser = ParseUser.getCurrentUser();
                                        currentUser.put("sex", SexType.FEMALE.getValue());
                                        currentUser.saveInBackground();
                                    } else {
                                        HandleResponseCode.onHandle(MeInfoActivity.this, status, false);
                                        ParseUser currentUser = ParseUser.getCurrentUser();
                                        currentUser.put("sex", SexType.MALE.getValue());
                                        currentUser.saveInBackground();
                                    }
                                }
                            });

                        }
                        dialog.cancel();
                        break;
                }
            }
        };
        manRl.setOnClickListener(listener);
        womanRl.setOnClickListener(listener);
    }

    public Dialog createConfirmDialog(Context context, View.OnClickListener listener){
        Dialog dialog = new Dialog(context, IdHelper.getStyle(context, "jmui_default_dialog_style"));
        View view = LayoutInflater.from(context).inflate(IdHelper.getLayout(context,
                "jmui_dialog_base_with_button"), null);
        dialog.setContentView(view);
        TextView title = (TextView) view.findViewById(IdHelper.getViewID(context, "jmui_title"));
        title.setText(IdHelper.getString(context, "identity_confirm"));
        final Button cancel = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_cancel_btn"));
        final Button commit = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_commit_btn"));
        cancel.setOnClickListener(listener);
        commit.setOnClickListener(listener);
        commit.setText(IdHelper.getString(context, "jmui_confirm"));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }


    public void selectGrade(int mGradeId) {
        GradeSelectDialog.Builder mBuilder = new GradeSelectDialog.Builder(mContext);
        mBuilder.setUserGrade(mGradeId);
        mBuilder.setPositiveButtonListener(new GradeSelectDialog.Builder.OnGradeSelectClickListener() {
            @Override
            public void onClick(DialogInterface dialog, String grade, int gradeIndex) {
                dialog.dismiss();
                mMeInfoView.setGrade(grade);
                if (gradeIndex != mGradeId){
                    ParseUser user = ParseUser.getCurrentUser();
                    user.put("gradeId",gradeIndex);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){
                                showToast(MeInfoActivity.this.getString(R.string.modify_success_toast));
                            }
                        }
                    });

                }
            }
        });
        GradeSelectDialog dialog =  mBuilder.create();
        dialog.show();

    }

    public void startSelectAreaActivity() {
        AreaSelectDialog.Builder mBuilder = new AreaSelectDialog.Builder(mContext);
        mBuilder.setPositiveButtonListener(new AreaSelectDialog.Builder.OnAreaSelectClickListener() {
            @Override
            public void onClick(DialogInterface dialog, String currentProvince, String currentCity, String currentDistrict, String currentZipCode) {
                dialog.dismiss();
                mMeInfoView.setRegion(currentProvince + currentCity + currentDistrict);
                ParseUser currentUser = ParseUser.getCurrentUser();
                currentUser.put("province",currentProvince);
                currentUser.put("city",currentCity);
                currentUser.put("district",currentDistrict);
                currentUser.saveInBackground();

                final ProgressDialog mProgressDialog = new ProgressDialog(mContext);
                mProgressDialog.setMessage(mContext.getString(R.string.modifying_hint));
                mProgressDialog.show();
                UserInfo myUserInfo = JMessageClient.getMyInfo();
                myUserInfo.setRegion(currentProvince + currentCity + currentDistrict);
                JMessageClient.updateMyInfo(UserInfo.Field.region, myUserInfo, new BasicCallback() {
                    @Override
                    public void gotResult(final int status, final String desc) {
                        mProgressDialog.dismiss();
                        if (status == 0) {
                            showToast(mContext.getString(R.string.modify_success_toast));
                        } else {
                            HandleResponseCode.onHandle(mContext, status, false);
                        }
                    }
                });
            }
        });
        AreaSelectDialog dialog = mBuilder.create();
        dialog.show();


    }


    public void selectMajor() {
        MajorSelectDialog.Builder mBuilder = new MajorSelectDialog.Builder(mContext);
        mBuilder.setPositiveButtonListener(new MajorSelectDialog.Builder.OnMajorSelectDialog() {
            @Override
            public void onClick(DialogInterface dialog, String currentMajorClass, String currentMajor, String currentTag) {
                dialog.dismiss();

                mMeInfoView.setMajor(currentMajor);
                ParseUser currentUser = ParseUser.getCurrentUser();
                currentUser.put("major",currentMajor);
                currentUser.put("categoryId", Integer.valueOf(currentTag));
                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null){
                            showToast(MeInfoActivity.this.getString(R.string.modify_success_toast));
                        }
                    }
                });
            }
        });
        CollegeSelectDialog dialog =  mBuilder.create();
        dialog.show();

    }

    public void startModifySchool(String school, int grade){
        Intent intent = new Intent();
        intent.putExtra("school", school);
        intent.putExtra("grade",grade);
        intent.setClass(this, ModifySchoolActivity.class);
        startActivityForResult(intent, MODIFY_SCHOOL_REQUEST_CODE);
    }

    public void startVerifyIdentity(boolean isVerified){
        Intent intent = new Intent(this,VerifyStatusActivity.class);
        intent.putExtra("isVerified",isVerified);

        startActivity(intent);
    }



    public void startModifyIdentityType(){
        //弹出一个确认框
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.jmui_cancel_btn:
                        mDialog.cancel();
                        break;
                    case R.id.jmui_commit_btn:

                        startActivity(new Intent(MeInfoActivity.this,IdentityConfirmActivity.class));

                        mDialog.cancel();
                        break;
                }
            }
        };
        mDialog = createConfirmDialog(mContext, listener);
        mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.show();

        //
    }

    public void startModifyEmailActivity(String email) {

        Intent intent = new Intent();
        intent.putExtra("email",email);
        intent.setClass(this, ModifyEmailActivity.class);
        startActivityForResult(intent, MODIFY_EMAIL_REQUEST_CODE);
    }

    public void startModifySignatureActivity() {
        Intent intent = new Intent();
        intent.putExtra("OldSignature", JMessageClient.getMyInfo().getSignature());
        intent.setClass(this, EditSignatureActivity.class);
        startActivityForResult(intent, MODIFY_SIGNATURE_REQUEST_CODE);
    }


    public void startModifyMyDesc() {
        Intent intent = new Intent(this,ModifyIntroduceActivity.class);
        startActivityForResult(intent, MODIFY_INTRODUCE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode){
                case MODIFY_SCHOOL_REQUEST_CODE:
                    String school = data.getStringExtra("school");
                    mMeInfoView.setSchool(school);
                    break;
                case MODIFY_NICKNAME_REQUEST_CODE:
                    mModifiedName = data.getStringExtra("nickName");
                    mMeInfoView.setNickName(mModifiedName);
                    break;
                case MODIFY_SIGNATURE_REQUEST_CODE:
                    mMeInfoView.setSignature(data.getStringExtra("signature"));
                    break;
                case SELECT_AREA_REQUEST_CODE:
                    //mMeInfoView.setRegion(data.getStringExtra("region"));
                    break;
                case MODIFY_EMAIL_REQUEST_CODE:
                    String email = data.getStringExtra("email");
                    mMeInfoView.setEmail(email);
                    break;
                case MODIFY_INTRODUCE_REQUEST_CODE:
                    String introduce = data.getStringExtra("description");
                    mMeInfoView.setDesc(introduce);
                    break;
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

    @Override
    public void onBackPressed() {
        setResultAndFinish();
        super.onBackPressed();
    }

    public void setResultAndFinish() {
        Intent intent = new Intent();
        intent.putExtra("newName", mModifiedName);
        setResult(Constants.RESULT_CODE_ME_INFO, intent);
        finish();
    }

    public void editUserImg(){
        showImgSelectDialog();
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

    @Override
    public void imagePermissionRefused() {
        showToast("照片获取请求被拒绝，请手动开启");
    }

    @Override
    public void imageUploadSuccess(String imgUrl, Uri localPath) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMeInfoView.showUserAvatar(localPath);
                EventBus.getDefault().post(new UserPictureUploadDone(imgUrl,localPath.getPath()));
            }
        });


    }

    @Override
    public void imageUploadFailed(Exception e) {
        showToast("图片上传失败");
    }

}
