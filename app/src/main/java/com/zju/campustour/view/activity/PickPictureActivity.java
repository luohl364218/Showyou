package com.zju.campustour.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

import com.zju.campustour.R;
import com.zju.campustour.model.chatting.utils.BitmapLoader;
import com.zju.campustour.model.chatting.utils.HandleResponseCode;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.view.chatting.adapter.PickPictureAdapter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;

public class PickPictureActivity extends BaseActivity {

    private GridView mGridView;
    //此相册下所有图片的路径集合
    private List<String> mList;
    //选中图片的路径集合
    private List<String> mPickedList;
    private Button mSendPictureBtn;
    private ImageButton mReturnBtn;
    private boolean mIsGroup = false;
    private PickPictureAdapter mAdapter;
    private String mTargetId;
    private String mTargetAppKey;
    private Conversation mConv;
    private ProgressDialog mDialog;
    private long mGroupId;
    private int[] mMsgIds;
    private static final int SEND_PICTURE = 200;
    private final MyHandler myHandler = new MyHandler(this);
    private int mIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_picture);
        mSendPictureBtn = (Button) findViewById(R.id.pick_picture_send_btn);
        mReturnBtn = (ImageButton) findViewById(R.id.return_btn);
        mGridView = (GridView) findViewById(R.id.child_grid);

        Intent intent = this.getIntent();
        mGroupId = intent.getLongExtra(Constants.GROUP_ID, 0);
        if (mGroupId != 0) {
            mIsGroup = true;
            mConv = JMessageClient.getGroupConversation(mGroupId);
        } else {
            mTargetId = intent.getStringExtra(Constants.TARGET_ID);
            mTargetAppKey = intent.getStringExtra(Constants.TARGET_APP_KEY);
            mConv = JMessageClient.getSingleConversation(mTargetId, mTargetAppKey);
        }
        mList = intent.getStringArrayListExtra("data");
        mAdapter = new PickPictureAdapter(this, mList, mGridView, mDensity);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(onItemListener);
        mSendPictureBtn.setOnClickListener(listener);
        mReturnBtn.setOnClickListener(listener);
    }


    private AdapterView.OnItemClickListener onItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> viewAdapter, View view, int position, long id) {
            Intent intent = new Intent();
            intent.putExtra("fromChatActivity", false);
            if (mIsGroup) {
                intent.putExtra(Constants.GROUP_ID, mGroupId);
            } else {
                intent.putExtra(Constants.TARGET_ID, mTargetId);
                intent.putExtra(Constants.TARGET_APP_KEY, mTargetAppKey);
            }
            intent.putStringArrayListExtra("pathList", (ArrayList<String>) mList);
            intent.putExtra(Constants.POSITION, position);
            intent.putExtra("pathArray", mAdapter.getSelectedArray());
            intent.setClass(PickPictureActivity.this, BrowserViewPagerActivity.class);
            startActivityForResult(intent, Constants.REQUEST_CODE_BROWSER_PICTURE);
        }
    };

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //点击发送按钮，发送选中的图片
                case R.id.pick_picture_send_btn:
                    //存放选中图片的路径
                    mPickedList = new ArrayList<String>();
                    //存放选中的图片的position
                    List<Integer> positionList;
                    positionList = mAdapter.getSelectItems();
                    //拿到选中图片的路径
                    for (int i = 0; i < positionList.size(); i++) {
                        mPickedList.add(mList.get(positionList.get(i)));
                    }
                    if (mPickedList.size() < 1) {
                        return;
                    } else {
                        mDialog = new ProgressDialog(PickPictureActivity.this);
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.setCancelable(false);
                        mDialog.setMessage(PickPictureActivity.this.getString(R.string.sending_hint));
                        mDialog.show();

                        getThumbnailPictures();
                    }
                    break;
                case R.id.return_btn:
                    finish();
                    break;
            }
        }

    };

    /**
     * 获得选中图片的缩略图路径
     */
    private void getThumbnailPictures() {
        mMsgIds = new int[mPickedList.size()];
        Bitmap bitmap;
        for (int i = 0; i < mPickedList.size(); i++) {
            if (BitmapLoader.verifyPictureSize(mPickedList.get(i))) {
                File file = new File(mPickedList.get(i));
                ImageContent.createImageContentAsync(file, new ImageContent.CreateImageContentCallback() {
                    @Override
                    public void gotResult(int status, String desc, ImageContent imageContent) {
                        if (status == 0) {
                            Message msg = mConv.createSendMessage(imageContent);
                            mMsgIds[mIndex] = msg.getId();
                        } else {
                            mMsgIds[mIndex] = -1;
                            HandleResponseCode.onHandle(PickPictureActivity.this, status, false);
                        }
                        mIndex++;
                        if (mIndex >= mPickedList.size()) {
                            myHandler.sendEmptyMessage(SEND_PICTURE);
                        }
                    }
                });
            } else {
                bitmap = BitmapLoader.getBitmapFromFile(mPickedList.get(i), 720, 1280);
                ImageContent.createImageContentAsync(bitmap, new ImageContent.CreateImageContentCallback() {
                    @Override
                    public void gotResult(int status, String desc, ImageContent imageContent) {
                        if (status == 0) {
                            Message msg = mConv.createSendMessage(imageContent);
                            mMsgIds[mIndex] = msg.getId();
                        } else {
                            mMsgIds[mIndex] = -1;
                            HandleResponseCode.onHandle(PickPictureActivity.this, status, false);
                        }
                        mIndex++;
                        if (mIndex >= mPickedList.size()) {
                            myHandler.sendEmptyMessage(SEND_PICTURE);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Constants.RESULT_CODE_SELECT_PICTURE) {
            if (data != null) {
                int[] selectedArray = data.getIntArrayExtra("pathArray");
                int sum = 0;
                for (int i : selectedArray) {
                    if (i > 0) {
                        ++sum;
                    }
                }
                if (sum > 0) {
                    String sendText = PickPictureActivity.this.getString(R.string.jmui_send) + "(" + sum + "/" + "9)";
                    mSendPictureBtn.setText(sendText);
                } else {
                    mSendPictureBtn.setText(PickPictureActivity.this.getString(R.string.jmui_send));
                }
                mAdapter.refresh(selectedArray);
            }

        } else if (resultCode == Constants.RESULT_CODE_BROWSER_PICTURE) {
            setResult(Constants.RESULT_CODE_SELECT_ALBUM, data);
            finish();
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<PickPictureActivity> mActivity;

        public MyHandler(PickPictureActivity activity) {
            mActivity = new WeakReference<PickPictureActivity>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            PickPictureActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case SEND_PICTURE:
                        Intent intent = new Intent();
                        intent.putExtra(Constants.MsgIDs, activity.mMsgIds);
                        activity.setResult(Constants.RESULT_CODE_SELECT_ALBUM, intent);
                        if (activity.mDialog != null) {
                            activity.mDialog.dismiss();
                        }
                        activity.finish();
                        break;
                }
            }
        }
    }
}
