package com.zju.campustour.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import com.parse.ParseException;
import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.bean.User;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.database.data.SchoolData;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.implement.UserInfoOpPresenterImpl;
import com.zju.campustour.presenter.ipresenter.IUserInfoOpPresenter;
import com.zju.campustour.presenter.protocal.event.NetworkChangeEvent;
import com.zju.campustour.view.activity.UserActivity;
import com.zju.campustour.view.adapter.FragmentViewPagerAdapter;
import com.zju.campustour.view.adapter.UserInfoAdapter;
import com.zju.campustour.view.iview.ISearchUserInfoView;
import com.zju.campustour.view.widget.DividerItemDecortion;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


public class InformChildOneFragment extends BaseFragment implements ISearchUserInfoView{


    private String TAG = "InformChildOneFragment";
    private View mRootView;
    private RecyclerView mRecyclerView;
    private UserInfoAdapter mItemInfoAdapter;
    private int state = Constants.STATE_NORMAL;
    private MaterialRefreshLayout mMaterialRefreshLayout;
    IUserInfoOpPresenter mUserInfoOpPresenter;


    private LinearLayout noResultHintWrapper;
    private TextView noResultHint;

    //area index in array
    int searchArea = -1;
    String searchSchool = null;
    String searchMajor = null;

    boolean isRefreshing = false;
    //todo 标志位，让界面视图发生改变
    boolean isMajorNotCommon = true;
    boolean isOrderByFans = false;
    boolean isOrderByLatest = false;


