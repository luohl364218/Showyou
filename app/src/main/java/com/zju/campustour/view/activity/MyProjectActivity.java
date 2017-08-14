package com.zju.campustour.view.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.database.models.Project;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.implement.ProjectInfoOpPresenterImpl;
import com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType;
import com.zju.campustour.presenter.protocal.enumerate.UserType;
import com.zju.campustour.presenter.protocal.event.ProjectDeleteEvent;
import com.zju.campustour.view.iview.ISearchProjectInfoView;
import com.zju.campustour.view.adapter.ProjectInfoAdapter;
import com.zju.campustour.view.widget.DividerItemDecortion;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zju.campustour.model.common.Constants.STATE_MORE;
import static com.zju.campustour.model.common.Constants.STATE_NORMAL;
import static com.zju.campustour.model.common.Constants.STATE_REFRESH;
import static com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType.BOOK_SUCCESS;
import static com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType.COLLECT;
import static com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType.FINISHED;

public class MyProjectActivity extends BaseActivity implements ISearchProjectInfoView,TabLayout.OnTabSelectedListener{

    @BindView(R.id.my_project_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.my_project_tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.my_project_refresh_view)
    MaterialRefreshLayout mMaterialRefreshLayout;

    @BindView(R.id.my_project_recycle_view)
    RecyclerView mRecyclerView;

