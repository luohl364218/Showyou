package com.zju.campustour.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.bean.StatusInfoModel;
import com.zju.campustour.model.chatting.utils.IdHelper;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.implement.StatusInfoOperator;
import com.zju.campustour.presenter.protocal.enumerate.IdentityType;
import com.zju.campustour.view.activity.FriendInfoActivity;
import com.zju.campustour.view.activity.IdentityConfirmActivity;
import com.zju.campustour.view.activity.SearchFriendDetailActivity;
import com.zju.campustour.view.activity.UserActivity;
import com.zju.campustour.view.adapter.FocusUserStatusAdapter;
import com.zju.campustour.view.adapter.HotUserStatusAdapter;
import com.zju.campustour.view.iview.IStatusInfoView;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;

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
    private Dialog mDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_status_info_child_2, container, false);
            mMaterialRefreshLayout = (MaterialRefreshLayout) mRootView.findViewById(R.id.refresh_view);
            mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycle_view);
            mStatusInfoOperator = new StatusInfoOperator(getContext(),this);
            mStatusInfoOperator.getMyFocusStatusInfo(0,10);

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

        mStatusInfoOperator.getMyFocusStatusInfo(mFocusUserStatusAdapter.getItemCount(),10);
        isRefreshing = false;
    }

    private void refreshStatusInfoData() {
        state = Constants.STATE_REFRESH;
        mStatusInfoOperator.getMyFocusStatusInfo(0,10);
    }


    @Override
    public void onStatusInfoGotSuccess(List<StatusInfoModel> mStatusInfoModels) {
        if (mStatusInfoModels.size() > 0 && mStatusInfoModels.size() <= 10){
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
                                Intent mIntent = new Intent(getActivity(), UserActivity.class);
                                mIntent.putExtra("provider",status.getUser());
                                mIntent.putExtra("position",position);
                                startActivity(mIntent);
                                break;
                            case R.id.favor_btn:
                                if (status.isFavorited()){
                                    showToast(getContext(),"您已经点赞过该状态");
                                }
                                else {
                                    mStatusInfoOperator.addStatusFavorInfo(status.getObjectId());
                                }
                                break;
                            case R.id.comment_btn:

                                ParseUser user = ParseUser.getCurrentUser();
                                String toUser = status.getUser().getUserName();
                                if (TextUtils.isEmpty(toUser))
                                    return;

                                if (user.getUsername().equals(toUser)) {
                                    showToast(getContext(),"你无法与自己聊天哦~");
                                    return;
                                }

                                talkToSomeone(toUser);

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

                if (mStatusInfoModels.size() < 10){
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

    private void talkToSomeone(String userName){

        //获取极光用户信息
        JMessageClient.getUserInfo(userName, new GetUserInfoCallback() {
            @Override
            public void gotResult(int status, String desc, UserInfo userInfo) {

                if (status == 0) {
                    Intent intent = new Intent();

                    if (userInfo.isFriend()) {
                        intent.setClass(getContext(), FriendInfoActivity.class);
                        intent.putExtra("fromContact", true);
                    } else {
                        intent.setClass(getContext(), SearchFriendDetailActivity.class);
                    }
                    intent.putExtra(Constants.TARGET_ID, userInfo.getUserName());
                    intent.putExtra(Constants.TARGET_APP_KEY, userInfo.getAppKey());
                    getActivity().startActivity(intent);
                } else {
                    showToast(getContext(),"该用户还未开通聊天功能");
                }
            }
        });
    }

}
