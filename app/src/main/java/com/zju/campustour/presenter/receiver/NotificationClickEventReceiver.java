package com.zju.campustour.presenter.receiver;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zju.campustour.model.common.Constants;
import com.zju.campustour.view.activity.ChatActivity;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.NotificationClickEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;

import static com.zju.campustour.model.common.Constants.TARGET_APP_KEY;
import static com.zju.campustour.model.common.Constants.TARGET_ID;

public class NotificationClickEventReceiver {
    private static final String TAG = NotificationClickEventReceiver.class.getSimpleName();

    private Context mContext;

    public NotificationClickEventReceiver(Context context) {
        mContext = context;
        //注册接收消息事件
        JMessageClient.registerEventReceiver(this);
    }

    /**
     * 收到消息处理
     * @param notificationClickEvent 通知点击事件
     */
    public void onEvent(NotificationClickEvent notificationClickEvent) {
        Log.d(TAG, "[onEvent] NotificationClickEvent !!!!");
        if (null == notificationClickEvent) {
            Log.w(TAG, "[onNotificationClick] message is null");
            return;
        }
        Message msg = notificationClickEvent.getMessage();
        if (msg != null) {
            String targetId = msg.getTargetID();
            String appKey = msg.getFromAppKey();
            ConversationType type = msg.getTargetType();
            Conversation conv;
            Intent notificationIntent = new Intent(mContext, ChatActivity.class);
            if (type == ConversationType.single) {
                conv = JMessageClient.getSingleConversation(targetId, appKey);
                notificationIntent.putExtra(TARGET_ID, targetId);
                notificationIntent.putExtra(TARGET_APP_KEY, appKey);
                Log.d("Notification", "msg.fromAppKey() " + appKey);
            } else {
                conv = JMessageClient.getGroupConversation(Long.parseLong(targetId));
                notificationIntent.putExtra(Constants.GROUP_ID, Long.parseLong(targetId));
            }
            conv.resetUnreadCount();
            Log.d("Notification", "Conversation unread msg reset");
//        notificationIntent.setAction(Intent.ACTION_MAIN);
            notificationIntent.putExtra("fromGroup", false);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(notificationIntent);
        }
    }

}
