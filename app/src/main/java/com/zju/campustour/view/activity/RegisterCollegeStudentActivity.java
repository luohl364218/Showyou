package com.zju.campustour.view.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
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
import com.zju.campustour.model.chatting.utils.HandleResponseCode;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.util.DbUtils;
import com.zju.campustour.model.util.SharePreferenceManager;
import com.zju.campustour.presenter.listener.MyTextWatch;
import com.zju.campustour.presenter.protocal.enumerate.IdentityType;
import com.zju.campustour.presenter.protocal.enumerate.SexType;
import com.zju.campustour.presenter.protocal.enumerate.UserType;
import com.zju.campustour.presenter.protocal.enumerate.VerifyStateType;
import com.zju.campustour.presenter.protocal.event.EditUserInfoDone;
import com.zju.campustour.presenter.protocal.event.UserPictureUploadDone;
import com.zju.campustour.view.widget.AreaSelectDialog;
import com.zju.campustour.view.widget.CollegeSelectDialog;
import com.zju.campustour.view.widget.GifSizeFilter;
import com.zju.campustour.view.widget.GradeSelectDialog;
import com.zju.campustour.view.widget.MajorSelectDialog;

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

public class RegisterCollegeStudentActivity extends BaseActivity {

    @BindView(R.id.register_info_one_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.regist_user_img)
    CircleImageView userImg;

    @BindView(R.id.real_name)
    EditText realName;

    @BindView(R.id.school_area_select)
    RelativeLayout schoolAreaSelect;

    @BindView(R.id.school_area)
    TextView schoolArea;

    @BindView(R.id.user_grade_select)
    RelativeLayout userGradeSelect;

    @BindView(R.id.student_grade)
    TextView userGrade;

    @BindView(R.id.user_school)
    EditText userSchool;

    @BindView(R.id.iv_click_to_choose_school)
    ImageView clickToChooseSchool;

    @BindView(R.id.iv_click_to_choose_major)
    ImageView majorSelect;

    @BindView(R.id.user_major)
    EditText majorName;

    @BindView(R.id.user_sex_select)
    RelativeLayout userSexSelect;

    @BindView(R.id.iv_user_sex)
    ImageView userSexIv;

    @BindView(R.id.user_sex)
    TextView userSexTv;

    @BindView(R.id.btn_finish)
    Button btnNext;

