package com.zju.campustour.view.chatting;


import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.presenter.protocal.enumerate.IdentityType;
import com.zju.campustour.presenter.protocal.enumerate.UserType;
import com.zju.campustour.presenter.protocal.enumerate.VerifyStateType;
import com.zju.campustour.presenter.protocal.event.UserTypeChangeEvent;

import org.greenrobot.eventbus.EventBus;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;

import static com.zju.campustour.model.common.Constants.GRADE_HIGH_SCHOOL;
import static com.zju.campustour.model.common.Constants.GRADE_JUNIOR_HIGH_SCHOOL;
import static com.zju.campustour.model.common.Constants.GRADE_PRIMARY_SCHOOL;


public class MeInfoView extends LinearLayout {

    // private LinearLayout mTitleBarContainer;

    private TextView mNicknameTv;
    private ImageView mTypeIv;
    private TextView mGenderTv;
    private ImageView mGenderIv;
    private TextView mVerifiedTv;
    private TextView mIdentityTypeTv;
    private TextView mRegionTv;
    private TextView mSignatureTv;
    private TextView mTypeTv;
    private TextView mSchoolTv;
    private TextView mGradeTv;
    private TextView mEmailTv;
    private TextView mMajorTv;
    private TextView mDescTv;
    private ImageButton mReturnBtn;
    private TextView mTitle;
    private ImageButton mMenuBtn;

    private LinearLayout mAvararLL;
    private LinearLayout mNickNameRl;
    private LinearLayout mSexRl;
    private LinearLayout mAreaRl;
    private LinearLayout mSignatureRl;
    private LinearLayout mTypeLayout;
    private LinearLayout mSchoolLayout;
    private LinearLayout mVerifyIdLayout;
    private LinearLayout mIdentityTypeLayout;
    private LinearLayout mGradeLayout;
    private LinearLayout mMajorLayout;
    private LinearLayout mEmailLayout;
    private LinearLayout mDescLayout;

    SimpleDraweeView mUserAvatar;

    private int gradeId;
    private boolean isVerified;
    private Context mContext;

    public void setSignature(String signature) {
        mSignatureTv.setText(signature);
    }

    public void setNickName(String nickName) {
        mNicknameTv.setText(nickName);
    }

    public void setSchool(String mSchool) {
        mSchoolTv.setText(mSchool);
    }

    public void setVerifiedTv(String mVerified){
        mVerifiedTv.setText(mVerified);
    }

    public void setIdentityTypeTv(String mIdentityType){
        mIdentityTypeTv.setText(mIdentityType);
    }

    public void setEmail(String email){
        mEmailTv.setText(email);
    }

    public void setDesc(String desc){
        mDescTv.setText(desc);
    }

    public void setMajor(String major){
        mMajorTv.setText(major);
    }

    public void setGender(boolean isMan) {
        if (isMan) {
            mGenderTv.setText(mContext.getString(R.string.man));
            mGenderIv.setImageResource(R.mipmap.sex_man);
        } else {
            mGenderTv.setText(mContext.getString(R.string.woman));
            mGenderIv.setImageResource(R.mipmap.sex_woman);
        }
    }

