package com.zju.campustour.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.database.models.HomepageSlideImg;
import com.zju.campustour.model.database.models.Project;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.model.util.DbUtils;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.implement.HomePageImgLoader;
import com.zju.campustour.presenter.implement.ProjectInfoOpPresenterImpl;
import com.zju.campustour.presenter.protocal.event.LoginDoneEvent;
import com.zju.campustour.presenter.protocal.event.ToolbarItemClickEvent;
import com.zju.campustour.presenter.protocal.event.LoadingDone;
import com.zju.campustour.presenter.protocal.event.NetworkChangeEvent;
import com.zju.campustour.presenter.protocal.event.RecycleViewRefreshEvent;
import com.zju.campustour.view.iview.IHomepageImgLoadView;
import com.zju.campustour.view.iview.ISearchProjectInfoView;
import com.zju.campustour.view.activity.InfoWebActivity;
import com.zju.campustour.view.activity.LoginActivity;
import com.zju.campustour.view.activity.MajorListActivity;
import com.zju.campustour.view.activity.ProjectActivity;
import com.zju.campustour.view.activity.UserActivity;
import com.zju.campustour.view.adapter.ProjectInfoAdapter;
import com.zju.campustour.view.widget.DividerItemDecortion;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


import static com.zju.campustour.model.common.Constants.STATE_MORE;
import static com.zju.campustour.model.common.Constants.STATE_NORMAL;
import static com.zju.campustour.model.common.Constants.STATE_REFRESH;
import static com.zju.campustour.model.common.Constants.imageUrls;

