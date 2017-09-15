package com.zju.campustour.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.parse.ParseException;
import com.zju.campustour.MainActivity;
import com.zju.campustour.R;
import com.zju.campustour.model.bean.User;
import com.zju.campustour.model.chatting.utils.HandleResponseCode;
import com.zju.campustour.model.chatting.entity.Event;
import com.zju.campustour.model.chatting.entity.EventType;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.bean.FriendEntry;
import com.zju.campustour.model.chatting.utils.DialogCreator;
import com.zju.campustour.presenter.chatting.controller.FriendInfoController;
import com.zju.campustour.presenter.chatting.tools.NativeImageLoader;
import com.zju.campustour.presenter.implement.UserInfoOpPresenterImpl;
import com.zju.campustour.view.application.CampusTourApplication;
import com.zju.campustour.view.chatting.FriendInfoView;
import com.zju.campustour.view.iview.ISearchUserInfoView;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.eventbus.EventBus;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class FriendInfoActivity extends BaseActivity implements ISearchUserInfoView {

    private Toolbar mToolbar;
    private FriendInfoView mFriendInfoView;
    private FriendInfoController mFriendInfoController;
    private String mTargetId;
    private long mGroupId;
    public UserInfo mUserInfo;
    private String mTitle;
    private boolean mIsGetAvatar = false;
    private String mTargetAppKey;
    private boolean mIsFromContact;
    public UserInfoOpPresenterImpl mUserInfoOpPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);

        mToolbar = (Toolbar) findViewById(R.id.friend_info_toolbar);
        mFriendInfoView = (FriendInfoView) findViewById(R.id.friend_info_view);
        mTargetId = getIntent().getStringExtra(Constants.TARGET_ID);
        mTargetAppKey = getIntent().getStringExtra(Constants.TARGET_APP_KEY);
        if (mTargetAppKey == null) {
            mTargetAppKey = JMessageClient.getMyInfo().getAppKey();
        }

        mUserInfoOpPresenter = new UserInfoOpPresenterImpl(this,this);
        mFriendInfoView.initModule();
        mFriendInfoController = new FriendInfoController(mFriendInfoView, this);
        mFriendInfoView.setListeners(mFriendInfoController);
        mFriendInfoView.setOnChangeListener(mFriendInfoController);
        mIsFromContact = getIntent().getBooleanExtra("fromContact", false);
        if (mIsFromContact) {
            updateUserInfo();
        } else {
            mGroupId = getIntent().getLongExtra(Constants.GROUP_ID, 0);
            Conversation conv;
            if (mGroupId == 0) {
                conv = JMessageClient.getSingleConversation(mTargetId, mTargetAppKey);
                mUserInfo = (UserInfo) conv.getTargetInfo();
            } else {
                conv = JMessageClient.getGroupConversation(mGroupId);
                GroupInfo groupInfo = (GroupInfo) conv.getTargetInfo();
                mUserInfo = groupInfo.getGroupMemberInfo(mTargetId, mTargetAppKey);
            }

            //先从Conversation里获得UserInfo展示出来
            mFriendInfoView.initInfo(mUserInfo);
            updateUserInfo();
        }

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = mUserInfo.getNickname();
                Intent intent = new Intent();
                intent.putExtra(Constants.NICKNAME, nickname);
                setResult(Constants.RESULT_CODE_FRIEND_INFO, intent);
                finish();
            }
        });


    }



    private void updateUserInfo() {
        //更新一次UserInfo
        final Dialog dialog = DialogCreator.createLoadingDialog(FriendInfoActivity.this,
                FriendInfoActivity.this.getString(R.string.jmui_loading));
        dialog.show();
        JMessageClient.getUserInfo(mTargetId, mTargetAppKey, new GetUserInfoCallback() {
            @Override
            public void gotResult(int status, String desc, final UserInfo userInfo) {
                dialog.dismiss();
                if (status == 0) {
                    mUserInfo = userInfo;
                    mTitle = userInfo.getNotename();
                    if (TextUtils.isEmpty(mTitle)) {
                        mTitle = userInfo.getNickname();
                    }
                    mFriendInfoView.initInfo(userInfo);
                } else {
                    HandleResponseCode.onHandle(FriendInfoActivity.this, status, false);
                }
            }
        });
    }


    /**
     * 如果是从群聊跳转过来，使用startActivity启动聊天界面，如果是单聊跳转过来，setResult然后
     * finish掉此界面
     */
    public void startChatActivity() {
        if (mIsFromContact) {
            Intent intent = new Intent(this, ChatActivity.class);
            String title = mUserInfo.getNotename();
            if (TextUtils.isEmpty(title)) {
                title = mUserInfo.getNickname();
                if (TextUtils.isEmpty(title)) {
                    title = mUserInfo.getUserName();
                }
            }
            intent.putExtra(Constants.CONV_TITLE, title);
            intent.putExtra(Constants.TARGET_ID, mUserInfo.getUserName());
            intent.putExtra(Constants.TARGET_APP_KEY, mUserInfo.getAppKey());
            startActivity(intent);
        } else {
            if (mGroupId != 0) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(Constants.TARGET_ID, mTargetId);
                intent.putExtra(Constants.TARGET_APP_KEY, mTargetAppKey);
                intent.setClass(this, ChatActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent();
                intent.putExtra("returnChatActivity", true);
                intent.putExtra(Constants.CONV_TITLE, mTitle);
                setResult(Constants.RESULT_CODE_FRIEND_INFO, intent);
            }
        }

        Conversation conv = JMessageClient.getSingleConversation(mTargetId, mTargetAppKey);
        //如果会话为空，使用EventBus通知会话列表添加新会话
        if (conv == null) {
            conv = Conversation.createSingleConversation(mTargetId, mTargetAppKey);
            EventBus.getDefault().post(new Event.Builder()
                    .setType(EventType.createConversation)
                    .setConversation(conv)
                    .build());
        }
        finish();
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public String getUserName() {
        return mUserInfo.getUserName();
    }

    public String getTargetAppKey() {
        return mTargetAppKey;
    }


    //点击头像预览大图
    public void startBrowserAvatar() {
        if (mUserInfo != null && !TextUtils.isEmpty(mUserInfo.getAvatar())) {
            if (mIsGetAvatar) {
                //如果缓存了图片，直接加载
                Bitmap bitmap = NativeImageLoader.getInstance().getBitmapFromMemCache(mUserInfo.getUserName());
                if (bitmap != null) {
                    Intent intent = new Intent();
                    intent.putExtra("browserAvatar", true);
                    intent.putExtra("avatarPath", mUserInfo.getUserName());
                    intent.setClass(this, BrowserViewPagerActivity.class);
                    startActivity(intent);
                }
            } else {
                final SweetAlertDialog dialog = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
                dialog.setTitleText(getString(R.string.jmui_loading));
                dialog.show();
                mUserInfo.getBigAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int status, String desc, Bitmap bitmap) {
                        if (status == 0) {
                            mIsGetAvatar = true;
                            //缓存头像
                            NativeImageLoader.getInstance().updateBitmapFromCache(mUserInfo.getUserName(), bitmap);
                            Intent intent = new Intent();
                            intent.putExtra("browserAvatar", true);
                            intent.putExtra("avatarPath", mUserInfo.getUserName());
                            intent.setClass(FriendInfoActivity.this, BrowserViewPagerActivity.class);
                            startActivity(intent);
                        } else {
                            HandleResponseCode.onHandle(FriendInfoActivity.this, status, false);
                        }
                        dialog.dismissWithAnimation();
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (resultCode == Constants.RESULT_CODE_EDIT_NOTENAME) {
            mTitle = data.getStringExtra(Constants.NOTENAME);
            mFriendInfoView.setNoteName(mTitle);
            FriendEntry friend = FriendEntry.getFriend(CampusTourApplication.getUserEntry(), mTargetId, mTargetAppKey);
            if (null != friend) {
                friend.displayName = mTitle;
                friend.save();
            }
        }
    }

    //将获得的最新的昵称返回到聊天界面
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Constants.CONV_TITLE, mTitle);
        setResult(Constants.RESULT_CODE_FRIEND_INFO, intent);
        finish();
        super.onBackPressed();
    }

    public int getWidth() {
        return mWidth;
    }

    public void delConvAndReturnMainActivity() {
        Conversation conversation = JMessageClient.getSingleConversation(mTargetId, mTargetAppKey);
        EventBus.getDefault().post(new Event.Builder().setType(EventType.deleteConversation)
                .setConversation(conversation)
                .build());
        JMessageClient.deleteSingleConversation(mTargetId, mTargetAppKey);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    @Override
    public void onGetProviderUserDone(List<User> mUsers) {
        if (mUsers == null)
            return;

        if (mUsers.size() > 0){
            Intent mIntent = new Intent(this, UserActivity.class);
            mIntent.putExtra("provider",mUsers.get(0));
            startActivity(mIntent);

        }
    }

    @Override
    public void onGetProviderUserError(ParseException e) {

    }

    @Override
    public void refreshUserOnlineState(boolean isOnline) {

    }
}
