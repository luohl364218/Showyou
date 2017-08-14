package com.zju.campustour.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseUser;
import com.zju.campustour.MainActivity;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.chatting.utils.HandleResponseCode;
import com.zju.campustour.presenter.listener.MyTextWatch;
import com.zju.campustour.presenter.protocal.enumerate.UserType;
import com.zju.campustour.presenter.protocal.event.EditUserInfoDone;
import com.zju.campustour.view.widget.AreaSelectDialog;
import com.zju.campustour.view.widget.CollegeSelectDialog;
import com.zju.campustour.view.widget.MajorSelectDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
@Deprecated
public class RegisterInfoTwoActivity extends BaseActivity {

    private int gradeIndex;

    @BindView(R.id.school_area_select)
    RelativeLayout schoolAreaSelect;

    @BindView(R.id.school_area)
    TextView schoolArea;

    @BindView(R.id.school_select)
    RelativeLayout schoolSelect;

    @BindView(R.id.user_school)
    EditText schoolName;

    @BindView(R.id.major_select)
    RelativeLayout majorSelect;

    @BindView(R.id.user_major)
    EditText majorName;

    @BindView(R.id.user_type_group)
    RadioGroup userType;

    @BindView(R.id.select_major_user)
    RadioButton majorUserBtn;

    @BindView(R.id.select_major_user_txt)
    TextView majorUserTxt;

    @BindView(R.id.user_type_hint_2)
    TextView majorUserHint;

    @BindView(R.id.user_desc_title)
    TextView userDescTitle;

    @BindView(R.id.user_desc_select)
    LinearLayout userDescContent;

    @BindView(R.id.user_desc)
    EditText userDesc;

    @BindView(R.id.btn_two_next)
    Button btnConfirm;

    @BindView(R.id.user_info_update_title_2)
    TextView title;

    private Context mContext = this;
    private String schoolProvince = "";
    private String schoolCity = "";
    private String schoolDistrict = "";
    private String collegeTag = "";
    private String school;
    private String majorClass;
    private String major;
    private int majorId;

    //当前用户
    ParseUser currentUser;
    //是否是编辑模式
    private boolean isEditMode = false;

