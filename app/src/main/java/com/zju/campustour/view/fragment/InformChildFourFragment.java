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
import com.parse.ParseException;
import com.zju.campustour.R;
import com.zju.campustour.model.bean.Project;
import com.zju.campustour.model.bean.User;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.implement.ProjectInfoOpPresenterImpl;
import com.zju.campustour.presenter.implement.UserInfoOpPresenterImpl;
import com.zju.campustour.presenter.ipresenter.IUserInfoOpPresenter;
import com.zju.campustour.presenter.protocal.event.LoadingDone;
import com.zju.campustour.presenter.protocal.event.NetworkChangeEvent;
import com.zju.campustour.view.activity.ProjectActivity;
import com.zju.campustour.view.activity.UserActivity;
import com.zju.campustour.view.adapter.ProjectInfoAdapter;
import com.zju.campustour.view.adapter.UserInfoAdapter;
import com.zju.campustour.view.iview.ISearchProjectInfoView;
import com.zju.campustour.view.iview.ISearchUserInfoView;
import com.zju.campustour.view.widget.DividerItemDecortion;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static com.zju.campustour.model.common.Constants.STATE_MORE;
import static com.zju.campustour.model.common.Constants.STATE_REFRESH;

/*最热门活动*/
public class InformChildFourFragment extends BaseFragment implements ISearchProjectInfoView {

    private String TAG = "InformChildFourFragment";
    private View mRootView;
    private RecyclerView mRecyclerView;
    private int state = Constants.STATE_NORMAL;
    private MaterialRefreshLayout mMaterialRefreshLayout;


