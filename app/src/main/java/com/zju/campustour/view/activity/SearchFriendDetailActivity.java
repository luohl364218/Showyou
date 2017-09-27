package com.zju.campustour.view.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.bean.User;
import com.zju.campustour.model.chatting.utils.DialogCreator;
import com.zju.campustour.model.chatting.utils.HandleResponseCode;
import com.zju.campustour.model.chatting.utils.IdHelper;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.presenter.chatting.tools.NativeImageLoader;
import com.zju.campustour.presenter.implement.UserInfoOpPresenterImpl;
import com.zju.campustour.presenter.protocal.enumerate.IdentityType;
import com.zju.campustour.view.chatting.widget.CircleImageView;
import com.zju.campustour.view.iview.ISearchUserInfoView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;

public class SearchFriendDetailActivity extends BaseActivity implements ISearchUserInfoView {

    @BindView(R.id.friend_detail_info_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.nick_name_tv)
    TextView mNickNameTv;
    @BindView(R.id.friend_detail_avatar)
    CircleImageView mAvatarIv;
    @BindView(R.id.gender_iv)
    ImageView mGenderIv;
    @BindView(R.id.gender_tv)
    TextView mGenderTv;
    @BindView(R.id.region_tv)
    TextView mAreaTv;
    @BindView(R.id.signature_tv)
    TextView mSignatureTv;
    @BindView(R.id.add_to_friend)
    Button mAddFriendBtn;
    @BindView(R.id.bt_chat)
    Button mBt_chat;

    private Context mContext;
    private boolean mIsGetAvatar = false;
    private String mUsername;
    private String mAppKey;
    private String mAvatarPath;
    private String mDisplayName;
    private Dialog mDialog;
    private UserInfoOpPresenterImpl mUserInfoOpPresenter;

