package com.zju.campustour.presenter.chatting.controller;

import android.view.View;
import android.view.View.OnClickListener;

import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.presenter.protocal.enumerate.UserType;
import com.zju.campustour.view.activity.MeInfoActivity;
import com.zju.campustour.view.chatting.MeInfoView;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;

import static com.zju.campustour.model.common.Constants.GRADE_HIGH_SCHOOL;


public class MeInfoController implements OnClickListener {

    private MeInfoView mMeInfoView;
    private MeInfoActivity mContext;


    public MeInfoController(MeInfoView view, MeInfoActivity context) {
        this.mMeInfoView = view;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_btn:
                mContext.setResultAndFinish();
                break;
            case R.id.avatar_ll:
                mContext.editUserImg();
                break;

            case R.id.nick_name_rl:
                mContext.startModifyNickNameActivity();
                break;
            case R.id.sex_rl:
                UserInfo userInfo = JMessageClient.getMyInfo();
                UserInfo.Gender gender = userInfo.getGender();
                mContext.showSexDialog(gender);
                break;
            case R.id.location_rl:
                mContext.startSelectAreaActivity();
                break;
            case R.id.sign_rl:
                mContext.startModifySignatureActivity();
                break;
            case R.id.type_rl:
                ParseUser user = ParseUser.getCurrentUser();
                int grade = user.getInt("gradeId");
                if (grade <= GRADE_HIGH_SCHOOL )
                    return;
                UserType userType = UserType.values()[user.getInt("userType")];
                mContext.showTypeDialog(userType);
                break;
            case R.id.school_rl:
                mContext.startModifySchool(mMeInfoView.getSchool(), mMeInfoView.getGrade());
                break;
            case R.id.verify_ll:
                mContext.startVerifyIdentity(mMeInfoView.getVerifyState());
                break;
            case R.id.identity_type_rl:
                mContext.startModifyIdentityType();
                break;
            case R.id.grade_rl:
                ParseUser currentUser = ParseUser.getCurrentUser();
                int gradeId = currentUser.getInt("gradeId");
                mContext.selectGrade(gradeId);
                break;
            case R.id.email_rl:
                mContext.startModifyEmailActivity(mMeInfoView.getEmail());
                break;
            case R.id.desc_rl:
                mContext.startModifyMyDesc();
                break;

            case R.id.major_rl:
                ParseUser user_1 = ParseUser.getCurrentUser();
                int mGradeId = user_1.getInt("gradeId");
                if (mGradeId <= GRADE_HIGH_SCHOOL )
                    return;
                mContext.selectMajor();
                break;

        }
    }

}