    ParseUser currentUser;
    private Context mContext = this;
    private boolean isRealNameNotNull = false;
    private boolean isGradeChosen = false;
    private boolean isImgSet = false;
    private boolean isSchoolNameNotNull = false;
    private boolean isMajorNotNull = false;
    private static final int REQUEST_CODE_CHOOSE = 23;
    private int gradeId = -1;
    private String schoolProvince = "";
    private String schoolCity = "";
    private String schoolDistrict = "";
    private SexType userSex = SexType.MALE;
    private Dialog dialog;
    private String collegeTag = "";
    private int majorId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_college_student);

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
                btnNext.setEnabled(isRealNameNotNull && isGradeChosen
                        && isSchoolNameNotNull && isMajorNotNull);
            }
        });

        /*手动输入学校名称*/
        userSchool.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isSchoolNameNotNull = !TextUtils.isEmpty(s.toString());
                btnNext.setEnabled(isRealNameNotNull && isGradeChosen
                        && isSchoolNameNotNull && isMajorNotNull);
            }
        });

        /*点击选择学校名称*/
        clickToChooseSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollegeSelectDialog.Builder mBuilder = new CollegeSelectDialog.Builder(mContext);
                mBuilder.setPositiveButtonListener(new CollegeSelectDialog.Builder.OnCollegeSelectDialog() {
                    @Override
                    public void onClick(DialogInterface dialog, String currentProvince, String currentSchool, String currentTag) {
                        dialog.dismiss();
                        if ("全部".equals(currentSchool) || "手写输入".equals(currentSchool)){
                            schoolProvince = currentProvince;
                            collegeTag = currentTag;
                            return;
                        }
                        schoolProvince = currentProvince;
                        userSchool.setText(currentSchool);
                        collegeTag = currentTag;
                    }
                });
                CollegeSelectDialog dialog =  mBuilder.create();
                dialog.setCancelable(true);
                dialog.show();
            }
        });


        /*用户年级选择*/
        userGradeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GradeSelectDialog.Builder mBuilder = new GradeSelectDialog.Builder(mContext);
                mBuilder.setPositiveButtonListener(new GradeSelectDialog.Builder.OnGradeSelectClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, String grade, int gradeIndex) {
                        if (gradeIndex <= Constants.GRADE_HIGH_SCHOOL || gradeIndex > Constants.GRADE_COLLEGE_SCHOOL ){
                            showToast("选择年级与身份不符，请重新选择");
                            return;
                        }
                        dialog.dismiss();
                        isGradeChosen = true;
                        btnNext.setEnabled(isRealNameNotNull && isSchoolNameNotNull && isMajorNotNull);
                        userGrade.setText(grade);
                        gradeId = gradeIndex;
                    }
                });
                GradeSelectDialog dialog = mBuilder.create();
                dialog.setCancelable(false);
                dialog.show();

            }
        });

        //学校地区要选择
        schoolAreaSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AreaSelectDialog.Builder mBuilder = new AreaSelectDialog.Builder(mContext);
                mBuilder.setPositiveButtonListener(new AreaSelectDialog.Builder.OnAreaSelectClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, String currentProvince, String currentCity, String currentDistrict, String currentZipCode) {
                        dialog.dismiss();
                        schoolArea.setText(currentProvince + " " + currentCity + " " + currentDistrict);
                        schoolProvince = currentProvince;
                        schoolCity = currentCity;
                        schoolDistrict = currentDistrict;
                    }
                });
                AreaSelectDialog dialog = mBuilder.create();
                dialog.setCancelable(true);
                dialog.show();

            }
        });

        /*用户性别选择*/
        userSexSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSexDialog(userSex);
            }
        });

        /*用户专业选择*/
        majorSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MajorSelectDialog.Builder mBuilder = new MajorSelectDialog.Builder(mContext);
                mBuilder.setPositiveButtonListener(new MajorSelectDialog.Builder.OnMajorSelectDialog() {
                    @Override
                    public void onClick(DialogInterface dialog, String currentMajorClass, String currentMajor, String currentTag) {
                        dialog.dismiss();
                        if ("手写输入".equals(currentMajor)){
                            majorId = Integer.valueOf(currentTag);
                            return;
                        }
                        majorName.setText(currentMajor);
                        majorId = Integer.valueOf(currentTag);
                    }
                });
                CollegeSelectDialog dialog =  mBuilder.create();
                dialog.setCancelable(true);
                dialog.show();
            }
        });

        //让按钮随着输入内容有效而使能
        majorName.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isMajorNotNull = !TextUtils.isEmpty(s.toString());
                btnNext.setEnabled(isRealNameNotNull && isGradeChosen
                        && isSchoolNameNotNull && isMajorNotNull);
            }
        });
    }

    public void showSexDialog(SexType gender) {
        dialog = new Dialog(this, R.style.jmui_default_dialog_style);
        final LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_set_sex, null);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
        RelativeLayout manRl = (RelativeLayout) view.findViewById(R.id.man_rl);
        RelativeLayout womanRl = (RelativeLayout) view.findViewById(R.id.woman_rl);
        ImageView manSelectedIv = (ImageView) view.findViewById(R.id.man_selected_iv);
        ImageView womanSelectedIv = (ImageView) view.findViewById(R.id.woman_selected_iv);

        if (gender == SexType.MALE) {
            manSelectedIv.setVisibility(View.VISIBLE);
            womanSelectedIv.setVisibility(View.GONE);
        } else if (gender == SexType.FEMALE) {
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
                        userSex = SexType.MALE;
                        setGender(true);
                        dialog.cancel();
                        break;
                    case R.id.woman_rl:
                        userSex = SexType.FEMALE;
                        setGender(false);
                        dialog.cancel();
                        break;
                }
            }
        };
        manRl.setOnClickListener(listener);
        womanRl.setOnClickListener(listener);
    }

    public void setGender(boolean isMan) {
        if (isMan) {
            userSexTv.setText(mContext.getString(R.string.man));
            userSexTv.setTextColor(getResources().getColor(R.color.colorPrimary));
            userSexIv.setImageResource(R.mipmap.sex_man);
        } else {
            userSexTv.setText(mContext.getString(R.string.woman));
            userSexTv.setTextColor(getResources().getColor(R.color.deeppink));
            userSexIv.setImageResource(R.mipmap.sex_woman);
        }
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
                            Matisse.from(RegisterCollegeStudentActivity.this)
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

    @OnClick(R.id.btn_finish)
    protected void updateUserInfo() {
        if (!isImgSet) {
            showToast("您好，别忘了选择头像");
            return;
        }

        if (gradeId < 0){
            showToast("请选择年级");
            return;
        }

        String userRealName = realName.getText().toString().trim();

        if (!TextUtils.isEmpty(schoolProvince)){
            currentUser.put(Constants.User_province, schoolProvince);
            currentUser.put(Constants.User_city,schoolCity);
            currentUser.put(Constants.User_district,schoolDistrict);
        }

        String schoolName = userSchool.getText().toString().trim();
        if (!TextUtils.isEmpty(schoolName)){
            currentUser.put(DbUtils.getSchoolTypeByGradeId(gradeId),schoolName);
        }
        else{
            showToast("请输入学校名称");
            return;
        }

        String majorInfo = majorName.getText().toString().trim();


        currentUser.put(Constants.User_major,majorInfo);
        //如果专业是用户选择输入，则可以拿到专业分类号
        if (majorId >= 0)
            currentUser.put(Constants.User_majorCategory,majorId);
        currentUser.put("realname",userRealName);
        currentUser.put("sex", userSex.getSexTypeId());
        currentUser.put(Constants.User_userType, UserType.PROVIDER.getUserTypeId());
        currentUser.put(Constants.User_identityType, IdentityType.COLLEGE_STUDENT.getIdentityId());
        currentUser.put(Constants.User_isVerified, false);
        currentUser.put(Constants.User_grade,gradeId);
        String grade = Constants.studentGrades[gradeId];
        currentUser.put("shortDescription","我的专业是" +majorInfo);
        currentUser.put("description","我是一名" + schoolName +grade+majorInfo+"专业学生");
        //上传极光 用户性别 个性签名  生活地区
        try {
            UserInfo myUserInfo = JMessageClient.getMyInfo();
            UserInfo.Gender mGender = userSex.getSexTypeId() == SexType.FEMALE.getSexTypeId() ? UserInfo.Gender.female : UserInfo.Gender.male;
            myUserInfo.setGender(mGender);
            JMessageClient.updateMyInfo(UserInfo.Field.gender, myUserInfo, new BasicCallback() {
                @Override
                public void gotResult(final int status, final String desc) {
                    if (status != 0) {
                        HandleResponseCode.onHandle(mContext, status, false);
                    }
                }
            });

            myUserInfo.setNickname("校游在校大学生 "+userRealName);
            JMessageClient.updateMyInfo(UserInfo.Field.nickname, myUserInfo, new BasicCallback() {
                @Override
                public void gotResult(final int status, final String desc) {
                    if (status != 0) {
                        HandleResponseCode.onHandle(mContext, status, false);
                    }
                }
            });

            myUserInfo.setSignature("我是一名" + schoolName +grade+majorInfo+"专业学生");
            JMessageClient.updateMyInfo(UserInfo.Field.signature, myUserInfo, new BasicCallback() {
                @Override
                public void gotResult(final int status, final String desc) {
                    if (status != 0) {
                        HandleResponseCode.onHandle(mContext, status, false);
                    }
                }
            });

            /*如果地区不为空*/
            if (!TextUtils.isEmpty(schoolProvince)) {
                myUserInfo.setRegion(schoolProvince + schoolCity + schoolDistrict);
                JMessageClient.updateMyInfo(UserInfo.Field.region, myUserInfo, new BasicCallback() {
                    @Override
                    public void gotResult(final int status, final String desc) {
                        if (status != 0) {
                            HandleResponseCode.onHandle(mContext, status, false);
                        }
                    }
                });
            }
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
