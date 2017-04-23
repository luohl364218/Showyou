package com.zju.campustour.view.widget.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by HeyLink on 2017/4/5.
 */

public class SuperViewPager extends ViewPager {

    private ViewPageHelper helper;

    public SuperViewPager(Context context) {
        this(context, null);
    }

    public SuperViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        helper=new ViewPageHelper(this);
    }

 /*   @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item,false);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        MScroller scroller = helper.getScroller();
        if(Math.abs(getCurrentItem() - item) > 1){
            scroller.setNoDuration(true);
            super.setCurrentItem(item, smoothScroll);
            scroller.setNoDuration(false);
        }else{
            scroller.setNoDuration(false);
            super.setCurrentItem(item, smoothScroll);
        }
    }*/

    public void setCurrentItemByTab(int item, boolean smoothScroll){
        MScroller scroller = helper.getScroller();
        scroller.setNoDuration(true);
        super.setCurrentItem(item, smoothScroll);
        scroller.setNoDuration(false);
    }
}
