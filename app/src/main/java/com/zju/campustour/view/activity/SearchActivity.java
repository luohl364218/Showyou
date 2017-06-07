package com.zju.campustour.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.parse.ParseException;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.implement.UserInfoOpPresenterImpl;
import com.zju.campustour.presenter.ipresenter.IUserInfoOpPresenter;
import com.zju.campustour.presenter.listener.MyTextWatch;
import com.zju.campustour.presenter.protocal.event.AreaAndSchoolSelectedEvent;
import com.zju.campustour.view.iview.ISearchUserInfoView;
import com.zju.campustour.view.adapter.UserInfoAdapter;
import com.zju.campustour.view.widget.DividerItemDecortion;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends BaseActivity implements ISearchUserInfoView {

    @BindView(R.id.search_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.fab)
    FloatingActionButton mFloatingActionButton;

    @BindView(R.id.searchContentEditText)
    EditText searchContent;

    @BindView(R.id.btn_search_confirm)
    Button confirmBtn;

    @BindView(R.id.search_result_recycle_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.search_refresh_view)
    MaterialRefreshLayout mMaterialRefreshLayout;

    private int state = Constants.STATE_NORMAL;
    //area index in array
    int searchArea = -1;
    String searchSchool = null;
    String searchMajor = null;
    IUserInfoOpPresenter mUserInfoOpPresenter;
    boolean isOrderByFans = false;
    boolean isOrderByLatest = false;
    private UserInfoAdapter mItemInfoAdapter;
    private Context mContext = this;

    //设置一个全局变量保存已有的user，只在单独更新某个item时使用
    private List<User> mUserList;
    boolean isRefreshing = false;
    //设置标志位，是否是输入搜索还是学校选择
    boolean isSearchContent = false;
    String conditions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        EventBus.getDefault().register(this);
        confirmBtn.setEnabled(false);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

        initRefreshLayout();
        //让按钮随着输入内容有效而使能
        searchContent.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                confirmBtn.setEnabled(!TextUtils.isEmpty(s.toString()));
            }
        });

        mUserInfoOpPresenter = new UserInfoOpPresenterImpl(this,this);
        isOrderByFans = false;
        isOrderByLatest = true;

        searchContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    //如果actionId是搜索的id，则进行下一步的操作

                    searchSchoolmate();
                }

                return false;
            }
        });
    }


    private void initRefreshLayout() {
        mMaterialRefreshLayout.setLoadMore(true);
        mMaterialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {

                //下拉刷新...
                isNetworkUseful = NetworkUtil.isNetworkAvailable(mContext);
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
        if (isSearchContent){
            mUserInfoOpPresenter.searchRelativeUserWithConditions(conditions,0);
        }
        else
            mUserInfoOpPresenter.queryProviderUserWithConditions(searchSchool,searchMajor,0,searchArea, -1,isOrderByFans, isOrderByLatest);
        //showLocalServiceItemInfoData();


    }

    private void loadMoreServiceInfoData(){

        state = Constants.STATE_MORE;
        if (isSearchContent) {
            mUserInfoOpPresenter.searchRelativeUserWithConditions(conditions, mItemInfoAdapter.getDatas().size());
        }
        else {
            mUserInfoOpPresenter.queryProviderUserWithConditions(searchSchool,searchMajor,mItemInfoAdapter.getDatas().size(),searchArea,-1,isOrderByFans, isOrderByLatest);
            isRefreshing = false;
            //mMaterialRefreshLayout.finishRefreshLoadMore();
        }


    }

    @OnClick(R.id.btn_search_confirm)
    public void searchSchoolmate(){
        conditions = searchContent.getText().toString().trim();
        if (TextUtils.isEmpty(conditions)){
            showToast("搜索内容不能为空");
            return;
        }

        InputMethodManager imm =(InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(confirmBtn.getWindowToken(), 0);

        isSearchContent = true;
        state = Constants.STATE_REFRESH;
        mUserInfoOpPresenter.searchRelativeUserWithConditions(conditions,0);

    }


    @OnClick(R.id.fab)
    public void openSchoolList(){
            Intent mIntent = new Intent(this, SchoolListActivity.class);
            startActivity(mIntent);
    }


    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSearchProviderWithConditions(AreaAndSchoolSelectedEvent event) {
        isSearchContent = false;
        searchSchool = event.getSchool();
        searchArea = event.getArea();
        state = Constants.STATE_REFRESH;
        mUserInfoOpPresenter.queryProviderUserWithConditions(searchSchool, searchMajor, 0, searchArea,-1,isOrderByFans, isOrderByLatest);

    }


    @Override
    public void onGetProviderUserDone(List<User> mUsers) {
        if (mUsers.size() != 0){
            if (mUserList == null)
                state = Constants.STATE_NORMAL;

            showRecycleView(mUsers);
        }
        else {
            try {
                if (mUserList == null)
                    state = Constants.STATE_NORMAL;
                mMaterialRefreshLayout.finishRefresh();
                mMaterialRefreshLayout.finishRefreshLoadMore();

                if (state == Constants.STATE_NORMAL) {
                    showToast("没有符合条件的校友");
                } else if (state == Constants.STATE_REFRESH) {
                    showToast("没有符合条件的校友");
                    mItemInfoAdapter.clearData();
                } else {
                    showToast("已经为你找到所有符合条件的同学");
                    mMaterialRefreshLayout.setLoadMore(false);
                }
            }catch (Exception e){
            }

        }
    }

    private void showRecycleView(List<User> mUsers) {
        if (mItemInfoAdapter == null)
            mItemInfoAdapter = new UserInfoAdapter(mUsers);
        switch (state){

            case Constants.STATE_NORMAL:
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                mItemInfoAdapter.setOnCardViewItemClickListener(new UserInfoAdapter.onCardViewItemClickListener() {
                    @Override
                    public void onClick(View v, int position, User provider) {
                        Intent mIntent = new Intent(SearchActivity.this, UserActivity.class);
                        mIntent.putExtra("provider",provider);
                        mIntent.putExtra("position",position);
                        startActivity(mIntent);
                    }
                });

                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setAdapter(mItemInfoAdapter);
                mRecyclerView.addItemDecoration(new DividerItemDecortion());
                if (mUsers.size() < 10){
                    showToast("已经获取全部数据");
                    mMaterialRefreshLayout.setLoadMore(false);
                }
                break;

            case Constants.STATE_REFRESH:

                mItemInfoAdapter.clearData();
                mItemInfoAdapter.addData(mUsers);
                mRecyclerView.scrollToPosition(0);

                break;

            case Constants.STATE_MORE:

                mItemInfoAdapter.addData(mItemInfoAdapter.getDatas().size(), mUsers);
                mRecyclerView.scrollToPosition(mItemInfoAdapter.getDatas().size());

                if (mUsers.size() < 10){
                    showToast("已经获取全部数据");
                    mMaterialRefreshLayout.setLoadMore(false);
                }
                break;
        }

        if (mMaterialRefreshLayout != null){
            mMaterialRefreshLayout.finishRefresh();
            mMaterialRefreshLayout.finishRefreshLoadMore();
        }

        mUserList = mItemInfoAdapter.getDatas();
    }

    @Override
    public void onGetProviderUserError(ParseException e) {

        showToast("报告,查询出错啦~请稍后再试~");
        mMaterialRefreshLayout.finishRefresh();
        mMaterialRefreshLayout.finishRefreshLoadMore();
        mMaterialRefreshLayout.setLoadMore(false);
    }

    @Override
    public void refreshUserOnlineState(boolean isOnline) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
