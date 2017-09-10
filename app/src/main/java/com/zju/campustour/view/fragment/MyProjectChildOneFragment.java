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
import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.bean.Project;
import com.zju.campustour.model.bean.User;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.implement.NewsGetImpl;
import com.zju.campustour.presenter.implement.ProjectInfoOpPresenterImpl;
import com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType;
import com.zju.campustour.presenter.protocal.enumerate.UserType;
import com.zju.campustour.presenter.protocal.event.ProjectDeleteEvent;
import com.zju.campustour.view.activity.MyProjectActivity;
import com.zju.campustour.view.activity.ProjectActivity;
import com.zju.campustour.view.activity.UserActivity;
import com.zju.campustour.view.adapter.ProjectInfoAdapter;
import com.zju.campustour.view.iview.ISearchProjectInfoView;
import com.zju.campustour.view.widget.DividerItemDecortion;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;

import static com.zju.campustour.model.common.Constants.STATE_MORE;
import static com.zju.campustour.model.common.Constants.STATE_NORMAL;
import static com.zju.campustour.model.common.Constants.STATE_REFRESH;
import static com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType.BOOK_SUCCESS;
import static com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType.COLLECT;


public class MyProjectChildOneFragment extends BaseFragment implements ISearchProjectInfoView {



    private View mRootView;
    private RecyclerView mRecyclerView;


    private MaterialRefreshLayout mMaterialRefreshLayout;
    private ProjectInfoOpPresenterImpl mProjectInfoPresenter;
    private ParseUser currentLoginUser;
    private UserType currentUserType = UserType.USER;
    private String tabName = "你还没有发布过活动哦，快去发布吧";
    private boolean isCurrentTabMine = true;
    private boolean isRefreshing = false;
    private int state = STATE_NORMAL;
    private UserProjectStateType type = BOOK_SUCCESS;
    private ProjectInfoAdapter mProjectAdapter;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_my_project_child_one, container, false);
            EventBus.getDefault().register(this);

            currentLoginUser = ParseUser.getCurrentUser();
            Intent mIntent = getActivity().getIntent();
            currentUserType = UserType.values()[mIntent.getIntExtra("userType",0)];

            initFragmentView();
            initRefreshLayout();

            mProjectInfoPresenter = new ProjectInfoOpPresenterImpl(this,getActivity());
            mProjectInfoPresenter.queryProjectWithUserId(currentLoginUser.getObjectId(), 0, 10);

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
        if (currentUserType == UserType.PROVIDER  && isCurrentTabMine)
            mProjectInfoPresenter.queryProjectWithUserId(currentLoginUser.getObjectId(),0,10);
        else
            mProjectInfoPresenter.queryProjectWithUserIdAndState(currentLoginUser.getObjectId(),type,0,10);

    }

    private void loadMoreServiceInfoData(){

        state = Constants.STATE_MORE;
        if (currentUserType == UserType.PROVIDER  && isCurrentTabMine)
            mProjectInfoPresenter.queryProjectWithUserId(currentLoginUser.getObjectId(),mProjectAdapter.getDatas().size(),10);
        else
            mProjectInfoPresenter.queryProjectWithUserIdAndState(currentLoginUser.getObjectId(),type,mProjectAdapter.getDatas().size(),10);

        isRefreshing = false;
    }

    private void showProjectRecycleView(List<Project> projectList) {



            if (mProjectAdapter == null)
                mProjectAdapter = new ProjectInfoAdapter(projectList, Constants.COLLECT_VIEW,getActivity());

            switch (state) {

                case Constants.STATE_NORMAL:
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    if (projectList == null)
                        return;

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
                    break;
                case STATE_REFRESH:
                    mMaterialRefreshLayout.finishRefresh();
                    mProjectAdapter.clearData();
                    mProjectAdapter.addData(projectList);
                    mRecyclerView.scrollToPosition(0);
                    break;
                case STATE_MORE:
                    mMaterialRefreshLayout.finishRefreshLoadMore();
                    mProjectAdapter.addData(mProjectAdapter.getDatas().size(), projectList);
                    mRecyclerView.scrollToPosition(mProjectAdapter.getDatas().size());

                    if (projectList.size() < 10){
                        showToast(getActivity(),"已经获取全部数据");
                        mMaterialRefreshLayout.setLoadMore(false);
                    }
                    break;
                default:
                    break;

            }


    }


    @Override
    public void onGetProjectInfoDone(List<? extends Object> mProjects) {

        if (mProjects.size() != 0){
            if (mProjectAdapter == null)
                state = STATE_NORMAL;
            showProjectRecycleView((List<Project>) mProjects);
        }
        else {
            try {
                mMaterialRefreshLayout.finishRefresh();
                mMaterialRefreshLayout.finishRefreshLoadMore();
                if (state == Constants.STATE_NORMAL) {
                    showToast(getActivity(),tabName);
                } else if (state == Constants.STATE_REFRESH) {
                    showToast(getActivity(),tabName);
                    if (mProjectAdapter != null)
                        mProjectAdapter.clearData();

                } else {
                    showToast(getActivity(),"已经为你找到所有活动信息");
                    mMaterialRefreshLayout.setLoadMore(false);
                }
            }catch (Exception e){
                Log.w(TAG,"Null Pointer Exception");
            }


        }

    }



    @Override
    public void onGetProjectInfoError(Exception e) {
        showToast(getActivity(),"获取活动信息出错，请稍后再试");

    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onReceiveProjectDeleteEvent(ProjectDeleteEvent projectDeleteEvent){
        if (projectDeleteEvent.isDelete())
            refreshServiceItemInfoData();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
