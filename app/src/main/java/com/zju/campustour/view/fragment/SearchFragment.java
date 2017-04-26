package com.zju.campustour.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.zju.campustour.R;
import com.zju.campustour.model.bean.ServiceItemInfo;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.presenter.implement.UserInfoOpPresenterImpl;
import com.zju.campustour.presenter.ipresenter.IUserInfoOpPresenter;
import com.zju.campustour.view.IView.ISearchUserInfoView;
import com.zju.campustour.view.adapter.ServiceItemInfoAdapter;
import com.zju.campustour.view.widget.DividerItemDecortion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HeyLink on 2017/4/1.
 */

public class SearchFragment extends Fragment implements ISearchUserInfoView {

    private String TAG = "SearchFragment";
    private View mRootView;
    private List<ServiceItemInfo> mServiceItemInfos;
    private RecyclerView mRecyclerView;
    private ServiceItemInfoAdapter mItemInfoAdapter;
    private int state = Constants.STATE_NORMAL;
    private MaterialRefreshLayout mMaterialRefreshLayout;
    IUserInfoOpPresenter mUserInfoOpPresenter;

    public SearchFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null){
            mRootView = inflater.inflate(R.layout.fragment_search, container, false);
            initRefreshLayout();
            mUserInfoOpPresenter = new UserInfoOpPresenterImpl(this);
            mUserInfoOpPresenter.queryProviderUserWithConditions(null,null,0);
            Log.d(TAG,"first create view--------------------");
        }

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null){
            parent.removeView(mRootView);
        }
        Log.d(TAG,"second create view--------------------");
        return mRootView;
    }

    private void initRefreshLayout() {

        mMaterialRefreshLayout = (MaterialRefreshLayout) mRootView.findViewById(R.id.refresh_view);
        mMaterialRefreshLayout.setLoadMore(true);
        mMaterialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                //下拉刷新...
                refreshServiceItemInfoData();
                mMaterialRefreshLayout.setLoadMore(true);
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                //上拉刷新...
                loadMoreServiceInfoData();
            }
        });
    }

    private void showRecycleView() {

        switch (state){

            case Constants.STATE_NORMAL:
                mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.fragment_home_recycle_view);

                LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());

                mItemInfoAdapter = new ServiceItemInfoAdapter(mServiceItemInfos);

                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setAdapter(mItemInfoAdapter);
                mRecyclerView.addItemDecoration(new DividerItemDecortion());
                break;

            case Constants.STATE_REFRESH:
                mItemInfoAdapter.clearData();
                mItemInfoAdapter.addData(mServiceItemInfos);
                mRecyclerView.scrollToPosition(0);
                mMaterialRefreshLayout.finishRefresh();
                break;

            case Constants.STATE_MORE:
                mItemInfoAdapter.addData(mItemInfoAdapter.getDatas().size(), mServiceItemInfos);
                mRecyclerView.scrollToPosition(mItemInfoAdapter.getDatas().size());
                mMaterialRefreshLayout.finishRefreshLoadMore();
                if (mServiceItemInfos.size() < 10){
                    Toast.makeText(getActivity(), "已经到底部了", Toast.LENGTH_SHORT).show();
                    mMaterialRefreshLayout.setLoadMore(false);
                }
                break;
        }
    }


    /*本地测试版本，以后用网络接口获得*/
    private void showLocalServiceItemInfoData(){

        mServiceItemInfos = new ArrayList<>();

        String url_1 = "http://image.bitauto.com/dealer/news/100057188/145a7c3a-6230-482b-b050-77a40c1571fd.jpg";
        String url_2 = "http://img.mp.itc.cn/upload/20170104/33e0d6708d3244b8a3563981a4d7c0c6_th.jpg";
        String url_3 = "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=246028538,2833419550&fm=21&gp=0.jpg";
        String url_4 = "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3334862492,180862699&fm=23&gp=0.jpg";
        ServiceItemInfo mItemInfo_1 = new ServiceItemInfo("1",url_1,
                "梅长苏","带你走进浙大玉泉校区","浙江大学","测控技术与仪器","研究生", 115);
        ServiceItemInfo mItemInfo_2 = new ServiceItemInfo("2",url_2,
                "何幸","想了解电气专业吗？","福州大学", "电力电子","研究生", 15);
        ServiceItemInfo mItemInfo_3 = new ServiceItemInfo("3",url_3,
                "罗素芬","带你领略山东大学的美景","山东大学","药学专业" ,"大四",50);
        ServiceItemInfo mItemInfo_4 = new ServiceItemInfo("4",url_4,
                "李思敏","清华大学的风景，你可别错过","清华大学","电子科学技术","研究生", 25);


        switch (state){
            case Constants.STATE_NORMAL:
                mServiceItemInfos.add(mItemInfo_1);
                mServiceItemInfos.add(mItemInfo_2);
                mServiceItemInfos.add(mItemInfo_3);
                mServiceItemInfos.add(mItemInfo_4);
                mServiceItemInfos.add(mItemInfo_1);
                mServiceItemInfos.add(mItemInfo_2);
                mServiceItemInfos.add(mItemInfo_3);
                mServiceItemInfos.add(mItemInfo_4);
                break;

            case Constants.STATE_REFRESH:
                mServiceItemInfos.add(mItemInfo_4);
                mServiceItemInfos.add(mItemInfo_3);
                mServiceItemInfos.add(mItemInfo_2);
                mServiceItemInfos.add(mItemInfo_1);
                mServiceItemInfos.add(mItemInfo_4);
                mServiceItemInfos.add(mItemInfo_1);
                mServiceItemInfos.add(mItemInfo_3);
                mServiceItemInfos.add(mItemInfo_2);
                break;

            case Constants.STATE_MORE:
                mServiceItemInfos.add(mItemInfo_4);
                mServiceItemInfos.add(mItemInfo_3);
                mServiceItemInfos.add(mItemInfo_2);
                mServiceItemInfos.add(mItemInfo_1);
                mServiceItemInfos.add(mItemInfo_4);
                mServiceItemInfos.add(mItemInfo_1);
                mServiceItemInfos.add(mItemInfo_3);
                mServiceItemInfos.add(mItemInfo_2);
                break;
        }

        showRecycleView();


    }

    private void refreshServiceItemInfoData(){

        state = Constants.STATE_REFRESH;
        mUserInfoOpPresenter.queryProviderUserWithConditions(null,null,0);
        //showLocalServiceItemInfoData();

    }

    private void loadMoreServiceInfoData(){

        state = Constants.STATE_MORE;
        mUserInfoOpPresenter.queryProviderUserWithConditions(null,null,mItemInfoAdapter.getDatas().size());

    }

    @Override
    public void onGetProviderUserDone(List<User> mUsers) {

        if (mUsers.size() != 0){
            showCloudServiceItemInfoData(mUsers);
        }
        else {
            showLocalServiceItemInfoData();
        }
    }

    private void showCloudServiceItemInfoData(List<User> mUsers) {
        mServiceItemInfos = new ArrayList<>();
        for (User user : mUsers){
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

        showRecycleView();
    }
}
