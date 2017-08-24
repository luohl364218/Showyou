package com.zju.campustour.presenter.implement;

import android.content.Context;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.zju.campustour.model.bean.NewsModule;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.protocal.enumerate.NewsType;
import com.zju.campustour.view.iview.INewsShowView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HeyLink on 2017/5/20.
 */

public class NewsGetImpl {

    List<NewsModule> mNewsModuleList;
    INewsShowView mNewsShowView;
    Context mContext;


    public NewsGetImpl(INewsShowView mNewsShowView, Context mContext) {
        this.mNewsShowView = mNewsShowView;
        this.mContext = mContext;
    }

    public void getNewsInfo(NewsType type, int start, int limit){
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;

        ParseQuery<ParseObject> mQuery = ParseQuery
                .getQuery("NewsRecommend")
                .orderByDescending("createdAt")
                .setSkip(start)
                .setLimit(limit)
                .whereEqualTo("newsType",type.getTypeId());

        mQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                mNewsModuleList = new ArrayList<NewsModule>();

                if (e == null && objects.size() > 0){
                    for (ParseObject mObject : objects){
                        NewsModule mModule = new NewsModule();
                        mModule.setImgUrl(mObject.getString("imgUrl"));
                        mModule.setLinkUrl(mObject.getString("linkUrl"));
                        mModule.setText(mObject.getString("content"));
                        mModule.setNewsTime(mObject.getDate("newsTime"));
                        mNewsModuleList.add(mModule);

                    }
                    mNewsShowView.onGetNewsDone(mNewsModuleList);
                }
                else
                    mNewsShowView.onGetNewsDone(mNewsModuleList);

            }
        });



    }


}
