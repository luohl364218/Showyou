package com.zju.campustour.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.implement.FocusMapOpPresenterImpl;
import com.zju.campustour.view.iview.ISearchUserInfoView;
import com.zju.campustour.view.adapter.UserInfoAdapter;
import com.zju.campustour.view.widget.DividerItemDecortion;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zju.campustour.model.common.Constants.STATE_NORMAL;

public class MySchoolmateActivity extends BaseActivity implements ISearchUserInfoView ,TabLayout.OnTabSelectedListener{

    @BindView(R.id.my_schoolmate_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.my_schoolmate_tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.my_schoolmate_refresh_view)
    MaterialRefreshLayout mMaterialRefreshLayout;

    @BindView(R.id.my_schoolmate_recycle_view)
    RecyclerView mRecyclerView;

    private Context mContext = this;
    private boolean isRefreshing = false;

    private UserInfoAdapter mItemInfoAdapter;
    private int state = Constants.STATE_NORMAL;
    private FocusMapOpPresenterImpl mFocusMapOpPresenter;
    private ParseUser currentLoginUser;
    private String TAG = getClass().getSimpleName();
    private String tabName = "你还没有关注别人哦，快去关注吧";
    private boolean isQueryFansNotFocus = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_schoolmate);
        ButterKnife.bind(this);
        currentLoginUser = ParseUser.getCurrentUser();

        /*让toolbar显示我们的标题*/
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        initView();
        initRefreshLayout();


        mFocusMapOpPresenter = new FocusMapOpPresenterImpl(this, mContext);
        mFocusMapOpPresenter.queryFansOrFocus(currentLoginUser.getObjectId(),isQueryFansNotFocus,0,10);



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
        tab.setText("我的关注");
        tab.setTag(Constants.TAG_FOCUS);

        mTabLayout.addTab(tab);

        tab= mTabLayout.newTab();
        tab.setText("我的粉丝");
        tab.setTag(Constants.TAG_FANS);

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
        mFocusMapOpPresenter.queryFansOrFocus(currentLoginUser.getObjectId(),isQueryFansNotFocus,0,10);

    }

    private void loadMoreServiceInfoData(){

        state = Constants.STATE_MORE;
        mFocusMapOpPresenter.queryFansOrFocus(currentLoginUser.getObjectId(),isQueryFansNotFocus,mItemInfoAdapter.getDatas().size(),10);
        isRefreshing = false;
    }


    @Override
    public void onGetProviderUserDone(List<User> mUsers) {
        //mRecyclerView
        if (mUsers.size() != 0){
            if (mItemInfoAdapter == null)
                state = STATE_NORMAL;
            showRecycleView(mUsers);
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

    private void showRecycleView(List<User> mUsers) {
        if (mItemInfoAdapter == null)
            mItemInfoAdapter = new UserInfoAdapter(mUsers);
        switch (state){

            case Constants.STATE_NORMAL:
                LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                mItemInfoAdapter.setOnCardViewItemClickListener(new UserInfoAdapter.onCardViewItemClickListener() {
                    @Override
                    public void onClick(View v, int position, User provider) {
                        Intent mIntent = new Intent(MySchoolmateActivity.this, UserActivity.class);
                        mIntent.putExtra("provider",provider);
                        mIntent.putExtra("position",position);
                        startActivity(mIntent);
                    }
                });

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
    public void onGetProviderUserError(ParseException e) {

    }

    @Override
    public void refreshUserOnlineState(boolean isOnline) {

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
            case Constants.TAG_FANS:
                tabName = "还没有人关注你哦，继续加油吧";
                state = Constants.STATE_REFRESH;
                isQueryFansNotFocus = true;
                mFocusMapOpPresenter.queryFansOrFocus(currentLoginUser.getObjectId(),isQueryFansNotFocus,0,10);

                break;

            case Constants.TAG_FOCUS:
                tabName = "你还没有关注别人哦，快去关注吧";
                state = Constants.STATE_REFRESH;
                isQueryFansNotFocus = false;
                mFocusMapOpPresenter.queryFansOrFocus(currentLoginUser.getObjectId(),isQueryFansNotFocus,0,10);
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
}
