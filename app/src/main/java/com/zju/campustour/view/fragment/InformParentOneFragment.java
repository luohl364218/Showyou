package com.zju.campustour.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;

import android.support.v4.view.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.zju.campustour.R;
import com.zju.campustour.model.util.TabLayoutUtil;
import com.zju.campustour.view.adapter.FragmentViewPagerAdapter;



public class InformParentOneFragment extends BaseFragment {

    private View mRootView;

    private TabLayout tablayout;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_inform_parent_one, container, false);
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
        mViewPagerAdapter.addTab(new InformChildOneFragment(), "最新");
        mViewPagerAdapter.addTab(new InformChildTwoFragment(), "中学");
        mViewPagerAdapter.addTab(new InformChildThreeFragment(), "大学");
        viewPager.setAdapter(mViewPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        //把tabLayout和Viewpager关联起来
        tablayout.setupWithViewPager(viewPager);
    }


}
