package com.zju.campustour.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.database.data.SchoolData;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.implement.UserInfoOpPresenterImpl;
import com.zju.campustour.presenter.ipresenter.IUserInfoOpPresenter;
import com.zju.campustour.presenter.protocal.event.AreaAndSchoolSelectedEvent;
import com.zju.campustour.presenter.protocal.event.LoadingDone;
import com.zju.campustour.presenter.protocal.event.NetworkChangeEvent;
import com.zju.campustour.view.IView.ISearchUserInfoView;
import com.zju.campustour.view.activity.SearchActivity;
import com.zju.campustour.view.activity.UserActivity;
import com.zju.campustour.view.activity.SchoolListActivity;
import com.zju.campustour.view.adapter.UserInfoAdapter;
import com.zju.campustour.view.widget.DividerItemDecortion;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


/**
 * Created by HeyLink on 2017/4/1.
 */

public class SearchFragment extends BaseFragment implements ISearchUserInfoView,TabLayout.OnTabSelectedListener {

    private String TAG = "SearchFragment";
    private View mRootView;
    private RecyclerView mRecyclerView;
    private UserInfoAdapter mItemInfoAdapter;
    private int state = Constants.STATE_NORMAL;
    private MaterialRefreshLayout mMaterialRefreshLayout;
    IUserInfoOpPresenter mUserInfoOpPresenter;

    private LinearLayout noResultHintWrapper;
    private TextView noResultHint;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;

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

    public SearchFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);//加上这句话，menu才会显示出来
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null){
            mRootView = inflater.inflate(R.layout.fragment_search, container, false);
            EventBus.getDefault().register(this);
            initFragmentView();
            initRefreshLayout();
            Log.d(TAG,"first create view--------------------");

        }

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null){
            parent.removeView(mRootView);
        }
        Log.d(TAG,"【SearchFragment】第二次创建--------------------");
        return mRootView;
    }

    private void initFragmentView() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.fragment_search_recycle_view);
        noResultHintWrapper = (LinearLayout) mRootView.findViewById(R.id.fragment_search_noResult_hint_wrapper);
        noResultHint = (TextView) mRootView.findViewById(R.id.fragment_search_noResult_hint);
        mMaterialRefreshLayout = (MaterialRefreshLayout) mRootView.findViewById(R.id.refresh_view);
        mTabLayout = (TabLayout) mRootView.findViewById(R.id.fragment_search_tab_layout);
        mToolbar = (Toolbar) mRootView.findViewById(R.id.fragment_search_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        mToolbar.inflateMenu(R.menu.search_fragment_menu);
        mToolbar.setTitle(R.string.toolbar_title);
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

        mUserInfoOpPresenter = new UserInfoOpPresenterImpl(this,getContext());
        isOrderByFans = false;
        isOrderByLatest = true;
        state = Constants.STATE_NORMAL;
        mUserInfoOpPresenter.queryProviderUserWithConditions(searchSchool,searchMajor,
                0,-1,-1,isOrderByFans,isOrderByLatest,isMajorNotCommon);
        initTab();
    }

    private void initTab(){


        TabLayout.Tab tab= mTabLayout.newTab();
        tab.setText("最新");
        tab.setTag(Constants.TAG_LATEST);

        mTabLayout.addTab(tab);

        tab= mTabLayout.newTab();
        tab.setText("热门");
        tab.setTag(Constants.TAG_HOT_RECOMMEND);

        mTabLayout.addTab(tab);

        tab= mTabLayout.newTab();
        tab.setText("省内");
        tab.setTag(Constants.TAG_SAME_PROVINCE);

        mTabLayout.addTab(tab);


        mTabLayout.setOnTabSelectedListener(this);

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

    private void refreshServiceItemInfoData(){

        state = Constants.STATE_REFRESH;
        mUserInfoOpPresenter.queryProviderUserWithConditions(searchSchool,searchMajor,0,searchArea, -1,isOrderByFans, isOrderByLatest,isMajorNotCommon);
        //showLocalServiceItemInfoData();


    }

    private void loadMoreServiceInfoData(){

        state = Constants.STATE_MORE;
        mUserInfoOpPresenter.queryProviderUserWithConditions(searchSchool,searchMajor,mItemInfoAdapter.getDatas().size(),searchArea,-1,isOrderByFans, isOrderByLatest,isMajorNotCommon);
        isRefreshing = false;
        //mMaterialRefreshLayout.finishRefreshLoadMore();

    }

    @Override
    public void onGetProviderUserDone(List<User> mUsers) {

        if (mUsers.size() != 0){
            if (mUserList != null && state == Constants.STATE_NORMAL)
                state = Constants.STATE_REFRESH;

            noResultHintWrapper.setVisibility(View.GONE);
            noResultHint.setVisibility(View.GONE);
            showRecycleView(mUsers);
        }
        else {
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
            isNetworkUseful = false;
            mMaterialRefreshLayout.finishRefresh();
            mMaterialRefreshLayout.finishRefreshLoadMore();
            mMaterialRefreshLayout.setLoadMore(false);
            showToast(getContext(),"网络连接已经断开");
        }
    }

   /* @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onHomeFragmentLoadingDone(LoadingDone done){
        if (done.isDone()){
            Log.d(TAG,"------------loading user info----------");
            isOrderByFans = false;
            isOrderByLatest = true;
            if (mItemInfoAdapter == null)
                state = Constants.STATE_NORMAL;
            else
                state = Constants.STATE_REFRESH;

            if (mFloatingActionButton!= null)
                mFloatingActionButton.show();
            if (isNetworkUseful)
                mUserInfoOpPresenter.queryProviderUserWithConditions(searchSchool,searchMajor,
                        0,-1,-1,isOrderByFans,isOrderByLatest,isMajorNotCommon);
            else {
                showWrongHint("网络连接出错啦~");
            }
        }

    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//解除订阅
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

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater mInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, mInflater);
        mInflater.inflate(R.menu.search_fragment_menu, menu);
        if (isMajorNotCommon)
            menu.getItem(0).setIcon(R.mipmap.icon_major_user);
        else
            menu.getItem(0).setIcon(R.mipmap.icon_common_user);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.list_grid_change_btn){
            if (isMajorNotCommon){
                showToast(getContext(),"普通用户");
                item.setIcon(R.mipmap.icon_common_user);
                isMajorNotCommon = false;
                state = Constants.STATE_REFRESH;
                mUserInfoOpPresenter.queryProviderUserWithConditions(searchSchool,searchMajor,0,searchArea, -1,isOrderByFans,isOrderByLatest,isMajorNotCommon);

            }
            else {
                showToast(getContext(),"专业用户");
                item.setIcon(R.mipmap.icon_major_user);
                isMajorNotCommon = true;
                state = Constants.STATE_REFRESH;
                mUserInfoOpPresenter.queryProviderUserWithConditions(searchSchool,searchMajor,0,searchArea, -1,isOrderByFans,isOrderByLatest,isMajorNotCommon);
            }

        }
        else if (id == R.id.fragment_search_btn){
            Intent mIntent = new Intent(getActivity(),SearchActivity.class);
            startActivity(mIntent);
        }

        return true;
    }
}
