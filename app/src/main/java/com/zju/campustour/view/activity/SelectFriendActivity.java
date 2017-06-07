package com.zju.campustour.view.activity;

import android.os.Bundle;

import com.zju.campustour.R;
import com.zju.campustour.presenter.chatting.controller.SelectFriendController;
import com.zju.campustour.view.chatting.SelectFriendView;

public class SelectFriendActivity extends BaseActivity {

    private SelectFriendView mView;
    private SelectFriendController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend);
        mView = (SelectFriendView) findViewById(R.id.select_friend_view);
        mView.initModule(mRatio, mDensity);
        mController = new SelectFriendController(mView, this);
        mView.setListeners(mController);
        mView.setSideBarTouchListener(mController);
        mView.setTextWatcher(mController);
    }
}
