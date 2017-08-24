package com.zju.campustour.view.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.bean.User;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.implement.ProjectUserMapOpPresenterImpl;
import com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType;
import com.zju.campustour.view.iview.IProjectUserInfoView;
import com.zju.campustour.view.adapter.UserInfoAdapter;
import com.zju.campustour.view.widget.DividerItemDecortion;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zju.campustour.model.common.Constants.STATE_NORMAL;
import static com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType.BOOK_SUCCESS;
import static com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType.COLLECT;

public class BookedStudentActivity extends BaseActivity implements TabLayout.OnTabSelectedListener,IProjectUserInfoView {

    @BindView(R.id.booked_student_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.booked_student_tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.booked_student_refresh_view)
    MaterialRefreshLayout mMaterialRefreshLayout;

    @BindView(R.id.booked_student_recycle_view)
    RecyclerView mRecyclerView;

    private Context mContext = this;
    private boolean isRefreshing = false;

    private UserInfoAdapter mItemInfoAdapter;
    private int state = Constants.STATE_NORMAL;
    private ProjectUserMapOpPresenterImpl mProjectUserMapOpPresenter;
    private ParseUser currentLoginUser;
    private String TAG = getClass().getSimpleName();
    private String tabName = "还没有人报名该项目";
    private String currentProjectId;
    private UserProjectStateType currentState = BOOK_SUCCESS;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked_student);
        Intent mIntent = getIntent();
        currentProjectId = mIntent.getStringExtra("projectId");
        if (TextUtils.isEmpty(currentProjectId))
            finish();
        ButterKnife.bind(this);
        currentLoginUser = ParseUser.getCurrentUser();
        if (currentLoginUser == null)
            finish();

         /*让toolbar显示我们的标题*/
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        initView();
        initRefreshLayout();
        mProjectUserMapOpPresenter = new ProjectUserMapOpPresenterImpl(this,this);
        mProjectUserMapOpPresenter.queryUserInfo(currentProjectId,BOOK_SUCCESS, 0, 10);

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

        TabLayout.Tab tab= mTabLayout.newTab();
        tab.setText("报名信息");
        tab.setTag(Constants.TAG_BOOKED);

        mTabLayout.addTab(tab);

        tab= mTabLayout.newTab();
        tab.setText("收藏信息");
        tab.setTag(Constants.TAG_FAVOR);

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
        mProjectUserMapOpPresenter.queryUserInfo(currentProjectId,currentState,0,10 );

    }

    private void loadMoreServiceInfoData(){

        state = Constants.STATE_MORE;
        mProjectUserMapOpPresenter.queryUserInfo(currentProjectId,currentState, mItemInfoAdapter.getDatas().size(),10);
        isRefreshing = false;
    }

    private void showRecycleView(List<User> mUsers) {
        if (mItemInfoAdapter == null)
            mItemInfoAdapter = new UserInfoAdapter(mUsers, true);
        switch (state){

            case Constants.STATE_NORMAL:
                LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                mItemInfoAdapter.setOnCardViewItemClickListener(new UserInfoAdapter.onCardViewItemClickListener() {
                    @Override
                    public void onClick(View v, int position, User provider) {
                        Intent mIntent = new Intent(BookedStudentActivity.this, UserActivity.class);
                        mIntent.putExtra("provider",provider);
                        mIntent.putExtra("position",position);
                        startActivity(mIntent);
                    }
                });
                mItemInfoAdapter.setBooked(true);
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setAdapter(mItemInfoAdapter);
                mRecyclerView.addItemDecoration(new DividerItemDecortion());

                break;

            case Constants.STATE_REFRESH:
                mMaterialRefreshLayout.finishRefresh();
                mItemInfoAdapter.clearData();
                mItemInfoAdapter.addData(mUsers);
                mRecyclerView.scrollToPosition(0);

                break;

            case Constants.STATE_MORE:
                mMaterialRefreshLayout.finishRefreshLoadMore();
                mItemInfoAdapter.addData(mItemInfoAdapter.getDatas().size(), mUsers);
                mRecyclerView.scrollToPosition(mItemInfoAdapter.getDatas().size());

                if (mUsers.size() < 10){
                    showToast("已经获取全部数据");
                    mMaterialRefreshLayout.setLoadMore(false);
                }
                break;
        }
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
            case Constants.TAG_BOOKED:
                tabName = "还没有人报名该项目";
                state = Constants.STATE_REFRESH;
                currentState = BOOK_SUCCESS;
                if (mItemInfoAdapter != null)
                    mItemInfoAdapter.setBooked(true);
                mProjectUserMapOpPresenter.queryUserInfo(currentProjectId,currentState,0,10);

                break;

            case Constants.TAG_FAVOR:
                tabName = "还没有人收藏该项目";
                state = Constants.STATE_REFRESH;
                currentState = COLLECT;
                if (mItemInfoAdapter != null)
                    mItemInfoAdapter.setBooked(false);
                mProjectUserMapOpPresenter.queryUserInfo(currentProjectId,currentState,0,10);
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


    @Override
    public void onGetProjectUserInfoDone(List<User> userList) {
        if (userList.size() != 0){
            if (mItemInfoAdapter == null)
                state = STATE_NORMAL;
            showRecycleView(userList);
        }
        else {
            try {
                mMaterialRefreshLayout.finishRefresh();
                mMaterialRefreshLayout.finishRefreshLoadMore();
                if (state == Constants.STATE_NORMAL) {
                    showToast(tabName);
                } else if (state == Constants.STATE_REFRESH) {
                    showToast(tabName);
                    if (mItemInfoAdapter != null)
                        mItemInfoAdapter.clearData();

                } else {
                    showToast("已经为你找到所有信息");
                    mMaterialRefreshLayout.setLoadMore(false);
                }
            }catch (Exception e){
                Log.w(TAG,"Null Pointer Exception");
            }


        }
    }
}
