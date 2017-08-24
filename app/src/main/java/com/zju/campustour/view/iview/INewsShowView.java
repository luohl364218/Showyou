package com.zju.campustour.view.iview;

import com.zju.campustour.model.bean.NewsModule;

import java.util.List;

/**
 * Created by HeyLink on 2017/5/20.
 */

public interface INewsShowView {

    public void onGetNewsDone(List<NewsModule> mNewsModules);
}
