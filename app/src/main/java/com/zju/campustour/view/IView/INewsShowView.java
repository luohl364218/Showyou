package com.zju.campustour.view.IView;

import com.zju.campustour.model.database.models.NewsModule;

import java.util.List;

/**
 * Created by HeyLink on 2017/5/20.
 */

public interface INewsShowView {

    public void onGetNewsDone(List<NewsModule> mNewsModules);
}
