package com.zju.campustour.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by WuyuShan on 2017/8/27.
 */

public class InformFragmentTableAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList;                         //fragment列表
    private List<String> titleList;

    public InformFragmentTableAdapter(FragmentManager fm,List<Fragment> fragment,List<String> title) {
        super(fm);
        this.fragmentList = fragment;
        this.titleList = title;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position % titleList.size());
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
