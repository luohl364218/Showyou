package com.zju.campustour.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yalantis.ucrop.UCrop;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.database.models.Project;
import com.zju.campustour.presenter.listener.MyTextWatch;
import com.zju.campustour.view.widget.ClearEditText;
import com.zju.campustour.view.widget.GifSizeFilter;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProjectNewActivity extends BaseActivity{

    @BindView(R.id.publish_project_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.publish_project_title)
    TextView mTitle;
    @BindView(R.id.publish_background_img)
    ImageView backgroundPic;
    @BindView(R.id.project_name)
    ClearEditText projectTitle;
    @BindView(R.id.project_max_accept_num)
    ClearEditText projectACNum;
    @BindView(R.id.project_original_price)
    ClearEditText projectOriginalPrice;
    @BindView(R.id.project_real_price)
    ClearEditText projectRealPrice;
    @BindView(R.id.project_desc)
    EditText projectDesc;
    @BindView(R.id.project_start_time)
    CalendarView projectTime;
    @BindView(R.id.project_tips)
    EditText projectTips;
    @BindView(R.id.btn_project_new)
    Button projectBtn;

    private Context mContext = this;

    private static int picId = 1;
    //是否是编辑模式
    private boolean isEditMode = false;
    ParseUser currentUser;
    Project currentProject;
    private static final int REQUEST_CODE_CHOOSE = 23;

    boolean isTitleNotNull = false;
    boolean isACNumNotNull = false;
    boolean isOriginalPriceNotNull = false;
    boolean isSalePriceNotNull = false;
    boolean isDescNotNull = false;
    boolean isTipsNotNull = false;
    boolean isProjectDateNotNull = false;
    boolean isImgSet = false;

    String imgUrl = "";
    Date startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_new);
        ButterKnife.bind(this);
        Intent mIntent = getIntent();
        isEditMode = mIntent.getBooleanExtra("isEditMode",false);
        if (isEditMode){
            currentProject = (Project) mIntent.getSerializableExtra("project");
        }

        currentUser = ParseUser.getCurrentUser();
        if (currentUser == null)
            finish();


        initOriginalView();
        initView();

    }

    private void initView() {

        //让按钮随着输入内容有效而使能
        projectTitle.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isTitleNotNull = !TextUtils.isEmpty(s.toString());
                projectBtn.setEnabled(isTitleNotNull && isACNumNotNull && isOriginalPriceNotNull && isSalePriceNotNull
                && isDescNotNull && isTipsNotNull &&isProjectDateNotNull);
            }
        });

        //让按钮随着输入内容有效而使能
        projectACNum.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isACNumNotNull = !TextUtils.isEmpty(s.toString());
                projectBtn.setEnabled(isTitleNotNull && isACNumNotNull && isOriginalPriceNotNull && isSalePriceNotNull
                        && isDescNotNull && isTipsNotNull &&isProjectDateNotNull);
            }
        });

        //让按钮随着输入内容有效而使能
        projectOriginalPrice.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isOriginalPriceNotNull = !TextUtils.isEmpty(s.toString());
                projectBtn.setEnabled(isTitleNotNull && isACNumNotNull && isOriginalPriceNotNull && isSalePriceNotNull
                        && isDescNotNull && isTipsNotNull &&isProjectDateNotNull);
            }
        });

        //让按钮随着输入内容有效而使能
        projectRealPrice.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isSalePriceNotNull = !TextUtils.isEmpty(s.toString());
                projectBtn.setEnabled(isTitleNotNull && isACNumNotNull && isOriginalPriceNotNull && isSalePriceNotNull
                        && isDescNotNull && isTipsNotNull &&isProjectDateNotNull);
            }
        });

        //让按钮随着输入内容有效而使能
        projectDesc.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isDescNotNull = !TextUtils.isEmpty(s.toString());
                projectBtn.setEnabled(isTitleNotNull && isACNumNotNull && isOriginalPriceNotNull && isSalePriceNotNull
                        && isDescNotNull && isTipsNotNull &&isProjectDateNotNull);
            }
        });

        //让按钮随着输入内容有效而使能
        projectTips.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isTipsNotNull = !TextUtils.isEmpty(s.toString());
                projectBtn.setEnabled(isTitleNotNull && isACNumNotNull && isOriginalPriceNotNull && isSalePriceNotNull
                        && isDescNotNull && isTipsNotNull &&isProjectDateNotNull);
            }
        });


        projectTime.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                isProjectDateNotNull = true;
                startTime = new Date(year-1900,month,dayOfMonth);
                projectBtn.setEnabled(isTitleNotNull && isACNumNotNull && isOriginalPriceNotNull && isSalePriceNotNull
                        && isDescNotNull && isTipsNotNull &&isProjectDateNotNull);
            }
        });


    }

    private void initOriginalView() {
        try{

            if (currentProject != null && isEditMode){
                mTitle.setText("修改活动");
                isImgSet = true;
                imgUrl = currentProject.getImgUrl();
                Glide.with(mContext).load(imgUrl).into(backgroundPic);

                projectTitle.setText(currentProject.getTitle());
                isTitleNotNull = true;
                projectACNum.setText(currentProject.getAcceptNum()+ "");
                isACNumNotNull = true;
                projectRealPrice.setText(currentProject.getSalePrice()+"");
                isSalePriceNotNull = true;
                projectDesc.setText(currentProject.getDescription());
                isDescNotNull = true;
                projectTips.setText(currentProject.getTips());
                isTipsNotNull = true;
                projectTime.setDate(currentProject.getStartTime().getTime());
                startTime = currentProject.getStartTime();
                isProjectDateNotNull = true;
                projectBtn.setEnabled(true);
                projectOriginalPrice.setText(currentProject.getPrice()+"");
                isOriginalPriceNotNull = true;
            }
            else {
                projectBtn.setEnabled(false);
            }

        }catch (Exception e){
        }
    }

    @OnClick(R.id.btn_project_new)
    public void addOrUpdateProject(){
        if (!isNetworkUseful){
            showToast("网络不可用，请稍后再试");
            return;
        }
        String projectName = projectTitle.getText().toString().trim();
        if (TextUtils.isEmpty(projectName)) {
            showToast("请输入活动标题");
            return;
        }

        String projectNum = projectACNum.getText().toString().trim();
        if (TextUtils.isEmpty(projectNum)) {
            showToast("请输入活动人数");
            return;
        }

        String projectPrice = projectOriginalPrice.getText().toString().trim();
        if (TextUtils.isEmpty(projectPrice)) {
            showToast("请输入活动原价");
            return;
        }

        String projectSalePrice = projectRealPrice.getText().toString().trim();
        if (TextUtils.isEmpty(projectSalePrice)) {
            showToast("请输入活动现价");
            return;
        }

        String projectDescription = projectDesc.getText().toString().trim();
        if (TextUtils.isEmpty(projectDescription)) {
            showToast("请输入活动描述");
            return;
        }

        String projectHint = projectTips.getText().toString().trim();
        if (TextUtils.isEmpty(projectHint)) {
            showToast("请输入活动提示");
            return;
        }

        if (startTime == null){
            showToast("请输入活动提示");
            return;
        }



     /*      if (!isImgSet){
            imgUrl=Constants.URL_DEFAULT_PROJECT_BG;
        }
     Project project = new Project(
                currentProject.getId(),
                currentUser,
                projectName,
                startTime,
                imgUrl,
                Integer.valueOf(projectPrice),
                Integer.valueOf(projectSalePrice),
                projectDescription,
                Integer.valueOf(projectNum),
                ProjectStateType.values()[0],
                0,
                projectHint
        );*/


        if (isEditMode){
            SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("活动修改中");
            pDialog.setCancelable(false);
            pDialog.show();

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Project");
            query.getInBackground(currentProject.getId(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null && object != null){

                        object.put("title",projectName);
                        currentProject.setTitle(projectName);
                        object.put("acceptNum",Integer.valueOf(projectNum));
                        currentProject.setAcceptNum(Integer.valueOf(projectNum));
                        object.put("price", Integer.valueOf(projectPrice));
                        currentProject.setPrice(Integer.valueOf(projectPrice));
                        object.put("salePrice",Integer.valueOf(projectSalePrice));
                        currentProject.setSalePrice(Integer.valueOf(projectSalePrice));
                        object.put("description",projectDescription);
                        currentProject.setDescription(projectDescription);
                        object.put("tips",projectHint);
                        currentProject.setTips(projectHint);
                        object.put("startTime",startTime);
                        currentProject.setStartTime(startTime);
                        if (isImgSet) {
                            object.put("imgUrl", imgUrl);
                            currentProject.setImgUrl(imgUrl);
                        }
                        object.saveEventually(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null){
                                    pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    pDialog.setTitleText("活动修改成功").setConfirmText("确定");
                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismissWithAnimation();

                                            Intent mIntent = new Intent(mContext, ProjectActivity.class);
                                            mIntent.putExtra("project",currentProject);
                                            startActivity(mIntent);

                                            finish();
                                        }
                                    });
                                }
                                else {
                                    pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                    pDialog.setTitleText("活动修改失败").setConfirmText("确定");
                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismissWithAnimation();
                                        }
                                    });
                                }
                            }
                        });
                    }
                    else {
                        pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        pDialog.setTitleText("活动修改失败").setConfirmText("确定");
                        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        });
                    }
                }
            });
        }
        else {

            SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("活动创建中");
            pDialog.setCancelable(false);
            pDialog.show();

            ParseObject project = new ParseObject("Project");
            project.put("userId",currentUser.getObjectId());
            project.put("title",projectName);
            project.put("acceptNum",Integer.valueOf(projectNum));
            project.put("price", Integer.valueOf(projectPrice));
            project.put("salePrice",Integer.valueOf(projectSalePrice));
            project.put("description",projectDescription);
            project.put("tips",projectHint);
            project.put("startTime",startTime);
            project.put("rateCount",1);
            project.put("refundable",true);
            project.put("score",10);
            project.put("providerV2",currentUser);
            project.put("projectState",0);
            project.put("collectorNum",0);
            project.put("bookedNum",0);
            project.put("identified",false);
            project.put("official",false);
            if (!isImgSet){
                project.put("imgUrl", Constants.URL_DEFAULT_PROJECT_BG);
            }
            else
                project.put("imgUrl",imgUrl);

            project.saveEventually(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){
                        pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        pDialog.setTitleText("活动创建成功").setConfirmText("确定");
                        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                finish();
                            }
                        });
                    }
                    else{
                        pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        pDialog.setTitleText("活动创建失败").setConfirmText("确定");
                        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        });
                    }
                }
            });
        }


    }


    @OnClick(R.id.publish_background_img)
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
                            Matisse.from(ProjectNewActivity.this)
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
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "SampleCropImage.jpeg"));
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
                            String imgUriTemp = "http://119.23.248.205:8080/pictures/" + uriSuffix;
                            isImgSet = true;
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

}
