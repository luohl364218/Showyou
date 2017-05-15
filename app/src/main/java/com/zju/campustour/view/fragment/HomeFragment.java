package com.zju.campustour.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.zju.campustour.model.database.models.Project;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.model.util.DbUtils;
import com.zju.campustour.presenter.implement.ProjectInfoOpPresenterImpl;
import com.zju.campustour.presenter.implement.UserInfoOpPresenterImpl;
import com.zju.campustour.presenter.protocal.enumerate.UserType;
import com.zju.campustour.presenter.protocal.event.LoginDoneEvent;
import com.zju.campustour.presenter.protocal.event.ToolbarItemClickEvent;
import com.zju.campustour.presenter.protocal.event.LoadingDone;
import com.zju.campustour.presenter.protocal.event.NetworkChangeEvent;
import com.zju.campustour.presenter.protocal.event.RecycleViewRefreshEvent;
import com.zju.campustour.view.IView.ISearchProjectInfoView;
import com.zju.campustour.view.IView.IUserLoginView;
import com.zju.campustour.view.activity.InfoWebActivity;
import com.zju.campustour.view.activity.LoginActivity;
import com.zju.campustour.view.activity.MajorListActivity;
import com.zju.campustour.view.activity.ProjectActivity;
import com.zju.campustour.view.activity.ProviderHomePageActivity;
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

public class HomeFragment extends Fragment implements View.OnClickListener, ISearchProjectInfoView{

    private View mRootView;
    private Toolbar mToolbar;
    private SliderLayout mSliderShow;
   /* private PullToRefreshScrollView mMaterialRefreshLayout;*/
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
    private Button hotMajorBtn;
    private Button remoteChatBtn;
    //专家推荐列表
    private RecyclerView mRecyclerView;
    private ProjectInfoAdapter mProjectAdapter;

    private TextView noResultHint;
    private int state = STATE_NORMAL;
    private boolean isNetworkValid = true;
    private boolean isGotAllData = false;

    //获取project信息的接口实现
    private ProjectInfoOpPresenterImpl mProjectInfoPresenter;
    //设置一个全局变量保存已有的project，只在单独更新某个item时使用
    private List<Project> mProjectList;

    //定义二维码扫描请求
    private int REQUEST_CODE = 1;
    private int CAMERA_REQUEST_CODE = 2;

    private String TAG = "HomeFragment";
    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null){
            mRootView = inflater.inflate(R.layout.fragment_home, container, false);
            initView();
            initSlideImages();
            mProjectInfoPresenter = new ProjectInfoOpPresenterImpl(this);
            mProjectInfoPresenter.getLimitProjectInfo(0,10);
            EventBus.getDefault().register(this);
            //initRefreshLayout();

        }

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null){
            parent.removeView(mRootView);
        }
        Log.d(TAG,"second create view--------------------");
        return mRootView;
    }

    private void initView() {
        mToolbar = (Toolbar) mRootView.findViewById(R.id.fragment_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mToolbar.setTitle("校游 Show You");
        mToolbar.setNavigationIcon(R.mipmap.icon_user_default);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                if (!drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        setHasOptionsMenu(true);

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
        hotMajorBtn = (Button)mRootView.findViewById(R.id.fragment_home_hotmajor_btn);
        remoteChatBtn = (Button)mRootView.findViewById(R.id.fragment_home_remotechat_btn);
        noResultHint = (TextView) mRootView.findViewById(R.id.fragment_home_noResult_hint);
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

        hotMajorBtn.setOnClickListener(this);
        remoteChatBtn.setOnClickListener(this);

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
                    Bundle bundle = new Bundle();
                    bundle.putString("web",Constants.imagewebs[(position + imageUrls.length )%imageUrls.length]);
                    intent.putExtras(bundle);

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
        if (mProjects.size() != 0){
            noResultHint.setVisibility(View.GONE);
            if (mProjectAdapter == null)
                state = STATE_NORMAL;
            showProjectRecycleView((List<Project>) mProjects);
        }
        else {
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

                mProjectAdapter = new ProjectInfoAdapter(projectList, Constants.FULL_VIEW,getContext());
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
                        Intent mIntent = new Intent(getActivity(), ProviderHomePageActivity.class);
                        mIntent.putExtra("provider",user);
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
                    Toast.makeText(getActivity(), "已经获取全部数据", Toast.LENGTH_SHORT).show();
                    isGotAllData = true;
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
            noResultHint.setVisibility(View.GONE);
            isNetworkValid = true;
           /* mProjectInfoPresenter.getLimitProjectInfo(0,10);*/
        }
        else {
            isNetworkValid = false;
            noResultHint.setVisibility(View.VISIBLE);
            noResultHint.setText("网络连接已经断开");
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
            if (mProjectInfoPresenter != null && mProjectAdapter == null)
                mProjectInfoPresenter.getLimitProjectInfo(0,10);
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

            case R.id.fragment_home_hotmajor_btn:
                Toast.makeText(getActivity(), "Sorry 此功能我们将在5月初完善", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fragment_home_remotechat_btn:
                Toast.makeText(getActivity(), "Sorry 此功能我们将在5月初完善", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//解除订阅
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater mInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        mInflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //点击二维码扫描
        EventBus.getDefault().post(new ToolbarItemClickEvent(id));
        return true;
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
        mProjectInfoPresenter.getLimitProjectInfo(mProjectAdapter.getDatas().size(),10);
    }

    private void refreshProjectItemInfoData() {
        state = Constants.STATE_REFRESH;
        mProjectInfoPresenter.getLimitProjectInfo(0,10);
    }
}