    private LinearLayout noResultHintWrapper;
    private TextView noResultHint;
    boolean isRefreshing = false;
    private ProjectInfoAdapter mProjectAdapter;
    //获取project信息的接口实现
    private ProjectInfoOpPresenterImpl mProjectInfoPresenter;
    //设置一个全局变量保存已有的project，只在单独更新某个item时使用
    private List<Project> mProjectList;
    private boolean isLatest = false;
    private boolean isHotest = false;


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
                    refreshProjectItemInfoData();
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
                    loadMoreProjectInfoData();
                }
                else{
                    mMaterialRefreshLayout.finishRefreshLoadMore();
                }
            }
        });
    }

    private void loadMoreProjectInfoData() {
        state = Constants.STATE_MORE;
        mProjectInfoPresenter.getLimitProjectInfo(mProjectAdapter.getDatas().size(),10,isLatest,isHotest,true);
        isRefreshing = false;
    }


    private void refreshProjectItemInfoData() {
        state = Constants.STATE_REFRESH;
        mProjectInfoPresenter.getLimitProjectInfo(0,10,isLatest,isHotest,true);
    }

    private void initFragmentView() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.fragment_search_recycle_view);
        noResultHintWrapper = (LinearLayout) mRootView.findViewById(R.id.fragment_search_noResult_hint_wrapper);
        noResultHint = (TextView) mRootView.findViewById(R.id.fragment_search_noResult_hint);
        mMaterialRefreshLayout = (MaterialRefreshLayout) mRootView.findViewById(R.id.refresh_view);


        mProjectInfoPresenter = new ProjectInfoOpPresenterImpl(this,getContext());
        isLatest = true;
        isHotest = false;
        state = Constants.STATE_NORMAL;


    }

    private void showProjectRecycleView(List<Project> projectList) {

        if (mProjectAdapter == null)
            mProjectAdapter = new ProjectInfoAdapter(projectList, Constants.FULL_VIEW,getContext());

        switch (state) {

            case Constants.STATE_NORMAL:
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

                mProjectAdapter.setOnProjectItemClickListener(new ProjectInfoAdapter.onProjectItemClickListener() {
                    @Override
                    public void onClick(View v, int position, Project project) {
                        Intent mIntent = new Intent(getActivity(), ProjectActivity.class);
                        mIntent.putExtra("project", project);
                        mIntent.putExtra("position",position);
                        startActivity(mIntent);
                    }
                });
                mProjectAdapter.setOnProjectProviderClickListener(new ProjectInfoAdapter.onProjectProviderClickListener() {
                    @Override
                    public void onClick(View v, int position, User user) {
                        Intent mIntent = new Intent(getActivity(), UserActivity.class);
                        mIntent.putExtra("provider",user);
                        mIntent.putExtra("position",position);
                        startActivity(mIntent);
                    }
                });
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setAdapter(mProjectAdapter);
                Log.d(TAG, "------------loading project info done----------");
                EventBus.getDefault().post(new LoadingDone(true));
                break;

            case Constants.STATE_REFRESH:
                mMaterialRefreshLayout.finishRefresh();
                mProjectAdapter.clearData();
                mProjectAdapter.addData(projectList);
                mRecyclerView.scrollToPosition(0);

                break;

            case Constants.STATE_MORE:
                mMaterialRefreshLayout.finishRefreshLoadMore();
                mProjectAdapter.addData(mProjectAdapter.getDatas().size(), projectList);
                mRecyclerView.scrollToPosition(mProjectAdapter.getDatas().size());

                if (projectList.size() < 10){
                    showToast(getContext(),"已经获取全部数据");
                }
                break;
            default:
                break;

        }

        mProjectList = mProjectAdapter.getDatas();
    }


    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onNetworkChangeEvent(NetworkChangeEvent event) {
        if (event.isValid()){
            isNetworkUseful = true;
            mMaterialRefreshLayout.setLoadMore(true);
            if (mProjectList == null || mProjectList.size() == 0) {
                state = Constants.STATE_NORMAL;
                mProjectInfoPresenter.getLimitProjectInfo(0,10,isLatest,isHotest,true);
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
        if (mProjectList == null || mProjectList.size() == 0)
            mProjectInfoPresenter.getLimitProjectInfo(0,10,isLatest,isHotest,true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//解除订阅
    }
    /**
     * 云端获取project数据后显示
     * @param mProjects
     */
    @Override
    public void onGetProjectInfoDone(List<? extends Object> mProjects) {

        if (mProjects.size() > 0 && mProjects.size() <= 10){
            if (mProjectList != null && state == Constants.STATE_NORMAL)
                state = Constants.STATE_REFRESH;

            noResultHintWrapper.setVisibility(View.GONE);
            noResultHint.setVisibility(View.GONE);
            showProjectRecycleView((List<Project>)mProjects);
        }
        else if (mProjects.size() == 0){
            try {
                mMaterialRefreshLayout.finishRefresh();
                mMaterialRefreshLayout.finishRefreshLoadMore();

                if (state == Constants.STATE_NORMAL) {
                    showToast(getContext(),"你好，请检查网络连接是否正常");
                    showWrongHint("网络连接已经断开~");
                } else if (state == Constants.STATE_REFRESH) {
                    mProjectAdapter.clearData();
                    showWrongHint("报告，没找到相关活动");
                } else {
                    showToast(getContext(),"已经为你找到所有符合条件的活动");
                    mMaterialRefreshLayout.setLoadMore(false);
                }
            }catch (Exception e){
                Log.w(TAG,"Null Pointer Exception");
            }

        }

    }

    private void showWrongHint(String mText) {

        if (mProjectList == null || mProjectList.size() == 0) {
            noResultHintWrapper.setVisibility(View.VISIBLE);
            noResultHint.setVisibility(View.VISIBLE);
            noResultHint.setText(mText);
        }
    }

    @Override
    public void onGetProjectInfoError(Exception e) {

        showWrongHint("查询出错啦~");

        showToast(getContext(),"报告,查询出错啦~请稍后再试~");
        mMaterialRefreshLayout.finishRefresh();
        mMaterialRefreshLayout.finishRefreshLoadMore();
        mMaterialRefreshLayout.setLoadMore(false);

    }

}
