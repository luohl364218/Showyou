package com.zju.campustour.view.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.zju.campustour.R;
import com.zju.campustour.model.bean.ServiceItemInfo;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.database.dao.MajorFIlesDao;
import com.zju.campustour.model.database.data.MajorData;
import com.zju.campustour.model.database.models.Major;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.presenter.implement.UserInfoOpPresenterImpl;
import com.zju.campustour.presenter.ipresenter.IUserInfoOpPresenter;
import com.zju.campustour.view.IView.ISearchUserInfoView;
import com.zju.campustour.view.adapter.ServiceItemInfoAdapter;
import com.zju.campustour.view.widget.DividerItemDecortion;

import java.util.ArrayList;
import java.util.List;


public class MajorProviderInfoActivity extends BaseActivity implements ISearchUserInfoView {

    //行家推荐列表
    ServiceItemInfoAdapter mItemInfoAdapter;
    //数据库操作相关
    MajorFIlesDao majorFIlesDao;

    //点击事件传入的专业名称和majorGroup位置信息
    int selectedGroupID = 0;
    String selectedMajorName = "";

    //此页面的Major
    Major theMajor;
    ExpandableTextView expandableTextView;
    RecyclerView listViewLike;
    RecyclerView listViewRecommend;
    RelativeLayout returnLayout;
    RelativeLayout topLayout;
    ImageView color1ImageView;
    ImageView color2ImageView;
    ImageView color3ImageView;
    TextView noResultTextView1;
    TextView noResultTextView2;
    Toolbar mToolbar;
    CollapsingToolbarLayout mToolbarLayout;
    private String TAG = getClass().getSimpleName();
    IUserInfoOpPresenter mUserInfoOpPresenter;
    private List<ServiceItemInfo> mServiceItemInfos;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_major_provider_info);

        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            getBundleExtras(extras);
        }
        mUserInfoOpPresenter = new UserInfoOpPresenterImpl(this);
        mUserInfoOpPresenter.queryProviderUserWithConditions(null, selectedMajorName,0);
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

        expandableTextView = (ExpandableTextView) findViewById(R.id.expand_text_view);
        listViewLike = (RecyclerView) findViewById(R.id.activity_major_info_listview_like);
        color1ImageView = (ImageView) findViewById(R.id.activity_major_info_color1_imageview);
        color2ImageView = (ImageView) findViewById(R.id.activity_major_info_color2_imageview);
        color3ImageView = (ImageView) findViewById(R.id.activity_major_info_color3_imageview);
        noResultTextView1 = (TextView) findViewById(R.id.activity_major_info_noresult_hint1);
        noResultTextView2 = (TextView) findViewById(R.id.activity_major_info_noresult_hint2);
        mToolbar = (Toolbar) findViewById(R.id.major_info_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.major_info_collapsing_toolbar);


        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    void initMajorInfo() {
        color1ImageView.setImageResource(MajorData.majorColorGroup[selectedGroupID]);
        color2ImageView.setImageResource(MajorData.majorColorGroup[selectedGroupID]);
        color3ImageView.setImageResource(MajorData.majorColorGroup[selectedGroupID]);

        majorFIlesDao = new MajorFIlesDao(this);
        Cursor majorCursor = majorFIlesDao.selectByMajorName(selectedMajorName, Constants.majorsTableName[selectedGroupID]);
        theMajor = majorCursorToMajor(majorCursor);

        mToolbarLayout.setTitle(theMajor.getMajorName());

        expandableTextView.setText(theMajor.getMajorAbstract());


    }

    private Major majorCursorToMajor(Cursor majorCursor) {
        Major major = null;

        if (majorCursor.moveToFirst()) {
            String majorAbstracts = majorCursor.getString(majorCursor.getColumnIndex("major_abstract"));
            int majorClass = majorCursor.getInt(majorCursor.getColumnIndex("major_class"));
            String majorCode = majorCursor.getString(majorCursor.getColumnIndex("major_code"));
            String majorName = majorCursor.getString(majorCursor.getColumnIndex("major_name"));

            major = new Major(majorAbstracts, majorClass, majorCode, majorName);
        }

        return major;
    }


    @Override
    public void onGetProviderUserDone(List<User> mUsers) {

        //-------------推荐列表:匹配专业---------------

        if (mUsers.size() != 0) {
            noResultTextView1.setVisibility(View.GONE);

            mServiceItemInfos = new ArrayList<>();
            for (User user : mUsers) {
                ServiceItemInfo mItemInfo = new ServiceItemInfo(
                        user.getId(),
                        user.getImgUrl(),
                        user.getUserName(),
                        user.getShortDescription(),
                        user.getSchool(),
                        user.getMajor(),
                        user.getGrade(),
                        user.getFansNum());
                mServiceItemInfos.add(mItemInfo);
            }

            showSameMajorProvider(mServiceItemInfos);


        }
        else {
            noResultTextView1.setVisibility(View.VISIBLE);
        }

        //-------------同类专业匹配列表---------------


        //无精确匹配结果
        noResultTextView2.setVisibility(View.VISIBLE);
        listViewLike.setFocusable(false);

    }

    private void showSameMajorProvider(List<ServiceItemInfo> mServiceItemInfos) {
        listViewRecommend = (RecyclerView) findViewById(R.id.activity_major_info_listview_recommend);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mItemInfoAdapter = new ServiceItemInfoAdapter(mServiceItemInfos);

        listViewRecommend.setLayoutManager(layoutManager);
        listViewRecommend.setAdapter(mItemInfoAdapter);
        listViewRecommend.addItemDecoration(new DividerItemDecortion());
    }
}
