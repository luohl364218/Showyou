package com.zju.campustour.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.parse.ParseException;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.presenter.implement.UserInfoOpPresenterImpl;
import com.zju.campustour.presenter.ipresenter.IUserInfoOpPresenter;
import com.zju.campustour.presenter.protocal.event.onAreaAndSchoolSelectedEvent;
import com.zju.campustour.presenter.protocal.event.onLoadingDone;
import com.zju.campustour.presenter.protocal.event.onNetworkChangeEvent;
import com.zju.campustour.view.IView.ISearchUserInfoView;
import com.zju.campustour.view.activity.ProviderHomePageActivity;
import com.zju.campustour.view.activity.SchoolListActivity;
import com.zju.campustour.view.adapter.UserInfoAdapter;
import com.zju.campustour.view.widget.DividerItemDecortion;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


/**
 * Created by HeyLink on 2017/4/1.
 */

public class SearchFragment extends Fragment implements ISearchUserInfoView, View.OnClickListener {

    private String TAG = "SearchFragment";
    private View mRootView;
    private RecyclerView mRecyclerView;
    private UserInfoAdapter mItemInfoAdapter;
    private int state = Constants.STATE_NORMAL;
    private MaterialRefreshLayout mMaterialRefreshLayout;
    IUserInfoOpPresenter mUserInfoOpPresenter;
    private FloatingActionButton mFloatingActionButton;
    private TextView noResultHint;

    //area index in array
    int searchArea = -1;
    String searchSchool = null;
    String searchMajor = null;

    //网络状态标志
    boolean isNetworkValid = true;

    boolean isRefreshing = false;

    public SearchFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null){
            mRootView = inflater.inflate(R.layout.fragment_search, container, false);
            mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.fragment_search_recycle_view);
            mFloatingActionButton = (FloatingActionButton) mRootView.findViewById(R.id.fab);
            noResultHint = (TextView) mRootView.findViewById(R.id.fragment_search_noResult_hint);
            mMaterialRefreshLayout = (MaterialRefreshLayout) mRootView.findViewById(R.id.refresh_view);
            mFloatingActionButton.setOnClickListener(this);
            initRefreshLayout();
            EventBus.getDefault().register(this);

            mUserInfoOpPresenter = new UserInfoOpPresenterImpl(this);

            Log.d(TAG,"first create view--------------------");

        }

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null){
            parent.removeView(mRootView);
        }
        Log.d(TAG,"【SearchFragment】第二次创建--------------------");
        return mRootView;
    }

    private void initRefreshLayout() {


        mMaterialRefreshLayout.setLoadMore(true);
        mMaterialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                Log.d(TAG,"----------ready to refreshing!!!!!!---------network is "+isNetworkValid);
                //下拉刷新...
                if (isNetworkValid){
                    Log.d(TAG,"----------refreshing!!!!!!---------network is "+isNetworkValid);
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

                Log.d(TAG,"onRefreshLoadMore");

                if (!isRefreshing && isNetworkValid) {
                    isRefreshing = true;
                    loadMoreServiceInfoData();
                }
                else{
                    mMaterialRefreshLayout.finishRefreshLoadMore();
                }
            }
        });
    }

    private void showRecycleView(List<User> mUsers) {

        switch (state){

            case Constants.STATE_NORMAL:
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                mItemInfoAdapter = new UserInfoAdapter(mUsers);
                mItemInfoAdapter.setOnCardViewItemClickListener(new UserInfoAdapter.onCardViewItemClickListener() {
                    @Override
                    public void onClick(View v, int position, User provider) {
                        Intent mIntent = new Intent(getActivity(), ProviderHomePageActivity.class);
                        mIntent.putExtra("provider",provider);
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
                    Toast.makeText(getActivity(), "已经获取全部数据", Toast.LENGTH_SHORT).show();
                    mMaterialRefreshLayout.setLoadMore(false);
                }
                break;
        }
    }

    private void refreshServiceItemInfoData(){

        state = Constants.STATE_REFRESH;
        mUserInfoOpPresenter.queryProviderUserWithConditions(searchSchool,searchMajor,0,searchArea, -1);
        //showLocalServiceItemInfoData();

    }

    private void loadMoreServiceInfoData(){

        state = Constants.STATE_MORE;
        mUserInfoOpPresenter.queryProviderUserWithConditions(searchSchool,searchMajor,mItemInfoAdapter.getDatas().size(),searchArea,-1);
        isRefreshing = false;
        //mMaterialRefreshLayout.finishRefreshLoadMore();

    }

    @Override
    public void onGetProviderUserDone(List<User> mUsers) {

        if (mUsers.size() != 0){
            noResultHint.setVisibility(View.GONE);
            showRecycleView(mUsers);
        }
        else {
            mMaterialRefreshLayout.finishRefresh();
            mMaterialRefreshLayout.finishRefreshLoadMore();
            noResultHint.setVisibility(View.VISIBLE);
            if (state == Constants.STATE_NORMAL){
                noResultHint.setText("你好，请检查网络连接是否正常");
            }
            else if (state == Constants.STATE_REFRESH ){
                mItemInfoAdapter.clearData();
                noResultHint.setText("报告，没找到符合条件的同学");
            }
            else
            {
                noResultHint.setText("已经为你找到所有符合条件的同学");
                mMaterialRefreshLayout.setLoadMore(false);
            }

        }
    }

    @Override
    public void onGetProviderUserError(ParseException e) {
        noResultHint.setVisibility(View.VISIBLE);
        noResultHint.setText("报告,查询出错啦~请稍后再试~");
        mMaterialRefreshLayout.finishRefresh();
        mMaterialRefreshLayout.finishRefreshLoadMore();
        mMaterialRefreshLayout.setLoadMore(false);
    }

    @Override
    public void refreshUserOnlineState(boolean isOnline) {

    }


    @Override
    public void onClick(View v) {


        switch (v.getId()){
            case R.id.fab:
                Intent mIntent = new Intent(getActivity(), SchoolListActivity.class);
                startActivity(mIntent);
                state = Constants.STATE_REFRESH;
                break;
        }

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void searchProviderWithConditions(onAreaAndSchoolSelectedEvent event) {

        searchSchool = event.getSchool();
        searchArea = event.getArea();
        mUserInfoOpPresenter.queryProviderUserWithConditions(searchSchool, searchMajor, 0, searchArea,-1);

    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onNetworkChangeEvent(onNetworkChangeEvent event) {
        if (event.isValid()){
            noResultHint.setVisibility(View.GONE);
            isNetworkValid = true;
            mMaterialRefreshLayout.setLoadMore(true);
            /*if (mItemInfoAdapter != null){
                state = Constants.STATE_REFRESH;
            }
            else{
                state = Constants.STATE_NORMAL;
            }
            mUserInfoOpPresenter.queryProviderUserWithConditions(searchSchool,searchMajor,0,searchArea, -1);*/
        }
        else {
            isNetworkValid = false;
            mMaterialRefreshLayout.finishRefresh();
            mMaterialRefreshLayout.finishRefreshLoadMore();
            mMaterialRefreshLayout.setLoadMore(false);
            noResultHint.setVisibility(View.VISIBLE);
            noResultHint.setText("网络连接已经断开");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onHomeFragmentLoadingDone(onLoadingDone done){
        if (done.isDone()){
            Log.d(TAG,"------------loading user info----------");

            mUserInfoOpPresenter.queryProviderUserWithConditions(searchSchool,searchMajor,0,-1,-1);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//解除订阅
    }

}