    //设置一个全局变量保存已有的user，只在单独更新某个item时使用
    private List<User> mUserList;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_inform_child_one, container, false);
            EventBus.getDefault().register(this);
            initFragmentView();
            initRefreshLayout();
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
        mUserInfoOpPresenter.queryProviderUserWithConditions(searchSchool,searchMajor,mItemInfoAdapter.getDatas().size(),searchArea,-1,isOrderByFans, isOrderByLatest,isMajorNotCommon);
        isRefreshing = false;
        //mMaterialRefreshLayout.finishRefreshLoadMore();
    }

    private void refreshServiceItemInfoData() {

        state = Constants.STATE_REFRESH;
        mUserInfoOpPresenter.queryProviderUserWithConditions(searchSchool,searchMajor,0,searchArea, -1,isOrderByFans, isOrderByLatest,isMajorNotCommon);
        //showLocalServiceItemInfoData();
    }

    private void initFragmentView() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.fragment_search_recycle_view);
        noResultHintWrapper = (LinearLayout) mRootView.findViewById(R.id.fragment_search_noResult_hint_wrapper);
        noResultHint = (TextView) mRootView.findViewById(R.id.fragment_search_noResult_hint);
        mMaterialRefreshLayout = (MaterialRefreshLayout) mRootView.findViewById(R.id.refresh_view);


        mUserInfoOpPresenter = new UserInfoOpPresenterImpl(this,getContext());
        isOrderByFans = false;
        isOrderByLatest = true;
        state = Constants.STATE_NORMAL;


    }

    private void showRecycleView(List<User> mUsers) {
        if (mItemInfoAdapter == null)
            mItemInfoAdapter = new UserInfoAdapter(mUsers);
        switch (state){

            case Constants.STATE_NORMAL:
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                mItemInfoAdapter.setOnCardViewItemClickListener(new UserInfoAdapter.onCardViewItemClickListener() {
                    @Override
                    public void onClick(View v, int position, User provider) {
                        Intent mIntent = new Intent(getActivity(), UserActivity.class);
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
                    showToast(getContext(),"已经获取全部数据");
                    mMaterialRefreshLayout.setLoadMore(false);
                }
                break;
        }

        mUserList = mItemInfoAdapter.getDatas();
    }


    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onNetworkChangeEvent(NetworkChangeEvent event) {
        if (event.isValid()){
            isNetworkUseful = true;
            mMaterialRefreshLayout.setLoadMore(true);
            if (mUserList == null || mUserList.size() == 0) {
                state = Constants.STATE_NORMAL;
                mUserInfoOpPresenter.queryProviderUserWithConditions(searchSchool,searchMajor,0,-1,-1,isOrderByFans,isOrderByLatest,isMajorNotCommon);
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
        if (mUserList == null || mUserList.size() == 0)
            mUserInfoOpPresenter.queryProviderUserWithConditions(searchSchool,searchMajor,
                    0,-1,-1,isOrderByFans,isOrderByLatest,isMajorNotCommon);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//解除订阅
    }



  /*  @Override
    public void onTabSelected(TabLayout.Tab tab) {

        int tag = (int) tab.getTag();
        if (mMaterialRefreshLayout != null){
            mMaterialRefreshLayout.finishRefresh();
            mMaterialRefreshLayout.finishRefreshLoadMore();
            mMaterialRefreshLayout.setLoadMore(true);
        }

        switch (tag){
            case Constants.TAG_HOT_RECOMMEND:
                searchSchool = null;
                searchMajor = null;
                searchArea = -1;
                state = Constants.STATE_REFRESH;
                isOrderByFans = true;
                isOrderByLatest = false;
                mUserInfoOpPresenter.queryProviderUserWithConditions(searchSchool,searchMajor,0,searchArea, -1,isOrderByFans,isOrderByLatest,isMajorNotCommon);
                break;
            case Constants.TAG_SAME_PROVINCE:
                ParseUser currentUser = ParseUser.getCurrentUser();
                String userProvince = "";
                if (currentUser == null)
                    userProvince = "北京市";
                else if (currentUser.getString("province") == null){
                    showToast(getContext(),"默认地区：北京市");
                    userProvince = "北京市";
                }
                else {
                    userProvince = currentUser.getString("province");
                }

                searchSchool = null;
                searchMajor = null;
                isOrderByFans = false;
                isOrderByLatest = false;
                int index = 0;
                for (int i = 0; i <= SchoolData.allAreaGroup.length; i++){
                    if (userProvince.equals(SchoolData.allAreaGroup[i])){
                        index = i;
                        break;
                    }
                }
                searchArea = index;
                state = Constants.STATE_REFRESH;
                mUserInfoOpPresenter.queryProviderUserWithConditions(searchSchool,searchMajor,0,searchArea, -1);
                break;
            case Constants.TAG_LATEST:
                searchSchool = null;
                searchMajor = null;
                searchArea = -1;
                state = Constants.STATE_REFRESH;
                isOrderByFans = false;
                isOrderByLatest = true;
                mUserInfoOpPresenter.queryProviderUserWithConditions(searchSchool,searchMajor,0,searchArea, -1,isOrderByFans,isOrderByLatest,isMajorNotCommon);
                break;
            default:
                break;
        }

    }
*/


    @Override
    public void onGetProviderUserDone(List<User> mUsers) {

        if (mUsers.size() > 0 && mUsers.size() <= 10){
            if (mUserList != null && state == Constants.STATE_NORMAL)
                state = Constants.STATE_REFRESH;

            noResultHintWrapper.setVisibility(View.GONE);
            noResultHint.setVisibility(View.GONE);
            showRecycleView(mUsers);
        }
        else if (mUsers.size() == 0){
            try {
                mMaterialRefreshLayout.finishRefresh();
                mMaterialRefreshLayout.finishRefreshLoadMore();

                if (state == Constants.STATE_NORMAL) {
                    showToast(getContext(),"你好，请检查网络连接是否正常");
                    showWrongHint("网络连接已经断开~");
                } else if (state == Constants.STATE_REFRESH) {
                    mItemInfoAdapter.clearData();
                    showWrongHint("报告，没找到符合条件的同学");
                } else {
                    showToast(getContext(),"已经为你找到所有符合条件的同学");
                    mMaterialRefreshLayout.setLoadMore(false);
                }
            }catch (Exception e){
                Log.w(TAG,"Null Pointer Exception");
            }

        }

    }

    private void showWrongHint(String mText) {

        if (mUserList == null || mUserList.size() == 0) {
            noResultHintWrapper.setVisibility(View.VISIBLE);
            noResultHint.setVisibility(View.VISIBLE);
            noResultHint.setText(mText);
        }
    }

    @Override
    public void onGetProviderUserError(ParseException e) {

        showWrongHint("查询出错啦~");

        showToast(getContext(),"报告,查询出错啦~请稍后再试~");
        mMaterialRefreshLayout.finishRefresh();
        mMaterialRefreshLayout.finishRefreshLoadMore();
        mMaterialRefreshLayout.setLoadMore(false);

    }

    @Override
    public void refreshUserOnlineState(boolean isOnline) {

    }
}
