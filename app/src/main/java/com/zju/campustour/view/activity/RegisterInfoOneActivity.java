package com.zju.campustour.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
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
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.util.SharePreferenceManager;
import com.zju.campustour.model.chatting.utils.HandleResponseCode;
import com.zju.campustour.presenter.listener.MyTextWatch;
import com.zju.campustour.presenter.protocal.event.UserPictureUploadDone;
import com.zju.campustour.view.widget.ClearEditText;
import com.zju.campustour.view.widget.GifSizeFilter;
import com.zju.campustour.view.widget.GradeSelectDialog;

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

import static com.zju.campustour.model.common.Constants.User_isVerified;
@Deprecated
public class RegisterInfoOneActivity extends BaseActivity{

    @BindView(R.id.register_info_one_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.regist_user_img)
    CircleImageView userImg;

    @BindView(R.id.real_name)
    ClearEditText realName;

    @BindView(R.id.user_grade)
    TextView userGrade;

    @BindView(R.id.user_email_addr)
    ClearEditText userEmail;

    @BindView(R.id.user_sex_type)
    RadioGroup userSexType;

    @BindView(R.id.user_short_desc)
    ClearEditText userShortDesc;

    @BindView(R.id.user_grade_select)
    RelativeLayout userGradeSelect;

    @BindView(R.id.btn_one_next)
    Button btnNext;

    @BindView(R.id.user_info_update_title_1)
    TextView title;

    private boolean isRealNameNotNull = false;
    private boolean isUserShortDesc = false;
    private Context mContext = this;
    private boolean isImgSet = false;
    private static final int REQUEST_CODE_CHOOSE = 23;
    //注册以后要将当前用户自动登录
    //private UserInfoOpPresenterImpl userLoginImpl;
    //拿到用户的年级层次
    private int gradeId;

    //当前用户
    String userName;
    String password;
    String phoneNum;
    ParseUser currentUser;

    //是否是编辑模式
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_info_one);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        Intent mIntent = getIntent();
        userName = mIntent.getStringExtra("userName");
        password = mIntent.getStringExtra("password");
        phoneNum = mIntent.getStringExtra("phone");
        isEditMode = mIntent.getBooleanExtra("isEditMode",false);
        currentUser = ParseUser.getCurrentUser();
        if (currentUser == null)
            return;
        initOriginalView();

        initView();
    }

    @Deprecated
    private void initOriginalView() {
        try{
            /*该方法在版本1.2以后已经废弃*/
            if (currentUser != null && isEditMode){
                title.setText("信息修改 1/2");
                realName.setText(currentUser.getString("realname"));
                isImgSet = true;
                isRealNameNotNull = true;
                userEmail.setText(currentUser.getString("emailAddr"));
                userGrade.setText(currentUser.getString("grade"));
                userSexType.check(currentUser.getInt("sex") == 0 ? R.id.select_male : R.id.select_female);
                userShortDesc.setText(currentUser.getString("shortDescription"));
                isUserShortDesc = true;
                gradeId = currentUser.getInt("gradeId");
                btnNext.setEnabled(true);
            }
            else {
                userSexType.check(R.id.select_male);
            }

        }catch (Exception e){
        }
    }

    private void initView(){

        //让按钮随着输入内容有效而使能
        realName.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isRealNameNotNull = !TextUtils.isEmpty(s.toString());
                btnNext.setEnabled((isRealNameNotNull && isUserShortDesc));
            }
        });

        userShortDesc.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isUserShortDesc = !TextUtils.isEmpty(s.toString());
                btnNext.setEnabled((isRealNameNotNull && isUserShortDesc));
            }
        });

        userGradeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GradeSelectDialog.Builder mBuilder = new GradeSelectDialog.Builder(mContext);
                mBuilder.setPositiveButtonListener(new GradeSelectDialog.Builder.OnGradeSelectClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, String grade, int gradeIndex) {
                        dialog.dismiss();
                        userGrade.setText(grade);
                        gradeId = gradeIndex ;
                    }
                });
                GradeSelectDialog dialog =  mBuilder.create();
                dialog.setCancelable(false);
                dialog.show();

            }
        });
    }


    @OnClick(R.id.btn_one_next)
    protected void updateUserInfo(){
        if (!isImgSet) {
            showToast("请选择头像");
            return;
        }

        String userRealName = realName.getText().toString().trim();
        if (TextUtils.isEmpty(userRealName)) {
            showToast("请输入真实姓名");
            return;
        }


        String grade = userGrade.getText().toString().trim();
        if ("点击选择".equals(grade)){
            showToast("请选择你的学历");
            return;
        }

        String email = userEmail.getText().toString().trim();

        int userSex = userSexType.getCheckedRadioButtonId() == R.id.select_male ? 0:1;

        String shortDesc = userShortDesc.getText().toString().trim();
        if (TextUtils.isEmpty(shortDesc)) {
            showToast("请用一句话完成自我介绍");
            return;
        }

        currentUser.put("realname",userRealName);
        currentUser.put("phoneNum",phoneNum);
        currentUser.put("emailAddr",email);
        currentUser.put("grade",grade);
        currentUser.put("sex",userSex);
        currentUser.put(User_isVerified,false);
        currentUser.put("shortDescription",shortDesc);
        currentUser.put("gradeId",gradeId);

        //上传极光 用户性别 个性签名
        try {
            UserInfo myUserInfo = JMessageClient.getMyInfo();
            myUserInfo.setGender(userSex == 1 ? UserInfo.Gender.female : UserInfo.Gender.male);
            JMessageClient.updateMyInfo(UserInfo.Field.gender, myUserInfo, new BasicCallback() {
                @Override
                public void gotResult(final int status, final String desc) {
                    if (status != 0) {
                        HandleResponseCode.onHandle(mContext, status, false);
                    }
                }
            });

            myUserInfo.setSignature(shortDesc);
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

        currentUser.saveInBackground();
        Intent mIntent = new Intent(this, RegisterInfoTwoActivity.class);
        mIntent.putExtra("gradeId",gradeId);
        mIntent.putExtra("isEditMode",isEditMode);
        startActivity(mIntent);
    }


    @OnClick(R.id.regist_user_img)
    public void chooseUserImg(){
             /*Intent mIntent = new Intent(this, RegisterInfoOneActivity.class);
                    mIntent.putExtra("isEditMode",true);
                    startActivity(mIntent);*/
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            Matisse.from(RegisterInfoOneActivity.this)
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

    @Override
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
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(),mDate.getTime()+"_showyou.jpeg"));
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
        String uriSuffix = currentUser.getObjectId()+suffix+mDate.getTime();
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
                SharePreferenceManager.putString(this,Constants.DB_USERIMG,event.getLocalImgUrl());
                currentUser.put("imgUrl",event.getCloudImgUrl());
                currentUser.saveInBackground();
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
}
