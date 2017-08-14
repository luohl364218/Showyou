package com.zju.campustour.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.zju.campustour.model.database.models.VerifyInfo;
import com.zju.campustour.presenter.implement.UserVerifyInfoImpl;
import com.zju.campustour.presenter.protocal.enumerate.IdentityType;
import com.zju.campustour.presenter.protocal.enumerate.VerifyStateType;
import com.zju.campustour.view.iview.IUserVerifyInfoView;
import com.zju.campustour.view.widget.GifSizeFilter;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.zju.campustour.model.common.Constants.URL_VERIFIED_ID_BG;

public class VerifyIdentityActivity extends BaseActivity implements IUserVerifyInfoView {

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
    private static final int REQUEST_CODE_CHOOSE = 23;
    boolean isImgSet = false;
    private Context mContext = this;
    String imgUrl = "";
    boolean isRefresh = false;
    VerifyInfo verifyInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_identity);
        ButterKnife.bind(this);
        currentUser = ParseUser.getCurrentUser();
        if (currentUser == null)
            finish();


        initView();
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
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            Matisse.from(VerifyIdentityActivity.this)
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
        Date now = new Date();

        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), now.getTime() + "_SampleCropImage.jpeg"));
        UCrop.of(sourceUri, destinationUri).withAspectRatio(4, 3).withMaxResultSize(800, 600).start(this);
    }

    public void imageUpLoad(String localPath) {

        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpeg");
        OkHttpClient client = new OkHttpClient();
        String suffix = localPath.substring(localPath.lastIndexOf("."));

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        File f = new File(localPath);
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
                            String imgUriTemp = "http://119.23.248.205:8080/pictures/" + uriSuffix;
                            verifyBtn.setEnabled(true);
                            imgUrl = imgUriTemp;
                            Glide.with(mContext).load(localPath).into(backgroundPic);
                        }
                    }));
                }

            }
        });

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




}
