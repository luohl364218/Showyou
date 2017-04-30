package com.zju.campustour.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.parse.ParseException;
import com.zju.campustour.R;
import com.zju.campustour.model.bean.ProjectItemInfo;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.database.models.Project;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.presenter.implement.ProjectInfoOpPresenterImpl;
import com.zju.campustour.presenter.implement.UserInfoOpPresenterImpl;
import com.zju.campustour.presenter.protocal.enumerate.SexType;
import com.zju.campustour.view.IView.ISearchProjectInfoView;
import com.zju.campustour.view.IView.ISearchUserInfoView;
import com.zju.campustour.view.adapter.RecommendProjectAdapter;
import com.zju.campustour.view.widget.DividerItemDecortion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProviderHomePageActivity extends AppCompatActivity implements ISearchUserInfoView,ISearchProjectInfoView, View.OnClickListener{
    private String selectedProviderId;
    private UserInfoOpPresenterImpl mUserInfoOpPresenter;
    private ProjectInfoOpPresenterImpl mProjectInfoOpPresenter;
    private User defaultUser;
    private RecommendProjectAdapter mProjectAdapter;

    private Toolbar mToolbar;
    private CollapsingToolbarLayout mToolbarLayout;
    private SimpleDraweeView userImage;
    private TextView providerName;
    private CircleImageView iconMan;
    private CircleImageView iconWoman;
    private ImageView iconCollege;
    private TextView college;
    private ImageView iconMajor;
    private TextView major;
    private ImageView iconGrade;
    private TextView grade;
    private ImageView online;
    private ImageView disOnline;
    private TextView onlineTxt;
    private TextView dealNum;
    private TextView focusNum;
    private TextView chatOnline;
    private RecyclerView projectList;
    private TextView noResultHint;
    private TextView aboutHim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_major_provider_home_page);

        Intent mIntent = getIntent();
        defaultUser = (User) mIntent.getSerializableExtra("provider");
        selectedProviderId = defaultUser.getId();
        mUserInfoOpPresenter = new UserInfoOpPresenterImpl(this);
        mProjectInfoOpPresenter = new ProjectInfoOpPresenterImpl(this);

        if (selectedProviderId != null) {
            mUserInfoOpPresenter.queryUserInfoWithId(selectedProviderId);
            mProjectInfoOpPresenter.queryProjectWithUserId(selectedProviderId);
        }
        initViewsAndEvents(defaultUser);

    }


    protected void initViewsAndEvents(User user) {

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initBasicView();
        initUserInfoView(user);

    }

    private void initBasicView() {
        mToolbar = (Toolbar) findViewById(R.id.activity_major_provider_page_toolbar);
        mToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.activity_major_provider_page_collapsing_toolbar);
        userImage = (SimpleDraweeView) findViewById(R.id.major_provider_page_userimage);
        providerName = (TextView) findViewById(R.id.major_provider_page_providerName);
        iconMan = (CircleImageView) findViewById(R.id.major_provider_page_sex_man);
        iconWoman = (CircleImageView) findViewById(R.id.major_provider_page_sex_woman);
        iconCollege = (ImageView) findViewById(R.id.icon_provider_college);
        college = (TextView) findViewById(R.id.major_provider_page_college);
        iconMajor = (ImageView) findViewById(R.id.icon_provider_major);
        major = (TextView) findViewById(R.id.major_provider_page_major);
        iconGrade = (ImageView) findViewById(R.id.icon_provider_grade);
        grade = (TextView) findViewById(R.id.major_provider_page_grade);
        online = (ImageView) findViewById(R.id.major_provider_page_online);
        disOnline = (ImageView) findViewById(R.id.major_provider_page_disOnline);
        onlineTxt = (TextView) findViewById(R.id.major_provider_page_online_txt);
        dealNum = (TextView) findViewById(R.id.major_provider_page_total_deals);
        focusNum = (TextView) findViewById(R.id.major_provider_page_attention_amount);
        chatOnline = (TextView) findViewById(R.id.major_provider_page_chat_button);
        noResultHint = (TextView) findViewById(R.id.major_provider_page_noResult_hint);
        aboutHim = (TextView)findViewById(R.id.major_provider_page_about);

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
            mActionBar.setDisplayShowTitleEnabled(false);
        }

    }

    @Override
    public void onGetProviderUserDone(List<User> mUsers) {

    }

    @Override
    public void onGetProviderUserError(ParseException e) {

    }

    @Override
    public void refreshUserOnlineState(boolean isOnline) {
         /*只更新在线与否的状态*/
        if (isOnline){
            online.setVisibility(View.VISIBLE);
            disOnline.setVisibility(View.GONE);
            onlineTxt.setText("在线");
        }
        else {
            onlineTxt.setText("下线");
            disOnline.setVisibility(View.VISIBLE);
            online.setVisibility(View.GONE);
        }

    }

    private void initUserInfoView(User user) {

        Uri uri = Uri.parse(user.getImgUrl());
        userImage.setImageURI(uri);
        providerName.setVisibility(View.VISIBLE);
        providerName.setText(user.getUserName());
        mToolbarLayout.setTitle(user.getUserName());
        mToolbarLayout.setCollapsedTitleGravity(View.SCROLL_INDICATOR_START);
        mToolbarLayout.setExpandedTitleGravity(View.SCROLL_INDICATOR_TOP);
        mToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        mToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.transparent));
        if (user.getSex() == SexType.MALE){
            iconMan.setVisibility(View.VISIBLE);
            iconWoman.setVisibility(View.GONE);
        }
        else {
            iconMan.setVisibility(View.GONE);
            iconWoman.setVisibility(View.VISIBLE);
        }

        iconCollege.setVisibility(View.VISIBLE);
        college.setVisibility(View.VISIBLE);
        college.setText(user.getSchool());

        iconMajor.setVisibility(View.VISIBLE);
        major.setVisibility(View.VISIBLE);
        major.setText(user.getMajor());
        iconGrade.setVisibility(View.VISIBLE);
        grade.setVisibility(View.VISIBLE);
        grade.setText(user.getGrade());

        if (user.isOnline()){
            online.setVisibility(View.VISIBLE);
            disOnline.setVisibility(View.GONE);
            onlineTxt.setText("在线");
        }
        else {
            onlineTxt.setText("下线");
            disOnline.setVisibility(View.VISIBLE);
            online.setVisibility(View.GONE);
        }

        dealNum.setText("20人成交");
        focusNum.setText(user.getFansNum()+"人关注");
        aboutHim.setText(user.getDescription());
    }

    @Override
    public void onGetProjectInfoDone(List<Project> mProjects) {
        if (mProjects.size() != 0){
            noResultHint.setVisibility(View.GONE);
            showProjectRecycleView(mProjects);
        }
        else {
            noResultHint.setVisibility(View.VISIBLE);
            noResultHint.setText("TA还没有提供相关项目");
            //showLocalProjectItemInfoData();
        }
    }

    private void showProjectRecycleView(List<Project> mProjectList) {
        projectList = (RecyclerView) findViewById(R.id.major_provider_page_project_list);
        projectList.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        if (mProjectList == null)
            return;
        mProjectAdapter = new RecommendProjectAdapter(mProjectList, Constants.PART_VIEW);

        projectList.setLayoutManager(layoutManager);
        projectList.setAdapter(mProjectAdapter);
        projectList.addItemDecoration(new DividerItemDecortion());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.major_provider_page_chat_button:
                Toast.makeText(this, "Sorry 此功能我们将在5月初完善", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
