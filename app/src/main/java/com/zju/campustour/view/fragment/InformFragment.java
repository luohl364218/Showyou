 package com.zju.campustour.view.fragment;



 import android.content.Intent;
 import android.os.Bundle;
 import android.support.annotation.Nullable;
 import android.support.design.widget.TabLayout;
 import android.support.v4.view.ViewPager;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.view.ViewGroup;
 import android.widget.ImageButton;

 import com.zju.campustour.R;
 import com.zju.campustour.model.util.TabLayoutUtil;
 import com.zju.campustour.view.activity.StatusNewActivity;
 import com.zju.campustour.view.adapter.FragmentViewPagerAdapter;

 public class InformFragment extends BaseFragment {



     private View mRootView;
     private ImageButton userIcon;
     private TabLayout tablayout;
     private ViewPager viewPager;

     @Nullable
     @Override
     public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         if (mRootView == null) {
             mRootView = inflater.inflate(R.layout.fragment_inform, container, false);
             initView();
         }

         ViewGroup parent = (ViewGroup) mRootView.getParent();
         if (parent != null){
             parent.removeView(mRootView);
         }

         return mRootView;
     }

     private void initView() {
         userIcon = (ImageButton) mRootView.findViewById(R.id.user_icon);
         tablayout = (TabLayout) mRootView.findViewById(R.id.tab_layout);
         viewPager = (ViewPager) mRootView.findViewById(R.id.view_pager);



         setTabs();
     }

     private void setTabs() {
         FragmentViewPagerAdapter mViewPagerAdapter = new FragmentViewPagerAdapter(getChildFragmentManager());
         mViewPagerAdapter.addTab(new InformParentOneFragment(), "校友");
         mViewPagerAdapter.addTab(new InformParentTwoFragment(), "活动");
         viewPager.setAdapter(mViewPagerAdapter);
         viewPager.setOffscreenPageLimit(4);
         //把tabLayout和Viewpager关联起来
         tablayout.setupWithViewPager(viewPager);


     }



 }
