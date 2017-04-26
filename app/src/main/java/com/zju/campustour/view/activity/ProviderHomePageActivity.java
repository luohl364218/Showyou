package com.zju.campustour.view.activity;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zju.campustour.R;
import com.zju.campustour.model.bean.ProjectItemInfo;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.database.models.Project;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.model.util.DbUtils;
import com.zju.campustour.presenter.implement.ProjectInfoOpPresenterImpl;
import com.zju.campustour.presenter.implement.UserInfoOpPresenterImpl;
import com.zju.campustour.presenter.protocal.enumerate.SexType;
import com.zju.campustour.view.IView.ISearchProjectInfoView;
import com.zju.campustour.view.IView.ISearchUserInfoView;
import com.zju.campustour.view.adapter.RecommendProjectAdapter;
import com.zju.campustour.view.widget.DividerItemDecortion;
import com.zju.campustour.view.widget.FullyLinearLayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ProviderHomePageActivity extends AppCompatActivity implements ISearchUserInfoView,ISearchProjectInfoView {
    private String selectedProviderId;
    private UserInfoOpPresenterImpl mUserInfoOpPresenter;
    private ProjectInfoOpPresenterImpl mProjectInfoOpPresenter;
    private User mUser;
    private List<ProjectItemInfo> mProjectItemInfos;
    private RecommendProjectAdapter mProjectAdapter;

    private Toolbar mToolbar;
    private CollapsingToolbarLayout mToolbarLayout;
    private SimpleDraweeView userImage;
    private ImageView iconProvider;
    private TextView providerName;
    private ImageView iconMan;
    private ImageView iconWoman;
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

        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            getBundleExtras(extras);
        }

        mUserInfoOpPresenter = new UserInfoOpPresenterImpl(this);
        mProjectInfoOpPresenter = new ProjectInfoOpPresenterImpl(this);

        if (selectedProviderId != null) {
            mUserInfoOpPresenter.queryUserInfoWithId(selectedProviderId);
            mProjectInfoOpPresenter.queryProjectWithUserId(selectedProviderId);
        }
        initViewsAndEvents();

    }


    protected void getBundleExtras(Bundle extras) {
        selectedProviderId = extras.getString("provider_id");
    }

    protected void initViewsAndEvents() {

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initBasicView();
    }

    private void initBasicView() {
        mToolbar = (Toolbar) findViewById(R.id.activity_major_provider_page_toolbar);
        mToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.activity_major_provider_page_collapsing_toolbar);
        userImage = (SimpleDraweeView) findViewById(R.id.major_provider_page_userimage);
        iconProvider = (ImageView) findViewById(R.id.icon_major_provider);
        providerName = (TextView) findViewById(R.id.major_provider_page_providerName);
        iconMan = (ImageView) findViewById(R.id.major_provider_page_sex_man);
        iconWoman = (ImageView) findViewById(R.id.major_provider_page_sex_woman);
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
        if (mUsers.size() != 0){
            mUser = mUsers.get(0);
            initUserInfoView();
        }

    }

    private void initUserInfoView() {
        DbUtils.setImg(userImage, mUser.getImgUrl(), 100);
        iconProvider.setVisibility(View.VISIBLE);
        providerName.setVisibility(View.VISIBLE);
        providerName.setText(mUser.getUserName());
        if (mUser.getSex() == SexType.MALE){
            iconMan.setVisibility(View.VISIBLE);
            iconWoman.setVisibility(View.GONE);
        }
        else {
            iconMan.setVisibility(View.GONE);
            iconWoman.setVisibility(View.VISIBLE);
        }

        iconCollege.setVisibility(View.VISIBLE);
        college.setVisibility(View.VISIBLE);
        college.setText(mUser.getSchool());

        iconMajor.setVisibility(View.VISIBLE);
        major.setVisibility(View.VISIBLE);
        major.setText(mUser.getMajor());
        iconGrade.setVisibility(View.VISIBLE);
        grade.setVisibility(View.VISIBLE);
        grade.setText(mUser.getGrade());

        if (mUser.isOnline()){
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
        focusNum.setText(mUser.getFansNum()+"人关注");
        aboutHim.setText(mUser.getDescription());
    }

    @Override
    public void onGetProjectInfoDone(List<Project> mProjects) {
        if (mProjects.size() != 0){
            noResultHint.setVisibility(View.GONE);
            showCloudProjectItemInfoData(mProjects);
        }
        else {
            noResultHint.setVisibility(View.VISIBLE);
            noResultHint.setText("TA还没有提供相关项目");
            //showLocalProjectItemInfoData();
        }
    }

    private void showCloudProjectItemInfoData(List<Project> mProjects) {

        mProjectItemInfos = new ArrayList<>();

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

        for (Project project : mProjects){

            ProjectItemInfo projectItem = new ProjectItemInfo(
                    project.getProvider().getId(),
                    project.getId(),
                    project.getProvider().getImgUrl(),
                    project.getTitle(),
                    sdf.format(project.getStartTime()),
                    project.getAcceptNum(),
                    project.getDescription(),
                    project.getImgUrl(),
                    project.getFavorites().size(),
                    project.getPrice(),
                    project.getAcceptNum()
            );
            mProjectItemInfos.add(projectItem);
        }

        showProjectRecycleView();
    }

    private void showProjectRecycleView() {
        projectList = (RecyclerView) findViewById(R.id.major_provider_page_project_list);
        projectList.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        if (mProjectItemInfos == null)
            return;
        mProjectAdapter = new RecommendProjectAdapter(mProjectItemInfos, Constants.PART_VIEW);

        projectList.setLayoutManager(layoutManager);
        projectList.setAdapter(mProjectAdapter);
        projectList.addItemDecoration(new DividerItemDecortion());

    }
}
