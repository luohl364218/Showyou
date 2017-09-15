package com.zju.campustour.presenter.chatting.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.activeandroid.ActiveAndroid;
import com.zju.campustour.R;
import com.zju.campustour.model.chatting.utils.HandleResponseCode;
import com.zju.campustour.model.bean.FriendEntry;
import com.zju.campustour.model.bean.UserEntry;
import com.zju.campustour.presenter.chatting.tools.HanziToPinyin;
import com.zju.campustour.presenter.chatting.tools.PinyinComparator;
import com.zju.campustour.presenter.protocal.event.UnreadFriendVerifyNum;
import com.zju.campustour.view.activity.FriendRecommendActivity;
import com.zju.campustour.view.chatting.ContactsView;
import com.zju.campustour.view.chatting.adapter.StickyListAdapter;
import com.zju.campustour.view.chatting.widget.SideBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.model.UserInfo;


public class ContactsController implements OnClickListener, SideBar.OnTouchingLetterChangedListener,
        TextWatcher {

    private ContactsView mContactsView;
    private Activity mContext;
    private List<FriendEntry> mList = new ArrayList<FriendEntry>();
    private StickyListAdapter mAdapter;
    private UserInfo mUserInfo;

    public ContactsController(ContactsView mContactsView, Activity context) {
        this.mContactsView = mContactsView;
        this.mContext = context;
    }

    public void initContacts() {
        final UserEntry user = UserEntry.getUser(JMessageClient.getMyInfo().getUserName(),
                JMessageClient.getMyInfo().getAppKey());
        List<FriendEntry> friends = user.getFriends();
        mContactsView.showLoadingHeader();
        ContactManager.getFriendList(new GetUserInfoListCallback() {
            @Override
            public void gotResult(int status, String desc, List<UserInfo> list) {
                if (status == 0) {
                    if (list.size() != 0) {
                        ActiveAndroid.beginTransaction();
                        try {
                            for (UserInfo userInfo : list) {
                                mUserInfo = userInfo;
                                String displayName = userInfo.getNotename();
                                if (TextUtils.isEmpty(displayName)) {
                                    displayName = userInfo.getNickname();
                                    if (TextUtils.isEmpty(displayName)) {
                                        displayName = userInfo.getUserName();
                                    }
                                }
                                String letter;
                                ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance()
                                        .get(displayName);
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
                                //避免重复请求时导致数据重复
                                FriendEntry friend = FriendEntry.getFriend(user,
                                        userInfo.getUserName(), userInfo.getAppKey());
                                if (null == friend) {
                                    if (TextUtils.isEmpty(userInfo.getAvatar())) {
                                        friend = new FriendEntry(userInfo.getUserName(), userInfo.getAppKey(),
                                                null, displayName, letter, user);
                                    } else {
                                        friend = new FriendEntry(userInfo.getUserName(), userInfo.getAppKey(),
                                                userInfo.getAvatarFile().getAbsolutePath(), displayName, letter, user);
                                    }
                                    friend.save();
                                    mList.add(friend);
                                }
                            }
                            ActiveAndroid.setTransactionSuccessful();
                        } finally {
                            ActiveAndroid.endTransaction();
                        }
                    }
                    mContactsView.dismissLoadingHeader();
                    Collections.sort(mList, new PinyinComparator());
                    mAdapter = new StickyListAdapter(mContext, mList, false);
                    mContactsView.setAdapter(mAdapter);
                } else {
                    mContactsView.dismissLoadingHeader();
                    HandleResponseCode.onHandle(mContext, status, false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.verify_rl:
                intent.setClass(mContext, FriendRecommendActivity.class);
                mContext.startActivity(intent);
                mContactsView.dismissNewFriends();
                //验证消息数量
                EventBus.getDefault().post(new UnreadFriendVerifyNum(0));
                break;
            case R.id.group_chat_rl:
                break;
            case R.id.return_btn:
                mContext.finish();
                break;

        }

    }

    public void refresh(FriendEntry entry) {
        mList.add(entry);
        if (null == mAdapter) {
            mAdapter = new StickyListAdapter(mContext, mList, false);
        } else {
            Collections.sort(mList, new PinyinComparator());
        }
        mAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onTouchingLetterChanged(String s) {
        //该字母首次出现的位置
        if (null != mAdapter) {
            int position = mAdapter.getSectionForLetter(s);
            Log.d("SelectFriendController", "Section position: " + position);
            if (position != -1 && position < mAdapter.getCount()) {
                mContactsView.setSelection(position);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        filterData(s.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<FriendEntry> filterDateList = new ArrayList<FriendEntry>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = mList;
        } else {
            filterDateList.clear();
            for (FriendEntry entry : mList) {
                String name = entry.displayName;
                if (name.contains(filterStr) || name.startsWith(filterStr)
                        || entry.letter.equals(filterStr.substring(0, 1).toUpperCase())) {
                    filterDateList.add(entry);
                }
            }
        }

        // 根据a-z进行排序
        if (null != filterDateList && null != mAdapter) {
            Collections.sort(filterDateList, new PinyinComparator());
            mAdapter.updateListView(filterDateList);
        }
    }

    public void refreshContact() {
        final UserEntry user = UserEntry.getUser(JMessageClient.getMyInfo().getUserName(),
                JMessageClient.getMyInfo().getAppKey());
        mList = user.getFriends();
        for (final FriendEntry en : mList) {
            JMessageClient.getUserInfo(en.username, new GetUserInfoCallback() {
                @Override
                public void gotResult(int i, String s, UserInfo userInfo) {
                    if (userInfo.isFriend()) {
                        //是好友
                    } else {
                        //不是好友
                        FriendEntry entry = FriendEntry.getFriend(user, userInfo.getUserName(), userInfo.getAppKey());
                        entry.delete();
                        UserEntry user = UserEntry.getUser(JMessageClient.getMyInfo().getUserName(),
                                JMessageClient.getMyInfo().getAppKey());
                        mList = user.getFriends();
                    }
                }
            });
        }
        Collections.sort(mList, new PinyinComparator());
        mAdapter = new StickyListAdapter(mContext, mList, false);
        mContactsView.setAdapter(mAdapter);
    }
}
