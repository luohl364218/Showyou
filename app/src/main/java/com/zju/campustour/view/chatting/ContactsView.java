package com.zju.campustour.view.chatting;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zju.campustour.R;
import com.zju.campustour.presenter.chatting.controller.ContactsController;
import com.zju.campustour.view.chatting.adapter.StickyListAdapter;
import com.zju.campustour.view.chatting.widget.SideBar;
import com.zju.campustour.view.chatting.widget.StickyListHeadersListView;


public class ContactsView extends LinearLayout{

    private ImageButton returnBtn;
    private EditText mSearchEt;
    private TextView mHint;
    private Context mContext;
	private StickyListHeadersListView mListView;
    private TextView mNewFriendNum;
    private RelativeLayout mFriendVerifyRl;
    private RelativeLayout mGroupRl;
    private SideBar mSideBar;
    private TextView mLetterHintTv;
    private LayoutInflater mInflater;
    private ImageView mLoadingIv;

	public ContactsView(Context context, AttributeSet attrs) {
		super(context, attrs);
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
		// TODO Auto-generated constructor stub
	}
	
	public void initModule(float ratio, float density){
        returnBtn = (ImageButton) findViewById(R.id.return_btn);
        mSearchEt = (EditText) findViewById(R.id.search_et);
        mHint = (TextView) findViewById(R.id.contact_hint);

        mListView = (StickyListHeadersListView) findViewById(R.id.sticky_list_view);
        mSideBar = (SideBar) findViewById(R.id.sidebar);
        mLetterHintTv = (TextView) findViewById(R.id.letter_hint_tv);
        mSideBar.setTextView(mLetterHintTv);
        mSideBar.setRatioAndDensity(ratio, density);
        mSideBar.bringToFront();

        View header = mInflater.inflate(R.layout.contact_list_header, null);
        mFriendVerifyRl = (RelativeLayout) header.findViewById(R.id.verify_rl);
        mGroupRl = (RelativeLayout) header.findViewById(R.id.group_chat_rl);
        mNewFriendNum = (TextView) header.findViewById(R.id.friend_verification_num);
        RelativeLayout loadingHeader = (RelativeLayout) mInflater.inflate(R.layout.jmui_drop_down_list_header, null);
        mLoadingIv = (ImageView) loadingHeader.findViewById(R.id.jmui_loading_img);
        mNewFriendNum.setVisibility(INVISIBLE);
        mListView.addHeaderView(header, null, false);
        mListView.addHeaderView(loadingHeader);
        mListView.setDrawingListUnderStickyHeader(true);
        mListView.setAreHeadersSticky(true);
        mListView.setStickyHeaderTopOffset(0);
	}

	public void setAdapter(StickyListAdapter adapter) {
		mListView.setAdapter(adapter);
        mSideBar.setIndex(adapter.getSections());
	}

    public void setListeners(ContactsController controller) {
        mFriendVerifyRl.setOnClickListener(controller);
        mGroupRl.setOnClickListener(controller);
        mSideBar.setOnClickListener(controller);
        returnBtn.setOnClickListener(controller);
    }

    public void setSideBarTouchListener(SideBar.OnTouchingLetterChangedListener listener) {
        mSideBar.setOnTouchingLetterChangedListener(listener);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setSelection(int position) {
        mListView.setSelection(position);
    }

    public void showNewFriends(int num) {
        mNewFriendNum.setVisibility(VISIBLE);
        mNewFriendNum.setText(String.valueOf(num));
    }

    public void dismissNewFriends() {
        mNewFriendNum.setVisibility(INVISIBLE);
    }

    public String getSearchText() {
        return mSearchEt.getText().toString();
    }

    public void setTextWatcher(TextWatcher watcher) {
        mSearchEt.addTextChangedListener(watcher);
    }

    public void showContact() {
        mSearchEt.setVisibility(VISIBLE);
        mSideBar.setVisibility(VISIBLE);
        mListView.setVisibility(VISIBLE);
        mHint.setVisibility(GONE);
    }

    public void clearSearchText() {
        mSearchEt.setText("");
    }

    public void showLoadingHeader() {
        mLoadingIv.setVisibility(View.VISIBLE);
        AnimationDrawable drawable = (AnimationDrawable) mLoadingIv.getDrawable();
        drawable.start();
        mLoadingIv.setVisibility(View.VISIBLE);
    }

    public void dismissLoadingHeader() {
        mLoadingIv.setVisibility(View.GONE);
    }
}