    private boolean isSchoolNotNull = false;
    private boolean isMajorNotNull = false;
    private boolean isSchoolAreaNotNull = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_info_two);
        ButterKnife.bind(this);
        Intent mIntent = getIntent();
        gradeIndex = mIntent.getIntExtra("gradeId",-1);
        isEditMode = mIntent.getBooleanExtra("isEditMode",false);
        currentUser = ParseUser.getCurrentUser();
        if (isEditMode && currentUser != null){
            //修改模式
            title.setText("信息修改 2/2");
            initOriginalView();
        }
        else {
          /*  //注册模式 自我介绍先不显示
            userDescTitle.setVisibility(View.GONE);
            userDescContent.setVisibility(View.GONE);*/

            userType.check(R.id.select_common_user);
        }

        initView();

    }

    private void initOriginalView() {
        //如果是大学生，隐藏地区选择，显示专业用户选项、提示
        if (gradeIndex > Constants.GRADE_HIGH_SCHOOL){
            try{
                //【大学用户】信息
                if (currentUser != null && isEditMode){
                    school = currentUser.getString("school");
                    schoolName.setText(school);
                    //大学是有省份数据的，不能忘了
                    schoolProvince = currentUser.getString("province");
                    isSchoolNotNull = true;
                    isSchoolAreaNotNull = true;
                    if (currentUser.getInt("userType") == 0)
                        userType.check(R.id.select_common_user );
                    else {
                      /*  //【专业用户】显示自我介绍
                        userDescTitle.setVisibility(View.VISIBLE);
                        userDescContent.setVisibility(View.VISIBLE);*/

                        userType.check(R.id.select_major_user);

                    }
                    userDesc.setText(currentUser.getString("description"));
                    major = currentUser.getString("major");
                    isMajorNotNull = true;
                    majorId = currentUser.getInt("categoryId");
                    majorName.setText(major);

                    btnConfirm.setEnabled(true);
                }

            }catch (Exception e){
            }
        }
        else {
            try{
                //【普通用户】信息
                if (currentUser != null && isEditMode){
                    schoolProvince = currentUser.getString("province");
                    schoolCity = currentUser.getString("city");
                    schoolDistrict = currentUser.getString("district");

                    schoolArea.setText(schoolProvince +" "+schoolCity+ " " + schoolDistrict);
                    isSchoolAreaNotNull = true;
                    String key = "";
                    if (gradeIndex <= Constants.GRADE_PRIMARY_SCHOOL)
                        key = "primarySchool";
                    else if (gradeIndex < Constants.GRADE_JUNIOR_HIGH_SCHOOL)
                        key = "juniorHighSchool";
                    else
                        key = "highSchool";

                    school = currentUser.getString(key);
                    schoolName.setText(school);
                    isSchoolNotNull = true;
                    userType.check(R.id.select_common_user );
                    userDesc.setText(currentUser.getString("description"));
                    btnConfirm.setEnabled(true);
                }

            }catch (Exception e){
            }
        }


    }

    private void initView() {

        //如果是大学生，隐藏地区选择，显示专业用户选项、提示
        if (gradeIndex > Constants.GRADE_HIGH_SCHOOL){
            isSchoolAreaNotNull = true;
            schoolAreaSelect.setVisibility(View.GONE);
            majorUserBtn.setVisibility(View.VISIBLE);
            majorUserTxt.setVisibility(View.VISIBLE);
            majorUserHint.setVisibility(View.VISIBLE);
            //学校只能点击，不能输入
            schoolName.setEnabled(false);
            schoolName.setClickable(true);
            schoolName.setHint("请点击左侧选择学校");

            //专业只能点击，不能输入
            majorSelect.setVisibility(View.VISIBLE);
            majorName.setEnabled(false);
            majorName.setClickable(true);
            majorName.setHint("请点击左侧选择专业");

            schoolSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CollegeSelectDialog.Builder mBuilder = new CollegeSelectDialog.Builder(mContext);
                    mBuilder.setPositiveButtonListener(new CollegeSelectDialog.Builder.OnCollegeSelectDialog() {
                        @Override
                        public void onClick(DialogInterface dialog, String currentProvince, String currentSchool, String currentTag) {
                            dialog.dismiss();
                            if ("全部".equals(currentSchool) || "手写输入".equals(currentSchool)){
                                schoolName.setEnabled(true);
                                schoolName.setHint("请输入你的大学名称");
                                schoolProvince = currentProvince;
                                collegeTag = currentTag;
                                schoolName.setClickable(false);
                                return;
                            }
                            schoolProvince = currentProvince;
                            school = currentSchool;
                            schoolName.setText(school);
                            collegeTag = currentTag;
                            isSchoolNotNull = true;
                            btnConfirm.setEnabled(isMajorNotNull);
                        }
                    });
                    CollegeSelectDialog dialog =  mBuilder.create();
                    dialog.setCancelable(false);
                    dialog.show();
                }
            });



            majorSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MajorSelectDialog.Builder mBuilder = new MajorSelectDialog.Builder(mContext);
                    mBuilder.setPositiveButtonListener(new MajorSelectDialog.Builder.OnMajorSelectDialog() {
                        @Override
                        public void onClick(DialogInterface dialog, String currentMajorClass, String currentMajor, String currentTag) {
                            dialog.dismiss();
                            if ("手写输入".equals(currentMajor)){
                                majorName.setEnabled(true);
                                majorName.setHint("请输入你的专业名称");
                                majorName.setClickable(false);
                                majorClass = currentMajorClass;
                                majorId = Integer.valueOf(currentTag);
                                return;
                            }
                            majorClass = currentMajorClass;
                            major = currentMajor;
                            majorName.setText(major);
                            majorId = Integer.valueOf(currentTag);
                            isMajorNotNull = true;
                            btnConfirm.setEnabled(isSchoolNotNull);
                        }
                    });
                    CollegeSelectDialog dialog =  mBuilder.create();
                    dialog.setCancelable(false);
                    dialog.show();
                }
            });
        }
        else {
            isMajorNotNull = true;
            schoolAreaSelect.setVisibility(View.VISIBLE);
            majorUserBtn.setVisibility(View.GONE);
            majorUserTxt.setVisibility(View.GONE);
            majorUserHint.setVisibility(View.GONE);
            majorSelect.setVisibility(View.GONE);
            //学校名称只能输入，不能选择
            schoolName.setEnabled(true);
            schoolName.setClickable(false);
            schoolName.setHint("输入学校名称，如 XX一中/XX中学");
            //让按钮随着输入内容有效而使能
            schoolName.addTextChangedListener(new MyTextWatch() {
                @Override
                public void afterTextChanged(Editable s) {
                    isSchoolNotNull = !TextUtils.isEmpty(s.toString());
                    btnConfirm.setEnabled((isSchoolNotNull && isSchoolAreaNotNull));
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
                        schoolArea.setText(currentProvince+" "+currentCity + " " + currentDistrict);
                        isSchoolAreaNotNull = true;
                        btnConfirm.setEnabled((isSchoolNotNull));
                        schoolProvince = currentProvince;
                        schoolCity = currentCity;
                        schoolDistrict = currentDistrict;
                    }
                });
                AreaSelectDialog dialog = mBuilder.create();
                dialog.setCancelable(false);
                dialog.show();

                }
            });


        }


        //让按钮随着输入内容有效而使能
        schoolName.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isSchoolNotNull = !TextUtils.isEmpty(s.toString());
                btnConfirm.setEnabled((isSchoolNotNull && isMajorNotNull && isSchoolAreaNotNull));
            }
        });
        //让按钮随着输入内容有效而使能
        majorName.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isMajorNotNull = !TextUtils.isEmpty(s.toString());
                btnConfirm.setEnabled((isSchoolNotNull && isMajorNotNull && isSchoolAreaNotNull));
            }
        });


       /* userType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.select_major_user){
                    //自我介绍在选中专业用户后显示
                    userDescTitle.setVisibility(View.VISIBLE);
                    userDescContent.setVisibility(View.VISIBLE);

                }
                else {
                    //自我介绍不显示
                    userDescTitle.setVisibility(View.GONE);
                    userDescContent.setVisibility(View.GONE);

                }
            }
        });*/
    }


    @OnClick(R.id.btn_two_next)
    protected void updateUserInfo(){

        school = schoolName.getText().toString().trim();
        if ( TextUtils.isEmpty(school)) {
            showToast("学校名称不能为空");
            return;
        }

        if ("".equals(schoolProvince)){
                showToast("学校所在地区不能为空");
                return;
        }

        String userDescription = userDesc.getText().toString().trim();
        if (TextUtils.isEmpty(userDescription)) {
            showToast("用户介绍不能为空");
            return;
        }

        boolean isMajorUser = userType.getCheckedRadioButtonId() == R.id.select_major_user;
        String majorNameInput = majorName.getText().toString().trim();
        if (gradeIndex > Constants.GRADE_HIGH_SCHOOL){

            if (TextUtils.isEmpty(majorNameInput) || "请输入你的专业名称".equals(majorNameInput)){
                showToast("专业不能为空");
                return;
            }

        }

        currentUser.put("province",schoolProvince);
        currentUser.put("city", schoolCity);
        currentUser.put("district",schoolDistrict);
        currentUser.put("description",userDescription);



       //按年级存储

        //1. 大学生+
        if (gradeIndex > Constants.GRADE_HIGH_SCHOOL){

            if (isMajorUser){
                //存储专业用户信息
                currentUser.put("userType", UserType.PROVIDER.getUserTypeId());
            }
            else {
                currentUser.put("userType", UserType.USER.getUserTypeId());
                //存储普通用户信息
            }
            currentUser.put("school",school);
            currentUser.put("collegeTag",collegeTag);
            currentUser.put("major",majorNameInput);
            currentUser.put("categoryId", majorId);
        }
        else if (gradeIndex > Constants.GRADE_JUNIOR_HIGH_SCHOOL){
            currentUser.put("userType", UserType.USER.getUserTypeId());
            //存储普通用户信息
            currentUser.put("highSchool",school);
        }
        else if (gradeIndex > Constants.GRADE_PRIMARY_SCHOOL){
            currentUser.put("userType", UserType.USER.getUserTypeId());
            //存储普通用户信息
            currentUser.put("juniorHighSchool",school);
        }
        else {
            currentUser.put("userType", UserType.USER.getUserTypeId());
            //存储普通用户信息
            currentUser.put("primarySchool",school);
        }


        currentUser.saveInBackground();
        //上传用户的昵称和地区
        try {
            UserInfo myUserInfo = JMessageClient.getMyInfo();
            myUserInfo.setNickname(school+" " + currentUser.getString("realname"));
            JMessageClient.updateMyInfo(UserInfo.Field.nickname, myUserInfo, null);

            myUserInfo.setRegion(schoolProvince+ schoolCity+schoolDistrict);
            JMessageClient.updateMyInfo(UserInfo.Field.region, myUserInfo, new BasicCallback() {
                @Override
                public void gotResult(final int status, final String desc) {
                    if (status != 0) {
                        HandleResponseCode.onHandle(mContext, status, false);
                    }
                }
            });
        }catch (Exception e){

        }

        EventBus.getDefault().post(new EditUserInfoDone(true));
        Intent mIntent = new Intent(this, MainActivity.class);
        if (isEditMode)
            showToast("完成修改，欢迎回来");
        else
            showToast("完成注册，欢迎加入校游");
        startActivity(mIntent);
        finish();
    }
}
