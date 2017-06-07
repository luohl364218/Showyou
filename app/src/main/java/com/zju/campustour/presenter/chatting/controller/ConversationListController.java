package com.zju.campustour.presenter.chatting.controller;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.chatting.utils.DialogCreator;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.chatting.tools.SortConvList;
import com.zju.campustour.view.activity.ChatActivity;
import com.zju.campustour.view.chatting.adapter.ConversationListAdapter;
import com.zju.campustour.view.fragment.ChatFragment;
import com.zju.campustour.view.chatting.ConversationListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;


public class ConversationListController implements OnClickListener,
        OnItemClickListener, OnItemLongClickListener {

    private ConversationListView mConvListView;
    private ChatFragment mContext;
    private List<Conversation> mDatas = new ArrayList<Conversation>();
    private ConversationListAdapter mListAdapter;
    private int mWidth;
    private Dialog mDialog;
    private Toast mToast;

    public ConversationListController(ConversationListView listView, ChatFragment context,
                                      int width) {
        this.mConvListView = listView;
        this.mContext = context;
        this.mWidth = width;
        initConvListAdapter();
    }

    // 得到会话列表
    private void initConvListAdapter() {
        mDatas = JMessageClient.getConversationList();
        if (mDatas == null)
            return;
        //对会话列表进行时间排序
        if (mDatas.size() > 1) {
            SortConvList sortList = new SortConvList();
            Collections.sort(mDatas, sortList);
        }

        mListAdapter = new ConversationListAdapter(mContext.getActivity(), mDatas);
        mConvListView.setConvListAdapter(mListAdapter);
        mConvListView.setHeaderListener(this);
    }

    @Override
    public void onClick(View v) {
       //没有自定义的menu了，不需要捕捉点击事件
        switch (v.getId()){
            case R.id.conv_list_header:
                if (NetworkUtil.isNetworkAvailable(mContext.getContext())){
                    mConvListView.dismissHeaderView();
                    showToast(mContext.getContext(),"网络连接恢复");
                }
                else
                    showToast(mContext.getContext(),"网络连接不可用");

                break;
        }

    }

    // 点击会话列表
    @Override
    public void onItemClick(AdapterView<?> viewAdapter, View view, int position, long id) {
        // TODO Auto-generated method stub
        final Intent intent = new Intent();
        try {
            if (position > 0) {
                // 减去 header view 个数
                Conversation conv = mDatas.get(position - 2);
                intent.putExtra(Constants.CONV_TITLE, conv.getTitle());
                if (null != conv) {
                    // 当前点击的会话是否为群组
                    if (conv.getType() == ConversationType.group) {
                        if (mListAdapter.includeAtMsg(conv)) {
                            intent.putExtra("atMsgId", mListAdapter.getAtMsgId(conv));
                        }
                        long groupId = ((GroupInfo) conv.getTargetInfo()).getGroupID();
                        intent.putExtra(Constants.GROUP_ID, groupId);
                        intent.putExtra(Constants.DRAFT, getAdapter().getDraft(conv.getId()));
                        intent.setClass(mContext.getActivity(), ChatActivity.class);
                        mContext.getActivity().startActivity(intent);
                        return;
                    } else {
                        String targetId = ((UserInfo) conv.getTargetInfo()).getUserName();
                        intent.putExtra(Constants.TARGET_ID, targetId);
                        intent.putExtra(Constants.TARGET_APP_KEY, conv.getTargetAppKey());
                        Log.d("ConversationList", "Target app key from conversation: " + conv.getTargetAppKey());
                        intent.putExtra(Constants.DRAFT, getAdapter().getDraft(conv.getId()));
                    }
                    intent.setClass(mContext.getActivity(), ChatActivity.class);
                    mContext.getActivity().startActivity(intent);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> viewAdapter, View view, final int position, long id) {
        if (position > 0) {
            try {
                final Conversation conv = mDatas.get(position - 2);
                if (conv != null) {
                    OnClickListener listener = new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (conv.getType() == ConversationType.group) {
                                JMessageClient.deleteGroupConversation(((GroupInfo) conv.getTargetInfo())
                                        .getGroupID());
                            } else {
                                //使用带AppKey的接口,可以删除跨/非跨应用的会话(如果不是跨应用,conv拿到的AppKey则是默认的)
                                JMessageClient.deleteSingleConversation(((UserInfo) conv.getTargetInfo())
                                        .getUserName(), conv.getTargetAppKey());
                            }
                            mDatas.remove(position - 2);
                            mListAdapter.notifyDataSetChanged();
                            mDialog.dismiss();
                        }
                    };
                    mDialog = DialogCreator.createDelConversationDialog(mContext.getActivity(), conv.getTitle(),
                            listener);
                    mDialog.show();
                    mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public ConversationListAdapter getAdapter() {
        return mListAdapter;
    }

    public void showToast(Context mContext, String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(mContext, text,
                        Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.show();
        }
    }
}
