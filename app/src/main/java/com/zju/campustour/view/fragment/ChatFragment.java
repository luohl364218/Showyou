package com.zju.campustour.view.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.zju.campustour.R;
import com.zju.campustour.model.chatting.entity.Event;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.chatting.utils.HandleResponseCode;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.chatting.controller.ConversationListController;
import com.zju.campustour.presenter.protocal.event.NetworkChangeEvent;
import com.zju.campustour.view.activity.ChatActivity;
import com.zju.campustour.view.activity.ContactsActivity;
import com.zju.campustour.view.activity.SearchActivity;
import com.zju.campustour.view.application.CampusTourApplication;
import com.zju.campustour.view.chatting.ConversationListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.CreateGroupCallback;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.ConversationRefreshEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by HeyLink on 2017/5/28.
 */

public class ChatFragment extends BaseFragment {

    private String TAG = "ChatFragment";
    private View mRootView;
    private ConversationListView mConvListView;
    private Toolbar mToolbar;
    private HandlerThread mThread;
    private BackgroundHandler mBackgroundHandler;
    private ConversationListController mConvListController;
    private MaterialRefreshLayout mMaterialRefreshLayout;
    private Activity mContext;
    private static final int REFRESH_CONVERSATION_LIST = 0x3000;
    private static final int DISMISS_REFRESH_HEADER = 0x3001;
    private static final int ROAM_COMPLETED = 0x3002;

