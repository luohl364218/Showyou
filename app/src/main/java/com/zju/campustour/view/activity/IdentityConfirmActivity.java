package com.zju.campustour.view.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.chatting.utils.HandleResponseCode;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.presenter.implement.LocationProvider;
import com.zju.campustour.presenter.protocal.enumerate.IdentityType;
import com.zju.campustour.view.iview.ILocationConsumerView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class IdentityConfirmActivity extends BaseActivity implements View.OnClickListener,ILocationConsumerView {

    @BindView(R.id.id_college_student)
    Button collegeStudentBtn;
    @BindView(R.id.id_school_student)
    Button schoolStudentBtn;
    @BindView(R.id.id_look_around)
    Button lookAroundBtn;
    @BindView(R.id.id_graduate_student)
    Button graduateStudentBtn;
    @BindView(R.id.id_school_teacher)
    Button schoolTeacherBtn;
    @BindView(R.id.id_student_parent)
    Button studentParentBtn;
    @BindView(R.id.btn_next)
    Button nextConfirm;

    boolean isCollegeStudent = false;
    boolean isSchoolStudent = false;
    boolean isLookAround = false;
    boolean isGraduateStudent = false;
    boolean isSchoolTeacher = false;
    boolean isStudentParent = false;
    ParseUser currentUser;
    private LocationProvider mLocationProvider;
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_confirm);
        ButterKnife.bind(this);
        currentUser = ParseUser.getCurrentUser();
        if (currentUser == null){
            showToast("当前用户不存在");
            return;
        }
        mContext = this;
        mLocationProvider = new LocationProvider(this,this);
        mLocationProvider.requestUserLocation();
        initViews();
    }

    private void initViews() {

        collegeStudentBtn.setOnClickListener(this);
        schoolStudentBtn.setOnClickListener(this);
        lookAroundBtn.setOnClickListener(this);
        graduateStudentBtn.setOnClickListener(this);
        schoolTeacherBtn.setOnClickListener(this);
        studentParentBtn.setOnClickListener(this);

        nextConfirm.setEnabled(false);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.id_college_student:
                nextConfirm.setEnabled(true);
                nextConfirm.setSelected(true);
                clearBtnSelectState();
                collegeStudentBtn.setSelected(true);
                isCollegeStudent = true;
                break;
            case R.id.id_school_student:
                nextConfirm.setEnabled(true);
                nextConfirm.setSelected(true);
                clearBtnSelectState();
                schoolStudentBtn.setSelected(true);
                isSchoolStudent = true;
                break;
            case R.id.id_look_around:
                nextConfirm.setEnabled(true);
                nextConfirm.setSelected(true);
                clearBtnSelectState();
                lookAroundBtn.setSelected(true);
                isLookAround = true;
                break;
            case R.id.id_school_teacher:
                nextConfirm.setEnabled(true);
                nextConfirm.setSelected(true);
                clearBtnSelectState();
                schoolTeacherBtn.setSelected(true);
                isSchoolTeacher = true;
                break;
            case R.id.id_graduate_student:
                nextConfirm.setEnabled(true);
                nextConfirm.setSelected(true);
                clearBtnSelectState();
                graduateStudentBtn.setSelected(true);
                isGraduateStudent = true;
                break;
            case R.id.id_student_parent:
                nextConfirm.setEnabled(true);
                nextConfirm.setSelected(true);
                clearBtnSelectState();
                studentParentBtn.setSelected(true);
                isStudentParent = true;
                break;
        }
    }

    public void clearBtnSelectState(){
        collegeStudentBtn.setSelected(false);
        schoolStudentBtn.setSelected(false);
        lookAroundBtn.setSelected(false);
        graduateStudentBtn.setSelected(false);
        schoolTeacherBtn.setSelected(false);
        studentParentBtn.setSelected(false);

        isCollegeStudent = false;
        isSchoolStudent = false;
        isLookAround = false;
        isGraduateStudent = false;
        isSchoolTeacher = false;
        isStudentParent = false;
    }

    @OnClick(R.id.btn_next)
    public void onNextBtnClicked(){
        if (isCollegeStudent){
            startActivity(new Intent(this,RegisterCollegeStudentActivity.class));
        }
        else if (isSchoolStudent){
            startActivity(new Intent(this,RegisterSchoolStudentActivity.class));
        }
        else if (isLookAround){
            startActivity(new Intent(this, RegisterLookAroundActivity.class));
        }
        else if (isGraduateStudent){
            startActivity(new Intent(this,RegisterGraduateStudentActivity.class));
        }
        else if (isSchoolTeacher){
            startActivity(new Intent(this,RegisterTeacherActivity.class));
        }
        else if (isStudentParent){
            startActivity(new Intent(this,RegisterParentActivity.class));
        }
        else {
            showToast("出错啦");
        }
    }

    @Override
    public void onLocationInfoGotSuccess(BDLocation mLocation) {
        if (mLocation == null)
            return;

        try {
            currentUser.put(Constants.User_country, mLocation.getCountry());
            currentUser.put(Constants.User_province, mLocation.getProvince());
            currentUser.put(Constants.User_city, mLocation.getCity());
            currentUser.put(Constants.User_district, mLocation.getDistrict());
            currentUser.put(Constants.User_street,mLocation.getStreet());
            //记录用户的地理位置
            ParseGeoPoint point = new ParseGeoPoint(mLocation.getLatitude(), mLocation.getLongitude());
            currentUser.put(Constants.User_position,point);

            UserInfo myUserInfo = JMessageClient.getMyInfo();
            myUserInfo.setRegion(mLocation.getProvince()+mLocation.getCity()+mLocation.getDistrict());
            JMessageClient.updateMyInfo(UserInfo.Field.region, myUserInfo, new BasicCallback() {
                @Override
                public void gotResult(final int status, final String desc) {
                    if (status != 0) {
                        HandleResponseCode.onHandle(mContext, status, false);
                    }
                }
            });
        }catch (Exception e){

        }finally {
            currentUser.saveInBackground();
        }

    }


    @Override
    public void onLocationRequestRefused() {
        showToast("地理位置请求被拒绝，请手动开启");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationProvider.onClose();
    }
}
