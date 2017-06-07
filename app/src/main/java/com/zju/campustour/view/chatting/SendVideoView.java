package com.zju.campustour.view.chatting;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.zju.campustour.R;
import com.zju.campustour.view.chatting.adapter.VideoAdapter;


public class SendVideoView extends LinearLayout {

    private ListView mListView;

    public SendVideoView(Context context) {
        super(context);
    }

    public SendVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initModule() {
        mListView = (ListView) findViewById(R.id.video_list_view);
    }

    public void setAdapter(VideoAdapter adapter) {
        mListView.setAdapter(adapter);
    }
}
