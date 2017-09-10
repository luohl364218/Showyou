package com.zju.campustour.view.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.cjj.MaterialRefreshLayout;
import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.presenter.protocal.enumerate.UserType;
import com.zju.campustour.view.adapter.FragmentViewPagerAdapter;
import com.zju.campustour.view.fragment.MyProjectChildFourFragment;
import com.zju.campustour.view.fragment.MyProjectChildOneFragment;
import com.zju.campustour.view.fragment.MyProjectChildThreeFragment;
import com.zju.campustour.view.fragment.MyProjectChildTwoFragment;
import com.zju.campustour.view.fragment.StatusInfoChildOneFragment;
import com.zju.campustour.view.fragment.StatusInfoChildThreeFrament;
import com.zju.campustour.view.fragment.StatusInfoChildTwoFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnotherMyProjectActivity extends BaseActivity {

    private UserType currentUserType = UserType.USER;

    @BindView(R.id.my_project_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.my_project_tab_layout)
    TabLayout mTabLayout;


    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_project);
        ButterKnife.bind(this);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUserType = UserType.values()[currentUser.getInt(Constants.User_userType)];

        initView();
        setTabs();

    }

    private void initView() {

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void setTabs() {
        FragmentViewPagerAdapter mViewPagerAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager());
        if (currentUserType == UserType.PROVIDER) {
            mViewPagerAdapter.addTab(new MyProjectChildOneFragment(), "我创建的");
        }
        mViewPagerAdapter.addTab(new MyProjectChildTwoFragment(), "收藏");
        mViewPagerAdapter.addTab(new MyProjectChildThreeFragment(), "预约");
        mViewPagerAdapter.addTab(new MyProjectChildFourFragment(), "完成");
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(4);
        //把tabLayout和Viewpager关联起来
        mTabLayout.setupWithViewPager(mViewPager);
    }

}
