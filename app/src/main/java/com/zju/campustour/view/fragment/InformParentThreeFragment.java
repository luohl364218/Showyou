package com.zju.campustour.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.bean.NewsModule;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.implement.NewsGetImpl;
import com.zju.campustour.presenter.protocal.enumerate.NewsType;
import com.zju.campustour.view.adapter.FragmentViewPagerAdapter;
import com.zju.campustour.view.iview.INewsShowView;
import com.zju.campustour.view.activity.InfoWebActivity;
import com.zju.campustour.view.adapter.NewsAdapter;
import com.zju.campustour.view.widget.DividerItemDecortion;

import java.util.List;

import static com.zju.campustour.model.common.Constants.STATE_NORMAL;

/**
 * Created by HeyLink on 2017/4/1.
 */

public class InformParentThreeFragment extends BaseFragment {

    private View mRootView;

    private TabLayout tablayout;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_inform_parent_three, container, false);
            initView();
        }

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null){
            parent.removeView(mRootView);
        }

        return mRootView;
    }

    private void initView() {

        tablayout = (TabLayout) mRootView.findViewById(R.id.tab_layout);
        viewPager = (ViewPager) mRootView.findViewById(R.id.view_pager);


        setTabs();
    }

    private void setTabs() {
        FragmentViewPagerAdapter mViewPagerAdapter = new FragmentViewPagerAdapter(getChildFragmentManager());
        mViewPagerAdapter.addTab(new MessageChildOneFragment(), "推荐");
        mViewPagerAdapter.addTab(new MessageChildTwoFragment(), "学习");
        mViewPagerAdapter.addTab(new MessageChildThreeFragment(), "科学");
        mViewPagerAdapter.addTab(new MessageChildFourFragment(), "时事");
        viewPager.setAdapter(mViewPagerAdapter);
        viewPager.setOffscreenPageLimit(4);
        //把tabLayout和Viewpager关联起来
        tablayout.setupWithViewPager(viewPager);
    }
}
