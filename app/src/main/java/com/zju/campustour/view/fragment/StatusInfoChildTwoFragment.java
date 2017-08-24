package com.zju.campustour.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.zju.campustour.R;
import com.zju.campustour.model.bean.StatusInfoModel;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.implement.StatusInfoOperator;
import com.zju.campustour.view.adapter.FocusUserStatusAdapter;
import com.zju.campustour.view.adapter.HotUserStatusAdapter;
import com.zju.campustour.view.iview.IStatusInfoView;

import java.util.List;

/**
 * Created by HeyLink on 2017/8/15.
 */

public class StatusInfoChildTwoFragment extends BaseFragment implements IStatusInfoView {

    private View mRootView;
    MaterialRefreshLayout mMaterialRefreshLayout;
    RecyclerView mRecyclerView;
    boolean isRefreshing = false;
    private int state = Constants.STATE_NORMAL;
    StatusInfoOperator mStatusInfoOperator;
    FocusUserStatusAdapter mFocusUserStatusAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_status_info_child_2, container, false);
            mMaterialRefreshLayout = (MaterialRefreshLayout) mRootView.findViewById(R.id.refresh_view);
            mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycle_view);
            mStatusInfoOperator = new StatusInfoOperator(getContext(),this);
            mStatusInfoOperator.getHotStatusInfo(0,20);

            initRefreshLayout();

        }

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null){
            parent.removeView(mRootView);
        }

        return mRootView;
    }


    //第一步：初始化RefreshLayout
    private void initRefreshLayout() {


        mMaterialRefreshLayout.setLoadMore(true);
        mMaterialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                //下拉刷新...
                isNetworkUseful = NetworkUtil.isNetworkAvailable(getContext());
                if (isNetworkUseful){
                    refreshStatusInfoData();
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
                    loadMoreStatusInfoData();
                }
                else{
                    mMaterialRefreshLayout.finishRefreshLoadMore();
                }
            }
        });
    }

    private void loadMoreStatusInfoData() {
        state = Constants.STATE_MORE;
        if (mFocusUserStatusAdapter == null){
            mMaterialRefreshLayout.finishRefreshLoadMore();
            return;
        }

        mStatusInfoOperator.getHotStatusInfo(mFocusUserStatusAdapter.getItemCount(),20);
        isRefreshing = false;
    }

    private void refreshStatusInfoData() {
        state = Constants.STATE_REFRESH;
        mStatusInfoOperator.getHotStatusInfo(0,20);
    }


    @Override
    public void onStatusInfoGotSuccess(List<StatusInfoModel> mStatusInfoModels) {
        if (mStatusInfoModels.size() > 0 && mStatusInfoModels.size() <= 20){
            if (mFocusUserStatusAdapter != null && state == Constants.STATE_NORMAL)
                state = Constants.STATE_REFRESH;

            showRecycleView(mStatusInfoModels);
        }
        else if (mStatusInfoModels.size() == 0){
            try {
                mMaterialRefreshLayout.finishRefresh();
                mMaterialRefreshLayout.finishRefreshLoadMore();

                if (state == Constants.STATE_NORMAL) {
                    showToast(getContext(),"没有找到相关用户动态");
                } else if (state == Constants.STATE_REFRESH) {
                    mFocusUserStatusAdapter.clearData();
                } else {
                    showToast(getContext(),"已经为你找到所有用户动态");
                    mMaterialRefreshLayout.setLoadMore(false);
                }
            }catch (Exception e){
                Log.w(TAG,"Null Pointer Exception");
            }

        }
    }

    private void showRecycleView(List<StatusInfoModel> mStatusInfoModels) {
        if (mFocusUserStatusAdapter == null)
            mFocusUserStatusAdapter = new FocusUserStatusAdapter(getContext(),mStatusInfoModels);


        switch (state){

            case Constants.STATE_NORMAL:
                mFocusUserStatusAdapter.setOnStatusInfoItemClickListener(new FocusUserStatusAdapter.StatusInfoItemListener() {
                    @Override
                    public void onClick(View v, int position, StatusInfoModel status) {

                        switch (v.getId()){
                            case R.id.status_img:

                                break;
                            case R.id.user_img:

                                break;
                            case R.id.favor_btn:

                                break;
                            case R.id.comment_btn:

                                break;
                            case R.id.share_btn:

                                break;
                            case R.id.report_btn:

                                break;

                        }



                    }
                });

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()){
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                };
               /* //使用瀑布流
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL){
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                };*/

                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setAdapter(mFocusUserStatusAdapter);
                //mRecyclerView.addItemDecoration(new DividerItemDecortion());

                break;

            case Constants.STATE_REFRESH:
                mMaterialRefreshLayout.finishRefresh();
                mFocusUserStatusAdapter.clearData();
                mFocusUserStatusAdapter.addData(mStatusInfoModels);
                mRecyclerView.scrollToPosition(0);

                break;

            case Constants.STATE_MORE:
                mMaterialRefreshLayout.finishRefreshLoadMore();
                mFocusUserStatusAdapter.addData(mFocusUserStatusAdapter.getItemCount(), mStatusInfoModels);
                mRecyclerView.scrollToPosition(mFocusUserStatusAdapter.getItemCount());

                if (mStatusInfoModels.size() < 20){
                    showToast(getContext(),"已经获取全部数据");
                    mMaterialRefreshLayout.setLoadMore(false);
                }
                break;
        }

    }

    @Override
    public void onStatusInfoGotError(Exception e) {
        showToast(getContext(),"获取用户状态出错，请稍后再试");
    }

    @Override
    public void onStatusInfoCommitSuccess() {

    }

    @Override
    public void onStatusInfoCommitError(Exception e) {

    }
}
