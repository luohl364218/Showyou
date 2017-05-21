package com.zju.campustour.presenter.implement;

import android.content.Context;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.zju.campustour.model.database.models.HomepageSlideImg;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.ipresenter.IImgDownLoadPresenter;
import com.zju.campustour.view.IView.IHomepageImgLoadView;
import com.zju.campustour.view.IView.IImgLoadView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HeyLink on 2017/5/20.
 */

public class HomePageImgLoader implements IImgDownLoadPresenter {


    private Context mContext;
    private IImgLoadView imgLoadView;
    private List<HomepageSlideImg> mHomepageSlideImgs;

    public HomePageImgLoader(Context mContext, IImgLoadView mImgLoadView) {
        this.mContext = mContext;
        this.imgLoadView = mImgLoadView;
    }

    @Override
    public void getImgList() {
        if (!NetworkUtil.isNetworkAvailable(mContext)){
            return;
        }

        mHomepageSlideImgs = new ArrayList<>();
        final IHomepageImgLoadView mImgLoadView = (IHomepageImgLoadView) imgLoadView;

        ParseQuery<ParseObject> mParseQuery = ParseQuery.getQuery("homePage");
        mParseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null &&  objects.size() >0){

                    for (ParseObject mObject: objects){
                        HomepageSlideImg mSlideImg = new HomepageSlideImg();
                        mSlideImg.setImgUrl(mObject.getString("imgUrl"));
                        mSlideImg.setLinkUrl(mObject.getString("linkUrl"));
                        mSlideImg.setDescription(mObject.getString("description"));

                        mHomepageSlideImgs.add(mSlideImg);
                    }

                    mImgLoadView.onImgUrlGot(mHomepageSlideImgs);

                }
                else
                    mImgLoadView.onImgUrlGot(mHomepageSlideImgs);
            }
        });


    }
}
