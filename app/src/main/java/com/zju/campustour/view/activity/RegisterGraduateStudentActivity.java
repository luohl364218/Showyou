package com.zju.campustour.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.zju.campustour.model.chatting.utils.DialogCreator;
import com.zju.campustour.model.chatting.utils.HandleResponseCode;
import com.zju.campustour.model.chatting.utils.IdHelper;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.util.SharePreferenceManager;
import com.zju.campustour.presenter.listener.MyTextWatch;
import com.zju.campustour.presenter.protocal.enumerate.DegreeType;
import com.zju.campustour.presenter.protocal.enumerate.IdentityType;
import com.zju.campustour.presenter.protocal.enumerate.SexType;
import com.zju.campustour.presenter.protocal.enumerate.UserType;
import com.zju.campustour.presenter.protocal.enumerate.VerifyStateType;
import com.zju.campustour.presenter.protocal.event.EditUserInfoDone;
import com.zju.campustour.presenter.protocal.event.UserPictureUploadDone;
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

public class RegisterGraduateStudentActivity extends BaseActivity{

    @BindView(R.id.register_info_one_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.regist_user_img)
    CircleImageView userImg;

    @BindView(R.id.real_name)
    ClearEditText realName;

    @BindView(R.id.user_degree)
    RelativeLayout userDegree;

    @BindView(R.id.graduate_degree)
    TextView graduteDegree;

    @BindView(R.id.user_company)
    RelativeLayout userCompany;

    @BindView(R.id.user_school)
    ClearEditText userSchool;

    @BindView(R.id.user_company_name)
    ClearEditText userCompanyName;

    @BindView(R.id.btn_finish)
    Button btnNext;

    ParseUser currentUser;
    private Context mContext = this;
    private boolean isRealNameNotNull = false;
    private boolean isImgSet = false;
    private boolean isSchoolNameNotNull = false;
    private static final int REQUEST_CODE_CHOOSE = 23;
    private int gradeId = -1;