    public void setType(boolean isCommon) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        int i = currentUser.getInt(Constants.User_userType);
        if (isCommon) {
            mTypeTv.setText(UserType.values()[i].getName());
            mTypeIv.setImageResource(R.mipmap.type_common);
            EventBus.getDefault().post(new UserTypeChangeEvent(true));
        } else {
            mTypeTv.setText(UserType.values()[i].getName());
            mTypeIv.setImageResource(R.mipmap.type_major);
            EventBus.getDefault().post(new UserTypeChangeEvent(false));
        }
    }

    public void setRegion(String region) {
        mRegionTv.setText(region);
    }

    public void setGrade(String mGrade) {
        mGradeTv.setText(mGrade);
    }


    private enum ChangeType {
        NICKNAME, LOCATION, SIGNATURE
    }

    public MeInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        // TODO Auto-generated constructor stub
    }

    public void initModule() {
        mReturnBtn = (ImageButton) findViewById(R.id.return_btn);
        mTitle = (TextView) findViewById(R.id.title);
        mMenuBtn = (ImageButton) findViewById(R.id.right_btn);
        mNickNameRl = (LinearLayout) findViewById(R.id.nick_name_rl);
        mAvararLL = (LinearLayout) findViewById(R.id.avatar_ll);
        mSexRl = (LinearLayout) findViewById(R.id.sex_rl);
        mAreaRl = (LinearLayout) findViewById(R.id.location_rl);

        mTypeLayout = (LinearLayout) findViewById(R.id.type_rl);
        mSchoolLayout = (LinearLayout) findViewById(R.id.school_rl);
        mVerifyIdLayout = (LinearLayout) findViewById(R.id.verify_ll);
        mIdentityTypeLayout = (LinearLayout) findViewById(R.id.identity_type_rl);
        mGradeLayout = (LinearLayout) findViewById(R.id.grade_rl);
        mEmailLayout = (LinearLayout) findViewById(R.id.email_rl);
        mDescLayout = (LinearLayout) findViewById(R.id.desc_rl);
        mMajorLayout = (LinearLayout) findViewById(R.id.major_rl);

        mTypeTv = (TextView) findViewById(R.id.type_tv);
        mSchoolTv = (TextView) findViewById(R.id.school_tv);
        mGradeTv = (TextView) findViewById(R.id.grade_tv);
        mEmailTv = (TextView) findViewById(R.id.email_tv);
        mDescTv = (TextView) findViewById(R.id.desc_tv);
        mMajorTv = (TextView) findViewById(R.id.major_tv);

        mTypeIv  = (ImageView) findViewById(R.id.type_icon);

        mRegionTv = (TextView) findViewById(R.id.region_tv);
        mSignatureRl = (LinearLayout) findViewById(R.id.sign_rl);
        mNicknameTv = (TextView) findViewById(R.id.nick_name_tv);
        mUserAvatar = (SimpleDraweeView) findViewById(R.id.user_avatar);
        mGenderTv = (TextView) findViewById(R.id.gender_tv);
        mGenderIv = (ImageView) findViewById(R.id.sex_icon);
        mVerifiedTv = (TextView) findViewById(R.id.verify_tv);
        mIdentityTypeTv = (TextView) findViewById(R.id.identity_type_tv) ;
        mSignatureTv = (TextView) findViewById(R.id.signature_tv);
        mTitle.setText(mContext.getString(R.string.detail_info));
        mMenuBtn.setVisibility(View.GONE);
        refreshUserInfo(JMessageClient.getMyInfo());
    }

    public void refreshUserInfo(UserInfo userInfo) {
        if (userInfo != null) {
            if (!TextUtils.isEmpty(userInfo.getNickname()))
                mNicknameTv.setText(userInfo.getNickname());
            if (userInfo.getGender() == UserInfo.Gender.male) {
                mGenderTv.setText(mContext.getString(R.string.man));
                mGenderIv.setImageResource(R.mipmap.sex_man);
            } else if (userInfo.getGender() == UserInfo.Gender.female) {
                mGenderTv.setText(mContext.getString(R.string.woman));
                mGenderIv.setImageResource(R.mipmap.sex_woman);
            } else {
                mGenderTv.setText(mContext.getString(R.string.unknown));
            }
            if (!TextUtils.isEmpty(userInfo.getRegion()))
                mRegionTv.setText(userInfo.getRegion());
            if (!TextUtils.isEmpty(userInfo.getSignature()))
                mSignatureTv.setText(userInfo.getSignature());
        }

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null){
            //如果是游客，隐藏一些信息
            if (currentUser.getInt(Constants.User_identityType) == IdentityType.LOOK_AROUND_USER.getIdentityId()){
                mTypeLayout.setVisibility(GONE);
                mSchoolLayout.setVisibility(GONE);
                mGradeLayout.setVisibility(GONE);
                mMajorLayout.setVisibility(GONE);
            }



            //显示当前头像
            mUserAvatar.setImageURI(currentUser.getString(Constants.User_imgUrl));

            //身份认证状态
            isVerified = currentUser.getBoolean(Constants.User_isVerified);
            mVerifiedTv.setText(isVerified?"已认证":"未认证");

            //身份类型显示
            int i = currentUser.getInt(Constants.User_identityType);
            mIdentityTypeTv.setText(IdentityType.values()[i].getIdentityName());


            if (currentUser.getInt("userType") == 0){
                mTypeTv.setText("普通用户");
                mTypeIv.setImageResource(R.mipmap.type_common);
            }
            else if (currentUser.getInt("userType") == 1){
                mTypeTv.setText("专业用户");
                mTypeIv.setImageResource(R.mipmap.type_major);
            }
            else
                mTypeTv.setText("未定义");

            String major =  currentUser.getString("major");
            if (TextUtils.isEmpty(major)){
                mMajorTv.setText("未填写");
            }
            else
                mMajorTv.setText(major);

            gradeId = currentUser.getInt("gradeId");
            String grade = Constants.studentGrades[gradeId];
            mGradeTv.setText(grade);



            String school = "";
            if (gradeId <= GRADE_PRIMARY_SCHOOL){
                school = currentUser.getString("primarySchool");
            }
            else if (gradeId <= GRADE_JUNIOR_HIGH_SCHOOL){
                school = currentUser.getString("juniorHighSchool");
            }
            else if (gradeId <= GRADE_HIGH_SCHOOL){
                school = currentUser.getString("highSchool");
            }
            else
                school = currentUser.getString("school");

            if (TextUtils.isEmpty(school))
                school =  "未填写";

            mSchoolTv.setText(school);

            if (!TextUtils.isEmpty(currentUser.getString("emailAddr"))){
                mEmailTv.setText(currentUser.getString("emailAddr"));
            }
            else
                mEmailTv.setText("未填写");

            if (!TextUtils.isEmpty(currentUser.getString("description"))){
                mDescTv.setText(currentUser.getString("description"));
            }
            else
                mDescTv.setText("未填写");
        }
    }

    public void setListeners(OnClickListener onClickListener) {
        mReturnBtn.setOnClickListener(onClickListener);
        mNickNameRl.setOnClickListener(onClickListener);
        mAvararLL.setOnClickListener(onClickListener);
        mSexRl.setOnClickListener(onClickListener);
        mAreaRl.setOnClickListener(onClickListener);
        mSignatureRl.setOnClickListener(onClickListener);
        mTypeLayout.setOnClickListener(onClickListener);
        mSchoolLayout.setOnClickListener(onClickListener);
        mVerifyIdLayout.setOnClickListener(onClickListener);
        mIdentityTypeLayout.setOnClickListener(onClickListener);
        mGradeLayout.setOnClickListener(onClickListener);
        mEmailLayout.setOnClickListener(onClickListener);
        mDescLayout.setOnClickListener(onClickListener);
        mMajorLayout.setOnClickListener(onClickListener);
////		JPushIMManager.getInstance().registerUserAvatarChangeObserver(this);
    }

    public String getSchool(){
        return mSchoolTv.getText().toString().trim();
    }

    public boolean getVerifyState(){
        return isVerified;
    }


    public int getGrade(){
        return gradeId;
    }

    public String getEmail(){
        return mEmailTv.getText().toString().trim();
    }

    public void showUserAvatar(Uri url){
        mUserAvatar.setImageURI(url);
    }

}
