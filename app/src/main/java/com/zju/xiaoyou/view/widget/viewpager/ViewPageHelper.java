package com.zju.xiaoyou.view.widget.viewpager;

import android.support.v4.view.ViewPager;

import java.lang.reflect.Field;

/**
 * Created by HeyLink on 2017/4/5.
 */

public class ViewPageHelper {

    private ViewPager viewPager;

    private MScroller scroller;

    public ViewPageHelper(ViewPager viewPager) {
        this.viewPager = viewPager;
        init();
    }

    public MScroller getScroller() {
        return scroller;
    }


    private void init(){
        scroller = new MScroller(viewPager.getContext());
        Class<ViewPager> cl = ViewPager.class;
        try {
            Field field = cl.getDeclaredField("mScroller");
            field.setAccessible(true);
            //利用反射设置mScroller域为自己定义的DiyScroller
            field.set(viewPager,scroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
