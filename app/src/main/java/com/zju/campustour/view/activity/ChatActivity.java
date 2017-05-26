package com.zju.campustour.view.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.bean.ChatModel;
import com.zju.campustour.model.bean.ItemModel;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.view.adapter.ChatAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends BaseActivity {

    @BindView(R.id.chat_toolbar)
    Toolbar chatToolBar;

    @BindView(R.id.chat_recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.txt_to_send)
    EditText sendContent;

    @BindView(R.id.btnSend)
    Button sendBtn;

    ParseUser currentUser;
    User talkToUser;


    private ChatAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        Intent mIntent = getIntent();
        talkToUser = (User) mIntent.getSerializableExtra("user");
        if (talkToUser.getId() == null)
            return;
        currentUser = ParseUser.getCurrentUser();
        chatToolBar.setTitle("与"+ talkToUser.getRealName()+"聊天");
        setSupportActionBar(chatToolBar);
        chatToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addListener();

        initRecycleView();
    }

    private void initRecycleView() {

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter = new ChatAdapter());

    }

    private void addListener() {

                //收到消息
                ArrayList<ItemModel> data = new ArrayList<>();

                        String content = "";
                        ChatModel model = new ChatModel();
                        model.setIcon(talkToUser.getImgUrl());
                        model.setContent(content);
                        data.add(new ItemModel(ItemModel.CHAT_A, model));

                adapter.addAll(data);


    }


    @OnClick(R.id.btnSend)
    public void sendMsg(){

        String content = sendContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)){
            showToast("发送的消息不能为空");
            return;
        }
        String toChatUsername = talkToUser.getId();
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此

//如果是群聊，设置chattype，默认是单聊
      /*  if (chatType == CHATTYPE_GROUP)
            message.setChatType(EMMessage.ChatType.GroupChat);*/
//发送消息

        ArrayList<ItemModel> data = new ArrayList<>();
        ChatModel model = new ChatModel();
        model.setIcon(currentUser.getString("imgUrl"));
        model.setContent(content);
        data.add(new ItemModel(ItemModel.CHAT_B, model));
        adapter.addAll(data);
        sendContent.setText("");
        hideKeyBorad(sendContent);
    }

    private void hideKeyBorad(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
