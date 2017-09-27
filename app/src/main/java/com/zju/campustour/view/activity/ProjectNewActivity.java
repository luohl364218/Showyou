package com.zju.campustour.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yalantis.ucrop.UCrop;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.bean.Project;
import com.zju.campustour.presenter.implement.ImageUploader;
import com.zju.campustour.presenter.listener.MyTextWatch;
import com.zju.campustour.presenter.protocal.enumerate.UploadImgType;
import com.zju.campustour.view.iview.IImageUploadView;
import com.zju.campustour.view.widget.ClearEditText;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProjectNewActivity extends BaseActivity implements IImageUploadView {

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

    boolean isTitleNotNull = false;
    boolean isACNumNotNull = false;
    boolean isOriginalPriceNotNull = false;
    boolean isSalePriceNotNull = false;
    boolean isDescNotNull = false;
    boolean isTipsNotNull = false;
    boolean isProjectDateNotNull = false;
    boolean isImgSet = false;

    String mImgUrl = "";
    Date startTime;

    private ImageUploader mImageUploader;

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

        mImageUploader = new ImageUploader(this,this);
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
                mImgUrl = currentProject.getImgUrl();
                Glide.with(mContext).load(mImgUrl).into(backgroundPic);

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
            mImgUrl=Constants.URL_DEFAULT_PROJECT_BG;
        }
     Project project = new Project(
                currentProject.getUserTypeId(),
                currentUser,
                projectName,
                startTime,
                mImgUrl,
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

                        //默认线上活动
                        object.put(Constants.Project_IsOffline,false);
                        currentProject.setOffline(false);

                        if (isImgSet) {
                            object.put("mImgUrl", mImgUrl);
                            currentProject.setImgUrl(mImgUrl);
                        }
                        object.saveInBackground(new SaveCallback() {
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
            project.put(Constants.Project_IsDelete,false);
            project.put(Constants.Project_IsOffline,false);
            project.put(Constants.Project_IsRecommend,false);
            if (!isImgSet){
                project.put(Constants.Project_ImgUrl, Constants.URL_DEFAULT_PROJECT_BG);
            }
            else
                project.put(Constants.Project_ImgUrl, mImgUrl);

            project.saveInBackground(new SaveCallback() {
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

                        mImageUploader.chooseUserImg(UploadImgType.IMG_PROJECT);
                        dialog.dismiss();

                        break;

                    case R.id.camera_btn:
                        mImageUploader.takePhoto(UploadImgType.IMG_PROJECT);
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

        isImgSet = true;
        mImgUrl = imgUrl;
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



    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
