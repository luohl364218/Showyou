package com.zju.campustour.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.presenter.implement.ImageUploader;
import com.zju.campustour.presenter.protocal.enumerate.UserType;
import com.zju.campustour.presenter.protocal.event.UserPictureUploadDone;
import com.zju.campustour.presenter.protocal.event.UserTypeChangeEvent;
import com.zju.campustour.view.activity.AnotherMyProjectActivity;
import com.zju.campustour.view.activity.LoginActivity;
import com.zju.campustour.view.activity.MeInfoActivity;
import com.zju.campustour.view.activity.MyProjectActivity;
import com.zju.campustour.view.activity.MySchoolmateActivity;
import com.zju.campustour.view.activity.ProjectNewActivity;
import com.zju.campustour.view.activity.SettingActivity;
import com.zju.campustour.view.iview.IImageUploadView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import cn.bmob.v3.update.BmobUpdateAgent;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HeyLink on 2017/9/5.
 */

public class MineFragment extends BaseFragment implements View.OnClickListener{

    private View mRootView;
    private SimpleDraweeView userImg;
    private CircleImageView userEditLogo;
    private ImageView mAvatarIv;
    private TextView loginHint;
    private TextView userName;
    private TextView userType;
    private Activity mContext;
    ParseUser currentLoginUser;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_mine, container, false);
            mContext = getActivity();
            currentLoginUser = ParseUser.getCurrentUser();
            EventBus.getDefault().register(this);
            initView();

        }

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null){
            parent.removeView(mRootView);
        }

        return mRootView;
    }

    private void initView() {
        userImg = (SimpleDraweeView) mRootView.findViewById(R.id.current_user_img);
        userImg.setOnClickListener(this);
        mAvatarIv = (ImageView)mRootView.findViewById(R.id.my_avatar_iv);
        loginHint = (TextView) mRootView.findViewById(R.id.login_hint);
        loginHint.setOnClickListener(this);
        userEditLogo = (CircleImageView) mRootView.findViewById(R.id.user_edit);
        userEditLogo.setOnClickListener(this);
        userName = (TextView) mRootView.findViewById(R.id.username);
        userType = (TextView) mRootView.findViewById(R.id.user_type);

        TextView myInfo = (TextView) mRootView.findViewById(R.id.edit_info);
        TextView myProject = (TextView) mRootView.findViewById(R.id.my_project);
        TextView myStatus = (TextView) mRootView.findViewById(R.id.my_status);
        TextView mySchoolmate = (TextView) mRootView.findViewById(R.id.my_schoolmate);
        TextView publishProject = (TextView) mRootView.findViewById(R.id.build_project);
        TextView mySetting = (TextView) mRootView.findViewById(R.id.my_setting);
        TextView checkUpdate = (TextView) mRootView.findViewById(R.id.check_update);

        myInfo.setOnClickListener(this);
        myProject.setOnClickListener(this);
        myStatus.setOnClickListener(this);
        mySchoolmate.setOnClickListener(this);
        publishProject.setOnClickListener(this);
        mySetting.setOnClickListener(this);
        checkUpdate.setOnClickListener(this);

        if (currentLoginUser == null){
            //双重防护， 只有同时登陆成功才能进入
            userImg.setImageURI(Uri.parse("www.cxx"));
            loginHint.setVisibility(View.VISIBLE);
            userEditLogo.setVisibility(View.GONE);

        }
        else{

            userEditLogo.setVisibility(View.VISIBLE);
            String userImgUrl = currentLoginUser.getString("imgUrl");
            userImg.setImageURI(Uri.parse(userImgUrl == null? "www.cxx" :userImgUrl));
            loginHint.setVisibility(View.GONE);
            userName.setText(currentLoginUser.getString(Constants.User_realName));
            userType.setText(UserType.values()[currentLoginUser.getInt("userType")].getName());
            currentLoginUser.put("online",true);
            currentLoginUser.saveInBackground();

        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.user_edit:
                //编辑我的信息
                startActivity(new Intent(mContext, MeInfoActivity.class));
                break;
            case R.id.login_hint:
            case R.id.current_user_img:
                currentLoginUser = ParseUser.getCurrentUser();
                if (currentLoginUser == null) {
                    Intent mIntent = new Intent(mContext, LoginActivity.class);
                    startActivity(mIntent);
                }

                break;
            case R.id.edit_info:
                //编辑我的信息
                startActivity(new Intent(mContext, MeInfoActivity.class));
                break;
            case R.id.my_project:
                //打开我的活动
                Intent mIntent_1 = new Intent(mContext, AnotherMyProjectActivity.class);
                currentLoginUser = ParseUser.getCurrentUser();
                if (currentLoginUser != null)
                    mIntent_1.putExtra("userType", currentLoginUser.getInt("userType"));
                else
                    mIntent_1.putExtra("userType", 0);

                startActivity(mIntent_1);
                break;
            case R.id.my_status:
                //// TODO: 2017/9/5 完善个人发布的状态显示
                break;

            case R.id.my_schoolmate:
                //打开我的校友
                startActivity(new Intent(mContext, MySchoolmateActivity.class));
                break;

            case R.id.build_project:
                //发布活动
                currentLoginUser = ParseUser.getCurrentUser();
                //修复Bug，未登录状态下不能发布活动
                if (currentLoginUser != null) {
                    UserType mUserType = UserType.values()[currentLoginUser.getInt("userType")];
                    if (mUserType == UserType.PROVIDER) {
                        Intent mIntent_2 = new Intent(mContext, ProjectNewActivity.class);
                        mIntent_2.putExtra("isEditMode", false);
                        startActivity(mIntent_2);
                    } else {
                        showToast(mContext,"同学你是普通用户，没有权限创建活动哦");
                    }
                }
                break;

            case R.id.my_setting:
                startActivity(new Intent(mContext, SettingActivity.class));
                break;

            case R.id.check_update:
                BmobUpdateAgent.forceUpdate(getContext());
                break;


        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onPictureUploadDoneEvent(UserPictureUploadDone event) {
        currentLoginUser = ParseUser.getCurrentUser();
        if (event != null && currentLoginUser != null){
            Uri mUri = Uri.fromFile(new File(event.getLocalImgUrl()));
            userImg.setImageURI(mUri);

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserTypeChangeEvent(UserTypeChangeEvent event){
        if (event.isCommonUser()){
            userType.setText(UserType.USER.getName());
        }
        else {
            userType.setText(UserType.PROVIDER.getName());
        }

    }

    @Override
    public void onDestroy() {

        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
