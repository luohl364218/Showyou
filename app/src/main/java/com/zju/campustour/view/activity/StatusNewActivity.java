package com.zju.campustour.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;
import com.zju.campustour.R;
import com.zju.campustour.model.bean.LabelInfo;
import com.zju.campustour.model.bean.PositionModel;
import com.zju.campustour.model.bean.StatusInfoModel;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.presenter.implement.ImageUploader;
import com.zju.campustour.presenter.implement.LabelInfoOperator;
import com.zju.campustour.presenter.implement.LocationProvider;
import com.zju.campustour.presenter.implement.StatusInfoOperator;
import com.zju.campustour.presenter.listener.MyTextWatch;
import com.zju.campustour.presenter.protocal.enumerate.UploadImgType;
import com.zju.campustour.presenter.protocal.event.LabelSelectEvent;
import com.zju.campustour.view.iview.IImageUploadView;
import com.zju.campustour.view.iview.ILabelInfoView;
import com.zju.campustour.view.iview.ILocationConsumerView;
import com.zju.campustour.view.iview.IStatusInfoView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StatusNewActivity extends BaseActivity implements ILocationConsumerView,IImageUploadView,IStatusInfoView, ILabelInfoView {


    private LocationProvider mLocationProvider;

    @BindView(R.id.return_btn)
    ImageButton returnBtn;
    @BindView(R.id.publish_btn)
    ImageButton publishBtn;
    @BindView(R.id.new_status_et)
    EditText statusContent;
    @BindView(R.id.new_status_img)
    ImageView imgSelect;
    @BindView(R.id.hide_location)
    TextView hideLocationTv;
    @BindView(R.id.city_location)
    TextView cityTv;
    @BindView(R.id.district_location)
    TextView districtTv;
    @BindView(R.id.street_location)
    TextView streetTv;
    @BindView(R.id.detail_location)
    TextView detailTv;
    @BindView(R.id.diy_location)
    EditText diyLocationTv;
    @BindView(R.id.select_topic_btn)
    ImageButton selectTopicBtn;
    @BindView(R.id.default_topic)
    TextView labelName;

    boolean isContentNotNull = false;
    boolean isStatusImgSelected = false;

    private ImageUploader mImageUploader;
    private String mImgUrl;
    private String province;
    private String city;
    private String district;
    private String street;
    private String detailLocation;
    private String diyLocation = "不知名的角落";
    private String labelId = "C7a5uazVn2";
    private String labelContent = "聊聊我们的班主任";
    private List<Poi> nearLocationList = new ArrayList<>();
    private PositionModel mPositionModel;
    //是否显示地址
    private boolean hidePosition = false;


    LabelInfoOperator mLabelInfoOperator;
    private StatusInfoOperator mStatusInfoOperator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_new);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        //更改顶部栏的颜色
        //changePrimaryDarkColor();

        mLocationProvider = new LocationProvider(this,this);
        mLocationProvider.requestUserLocation();
        mImageUploader = new ImageUploader(this,this);

        mStatusInfoOperator = new StatusInfoOperator(this,this);

        mLabelInfoOperator = new LabelInfoOperator(this,this);
        mLabelInfoOperator.getRecommendLabel(0,1);

        initView();
    }

    private void initView() {

        statusContent.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isContentNotNull = !TextUtils.isEmpty(s.toString());
                if (isContentNotNull){
                    publishBtn.setImageResource(R.mipmap.publish_yellow);
                }
                else
                    publishBtn.setImageResource(R.mipmap.publish_gray);

            }
        });

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitConfirmDialog();
            }
        });

        labelName.setText("#"+labelContent+"#");

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void changePrimaryDarkColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            /*window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);*/
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.darkColorPrimary));   //这里动态修改颜色
        }
    }

    @Override
    public void onLocationInfoGotSuccess(BDLocation mLocation) {
        if (mLocation == null)
            return;

        nearLocationList = mLocation.getPoiList();
        province = mLocation.getProvince();
        city = mLocation.getCity();
        district = mLocation.getDistrict();
        street = mLocation.getStreet();

        if (TextUtils.isEmpty(city)){
            city = "无名市";
            showToast("位置获取失败，百度的锅，请重新进入该页面");
        }


        if (TextUtils.isEmpty(district))
            district = "未知区";

        if (TextUtils.isEmpty(street)){
            street = "某个角落";
        }


        cityTv.setText(city);
        districtTv.setText(district);
        streetTv.setText(street);
        detailTv.setText(""+nearLocationList.get(0).getName());

        detailLocation = nearLocationList.get(0).getName();

        mPositionModel = new PositionModel(mLocation.getLatitude(),mLocation.getLongitude());

    }

    @Override
    public void onLocationRequestRefused() {
        showToast("地理位置请求被拒绝，请手动开启");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationProvider.onClose();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.publish_btn)
    void onCommitButtonClicked(){

        if (!isContentNotNull && !isStatusImgSelected){

            showToast("请输入状态内容");
            return;

        }

        detailLocation = detailTv.getText().toString().trim();

        StatusInfoModel mStatusInfoModel = new StatusInfoModel();
        mStatusInfoModel.setImgUrl(mImgUrl);
        mStatusInfoModel.setContent(statusContent.getText().toString().trim());
        mStatusInfoModel.setFavourCount(0);
        mStatusInfoModel.setCommentCount(0);
        mStatusInfoModel.setDeleted(false);
        //// TODO: 2017/8/23
        mStatusInfoModel.setLabelId(labelId);
        mStatusInfoModel.setLabelContent(labelContent);

        mStatusInfoModel.setProvince(province);
        mStatusInfoModel.setCity(city);
        mStatusInfoModel.setDistrict(district);
        mStatusInfoModel.setStreet(street);
        mStatusInfoModel.setStatusPosition(mPositionModel);
        mStatusInfoModel.setDiyLocation(diyLocation);
        mStatusInfoModel.setDetailLocation(detailLocation);
        mStatusInfoModel.setHidePosition(hidePosition);

        mStatusInfoOperator.publishStatusInfo(mStatusInfoModel);
    }

    @OnClick(R.id.new_status_img)
    void onImgButtonClicked(){

        showImgSelectDialog();

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
        RelativeLayout cancelBtn = (RelativeLayout) view.findViewById(R.id.cancel_btn);

        View.OnClickListener listener = new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                switch (v.getId()){

                    case R.id.album_btn:

                        mImageUploader.chooseUserImg(UploadImgType.IMG_STATUS);
                        dialog.dismiss();

                        break;

                    case R.id.camera_btn:
                        mImageUploader.takePhoto(UploadImgType.IMG_STATUS);
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
                mImgUrl = imgUrl;
                isStatusImgSelected = true;
                publishBtn.setImageResource(R.mipmap.publish_yellow);
                Glide.with(getApplicationContext()).load(localPath).into(imgSelect);
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
    public void onStatusInfoGotSuccess(List<StatusInfoModel> mStatusInfoModels) {
    }

    @Override
    public void onStatusInfoGotError(Exception e) {
    }

    @Override
    public void onStatusInfoCommitSuccess() {
        showToast("状态发布成功");
        finish();
    }

    @Override
    public void onStatusInfoCommitError(Exception e) {
        showToast("状态发布失败，请检查网络连接");
    }


    @OnClick(R.id.location_icon)
    public void onLocationIconClicked(){
        showLocationSelectDialog();
    }

    @OnClick(R.id.city_location)
    public void onCityTvClicked(){
        showLocationSelectDialog();
    }

    @OnClick(R.id.district_location)
    public void onDistrictClicked(){
        showLocationSelectDialog();
    }

    @OnClick(R.id.hide_location)
    public void onHideLocationClicked()
    {
        showLocationSelectDialog();
    }

    private void showLocationSelectDialog() {

        final Dialog dialog = new Dialog(this, R.style.jmui_default_dialog_style);
        final LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_location_hide_select, null);
        dialog.setContentView(view);
        dialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        RelativeLayout showHideLocationBtn = (RelativeLayout) view.findViewById(R.id.btn_1);
        RelativeLayout editDiyLocationBtn = (RelativeLayout) view.findViewById(R.id.btn_2);
        View divider = view.findViewById(R.id.divider_1);
        RelativeLayout cancelBtn = (RelativeLayout) view.findViewById(R.id.cancel_btn);
        TextView selectBtn1 = (TextView) view.findViewById(R.id.select_btn_tv1);

        if (!hidePosition){
            selectBtn1.setText("隐藏位置");
            divider.setVisibility(View.VISIBLE);
            editDiyLocationBtn.setVisibility(View.VISIBLE);
        }
        else {
            editDiyLocationBtn.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
            selectBtn1.setText("显示位置");
        }

        View.OnClickListener listener = new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                switch (v.getId()){

                    case R.id.btn_1:
                        //显示或隐藏地址
                        hidePosition = !hidePosition;

                        if (hidePosition){
                            cityTv.setVisibility(View.GONE);
                            districtTv.setVisibility(View.GONE);
                            detailTv.setVisibility(View.GONE);
                            diyLocationTv.setVisibility(View.GONE);
                            hideLocationTv.setVisibility(View.VISIBLE);
                        }
                        else {
                            hideLocationTv.setVisibility(View.GONE);
                            cityTv.setVisibility(View.VISIBLE);
                            districtTv.setVisibility(View.VISIBLE);
                            detailTv.setVisibility(View.VISIBLE);
                            //mLocationProvider.requestUserLocation();
                        }

                        dialog.dismiss();

                        break;

                    case R.id.btn_2:
                        //附近地址
                        showNearByLocationsDialog();
                        dialog.dismiss();

                        break;
                    case R.id.cancel_btn:
                        dialog.dismiss();
                        break;

                }
            }
        };


        showHideLocationBtn.setOnClickListener(listener);
        editDiyLocationBtn.setOnClickListener(listener);
        cancelBtn.setOnClickListener(listener);
    }


    @OnClick(R.id.diy_location)
    @Deprecated
    public void onDiyLocationClicked(){
        showLocationDiyDialog();
    }

    @Deprecated
    private void showLocationDiyDialog() {

        final Dialog dialog = new Dialog(this, R.style.jmui_default_dialog_style);
        final LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_edit_diy_location, null);
        dialog.setContentView(view);
        dialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        EditText diyLocationEt = (EditText) view.findViewById(R.id.location_et);
        Button confirmBtn = (Button) view.findViewById(R.id.confirm_btn);
        Button cancelBtn = (Button) view.findViewById(R.id.cancel_btn);
        View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()){

                    case R.id.confirm_btn:

                        dialog.dismiss();
                        diyLocation = diyLocationEt.getText().toString().trim();
                        if (TextUtils.isEmpty(diyLocation))
                            return;

                        diyLocationTv.setHint(diyLocation);

                        break;

                    case R.id.cancel_btn:
                        dialog.dismiss();
                        break;
                }
            }
        };

        confirmBtn.setOnClickListener(listener);
        cancelBtn.setOnClickListener(listener);

    }

    @OnClick(R.id.detail_location)
    public void onDetailTvClicked(){
        showNearByLocationsDialog();
    }

    public void showNearByLocationsDialog() {
        if (nearLocationList.isEmpty())
            return;

        final Dialog dialog = new Dialog(this, R.style.jmui_default_dialog_style);
        final LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_location_select, null);
        dialog.setContentView(view);
        dialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        RelativeLayout Btn1 = (RelativeLayout) view.findViewById(R.id.btn_1);
        RelativeLayout Btn2 = (RelativeLayout) view.findViewById(R.id.btn_2);
        RelativeLayout Btn3 = (RelativeLayout) view.findViewById(R.id.btn_3);
        RelativeLayout Btn4 = (RelativeLayout) view.findViewById(R.id.btn_4);
        //RelativeLayout cancelBtn = (RelativeLayout) view.findViewById(R.id.cancel_btn);

        TextView title = (TextView) view.findViewById(R.id.title);
        TextView tv1 = (TextView) view.findViewById(R.id.select_btn_tv1);
        TextView tv2 = (TextView) view.findViewById(R.id.select_btn_tv2);
        TextView tv3 = (TextView) view.findViewById(R.id.select_btn_tv3);
        TextView tv4 = (TextView) view.findViewById(R.id.select_btn_tv4);

        title.setText(nearLocationList.get(0).getName()+"(推荐)");
        tv1.setText(nearLocationList.get(1).getName());
        tv2.setText(nearLocationList.get(2).getName());
        tv3.setText(nearLocationList.get(3).getName());
        tv4.setText(nearLocationList.get(4).getName());

        View.OnClickListener listener = new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                switch (v.getId()){
                    case R.id.title:
                        dialog.dismiss();
                        break;
                    case R.id.btn_1:
                        detailTv.setText(tv1.getText());
                        dialog.dismiss();
                        break;

                    case R.id.btn_2:
                        detailTv.setText(tv2.getText());
                        dialog.dismiss();
                        break;
                    case R.id.btn_3:
                        detailTv.setText(tv3.getText());
                        dialog.dismiss();
                        break;
                    case R.id.btn_4:
                        detailTv.setText(tv4.getText());
                        dialog.dismiss();
                        break;
                    case R.id.cancel_btn:
                        dialog.dismiss();
                        break;

                }
            }
        };

        title.setOnClickListener(listener);
        Btn1.setOnClickListener(listener);
        Btn2.setOnClickListener(listener);
        Btn3.setOnClickListener(listener);
        Btn4.setOnClickListener(listener);

    }


    @OnClick(R.id.default_topic)
    public void onDefaultTopicSelect(){
        labelName.setVisibility(View.GONE);
        statusContent.append("#"+labelContent+"#");

    }

    @OnClick(R.id.select_topic_btn)
    public void onTopicSelectBtnClicked(){
        startActivity(new Intent(this, LabelSelectActivity.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLabelSelectEvent(LabelSelectEvent event){

        labelId = event.getLabelInfo().getLabelId();
        labelContent = event.getLabelInfo().getContent();
        statusContent.append("#"+labelContent+"#");
        labelName.setVisibility(View.GONE);

    }

    @Override
    public void onLabelInfoGotSuccess(List<LabelInfo> mLabelInfos) {
        if (mLabelInfos == null || mLabelInfos.size() == 0)
            return;

        LabelInfo mLabelInfo = mLabelInfos.get(0);
        labelContent = mLabelInfo.getContent();
        labelId = mLabelInfo.getLabelId();
    }

    @Override
    public void onLabelInfoGotFailed(Exception e) {

    }


    private void showExitConfirmDialog() {

        final Dialog dialog = new Dialog(this, R.style.jmui_default_dialog_style);
        final LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.jmui_dialog_base_with_button, null);
        dialog.setContentView(view);
        dialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        TextView title = (TextView) view.findViewById(R.id.jmui_title);
        Button confirmBtn = (Button) view.findViewById(R.id.jmui_commit_btn);
        Button cancelBtn = (Button) view.findViewById(R.id.jmui_cancel_btn);

        title.setText("你的动态还未发布，确定不发么？");
        confirmBtn.setText("不发了");
        cancelBtn.setText("再想想");
        View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()){

                    case R.id.jmui_commit_btn:

                        dialog.dismiss();
                        finish();

                        break;

                    case R.id.jmui_cancel_btn:
                        dialog.dismiss();
                        break;
                }
            }
        };

        confirmBtn.setOnClickListener(listener);
        cancelBtn.setOnClickListener(listener);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            showExitConfirmDialog();
        }
        return super.onKeyDown(keyCode, event);
    }
}
