package com.zju.campustour.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.bean.User;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.implement.FocusMapOpPresenterImpl;
import com.zju.campustour.presenter.implement.ProjectInfoOpPresenterImpl;
import com.zju.campustour.presenter.protocal.enumerate.UserType;
import com.zju.campustour.view.activity.MySchoolmateActivity;
import com.zju.campustour.view.activity.UserActivity;
import com.zju.campustour.view.adapter.UserInfoAdapter;
import com.zju.campustour.view.iview.ISearchUserInfoView;
import com.zju.campustour.view.widget.DividerItemDecortion;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.zju.campustour.model.common.Constants.STATE_NORMAL;
import static com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType.COLLECT;


public class MySchoolmateChildTwoFragment extends BaseFragment implements ISearchUserInfoView {


    private View mRootView;
    private RecyclerView mRecyclerView;
    private MaterialRefreshLayout mMaterialRefreshLayout;



    private boolean isRefreshing = false;
    private UserInfoAdapter mItemInfoAdapter;
    private int state = Constants.STATE_NORMAL;
    private FocusMapOpPresenterImpl mFocusMapOpPresenter;
    private ParseUser currentLoginUser;
    private String TAG = getClass().getSimpleName();
    private String tabName = "还没有人关注你哦，继续加油吧";
    private boolean isQueryFansNotFocus = true;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_my_schoolmate_child_two, container, false);
            EventBus.getDefault().register(this);
            currentLoginUser = ParseUser.getCurrentUser();


            initFragmentView();
            initRefreshLayout();


            mFocusMapOpPresenter = new FocusMapOpPresenterImpl(this, getActivity());
            mFocusMapOpPresenter.queryFansOrFocus(currentLoginUser.getObjectId(),isQueryFansNotFocus,0,10);



        }

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null){
            parent.removeView(mRootView);
        }

        return mRootView;

    }

    private void initFragmentView() {

        mMaterialRefreshLayout = (MaterialRefreshLayout) mRootView.findViewById(R.id.my_project_refresh_view);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.my_project_recycle_view);

    }


    private void initRefreshLayout() {


        mMaterialRefreshLayout.setLoadMore(true);
        mMaterialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {

                //下拉刷新...
                isNetworkUseful = NetworkUtil.isNetworkAvailable(getActivity());
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

    private void showRecycleView(List<User> mUsers) {
        if (mItemInfoAdapter == null)
            mItemInfoAdapter = new UserInfoAdapter(mUsers);
        switch (state){

            case Constants.STATE_NORMAL:
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
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
    }

    @Override
    public void onGetProviderUserDone(List<User> mUsers) {

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
                    showToast(getContext(),tabName);
                } else if (state == Constants.STATE_REFRESH) {
                    showToast(getContext(),tabName);
                    if (mItemInfoAdapter != null)
                        mItemInfoAdapter.clearData();

                } else {
                    showToast(getContext(),"已经为你找到所有信息");
                    mMaterialRefreshLayout.setLoadMore(false);
                }
            }catch (Exception e){
                Log.w(TAG,"Null Pointer Exception");
            }


        }

    }

    @Override
    public void onGetProviderUserError(ParseException e) {

    }

    @Override
    public void refreshUserOnlineState(boolean isOnline) {

    }
}