    private Context mContext = this;
    private boolean isRefreshing = false;
    //获取project信息的接口实现
    private ProjectInfoOpPresenterImpl mProjectInfoPresenter;
    private ProjectInfoAdapter mProjectAdapter;
    private int state = STATE_NORMAL;
    private ParseUser currentLoginUser;
    private UserType currentUserType = UserType.USER;
    private boolean isCurrentTabMine = false;
    private UserProjectStateType type = BOOK_SUCCESS;
    private String TAG = getClass().getSimpleName();
    private String tabName = "你还没有发布过活动哦，快去发布吧";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_project);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        currentLoginUser = ParseUser.getCurrentUser();
        Intent mIntent = getIntent();
        currentUserType = UserType.values()[mIntent.getIntExtra("userType",0)];

        /*让toolbar显示我们的标题*/
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        initView();
        initRefreshLayout();

        mProjectInfoPresenter = new ProjectInfoOpPresenterImpl(this,mContext);
        if (currentUserType == UserType.PROVIDER) {
            tabName = "你还没有发布过活动哦，快去发布吧";
            isCurrentTabMine = true;
            mProjectInfoPresenter.queryProjectWithUserId(currentLoginUser.getObjectId(), 0, 10);
        }
        else {
            tabName = "你还没有收藏活动哦，快去收藏吧";
            isCurrentTabMine = false;
            mProjectInfoPresenter.queryProjectWithUserIdAndState(currentLoginUser.getObjectId(),COLLECT,0,10);
        }
    }

    private void initView() {

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initTab();
    }

    private void initTab(){

        TabLayout.Tab tab;

        if (currentUserType == UserType.PROVIDER){
            tab= mTabLayout.newTab();
            tab.setText("我创建的");
            tab.setTag(Constants.TAG_MINE);
            mTabLayout.addTab(tab);
        }

        tab= mTabLayout.newTab();
        tab.setText("收藏");
        tab.setTag(Constants.TAG_FAVOR);
        mTabLayout.addTab(tab);

        tab= mTabLayout.newTab();
        tab.setText("预约");
        tab.setTag(Constants.TAG_BOOKED);

        mTabLayout.addTab(tab);

        tab= mTabLayout.newTab();
        tab.setText("完成");
        tab.setTag(Constants.TAG_FINISHED);

        mTabLayout.addTab(tab);

        mTabLayout.setOnTabSelectedListener(this);

    }

    private void initRefreshLayout() {


        mMaterialRefreshLayout.setLoadMore(true);
        mMaterialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {

                //下拉刷新...
                isNetworkUseful = NetworkUtil.isNetworkAvailable(mContext);
                if (isNetworkUseful){
                    refreshServiceItemInfoData();
                    //加载完所有后会禁止加载更多，需要通过下拉刷新恢复
                    mMaterialRefreshLayout.setLoadMore(true);
                }
                else{
                    mMaterialRefreshLayout.finishRefresh();
                }
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {

                if (!isRefreshing && isNetworkUseful) {
                    isRefreshing = true;
                    loadMoreServiceInfoData();
                }
                else{
                    mMaterialRefreshLayout.finishRefreshLoadMore();
                }
            }
        });
    }

    private void refreshServiceItemInfoData(){

        state = Constants.STATE_REFRESH;
        if (currentUserType == UserType.PROVIDER  && isCurrentTabMine)
            mProjectInfoPresenter.queryProjectWithUserId(currentLoginUser.getObjectId(),0,10);
        else
            mProjectInfoPresenter.queryProjectWithUserIdAndState(currentLoginUser.getObjectId(),type,0,10);

    }

    private void loadMoreServiceInfoData(){

        state = Constants.STATE_MORE;
        if (currentUserType == UserType.PROVIDER  && isCurrentTabMine)
            mProjectInfoPresenter.queryProjectWithUserId(currentLoginUser.getObjectId(),mProjectAdapter.getDatas().size(),10);
        else
            mProjectInfoPresenter.queryProjectWithUserIdAndState(currentLoginUser.getObjectId(),type,mProjectAdapter.getDatas().size(),10);

        isRefreshing = false;
    }

    private void showProjectRecycleView(List<Project> projectList) {

        if (mProjectAdapter == null)
            mProjectAdapter = new ProjectInfoAdapter(projectList, Constants.COLLECT_VIEW,mContext);

        switch (state) {

            case Constants.STATE_NORMAL:
                LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                if (projectList == null)
                    return;

                mProjectAdapter.setOnProjectItemClickListener(new ProjectInfoAdapter.onProjectItemClickListener() {
                    @Override
                    public void onClick(View v, int position, Project project) {
                        Intent mIntent = new Intent(MyProjectActivity.this, ProjectActivity.class);
                        mIntent.putExtra("project", project);
                        mIntent.putExtra("position",position);
                        startActivity(mIntent);
                    }
                });
                mProjectAdapter.setOnProjectProviderClickListener(new ProjectInfoAdapter.onProjectProviderClickListener() {
                    @Override
                    public void onClick(View v, int position, User user) {
                        Intent mIntent = new Intent(MyProjectActivity.this, UserActivity.class);
                        mIntent.putExtra("provider",user);
                        mIntent.putExtra("position",position);
                        startActivity(mIntent);
                    }
                });
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setAdapter(mProjectAdapter);
                mRecyclerView.addItemDecoration(new DividerItemDecortion());
                break;
            case STATE_REFRESH:
                mMaterialRefreshLayout.finishRefresh();
                mProjectAdapter.clearData();
                mProjectAdapter.addData(projectList);
                mRecyclerView.scrollToPosition(0);
                break;
            case STATE_MORE:
                mMaterialRefreshLayout.finishRefreshLoadMore();
                mProjectAdapter.addData(mProjectAdapter.getDatas().size(), projectList);
                mRecyclerView.scrollToPosition(mProjectAdapter.getDatas().size());

                if (projectList.size() < 10){
                    showToast("已经获取全部数据");
                    mMaterialRefreshLayout.setLoadMore(false);
                }
                break;
            default:
                break;

        }

    }


    @Override
    public void onGetProjectInfoDone(List<? extends Object> mProjects) {
        //mRecyclerView
        if (mProjects.size() != 0){
            if (mProjectAdapter == null)
                state = STATE_NORMAL;
            showProjectRecycleView((List<Project>) mProjects);
        }
        else {
            try {
            mMaterialRefreshLayout.finishRefresh();
            mMaterialRefreshLayout.finishRefreshLoadMore();
            if (state == Constants.STATE_NORMAL) {
                showToast(tabName);
            } else if (state == Constants.STATE_REFRESH) {
                showToast(tabName);
                if (mProjectAdapter != null)
                    mProjectAdapter.clearData();

            } else {
                showToast("已经为你找到所有活动信息");
                mMaterialRefreshLayout.setLoadMore(false);
            }
            }catch (Exception e){
                Log.w(TAG,"Null Pointer Exception");
            }


        }
    }

    @Override
    public void onGetProjectInfoError(Exception e) {
        showToast("获取活动信息出错，请稍后再试");
    }



    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        int tag = (int) tab.getTag();
        if (mMaterialRefreshLayout != null){
            mMaterialRefreshLayout.finishRefresh();
            mMaterialRefreshLayout.finishRefreshLoadMore();
            mMaterialRefreshLayout.setLoadMore(true);
        }

        switch (tag) {
            case Constants.TAG_MINE:
                tabName = "你还没有发布过活动哦，快去发布吧";
                state = Constants.STATE_REFRESH;
                isCurrentTabMine = true;
                mProjectInfoPresenter.queryProjectWithUserId(currentLoginUser.getObjectId(), 0, 10);
                break;

            case Constants.TAG_FAVOR:
                tabName = "你还没有收藏活动哦，快去收藏吧";
                state = Constants.STATE_REFRESH;
                isCurrentTabMine = false;
                type = COLLECT;
                mProjectInfoPresenter.queryProjectWithUserIdAndState(currentLoginUser.getObjectId(),type,0,10);
                break;

            case Constants.TAG_BOOKED:
                tabName = "你预约的活动为空哦，快去预约吧";
                state = Constants.STATE_REFRESH;
                isCurrentTabMine = false;
                type = BOOK_SUCCESS;
                mProjectInfoPresenter.queryProjectWithUserIdAndState(currentLoginUser.getObjectId(),type,0,10);
                break;
            case Constants.TAG_FINISHED:
                tabName = "你还没有已经完成的活动";
                state = Constants.STATE_REFRESH;
                isCurrentTabMine = false;
                type = FINISHED;
                mProjectInfoPresenter.queryProjectWithUserIdAndState(currentLoginUser.getObjectId(),type,0,10);
                break;

            default:
                break;
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onReceiveProjectDeleteEvent(ProjectDeleteEvent projectDeleteEvent){
        if (projectDeleteEvent.isDelete())
            refreshServiceItemInfoData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
