package com.zju.campustour.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.bean.NewsModule;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.implement.NewsGetImpl;
import com.zju.campustour.presenter.protocal.enumerate.NewsType;
import com.zju.campustour.view.iview.INewsShowView;
import com.zju.campustour.view.activity.InfoWebActivity;
import com.zju.campustour.view.adapter.NewsAdapter;
import com.zju.campustour.view.widget.DividerItemDecortion;

import java.util.List;

import static com.zju.campustour.model.common.Constants.STATE_NORMAL;

/**
 * Created by HeyLink on 2017/4/1.
 */
@Deprecated
public class MessageFragment extends BaseFragment implements INewsShowView ,TabLayout.OnTabSelectedListener{

    private View mRootView;
    private String TAG = "MessageFragment";

    private ImageButton mImageButton;
    private RecyclerView mRecyclerView;
    private NewsAdapter mNewsAdapter;
    private NewsGetImpl mNewsGet;
    private TabLayout mTabLayout;
    private int state = STATE_NORMAL;
    private MaterialRefreshLayout mMaterialRefreshLayout;
    boolean isRefreshing = false;
    NewsType mType = NewsType.RECOMMEND;

    public MessageFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null){
            mRootView = inflater.inflate(R.layout.fragment_message, container, false);
            Log.d(TAG,"first create view--------------------");
            initFragmentView();
            initRefreshLayout();
            mNewsGet = new NewsGetImpl(this,getContext());
            mNewsGet.getNewsInfo(mType,0,10);
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null){
            parent.removeView(mRootView);
        }

        return mRootView;
    }

    private void initFragmentView() {
        mImageButton = (ImageButton) mRootView.findViewById(R.id.fragment_message_user_icon);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.fragment_news_recycle_view);
        mMaterialRefreshLayout = (MaterialRefreshLayout) mRootView.findViewById(R.id.refresh_view);
        mTabLayout = (TabLayout) mRootView.findViewById(R.id.fragment_message_tab_layout);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                if (!drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        initTab();

    }

    private void initTab(){

        TabLayout.Tab tab= mTabLayout.newTab();
        tab.setText("推荐");
        tab.setTag(Constants.TAG_RECOMMEND);

        mTabLayout.addTab(tab);

        tab= mTabLayout.newTab();
        tab.setText("学习");
        tab.setTag(Constants.TAG_STUDY);

        mTabLayout.addTab(tab);

        tab= mTabLayout.newTab();
        tab.setText("科学");
        tab.setTag(Constants.TAG_SCIENCE);

        mTabLayout.addTab(tab);

        tab= mTabLayout.newTab();
        tab.setText("时事");
        tab.setTag(Constants.TAG_NEWS);

        mTabLayout.addTab(tab);

        mTabLayout.setOnTabSelectedListener(this);

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        int tag = (int) tab.getTag();
        if (mMaterialRefreshLayout != null){
            mMaterialRefreshLayout.finishRefresh();
            mMaterialRefreshLayout.finishRefreshLoadMore();
            mMaterialRefreshLayout.setLoadMore(true);
        }

        switch (tag){
            case Constants.TAG_RECOMMEND:

                state = Constants.STATE_REFRESH;
                mType = NewsType.RECOMMEND;
                mNewsGet.getNewsInfo(mType,0,10);

                break;
            case Constants.TAG_STUDY:
                state = Constants.STATE_REFRESH;
                mType = NewsType.STUDY_METHOD;
                mNewsGet.getNewsInfo(mType,0,10);
                break;
            case Constants.TAG_SCIENCE:

                state = Constants.STATE_REFRESH;
                mType = NewsType.SCIENCE;
                mNewsGet.getNewsInfo(mType,0,10);
                break;

            case Constants.TAG_NEWS:

                state = Constants.STATE_REFRESH;
                mType = NewsType.NEWS;
                mNewsGet.getNewsInfo(mType,0,10);
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
                    refreshMessageItemInfoData();
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
                    loadMoreMessageInfoData();
                }
                else{
                    mMaterialRefreshLayout.finishRefreshLoadMore();
                }
            }
        });
    }

    private void loadMoreMessageInfoData() {
        state = Constants.STATE_MORE;
        mNewsGet.getNewsInfo(mType,mNewsAdapter.getDatas().size(),10);
        isRefreshing = false;
    }

    private void refreshMessageItemInfoData() {
        state = Constants.STATE_REFRESH;
        mNewsGet.getNewsInfo(mType,0,10);

    }


    @Override
    public void onGetNewsDone(List<NewsModule> mNewsModules) {

        if (mNewsModules.size() > 0 && mNewsModules.size() <= 10){
            if (mNewsAdapter == null)
                state = STATE_NORMAL;

            showRecycleView(mNewsModules);
        }
        else if (mNewsModules.size() == 0){
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

    private void showRecycleView(List<NewsModule> mNewsModules) {

        if (mNewsAdapter == null)
            mNewsAdapter = new NewsAdapter(mNewsModules);

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
                mNewsAdapter.addData(mNewsModules);
                mRecyclerView.scrollToPosition(0);

                break;

            case Constants.STATE_MORE:
                mMaterialRefreshLayout.finishRefreshLoadMore();
                mNewsAdapter.addData(mNewsAdapter.getDatas().size(), mNewsModules);
                mRecyclerView.scrollToPosition(mNewsAdapter.getDatas().size());

                if (mNewsModules.size() < 10){
                    showToast(getContext(),"已经获取全部数据");
                    mMaterialRefreshLayout.setLoadMore(false);
                }
                break;
        }
    }
}
