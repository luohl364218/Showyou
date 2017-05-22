package com.zju.campustour.presenter.implement;

import android.content.Context;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.zju.campustour.model.database.models.NewsModule;
import com.zju.campustour.view.IView.INewsShowView;

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

    public void getNewsInfo(){
        ParseQuery<ParseObject> mQuery = ParseQuery.getQuery("NewsRecommend");
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
