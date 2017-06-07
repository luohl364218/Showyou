package com.zju.campustour.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.view.chatting.adapter.AtMemberAdapter;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;

public class AtMemberActivity extends BaseActivity {

    private List<UserInfo> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_at_member);
        ListView listView = (ListView) findViewById(R.id.at_member_list_view);
        TextView title = (TextView) findViewById(R.id.jmui_title_tv);
        ImageButton returnBtn = (ImageButton) findViewById(R.id.return_btn);
        Button rightBtn = (Button) findViewById(R.id.jmui_commit_btn);

        title.setText(this.getString(R.string.select_member_to_reply));
        rightBtn.setVisibility(View.GONE);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        long groupId = getIntent().getLongExtra(Constants.GROUP_ID, 0);
        if (0 != groupId) {
            Conversation conv = JMessageClient.getGroupConversation(groupId);
            GroupInfo groupInfo = (GroupInfo) conv.getTargetInfo();
            mList = groupInfo.getGroupMembers();
            mList.remove(JMessageClient.getMyInfo());
            AtMemberAdapter adapter = new AtMemberAdapter(this, mList);
            listView.setAdapter(adapter);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                UserInfo userInfo = mList.get(position);
                Intent intent = new Intent();
                intent.putExtra(Constants.NAME, userInfo.getNickname());
                intent.putExtra(Constants.TARGET_ID, userInfo.getUserName());
                intent.putExtra(Constants.TARGET_APP_KEY, userInfo.getAppKey());
                setResult(Constants.RESULT_CODE_AT_MEMBER, intent);
                finish();
            }
        });
    }
}
