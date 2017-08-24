package com.zju.campustour.view.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.parse.ParseException;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.database.dao.MajorFIlesDao;
import com.zju.campustour.model.database.data.MajorData;
import com.zju.campustour.model.bean.MajorModel;
import com.zju.campustour.model.bean.User;
import com.zju.campustour.presenter.implement.MajorInfoPresenterImpl;
import com.zju.campustour.presenter.implement.UserInfoOpPresenterImpl;
import com.zju.campustour.view.iview.IMajorInfoInterestView;
import com.zju.campustour.view.iview.ISearchUserInfoView;
import com.zju.campustour.view.adapter.UserInfoAdapter;
import com.zju.campustour.view.widget.DividerItemDecortion;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MajorInfoActivity extends BaseActivity implements ISearchUserInfoView ,IMajorInfoInterestView {

    //行家推荐列表
    UserInfoAdapter mItemInfoAdapter;
    //行家推荐列表
    UserInfoAdapter mItemInfoAdapter_1;
    //数据库操作相关
    MajorFIlesDao majorFIlesDao;

    //点赞次数
    int interestNum = 0;

    //点击事件传入的专业名称和majorGroup位置信息
    int selectedGroupID = 0;
    String selectedMajorName = "";

    //此页面的Major
    MajorModel theMajor;
    @BindView(R.id.expand_text_view)
    ExpandableTextView expandableTextView;
    @BindView(R.id.activity_major_info_listview_like)
    RecyclerView listViewLike;
    @BindView(R.id.activity_major_info_listview_recommend)
    RecyclerView listViewRecommend;

    @BindView(R.id.activity_major_info_color0_imageview)
    ImageView color0ImageView;
    @BindView(R.id.activity_major_info_color1_imageview)
    ImageView color1ImageView;
    @BindView(R.id.activity_major_info_color2_imageview)
    ImageView color2ImageView;
    @BindView(R.id.activity_major_info_color3_imageview)
    ImageView color3ImageView;
    @BindView(R.id.activity_major_info_noresult_hint1)
    TextView noResultTextView1;
    @BindView(R.id.activity_major_info_noresult_hint2)
    TextView noResultTextView2;
    @BindView(R.id.major_info_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.major_info_collapsing_toolbar)
    CollapsingToolbarLayout mToolbarLayout;

    @BindView(R.id.major_image_view)
    ImageView majorBackgroundImg;
    @BindView(R.id.major_favor_tab)
    FloatingActionButton interestTab;

    @BindView(R.id.major_interest_num)
    TextView majorInterestNum;

    private String TAG = getClass().getSimpleName();
    UserInfoOpPresenterImpl mUserInfoOpPresenter;
    private List<User> mTheSameMajorProviderUserItemInfos;
    private List<User> mSimilarMajorProviderUserItemInfos;
    MajorInfoPresenterImpl mMajorInfoPresenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_major_info);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            getBundleExtras(extras);
        }
        mUserInfoOpPresenter = new UserInfoOpPresenterImpl(this,this);
        mUserInfoOpPresenter.queryMajorStudent(selectedMajorName,selectedGroupID);
        mMajorInfoPresenter = new MajorInfoPresenterImpl(this,this);
        mMajorInfoPresenter.getMajorInterest(selectedMajorName);
        initViewsAndEvents();
    }


    protected void getBundleExtras(Bundle extras) {
        selectedGroupID = extras.getInt("group_id");
        selectedMajorName = extras.getString("major_name");

        Log.d(TAG, "groupid = " + selectedGroupID + "  majorname = " + selectedMajorName);

    }


    protected void initViewsAndEvents() {

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initBasicView();
        initMajorInfo();
    }

    private void initBasicView() {

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    void initMajorInfo() {
        color0ImageView.setImageResource(MajorData.majorColorGroup[selectedGroupID]);
        color1ImageView.setImageResource(MajorData.majorColorGroup[selectedGroupID]);
        color2ImageView.setImageResource(MajorData.majorColorGroup[selectedGroupID]);
        color3ImageView.setImageResource(MajorData.majorColorGroup[selectedGroupID]);

        majorFIlesDao = new MajorFIlesDao(this);
        Cursor majorCursor = majorFIlesDao.selectByMajorName(selectedMajorName, Constants.majorsTableName[selectedGroupID]);
        theMajor = majorCursorToMajor(majorCursor);

        if (null != majorCursor) {
            majorCursor.close();
        }

        mToolbarLayout.setTitle(theMajor.getName());

        expandableTextView.setText(theMajor.getMajorAbstract());

        Glide.with(this).load(theMajor.getImgUrl()).into(majorBackgroundImg);
    }

    private MajorModel majorCursorToMajor(Cursor majorCursor) {
        MajorModel major = new MajorModel();

        if (majorCursor.moveToFirst()) {
            String majorAbstracts = majorCursor.getString(majorCursor.getColumnIndex("major_abstract"));
            int majorClass = majorCursor.getInt(majorCursor.getColumnIndex("major_class"));
            String majorCode = majorCursor.getString(majorCursor.getColumnIndex("major_code"));
            String majorName = majorCursor.getString(majorCursor.getColumnIndex("major_name"));
            String majorImg = majorCursor.getString(majorCursor.getColumnIndex("major_img"));
            int majorInterests = majorCursor.getInt(majorCursor.getColumnIndex("major_interests"));

            major.setMajorAbstract(majorAbstracts);
            major.setMajorClass(majorClass);
            major.setMajorCode(majorCode);
            major.setName(majorName);
            major.setInterests(majorInterests);
            major.setImgUrl(majorImg);
        }

        return major;
    }


    @Override
    public void onGetProviderUserDone(List<User> mUsers) {

        //-------------推荐列表:匹配专业---------------
        if (mUsers.size() != 0) {
            mSimilarMajorProviderUserItemInfos = new ArrayList<>();
            mTheSameMajorProviderUserItemInfos = new ArrayList<>();

            for (User user : mUsers) {

                if (user.getMajor() != null){
                    if (user.getMajor().equals(selectedMajorName)){
                        mTheSameMajorProviderUserItemInfos.add(user);
                    }
                    else {
                        mSimilarMajorProviderUserItemInfos.add(user);
                    }
                }

            }

        }

        if (mTheSameMajorProviderUserItemInfos.size() != 0){
            noResultTextView1.setVisibility(View.GONE);
            showSameMajorProvider(mTheSameMajorProviderUserItemInfos);
        }
        else {
            noResultTextView1.setVisibility(View.VISIBLE);
        }

        if (mSimilarMajorProviderUserItemInfos.size() != 0){
            noResultTextView2.setVisibility(View.GONE);
            List<User> mUserList;
            if (mSimilarMajorProviderUserItemInfos.size() >= 10)
                mUserList = mSimilarMajorProviderUserItemInfos.subList(0,10);
            else
                mUserList = mSimilarMajorProviderUserItemInfos;
            showSimilarMajorProvider(mUserList);
        }
        else {
            noResultTextView2.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onGetProviderUserError(ParseException e) {

    }

    @Override
    public void refreshUserOnlineState(boolean isOnline) {

    }

    private void showSimilarMajorProvider(List<User> mSimilarMajorProviderUserItemInfos) {
        listViewLike.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mItemInfoAdapter_1 = new UserInfoAdapter(mSimilarMajorProviderUserItemInfos);
        mItemInfoAdapter_1.setOnCardViewItemClickListener(new UserInfoAdapter.onCardViewItemClickListener() {
            @Override
            public void onClick(View v, int position, User provider) {
                Intent mIntent = new Intent(getApplication(), UserActivity.class);
                mIntent.putExtra("provider",provider);
                startActivity(mIntent);
            }
        });

        listViewLike.setLayoutManager(layoutManager);
        listViewLike.setAdapter(mItemInfoAdapter_1);
        listViewLike.addItemDecoration(new DividerItemDecortion());

    }

    private void showSameMajorProvider(List<User> mProviderUserItemInfos) {


        listViewRecommend.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mItemInfoAdapter = new UserInfoAdapter(mProviderUserItemInfos);
        mItemInfoAdapter.setOnCardViewItemClickListener(new UserInfoAdapter.onCardViewItemClickListener() {
            @Override
            public void onClick(View v, int position, User provider) {
                Intent mIntent = new Intent(getApplication(), UserActivity.class);
                mIntent.putExtra("provider",provider);
                startActivity(mIntent);
            }
        });

        listViewRecommend.setLayoutManager(layoutManager);
        listViewRecommend.setAdapter(mItemInfoAdapter);
        listViewRecommend.addItemDecoration(new DividerItemDecortion());
    }

    @OnClick(R.id.major_favor_tab)
    public void onInterestTabClicked(){
        ++interestNum;
        majorInterestNum.setText(""+interestNum);
        mMajorInfoPresenter.addMajorInterests(selectedMajorName);
        interestTab.hide();
        showToast("你点赞了"+selectedMajorName+"专业！");
    }

    @Override
    public void onCurrentMajorGotSuccess(MajorModel major) {

        if (major != null ){
            interestNum = major.getInterests();
            majorInterestNum.setText(""+interestNum);
        }
        else
            majorInterestNum.setText("0");
    }

    @Override
    public void onCurrentMajorGotError(Exception e) {
        majorInterestNum.setText("0");
    }
}
