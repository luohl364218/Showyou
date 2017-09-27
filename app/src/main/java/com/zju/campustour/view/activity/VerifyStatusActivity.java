package com.zju.campustour.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.bean.VerifyInfo;
import com.zju.campustour.presenter.implement.UserVerifyInfoImpl;
import com.zju.campustour.presenter.protocal.enumerate.IdentityType;
import com.zju.campustour.presenter.protocal.enumerate.VerifyStateType;
import com.zju.campustour.view.iview.IUserVerifyInfoView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VerifyStatusActivity extends BaseActivity implements IUserVerifyInfoView {

    @BindView(R.id.verify_not_yet_iv)
    ImageView verifyNotYetIv;

    @BindView(R.id.verify_done_iv)
    ImageView verifyDoneIv;

    @BindView(R.id.verify_ing_iv)
    ImageView verifyIngIv;

    @BindView(R.id.verify_failed_iv)
    ImageView verifyFailedIV;

    @BindView(R.id.verify_not_yet_tv)
    TextView verifyNotYetTv;

    @BindView(R.id.verify_ing_tv)
    TextView verifyIngTv;

    @BindView(R.id.verify_done_tv)
    TextView verifyDoneTv;

    @BindView(R.id.verify_failed_tv)
    TextView verifyFailedTv;

    @BindView(R.id.identity_type)
    TextView identityType;

    @BindView(R.id.reply_comment_tv)
    TextView replyComment;

    @BindView(R.id.verify_status_btn)
    Button verifyBtn;

    @BindView(R.id.verify_loading_iv)
    ImageView verifyLoadingIv;

    @BindView(R.id.verify_loading_tv)
    TextView verifyLoadingTv;

    @BindView(R.id.return_btn)
    ImageButton mReturnBtn;

    @BindView(R.id.title)
    TextView mTitle;

    @BindView(R.id.right_btn)
    ImageButton mMenuBtn;


    ParseUser currentUser;
    UserVerifyInfoImpl userVerifyInfoImpl;
    VerifyInfo mVerifyInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifys_satus);

        ButterKnife.bind(this);
        currentUser = ParseUser.getCurrentUser();

        if(currentUser  == null){
            showToast("您还未登陆哦");
            return;
        }

        initView();

    }

    @Override
    protected void onStart(){
        super.onStart();
        getUserVerifyInfo();

    }

    private void getUserVerifyInfo() {
        Boolean isVerified = currentUser.getBoolean(Constants.User_isVerified);
        if(!isVerified){
            if (userVerifyInfoImpl == null)
                userVerifyInfoImpl = new UserVerifyInfoImpl(this,this);
            userVerifyInfoImpl.queryVerifyInfoState(currentUser.getObjectId(),
                    IdentityType.values()[currentUser.getInt(Constants.User_identityType)]);
        }else {
            verifyNotYetIv.setVisibility(View.GONE);
            verifyDoneIv.setVisibility(View.VISIBLE);
            verifyIngIv.setVisibility(View.GONE);
            verifyFailedIV.setVisibility(View.GONE);
            verifyLoadingIv.setVisibility(View.GONE);
            verifyNotYetTv.setVisibility(View.GONE);
            verifyIngTv.setVisibility(View.GONE);
            verifyDoneTv.setVisibility(View.VISIBLE);
            verifyFailedTv.setVisibility(View.GONE);
            verifyLoadingTv.setVisibility(View.GONE);
            replyComment.setVisibility(View.GONE);
            verifyBtn.setVisibility(View.GONE);
            verifyDoneTv.getResources().getColor(R.color.green);
            currentUser.put(Constants.User_isVerified,true);
            currentUser.saveInBackground();
        }

    }


    private void initView(){
        mTitle.setText("当前认证状态");
        mMenuBtn.setVisibility(View.GONE);
        mReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        identityType.setText(String.format("您当前身份类型为：%s",
                IdentityType.values()[currentUser.getInt(Constants.User_identityType)].getIdentityName()));

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerifyStatusActivity.this,VerifyIdentityActivity.class);
                intent.putExtra("VerifyInfo",mVerifyInfo);
                startActivity(intent);
            }
        });
    }



    private void initVerifyStateView(VerifyInfo verifyInfo){

        VerifyStateType currentState ;
        if (verifyInfo.getSubmitUserId() == null)
            currentState = VerifyStateType.VERIFY_NOT_YET;
        else {
            currentState = verifyInfo.getSubmitVerifyStateType();

            mVerifyInfo = verifyInfo;
        }

        switch (currentState){

            case VERIFY_NOT_YET:
                verifyNotYetIv.setVisibility(View.VISIBLE);
                verifyDoneIv.setVisibility(View.GONE);
                verifyIngIv.setVisibility(View.GONE);
                verifyFailedIV.setVisibility(View.GONE);
                verifyLoadingIv.setVisibility(View.GONE);
                verifyNotYetTv.setVisibility(View.VISIBLE);
                verifyIngTv.setVisibility(View.GONE);
                verifyDoneTv.setVisibility(View.GONE);
                verifyFailedTv.setVisibility(View.GONE);
                replyComment.setVisibility(View.GONE);
                verifyLoadingTv.setVisibility(View.GONE);
                verifyBtn.setText("去认证");
                verifyBtn.setEnabled(true);
                break;
            case VERIFY_DONE:
                verifyNotYetIv.setVisibility(View.GONE);
                verifyDoneIv.setVisibility(View.VISIBLE);
                verifyIngIv.setVisibility(View.GONE);
                verifyFailedIV.setVisibility(View.GONE);
                verifyLoadingIv.setVisibility(View.GONE);
                verifyNotYetTv.setVisibility(View.GONE);
                verifyIngTv.setVisibility(View.GONE);
                verifyDoneTv.setVisibility(View.VISIBLE);
                verifyFailedTv.setVisibility(View.GONE);
                verifyLoadingTv.setVisibility(View.GONE);
                replyComment.setVisibility(View.GONE);
                verifyBtn.setVisibility(View.GONE);
                verifyDoneTv.getResources().getColor(R.color.green);
                currentUser.put(Constants.User_isVerified,true);
                currentUser.saveInBackground();
                break;

            case VERIFY_ING:
                verifyNotYetIv.setVisibility(View.GONE);
                verifyDoneIv.setVisibility(View.GONE);
                verifyIngIv.setVisibility(View.VISIBLE);
                verifyFailedIV.setVisibility(View.GONE);
                verifyLoadingIv.setVisibility(View.GONE);
                verifyNotYetTv.setVisibility(View.GONE);
                verifyIngTv.setVisibility(View.VISIBLE);
                verifyDoneTv.setVisibility(View.GONE);
                verifyFailedTv.setVisibility(View.GONE);
                verifyLoadingTv.setVisibility(View.GONE);
                replyComment.setVisibility(View.GONE);
                verifyBtn.setText("修改信息");
                verifyBtn.setEnabled(true);
                break;

            case VERIFY_FAILED:
                verifyNotYetIv.setVisibility(View.GONE);
                verifyDoneIv.setVisibility(View.GONE);
                verifyIngIv.setVisibility(View.GONE);
                verifyFailedIV.setVisibility(View.VISIBLE);
                verifyLoadingIv.setVisibility(View.GONE);
                verifyNotYetTv.setVisibility(View.GONE);
                verifyIngTv.setVisibility(View.GONE);
                verifyDoneTv.setVisibility(View.GONE);
                verifyFailedTv.setVisibility(View.VISIBLE);
                verifyLoadingTv.setVisibility(View.GONE);
                replyComment.setVisibility(View.VISIBLE);
                replyComment.setText(String.format("原因为： %s", verifyInfo.getReplyComment()));
                verifyFailedTv.setTextColor(getResources().getColor(R.color.red));
                verifyBtn.setText("重新提交");
                verifyBtn.setEnabled(true);
                break;

            default:
                break;

        }


    }


    @Override
    public void onSubmitVerifyInfoSuccess(boolean isRefresh) {

    }

    @Override
    public void onSubmitVerifyInfoFailed(Exception e) {

    }

    @Override
    public void onQueryVerifyInfoDone(VerifyInfo verifyInfo) {

        if (verifyInfo != null)
            initVerifyStateView(verifyInfo);
        else
            showToast("获取审核状态失败，请稍后再试");
    }
}