/**
 * Created by HeyLink on 2017/4/1.
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener, ISearchProjectInfoView,IHomepageImgLoadView{

    private View mRootView;
    private SliderLayout mSliderShow;
    private ImageButton userBtn;
    private ImageButton scanBtn;
    //首页科目分类按钮
    private LinearLayout renwenBtn;
    private LinearLayout gongxueBtn;
    private LinearLayout lixueBtn;
    private LinearLayout yixueBtn;
    private LinearLayout nongxueBtn;
    private LinearLayout jingjixueBtn;
    private LinearLayout yishuxueBtn;
    private LinearLayout allSubjectsBtn;
    //两大话题按钮入口
    private Button latestBtn;
    private Button hotestBtn;

    private LinearLayout latestIcon;
    private LinearLayout hotestIcon;
    //专家推荐列表
    private RecyclerView mRecyclerView;
    private ProjectInfoAdapter mProjectAdapter;

    private HomePageImgLoader mHomePageImgLoader;

    private TextView noResultHint;
    private int state = STATE_NORMAL;

    //获取project信息的接口实现
    private ProjectInfoOpPresenterImpl mProjectInfoPresenter;
    //设置一个全局变量保存已有的project，只在单独更新某个item时使用
    private List<Project> mProjectList;

    //定义二维码扫描请求

    private boolean isLatest = false;
    private boolean isHotest = false;

    private String TAG = "HomeFragment";
    public HomeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null){
            mRootView = inflater.inflate(R.layout.fragment_home, container, false);
            initView();

            mProjectInfoPresenter = new ProjectInfoOpPresenterImpl(this,getContext());

            /*异步请求首页轮播数据*/
            mHomePageImgLoader = new HomePageImgLoader(getContext(),this);
            mHomePageImgLoader.getImgList();

            isLatest = false;
            isHotest = false;
            mProjectInfoPresenter.getLimitProjectInfo(0,10,isLatest,isHotest);

            EventBus.getDefault().register(this);

        }

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null){
            parent.removeView(mRootView);
        }
        Log.d(TAG,"second create view--------------------");
        return mRootView;
    }

    private void initView() {

        userBtn = (ImageButton) mRootView.findViewById(R.id.fragment_home_user_icon);
        scanBtn = (ImageButton) mRootView.findViewById(R.id.fragment_home_right_btn);

        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                if (!drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        scanBtn.setOnClickListener(this);


        //mMaterialRefreshLayout = (PullToRefreshScrollView) mRootView.findViewById(R.id.home_refresh_view);
        renwenBtn = (LinearLayout)mRootView.findViewById(R.id.fragment_home_renwen);
        gongxueBtn = (LinearLayout)mRootView.findViewById(R.id.fragment_home_gongxue);
        lixueBtn = (LinearLayout)mRootView.findViewById(R.id.fragment_home_lixue);
        yixueBtn = (LinearLayout)mRootView.findViewById(R.id.fragment_home_yixue);
        nongxueBtn = (LinearLayout)mRootView.findViewById(R.id.fragment_home_nongxue);
        jingjixueBtn = (LinearLayout)mRootView.findViewById(R.id.fragment_home_jingjixue);
        yishuxueBtn = (LinearLayout)mRootView.findViewById(R.id.fragment_home_yishuxue);
        allSubjectsBtn = (LinearLayout)mRootView.findViewById(R.id.fragment_home_allsubjects);
        //两大话题按钮入口
        latestBtn = (Button)mRootView.findViewById(R.id.fragment_home_latest_btn);
        hotestBtn = (Button)mRootView.findViewById(R.id.fragment_home_hotest_btn);

        latestIcon = (LinearLayout) mRootView.findViewById(R.id.latest_project_recomment);
        hotestIcon = (LinearLayout) mRootView.findViewById(R.id.hot_project_recomment);

        noResultHint = (TextView) mRootView.findViewById(R.id.fragment_home_noResult_hint);
        if (!isNetworkUseful){
            noResultHint.setText("无法连接到服务器");
        }
        //专家推荐列表
        mRecyclerView = (RecyclerView)mRootView.findViewById(R.id.fragment_home_recycle_view);

        renwenBtn.setOnClickListener(this);
        gongxueBtn.setOnClickListener(this);
        lixueBtn.setOnClickListener(this);
        nongxueBtn.setOnClickListener(this);
        jingjixueBtn.setOnClickListener(this);
        yishuxueBtn.setOnClickListener(this);
        yixueBtn.setOnClickListener(this);
        allSubjectsBtn.setOnClickListener(this);

        latestBtn.setOnClickListener(this);
        hotestBtn.setOnClickListener(this);

    }

    private void initSlideImages() {
        mSliderShow = (SliderLayout) mRootView.findViewById(R.id.slider);

        for (int i = 0; i < Constants.imageUrls.length; i++) {
            TextSliderView slideView = new TextSliderView(this.getActivity());
            slideView.image(Constants.imageUrls[i]);
            slideView.description(Constants.imageContentDescs[i]);
            slideView.setScaleType(BaseSliderView.ScaleType.Fit);
            int position = i;
            slideView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    Intent intent = new Intent(getActivity(), InfoWebActivity.class);
                    intent.putExtra("web",Constants.imagewebs[(position + imageUrls.length )%imageUrls.length]);
                    getActivity().startActivity(intent);
                }
            });
            mSliderShow.addSlider(slideView);
        }

        mSliderShow.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        mSliderShow.startAutoCycle();
        mSliderShow.setDuration(3000);
    }

    private void initSlideImagesOnline(List<HomepageSlideImg> mSlideImgs) {
        mSliderShow = (SliderLayout) mRootView.findViewById(R.id.slider);

        for (int i = 0; i < mSlideImgs.size(); i++) {
            HomepageSlideImg mSlideImg = mSlideImgs.get(i);
            TextSliderView slideView = new TextSliderView(this.getActivity());
            slideView.image(mSlideImg.getImgUrl());
            slideView.description(mSlideImg.getDescription());
            slideView.setScaleType(BaseSliderView.ScaleType.Fit);
            int position = i;
            slideView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    Intent intent = new Intent(getActivity(), InfoWebActivity.class);
                    intent.putExtra("web",mSlideImg.getLinkUrl());
                    getActivity().startActivity(intent);
                }
            });
            mSliderShow.addSlider(slideView);
        }

        mSliderShow.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        mSliderShow.startAutoCycle();
        mSliderShow.setDuration(3000);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mSliderShow!=null){
            mSliderShow.startAutoCycle();
        }
    }

    /**
     * 跳转到对应的专业列表处
     * @param position
     */
    void startActivityWithData(int position){

        Intent intent = new Intent(getActivity(),MajorListActivity.class);
        intent.putExtra("position",position);

        startActivity(intent);
    }

    /**
     * 云端获取project数据后显示
     * @param mProjects
     */
    @Override
    public void onGetProjectInfoDone(List<? extends Object> mProjects) {
        //mRecyclerView
        if (mProjects.size() > 0 && mProjects.size() <= 10){
            noResultHint.setVisibility(View.GONE);
            if (mProjectAdapter == null)
                state = STATE_NORMAL;
            showProjectRecycleView((List<Project>)mProjects);
        }
        else if (mProjects.size() == 0){
            //showLocalProjectItemInfoData();
            //mMaterialRefreshLayout.onRefreshComplete();
            noResultHint.setVisibility(View.VISIBLE);
            noResultHint.setText("请检查网络连接是否断开");

        }
    }

    @Override
    public void onGetProjectInfoError(Exception e) {

        ParseUser.logOut();
        Intent mIntent = new Intent(getActivity(), LoginActivity.class);
        startActivity(mIntent);

    }

    private void showProjectRecycleView(List<Project> projectList) {

        if (mProjectAdapter == null)
            mProjectAdapter = new ProjectInfoAdapter(projectList, Constants.FULL_VIEW,getContext());

        switch (state) {

            case Constants.STATE_NORMAL:
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()) {
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                };
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
                mRecyclerView.addItemDecoration(new DividerItemDecortion());
                Log.d(TAG, "------------loading project info done----------");
                EventBus.getDefault().post(new LoadingDone(true));
                break;
            case STATE_REFRESH:
                //mMaterialRefreshLayout.onRefreshComplete();
                //mMaterialRefreshLayout.finishRefresh();
                mProjectAdapter.clearData();
                mProjectAdapter.addData(projectList);
                mRecyclerView.scrollToPosition(0);
                break;
            case STATE_MORE:
                //mMaterialRefreshLayout.onRefreshComplete();
                //mMaterialRefreshLayout.finishRefreshLoadMore();
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
        if (NetworkUtil.isNetworkAvailable(getContext())){
            noResultHint.setVisibility(View.GONE);
            isNetworkUseful = true;
            /*如果当前界面已经显示了就不需要刷新*/
            if (mProjectList == null || mProjectList.size() == 0){
                state = STATE_NORMAL;
                isLatest = false;
                isHotest = false;
                mProjectInfoPresenter.getLimitProjectInfo(0,10,isLatest,isHotest);
            }

        }
        else {
            isNetworkUseful = false;
            if (mProjectList == null || mProjectList.size() == 0){
                noResultHint.setVisibility(View.VISIBLE);
                noResultHint.setText("网络连接不可用");
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onRecycleViewRefreshEvent(RecycleViewRefreshEvent event) {
        int selectedPosition = event.getPosition();
        if (selectedPosition >= 0 && mProjectList != null){
            ParseQuery<ParseObject> query_project = ParseQuery.getQuery("Project").include("providerV2")
                    .whereEqualTo("objectId",mProjectList.get(selectedPosition).getId());
            query_project.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null && objects.size() != 0){
                        ((DefaultItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                        Project mProject = DbUtils.getProject(objects.get(0));
                        mProjectAdapter.refreshSelectedData(selectedPosition,mProject);
                    }
                }
            });
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onLoginDoneEvent(LoginDoneEvent event) {
        if (event.isLogin()){
            if (mProjectInfoPresenter != null && mProjectAdapter == null){
                isLatest = false;
                isHotest = false;
                mProjectInfoPresenter.getLimitProjectInfo(0,10,isLatest,isHotest);
            }

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mSliderShow!=null){
            mSliderShow.stopAutoCycle();
        }
    }

    @Override
    public void onClick(View v) {
        int btnId = v.getId();

        switch (btnId){
            case R.id.fragment_home_renwen:
                startActivityWithData(4);
                break;
            case R.id.fragment_home_gongxue:
                startActivityWithData(7);
                break;
            case R.id.fragment_home_lixue:
                startActivityWithData(6);
                break;
            case R.id.fragment_home_nongxue:
                startActivityWithData(8);
                break;
            case R.id.fragment_home_yixue:
                startActivityWithData(9);
                break;
            case R.id.fragment_home_jingjixue:
                startActivityWithData(1);
                break;
            case R.id.fragment_home_yishuxue:
                startActivityWithData(11);
                break;
            case R.id.fragment_home_allsubjects:
                //全部专业
                startActivityWithData(100);
                break;

            case R.id.fragment_home_latest_btn:
                isLatest = true;
                isHotest = false;
                state = Constants.STATE_REFRESH;
                refreshProjectItemInfoData();
                latestIcon.setVisibility(View.VISIBLE);
                hotestIcon.setVisibility(View.GONE);
                break;
            case R.id.fragment_home_hotest_btn:
                isLatest = false;
                isHotest = true;
                state = Constants.STATE_REFRESH;
                refreshProjectItemInfoData();
                latestIcon.setVisibility(View.GONE);
                hotestIcon.setVisibility(View.VISIBLE);
                break;

            case R.id.fragment_home_right_btn:
                //点击二维码扫描
                EventBus.getDefault().post(new ToolbarItemClickEvent(R.id.fragment_home_right_btn));

            default:
                break;

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//解除订阅
    }

    private void initRefreshLayout() {
       /* mMaterialRefreshLayout.setMode(PullToRefreshBase.Mode.BOTH);
        mMaterialRefreshLayout.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                Log.d(TAG,"----------ready to refreshing!!!!!!---------network is "+isNetworkValid);
                //下拉刷新...
                if (isNetworkValid){
                    Log.d(TAG,"----------refreshing!!!!!!---------network is "+isNetworkValid);
                    refreshProjectItemInfoData();
                    //加载完所有后会禁止加载更多，需要通过下拉刷新恢复
                    isGotAllData = false;
                }
                else{
                    mMaterialRefreshLayout.onRefreshComplete();
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                Log.d(TAG,"onRefreshLoadMore");

                if (!isGotAllData && isNetworkValid) {
                    loadMoreProjectInfoData();
                }
                else{
                    mMaterialRefreshLayout.onRefreshComplete();
                }
            }
        });*/
    }

    private void loadMoreProjectInfoData() {
        state = Constants.STATE_MORE;
        mProjectInfoPresenter.getLimitProjectInfo(mProjectAdapter.getDatas().size(),10,false,false);
    }


    private void refreshProjectItemInfoData() {
        state = Constants.STATE_REFRESH;
        mProjectInfoPresenter.getLimitProjectInfo(0,10,isLatest,isHotest);
    }

    @Override
    public void onImgUrlGot(List<HomepageSlideImg> mHomepageSlideImgs) {

        if (mHomepageSlideImgs.size() >0){
            initSlideImagesOnline(mHomepageSlideImgs);
        }
        else {
            initSlideImages();
        }

    }
}