    /*学位选择Dialog*/
    private Dialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_graduate_student);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        currentUser = ParseUser.getCurrentUser();
        if (currentUser == null)
            return;

        initView();
    }


    private void initView() {

        //让按钮随着输入内容有效而使能
        realName.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isRealNameNotNull = !TextUtils.isEmpty(s.toString());
                btnNext.setEnabled(isRealNameNotNull && isSchoolNameNotNull);
            }
        });

        userSchool.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isSchoolNameNotNull = !TextUtils.isEmpty(s.toString());
                btnNext.setEnabled(isRealNameNotNull && isSchoolNameNotNull);
            }
        });

        /*用户学位选择*/
        userDegree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog = DialogCreator.createDegreeChooseDialog(mContext, new View.OnClickListener() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == IdHelper.getViewID(mContext, "degree_choose_undergraduate_btn")) {
                            gradeId = DegreeType.UNDERGRADUATE_STUDENT.getTypeId();
                            graduteDegree.setText(DegreeType.UNDERGRADUATE_STUDENT.getTypeName());
                            mDialog.dismiss();
                        } else if (v.getId() == IdHelper.getViewID(mContext, "degree_choose_postgraduate_btn")) {
                            gradeId = DegreeType.POSTGRADUATE_STUDENT.getTypeId();
                            graduteDegree.setText(DegreeType.POSTGRADUATE_STUDENT.getTypeName());
                            mDialog.dismiss();
                        } else {
                            gradeId = DegreeType.DOCTOR_STUDENT.getTypeId();
                            graduteDegree.setText(DegreeType.DOCTOR_STUDENT.getTypeName());
                            mDialog.dismiss();
                        }
                    }
                });
                mDialog.show();
                mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
            }
        });

    }

    @OnClick(R.id.regist_user_img)
    public void chooseUserImg(){

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            Matisse.from(RegisterGraduateStudentActivity.this)
                                    .choose(MimeType.ofAll(), false)
                                    .countable(true)
                                    .maxSelectable(1)
                                    .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                                    .gridExpectedSize(
                                            getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                    .thumbnailScale(0.85f)
                                    .imageEngine(new GlideEngine())
                                    .forResult(REQUEST_CODE_CHOOSE);
                        } else {
                            showToast("权限请求被拒绝");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CODE_CHOOSE:
                if (data == null)
                    break;
                List<Uri> mUriList = Matisse.obtainResult(data);
                Uri mUri = mUriList.get(0);

                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(mUri,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                final String picturePath = cursor.getString(columnIndex);
                cursor.close();
                startCrop(picturePath);
                break;
            case UCrop.REQUEST_CROP:
                if (data == null)
                    break;
                Uri croppedFileUri = UCrop.getOutput(data);

                if (croppedFileUri != null) {
                    imageUpLoad(croppedFileUri.getPath());
                }

                break;
            default:
        }

    }

    private void startCrop(String url) {
        Uri sourceUri = Uri.fromFile(new File(url));
        //裁剪后保存到文件中
        Date mDate = new Date();
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), mDate.getTime()+"_showyou.jpeg"));
        UCrop.of(sourceUri, destinationUri).withAspectRatio(1, 1).withMaxResultSize(800, 800).start(this);
    }

    public void imageUpLoad(String localPath) {

        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpeg");
        OkHttpClient client = new OkHttpClient();
        String suffix = localPath.substring(localPath.lastIndexOf("."));

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        File f = new File(localPath);
        //上传极光聊天头像
        JMessageClient.updateUserAvatar(f,null);
        builder.addFormDataPart("file", f.getName(), RequestBody.create(MEDIA_TYPE_PNG, f));
        Date mDate = new Date();
        String uriSuffix = currentUser.getObjectId()+mDate.getTime()+suffix;
        final MultipartBody requestBody = builder.addFormDataPart("name",uriSuffix).build();
        //构建请求
        final Request request = new Request.Builder()
                .url("http://119.23.248.205:8080")//地址
                .post(requestBody)//添加请求体
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("同学，不好意思，照片上传失败啦");
                    }
                }));

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.code() == 500){
                    runOnUiThread(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("同学，照片太大，上传失败啦");
                        }
                    }));
                }
                else {
                    runOnUiThread(new Thread(new Runnable() {
                        @Override
                        public void run() {

                            showToast("报告同学，照片上传成功");
                            String imgUri = "http://119.23.248.205:8080/pictures/" + uriSuffix;
                            EventBus.getDefault().post(new UserPictureUploadDone(imgUri, localPath));
                        }
                    }));
                }

            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onPictureUploadDoneEvent(UserPictureUploadDone event) {
        currentUser = ParseUser.getCurrentUser();
        if (event != null && currentUser != null){
            try{
                SharePreferenceManager.putString(this, Constants.DB_USERIMG,event.getLocalImgUrl());
                currentUser.put("imgUrl",event.getCloudImgUrl());
                currentUser.saveEventually();
                Uri mUri = Uri.fromFile(new File(event.getLocalImgUrl()));
                isImgSet = true;
                Glide.with(this).load(mUri).into(userImg);
            }catch (Exception e){

            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.btn_finish)
    protected void updateUserInfo() {
        if (!isImgSet) {
            showToast("您好，请为自己选择专属头像吧~");
            return;
        }

        String userRealName = realName.getText().toString().trim();

        String schoolName = userSchool.getText().toString().trim();

        if (!TextUtils.isEmpty(schoolName)){
            currentUser.put(Constants.User_collegeSchool,schoolName);
        }
        else{
            showToast("请输入学校名称");
            return;
        }

        currentUser.put("realname",userRealName);
        currentUser.put("sex", SexType.MALE.getSexTypeId());
        currentUser.put(Constants.User_userType, UserType.USER.getUserTypeId());
        currentUser.put(Constants.User_identityType, IdentityType.GRADUATE_STUDENT.getIdentityId());
        currentUser.put(Constants.User_isVerified, false);
        String degree = "";
        if (gradeId > 0) {
            currentUser.put(Constants.User_grade, gradeId);
            degree = graduteDegree.getText().toString().trim();
        }
        currentUser.put("shortDescription","我是一名" +schoolName + degree + "毕业生");

        String companyName = userCompanyName.getText().toString().trim();
        String companyDesc = "";
        if (!TextUtils.isEmpty(companyName)){
            companyDesc = ",现就职于" + companyName;
        }

        currentUser.put("description","我是一名" + schoolName + degree +"毕业生" + companyDesc);
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

            myUserInfo.setNickname("校游毕业大学生 "+userRealName);
            JMessageClient.updateMyInfo(UserInfo.Field.nickname, myUserInfo, new BasicCallback() {
                @Override
                public void gotResult(final int status, final String desc) {
                    if (status != 0) {
                        HandleResponseCode.onHandle(mContext, status, false);
                    }
                }
            });

            myUserInfo.setSignature("我是一名" +schoolName + degree +"毕业生");
            JMessageClient.updateMyInfo(UserInfo.Field.signature, myUserInfo, new BasicCallback() {
                @Override
                public void gotResult(final int status, final String desc) {
                    if (status != 0) {
                        HandleResponseCode.onHandle(mContext, status, false);
                    }
                }
            });
        }catch (Exception e){

        }

        currentUser.saveEventually();

        EventBus.getDefault().post(new EditUserInfoDone(true));
        Intent mIntent = new Intent(this, MainActivity.class);

        showToast("完成注册，欢迎加入校游");
        startActivity(mIntent);
        finish();

    }


}
