package com.zju.campustour.view.fragment;


import android.content.Intent;

import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;

import com.zju.campustour.R;
import com.zju.campustour.model.bean.NewsModule;
import com.zju.campustour.model.bean.User;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.implement.NewsGetImpl;

import com.zju.campustour.presenter.ipresenter.IUserInfoOpPresenter;
import com.zju.campustour.presenter.protocal.enumerate.NewsType;
import com.zju.campustour.presenter.protocal.event.NetworkChangeEvent;
import com.zju.campustour.view.activity.InfoWebActivity;
import com.zju.campustour.view.activity.UserActivity;
import com.zju.campustour.view.adapter.NewsAdapter;
import com.zju.campustour.view.adapter.UserInfoAdapter;
import com.zju.campustour.view.iview.INewsShowView;

import com.zju.campustour.view.widget.DividerItemDecortion;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import java.util.List;

import static com.zju.campustour.model.common.Constants.STATE_NORMAL;

public class MessageChildTwoFragment extends BaseFragment implements INewsShowView {

    private String TAG = "MessageChildTwoFragment";
    private View mRootView;
    private RecyclerView mRecyclerView;

    private int state = Constants.STATE_NORMAL;
    private MaterialRefreshLayout mMaterialRefreshLayout;

    private NewsGetImpl mNewsGet;
    NewsType mType = NewsType.STUDY_METHOD;
    private TextView noResultHint;
    private NewsAdapter mNewsAdapter;

    boolean isRefreshing = false;

    private List<NewsModule> mNewsModules;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_message_child_two, container, false);
            EventBus.getDefault().register(this);
            initFragmentView();
            initRefreshLayout();
            mNewsGet = new NewsGetImpl(this,getContext());
            Log.d(TAG,"first create view--------------------");


        }

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null){
            parent.removeView(mRootView);
        }

        return mRootView;

    }

    private void initRefreshLayout() {

        mMaterialRefreshLayout.setLoadMore(true);
        mMaterialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                Log.d(TAG,"----------ready to refreshing!!!!!!---------network is "+isNetworkUseful);
                //下拉刷新...
                isNetworkUseful = NetworkUtil.isNetworkAvailable(getContext());
                if (isNetworkUseful){
                    Log.d(TAG,"----------refreshing!!!!!!---------network is "+isNetworkUseful);
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

    private void loadMoreServiceInfoData() {

        state = Constants.STATE_MORE;
        mNewsGet.getNewsInfo(mType,mNewsAdapter.getDatas().size(),10);
        isRefreshing = false;

    }

    private void refreshServiceItemInfoData() {

        state = Constants.STATE_REFRESH;
        mNewsGet.getNewsInfo(mType,0,10);
    }

    private void initFragmentView() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.fragment_search_recycle_view);
        noResultHint = (TextView) mRootView.findViewById(R.id.fragment_search_noResult_hint);
        mMaterialRefreshLayout = (MaterialRefreshLayout) mRootView.findViewById(R.id.refresh_view);





    }

    private void showRecycleView(List<NewsModule> newsModules) {
        if (mNewsAdapter == null)
            mNewsAdapter = new NewsAdapter(newsModules);

        switch (state){

            case Constants.STATE_NORMAL:
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

                mNewsAdapter.setOnCardViewItemClickListener(new NewsAdapter.onCardViewItemClickListener() {
                    @Override
                    public void onClick(View v, String url) {
                        Intent mIntent = new Intent(getActivity(), InfoWebActivity.class);
                        mIntent.putExtra("web",url);
                        startActivity(mIntent);
                    }
                });

                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setAdapter(mNewsAdapter);
                mRecyclerView.addItemDecoration(new DividerItemDecortion());

                break;

            case Constants.STATE_REFRESH:
                mMaterialRefreshLayout.finishRefresh();
                mNewsAdapter.clearData();
                mNewsAdapter.addData(newsModules);
                mRecyclerView.scrollToPosition(0);

                break;

            case Constants.STATE_MORE:
                mMaterialRefreshLayout.finishRefreshLoadMore();
                mNewsAdapter.addData(mNewsAdapter.getDatas().size(), newsModules);
                mRecyclerView.scrollToPosition(mNewsAdapter.getDatas().size());

                if (newsModules.size() < 10){
                    showToast(getContext(),"已经获取全部数据");
                    mMaterialRefreshLayout.setLoadMore(false);
                }
                break;
        }

        mNewsModules = mNewsAdapter.getDatas();
    }


    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onNetworkChangeEvent(NetworkChangeEvent event) {
        if (event.isValid()){
            isNetworkUseful = true;
            mMaterialRefreshLayout.setLoadMore(true);
            if (mNewsModules == null || mNewsModules.size() == 0) {
                state = Constants.STATE_NORMAL;
                mNewsGet.getNewsInfo(mType,0,10);
            }

        }
        else {
            if (NetworkUtil.isNetworkAvailable(getContext()))
                return;
            isNetworkUseful = false;
            mMaterialRefreshLayout.finishRefresh();
            mMaterialRefreshLayout.finishRefreshLoadMore();
            mMaterialRefreshLayout.setLoadMore(false);
            showToast(getContext(),"网络连接已经断开");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mNewsModules == null || mNewsModules.size() == 0)
            mNewsGet.getNewsInfo(mType,0,10);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//解除订阅
    }

    @Override
    public void onGetNewsDone(List<NewsModule> newsModules) {
        if (newsModules.size() > 0 && newsModules.size() <= 10){
            if (mNewsModules != null && state == Constants.STATE_NORMAL)
                state = Constants.STATE_REFRESH;

            showRecycleView(newsModules);
        }
        else if (newsModules.size() == 0){
            try {
                mMaterialRefreshLayout.finishRefresh();
                mMaterialRefreshLayout.finishRefreshLoadMore();

                if (state == Constants.STATE_NORMAL) {
                    showToast(getContext(),"你好，请检查网络连接是否正常");
                } else if (state == Constants.STATE_REFRESH) {
                    mNewsAdapter.clearData();
                    showToast(getContext(),"抱歉，暂无相关资讯");
                } else {
                    showToast(getContext(),"已经为你找到所有资讯");
                    mMaterialRefreshLayout.setLoadMore(false);
                }
            }catch (Exception e){
                Log.w(TAG,"Null Pointer Exception");
            }

        }
    }
}