    private UserInfo mToUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend_detail);
        ButterKnife.bind(this);
        mContext = this;
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mUserInfoOpPresenter = new UserInfoOpPresenterImpl(this,this);
        inModule();


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                switch (view.getId()) {

                    case R.id.add_to_friend:
                        //游客无权建立联系
                        if (currentUser.getInt(Constants.User_identityType) == IdentityType.LOOK_AROUND_USER.getIdentityId()){
                            startModifyIdentityType();
                        }
                        else {
                            Intent intent = new Intent();
                            intent.setClass(mContext, SendInvitationActivity.class);
                            intent.putExtra("targetUsername", mUsername);
                            intent.putExtra(Constants.AVATAR, mAvatarPath);
                            intent.putExtra(Constants.TARGET_APP_KEY, mAppKey);
                            intent.putExtra(Constants.NICKNAME, mDisplayName);
                            startActivity(intent);
                        }
                        break;
                    case R.id.friend_detail_avatar:
                        mUserInfoOpPresenter.queryUserInfoWithUserName(mToUserInfo.getUserName());
                        break;
                    case R.id.bt_chat:
                        //游客无权建立联系
                        if (currentUser.getInt(Constants.User_identityType) == IdentityType.LOOK_AROUND_USER.getIdentityId()){
                            startModifyIdentityType();
                        }
                        else {

                            Intent intentChat = new Intent(SearchFriendDetailActivity.this, ChatActivity.class);
                            String title = mToUserInfo.getNotename();
                            if (TextUtils.isEmpty(title)) {
                                title = mToUserInfo.getNickname();
                                if (TextUtils.isEmpty(title)) {
                                    title = mToUserInfo.getUserName();
                                }
                            }
                            intentChat.putExtra(Constants.CONV_TITLE, title);
                            intentChat.putExtra(Constants.TARGET_ID, mToUserInfo.getUserName());
                            intentChat.putExtra(Constants.TARGET_APP_KEY, mToUserInfo.getAppKey());
                            startActivity(intentChat);
                        }
                        break;
                }
            }
        };

        mAddFriendBtn.setOnClickListener(listener);
        mAvatarIv.setOnClickListener(listener);
        mBt_chat.setOnClickListener(listener);


    }

    private void inModule() {
        final Dialog dialog = DialogCreator.createLoadingDialog(this, this.getString(R.string.jmui_loading));
        dialog.show();
        Intent intent = getIntent();
        mUsername = intent.getStringExtra(Constants.TARGET_ID);
        mAppKey = intent.getStringExtra(Constants.TARGET_APP_KEY);
        JMessageClient.getUserInfo(mUsername, mAppKey, new GetUserInfoCallback() {
            @Override
            public void gotResult(int status, String desc, final UserInfo userInfo) {
                dialog.dismiss();
                if (status == 0) {
                    mToUserInfo = userInfo;
                    Bitmap bitmap = NativeImageLoader.getInstance().getBitmapFromMemCache(mUsername);
                    if (null != bitmap) {
                        mAvatarIv.setImageBitmap(bitmap);
                    } else if (!TextUtils.isEmpty(userInfo.getAvatar())) {
                        mAvatarPath = userInfo.getAvatarFile().getPath();
                        userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                            @Override
                            public void gotResult(int status, String desc, Bitmap bitmap) {
                                if (status == 0) {
                                    mAvatarIv.setImageBitmap(bitmap);
                                    NativeImageLoader.getInstance().updateBitmapFromCache(mUsername, bitmap);
                                } else {
                                    HandleResponseCode.onHandle(mContext, status, false);
                                }
                            }
                        });
                    }

                    mDisplayName = userInfo.getNickname();
                    if (TextUtils.isEmpty(mDisplayName)) {
                        mDisplayName = userInfo.getUserName();
                    }
                    mNickNameTv.setText(mDisplayName);
                    if (userInfo.getGender() == UserInfo.Gender.male) {
                        mGenderTv.setText(mContext.getString(R.string.man));
                        mGenderIv.setImageResource(R.mipmap.sex_man);
                    } else if (userInfo.getGender() == UserInfo.Gender.female) {
                        mGenderTv.setText(mContext.getString(R.string.woman));
                        mGenderIv.setImageResource(R.mipmap.sex_woman);
                    } else {
                        mGenderTv.setText(mContext.getString(R.string.unknown));
                    }
                    mAreaTv.setText(userInfo.getRegion());
                    mSignatureTv.setText(userInfo.getSignature());
                } else {
                    HandleResponseCode.onHandle(mContext, status, false);
                }
            }
        });
    }

    private void startBrowserAvatar() {
        if (null != mAvatarPath) {
            if (mIsGetAvatar) {
                //如果缓存了图片，直接加载
                Bitmap bitmap = NativeImageLoader.getInstance().getBitmapFromMemCache(mUsername);
                if (bitmap != null) {
                    Intent intent = new Intent();
                    intent.putExtra("browserAvatar", true);
                    intent.putExtra("avatarPath", mUsername);
                    intent.setClass(this, BrowserViewPagerActivity.class);
                    startActivity(intent);
                }
            } else {
                final Dialog dialog = DialogCreator.createLoadingDialog(this, this.getString(R.string.jmui_loading));
                dialog.show();
                JMessageClient.getUserInfo(mUsername, new GetUserInfoCallback() {
                    @Override
                    public void gotResult(int status, String desc, UserInfo userInfo) {
                        if (status == 0) {
                            userInfo.getBigAvatarBitmap(new GetAvatarBitmapCallback() {
                                @Override
                                public void gotResult(int status, String desc, Bitmap bitmap) {
                                    if (status == 0) {
                                        mIsGetAvatar = true;
                                        //缓存头像
                                        NativeImageLoader.getInstance().updateBitmapFromCache(mUsername, bitmap);
                                        Intent intent = new Intent();
                                        intent.putExtra("browserAvatar", true);
                                        intent.putExtra("avatarPath", mUsername);
                                        intent.setClass(mContext, BrowserViewPagerActivity.class);
                                        startActivity(intent);
                                    } else {
                                        HandleResponseCode.onHandle(mContext, status, false);
                                    }
                                    dialog.dismiss();
                                }
                            });
                        } else {
                            dialog.dismiss();
                            HandleResponseCode.onHandle(mContext, status, false);
                        }
                    }
                });
            }
        }
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

    public void startModifyIdentityType(){
        //弹出一个确认框
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.jmui_cancel_btn:
                        mDialog.cancel();
                        break;
                    case R.id.jmui_commit_btn:

                        startActivity(new Intent(SearchFriendDetailActivity.this,IdentityConfirmActivity.class));

                        mDialog.cancel();
                        break;
                }
            }
        };
        mDialog = createConfirmDialog(mContext, listener);
        mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.show();

        //
    }

    public Dialog createConfirmDialog(Context context, View.OnClickListener listener){
        Dialog dialog = new Dialog(context, IdHelper.getStyle(context, "jmui_default_dialog_style"));
        View view = LayoutInflater.from(context).inflate(IdHelper.getLayout(context,
                "jmui_dialog_base_with_button_two"), null);
        dialog.setContentView(view);
        TextView title = (TextView) view.findViewById(IdHelper.getViewID(context, "jmui_title"));
        title.setText(IdHelper.getString(context, "look_around_hint"));
        final Button cancel = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_cancel_btn"));
        final Button commit = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_commit_btn"));
        cancel.setOnClickListener(listener);
        commit.setOnClickListener(listener);
        commit.setText(IdHelper.getString(context, "jmui_confirm"));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
}
