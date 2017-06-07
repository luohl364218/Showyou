package com.zju.campustour.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.zju.campustour.R;
import com.zju.campustour.model.database.models.FriendRecommendEntry;
import com.zju.campustour.model.database.models.UserEntry;
import com.zju.campustour.model.util.SharePreferenceManager;
import com.zju.campustour.view.application.CampusTourApplication;
import com.zju.campustour.view.chatting.adapter.FriendRecommendAdapter;

import java.util.List;

public class FriendRecommendActivity extends BaseActivity {

    private ImageButton mReturnBtn;
    private TextView mTitle;
    private ImageButton mClearBtn;
    private FriendRecommendAdapter mAdapter;
    private ListView mListView;
    private List<FriendRecommendEntry> mList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_recommend);
        final Context context = this;
        mReturnBtn = (ImageButton) findViewById(R.id.return_btn);
        mTitle = (TextView) findViewById(R.id.title);
        mTitle.setText(this.getString(R.string.friend_verify_title));
        mClearBtn = (ImageButton) findViewById(R.id.right_btn);
        mClearBtn.setImageDrawable(this.getResources().getDrawable(R.mipmap.delete));
        mListView = (ListView) findViewById(R.id.friend_recommend_list_view);


        UserEntry user = CampusTourApplication.getUserEntry();
        if (null != user) {
            mList = user.getRecommends();
            mAdapter = new FriendRecommendAdapter(this, mList, mDensity, mWidth);
            mListView.setAdapter(mAdapter);
        } else {
            Log.e("FriendRecommendActivity", "Unexpected error: User table null");
        }

        mReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendRecommendActivity.this.finish();
            }
        });

        mClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActiveAndroid.beginTransaction();
                try {
                    for (FriendRecommendEntry entry : mList) {
                        entry.delete();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
                mAdapter.clearAll();
            }
        });

        SharePreferenceManager.setCachedNewFriendNum(this,0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
}