    public ChatFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);//加上这句话，menu才会显示出来
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null){
            mRootView = inflater.inflate(R.layout.fragment_chat, container, false);
            EventBus.getDefault().register(this);
            initFragmentView();
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
        mConvListController.getAdapter().notifyDataSetChanged();
        if (mToolbar != null)
            mToolbar.setTitle("");
        //todo 更新bottombar上面的数字
       /* EventBus.getDefault().post(new UnreadMsgEvent(mConvListController.getAdapter().getUnreadMsg()));
        mConvListController.getAdapter().clearUnreadMsg();*/
    }



    private void initFragmentView() {
        mContext = this.getActivity();
        mToolbar = (Toolbar) mRootView.findViewById(R.id.fragment_chat_toolbar);
        mMaterialRefreshLayout = (MaterialRefreshLayout) mRootView.findViewById(R.id.refresh_view);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        mToolbar.inflateMenu(R.menu.chat_fragment_menu);
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.mipmap.icon_contact);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ContactsActivity.class));
            }
        });

        mConvListView = new ConversationListView(mRootView, this.getActivity());
        mConvListView.initModule();

        mThread = new HandlerThread("Work on MainActivity");
        mThread.start();

        mBackgroundHandler = new BackgroundHandler(mThread.getLooper());
        mConvListController = new ConversationListController(mConvListView, this, mWidth);
        mConvListView.setItemListeners(mConvListController);
        mConvListView.setLongClickListener(mConvListController);

        initRefreshLayout();

        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            mConvListView.showHeaderView();
        } else {
            mConvListView.dismissHeaderView();
            mConvListView.showLoadingHeader();
            mBackgroundHandler.sendEmptyMessageDelayed(DISMISS_REFRESH_HEADER, 1000);
        }

    }

    private void initRefreshLayout() {

        mMaterialRefreshLayout.setLoadMore(false);
        mMaterialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                //下拉刷新...
                isNetworkUseful = NetworkUtil.isNetworkAvailable(getContext());
                if (isNetworkUseful){
                    mConvListController.refreshConvListAdapter();

                }

                mMaterialRefreshLayout.finishRefresh();

            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onNetworkChangeEvent(NetworkChangeEvent event) {
        if (NetworkUtil.isNetworkAvailable(mContext)) {
            mConvListView.dismissHeaderView();
        }
        else{
            mConvListView.showHeaderView();

            mContext.runOnUiThread(()->{
                int count = 0;
                while (count < 10){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException mE) {
                        mE.printStackTrace();
                    }
                    if (NetworkUtil.isNetworkAvailable(mContext) ){

                        mConvListView.dismissHeaderView();
                        break;
                    }
                    count++;
                }
            });
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater mInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        mInflater.inflate(R.menu.chat_fragment_menu, menu);

        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.chat_toolbar_talk:
                /*创建群聊*/
                SweetAlertDialog mSweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
                mSweetAlertDialog.setTitleText(mContext.getString(R.string.creating_hint));
                mSweetAlertDialog.show();
                JMessageClient.createGroup("", "", new CreateGroupCallback() {

                    @Override
                    public void gotResult(final int status, String msg, final long groupId) {
                        mSweetAlertDialog.dismissWithAnimation();
                        if (status == 0) {
                            Conversation conv = Conversation.createGroupConversation(groupId);
                            mConvListController.getAdapter().setToTop(conv);
                            Intent intent = new Intent();
                            //设置跳转标志
                            intent.putExtra("fromGroup", true);
                            intent.putExtra(Constants.MEMBERS_COUNT, 1);
                            intent.putExtra(Constants.GROUP_ID, groupId);
                            intent.setClass(mContext, ChatActivity.class);
                            mContext.startActivity(intent);
                        } else {
                            HandleResponseCode.onHandle(mContext, status, false);
                            Log.i("CreateGroupController", "status : " + status);
                        }
                    }
                });

                break;
            case R.id.chat_toolbar_add_friend:
                Intent intent = new Intent(mContext, SearchActivity.class);
                mContext.startActivity(intent);
                break;
            default:
        }
        return true;
    }


    private class BackgroundHandler extends Handler {
        public BackgroundHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_CONVERSATION_LIST:
                    Conversation conv = (Conversation) msg.obj;
                    mConvListController.getAdapter().setToTop(conv);

                    break;
                case DISMISS_REFRESH_HEADER:
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mConvListView.dismissLoadingHeader();
                        }
                    });
                    break;
                case ROAM_COMPLETED:
                    conv = (Conversation) msg.obj;
                    mConvListController.getAdapter().addAndSort(conv);

                    break;
            }
        }
    }


    /**
     * 在会话列表中接收在线消息
     *
     * @param event 消息事件
     */
    public void onEvent(MessageEvent event) {
        Message msg = event.getMessage();
        Log.d(TAG, "收到消息：msg = " + msg.toString());
        ConversationType convType = msg.getTargetType();
        if (convType == ConversationType.group) {
            long groupID = ((GroupInfo) msg.getTargetInfo()).getGroupID();
            Conversation conv = JMessageClient.getGroupConversation(groupID);
            if (conv != null && mConvListController != null) {
                if (msg.isAtMe()) {
                    CampusTourApplication.isNeedAtMsg = true;
                    mConvListController.getAdapter().putAtConv(conv, msg.getId());
                }
                mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST,
                        conv));
            }
        } else {
            final UserInfo userInfo = (UserInfo) msg.getTargetInfo();
            final String targetId = userInfo.getUserName();
            final Conversation conv = JMessageClient.getSingleConversation(targetId, userInfo.getAppKey());
            if (conv != null && mConvListController != null) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //如果设置了头像
                        if (!TextUtils.isEmpty(userInfo.getAvatar())) {
                            //如果本地不存在头像
                            userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                                @Override
                                public void gotResult(int status, String desc, Bitmap bitmap) {
                                    if (status == 0) {

                                        mConvListController.getAdapter().notifyDataSetChanged();
                                    } else {
                                        HandleResponseCode.onHandle(mContext, status, false);
                                    }
                                }
                            });
                        }
                    }
                });
                mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST,
                        conv));
            }
        }
    }

    /**
     * 接收离线消息
     * @param event 离线消息事件
     */
    public void onEvent(OfflineMessageEvent event) {
        Conversation conv = event.getConversation();
        mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST, conv));
    }

    /**
     * 消息漫游完成事件
     * @param event 漫游完成后， 刷新会话事件
     */
    public void onEvent(ConversationRefreshEvent event) {
        Conversation conv = event.getConversation();
        mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST, conv));
    }


    public void onEventMainThread(Event event) {
        switch (event.getType()) {
            case createConversation:
                Conversation conv = event.getConversation();
                if (conv != null) {
                    mConvListController.getAdapter().addNewConversation(conv);
                }
                break;
            case deleteConversation:
                conv = event.getConversation();
                if (null != conv) {
                    mConvListController.getAdapter().deleteConversation(conv);
                }
                break;
            //收到保存为草稿事件
            case draft:
                conv = event.getConversation();
                String draft = event.getDraft();
                //如果草稿内容不为空，保存，并且置顶该会话
                if (!TextUtils.isEmpty(draft)) {
                    mConvListController.getAdapter().putDraftToMap(conv, draft);
                    mConvListController.getAdapter().setToTop(conv);
                    //否则删除
                } else {
                    mConvListController.getAdapter().delDraftFromMap(conv);
                }
                break;
            case addFriend:
                break;
        }
    }

    public void sortConvList() {
        if (mConvListController != null) {
            mConvListController.getAdapter().sortConvList();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mBackgroundHandler.removeCallbacksAndMessages(null);
        mThread.getLooper().quit();
        super.onDestroy();
    }
}
