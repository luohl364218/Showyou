package com.zju.campustour.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zju.campustour.R;
import com.zju.campustour.model.chatting.entity.Event;
import com.zju.campustour.model.chatting.entity.EventType;
import com.zju.campustour.model.chatting.entity.FriendInvitation;
import com.zju.campustour.model.chatting.utils.HandleResponseCode;
import com.zju.campustour.model.bean.FriendEntry;
import com.zju.campustour.model.bean.FriendRecommendEntry;
import com.zju.campustour.model.bean.UserEntry;
import com.zju.campustour.model.util.SharePreferenceManager;
import com.zju.campustour.presenter.chatting.controller.ContactsController;
import com.zju.campustour.presenter.chatting.tools.HanziToPinyin;
import com.zju.campustour.presenter.protocal.event.UnreadFriendVerifyNum;
import com.zju.campustour.view.application.CampusTourApplication;
import com.zju.campustour.view.chatting.ContactsView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.android.api.model.UserInfo;


public class ContactsFragment extends BaseFragment {
    private View mRootView;
    private ContactsView mContactsView;
    private ContactsController mContactsController;
    private Activity mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mRootView == null){
            mRootView = inflater.inflate(R.layout.fragment_contacts, container, false);
            mContext = this.getActivity();
            EventBus.getDefault().register(this);
            mContactsView = (ContactsView) mRootView.findViewById(R.id.contacts_view);
            mContactsView.initModule(mRatio, mDensity);
            mContactsController = new ContactsController(mContactsView, this.getActivity());
            mContactsView.setOnClickListener(mContactsController);
            mContactsView.setListeners(mContactsController);
            mContactsView.setSideBarTouchListener(mContactsController);
            mContactsView.setTextWatcher(mContactsController);
            mContactsController.initContacts();

        }

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null){
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mContactsView.showContact();
        mContactsController.refreshContact();
    }

    /**
     * 接收到好友相关事件
     *
     * @param event ContactNotifyEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onEvent(ContactNotifyEvent event) {
        final UserEntry user = CampusTourApplication.getUserEntry();
        final String reason = event.getReason();
        final String username = event.getFromUsername();
        final String appKey = event.getfromUserAppKey();
        //收到接受好友请求
        if (event.getType() == ContactNotifyEvent.Type.invite_accepted) {
            //add friend to contact
            JMessageClient.getUserInfo(username, appKey, new GetUserInfoCallback() {
                @Override
                public void gotResult(int status, String desc, final UserInfo userInfo) {
                    if (status == 0) {
                        String name = userInfo.getNickname();
                        if (TextUtils.isEmpty(name)) {
                            name = userInfo.getUserName();
                        }

                        FriendEntry friendEntry = FriendEntry.getFriend(user, username, appKey);
                        if (null == friendEntry) {
                            final FriendEntry newFriend = new FriendEntry(username, appKey,
                                    userInfo.getAvatar(), name, getLetter(name), user);
                            newFriend.save();
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mContactsController.refresh(newFriend);
                                }
                            });
                        }
                    }
                }
            });
            FriendRecommendEntry entry = FriendRecommendEntry.getEntry(user, username, appKey);
            Log.d("ContactsFragment", "entry " + entry);
            entry.state = FriendInvitation.ACCEPTED.getValue();
            entry.save();
            //拒绝好友请求
        } else if (event.getType() == ContactNotifyEvent.Type.invite_declined) {
            FriendRecommendEntry entry = FriendRecommendEntry.getEntry(user, username, appKey);
            entry.state = FriendInvitation.BE_REFUSED.getValue();
            entry.reason = reason;
            entry.save();
            //收到好友邀请事件
        } else if (event.getType() == ContactNotifyEvent.Type.invite_received) {
            JMessageClient.getUserInfo(username, appKey, new GetUserInfoCallback() {
                @Override
                public void gotResult(int status, String desc, UserInfo userInfo) {
                    if (status == 0) {
                        String name = userInfo.getNickname();
                        if (TextUtils.isEmpty(name)) {
                            name = userInfo.getUserName();
                        }
                        FriendRecommendEntry entry = FriendRecommendEntry.getEntry(user, username, appKey);
                        if (null == entry) {
                            if (null != userInfo.getAvatar()) {
                                String path = userInfo.getAvatarFile().getPath();
                                entry = new FriendRecommendEntry(username, appKey, path,
                                        name, reason, FriendInvitation.INVITED.getValue(), user);
                            } else {
                                entry = new FriendRecommendEntry(username, appKey, null,
                                        username, reason, FriendInvitation.INVITED.getValue(), user);
                            }
                        } else {
                            entry.state = FriendInvitation.INVITED.getValue();
                            entry.reason = reason;
                        }
                        entry.save();
                        int showNum = SharePreferenceManager.getCachedNewFriendNum(mContext) + 1;
                        mContactsView.showNewFriends(showNum);
                        //验证消息数量
                        EventBus.getDefault().post(new UnreadFriendVerifyNum(showNum));
                        SharePreferenceManager.setCachedNewFriendNum(mContext,showNum);
                    } else {
                        HandleResponseCode.onHandle(mContext, status, false);
                    }
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onEventMainThread(Event event) {
        if (event.getType() == EventType.addFriend) {
            FriendRecommendEntry recommendEntry = FriendRecommendEntry.getEntry(event.getFriendId());
            if (null != recommendEntry) {
                FriendEntry friendEntry = FriendEntry.getFriend(recommendEntry.user,
                        recommendEntry.username, recommendEntry.appKey);
                if (null == friendEntry) {
                    friendEntry = new FriendEntry(recommendEntry.username, recommendEntry.appKey,
                            recommendEntry.avatar, recommendEntry.displayName,
                            getLetter(recommendEntry.displayName), recommendEntry.user);
                    friendEntry.save();
                    mContactsController.refresh(friendEntry);
                }
            }
        }
    }

    private String getLetter(String name) {
        String letter;
        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance()
                .get(name);
        StringBuilder sb = new StringBuilder();
        if (tokens != null && tokens.size() > 0) {
            for (HanziToPinyin.Token token : tokens) {
                if (token.type == HanziToPinyin.Token.PINYIN) {
                    sb.append(token.target);
                } else {
                    sb.append(token.source);
                }
            }
        }
        String sortString = sb.toString().substring(0, 1).toUpperCase();
        if (sortString.matches("[A-Z]")) {
            letter = sortString.toUpperCase();
        } else {
            letter = "#";
        }
        return letter;
    }
}