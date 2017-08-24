package com.zju.campustour.presenter.implement;

import android.content.Context;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.zju.campustour.model.bean.LabelInfo;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.util.DbUtils;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.ipresenter.ILabelInfoOpPresenter;
import com.zju.campustour.view.iview.ILabelInfoView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HeyLink on 2017/8/23.
 */

public class LabelInfoOperator implements ILabelInfoOpPresenter {

    List<LabelInfo> mLabelInfos;
    ILabelInfoView mILabelInfoView;
    Context mContext;

    public LabelInfoOperator(ILabelInfoView mILabelInfoView, Context mContext) {
        this.mILabelInfoView = mILabelInfoView;
        this.mContext = mContext;
    }

    @Override
    public void getRecommendLabel(int start, int count) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.LabelInfo_TableName);
        query.setSkip(start)
             .setLimit(count).include(Constants.LabelInfo_User)
             .whereEqualTo(Constants.LabelInfo_Deleted,false)
             .orderByDescending(Constants.LabelInfo_JoinNum);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                mLabelInfos = new ArrayList<>();

                if (e == null ){
                    if (objects.size() > 0) {
                        for (ParseObject mObject : objects) {
                            LabelInfo mLabelInfo = DbUtils.getLabelInfo(mObject);
                            mLabelInfos.add(mLabelInfo);
                        }
                    }
                    mILabelInfoView.onLabelInfoGotSuccess(mLabelInfos);
                }
                else {
                    mILabelInfoView.onLabelInfoGotFailed(e);
                }

            }
        });

    }

    @Override
    public void searchLabel(String label) {
        if (!NetworkUtil.isNetworkAvailable(mContext) || label == null)
            return;

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.LabelInfo_TableName);
        query.setLimit(20)
                .include(Constants.LabelInfo_User)
                .whereEqualTo(Constants.LabelInfo_Deleted,false)
                .whereContains(Constants.LabelInfo_Content,label)
                .orderByDescending(Constants.LabelInfo_JoinNum);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                mLabelInfos = new ArrayList<>();

                if (e == null ){
                    if (objects.size() > 0) {
                        for (ParseObject mObject : objects) {
                            LabelInfo mLabelInfo = DbUtils.getLabelInfo(mObject);
                            mLabelInfos.add(mLabelInfo);
                        }
                    }
                    mILabelInfoView.onLabelInfoGotSuccess(mLabelInfos);
                }
                else {
                    mILabelInfoView.onLabelInfoGotFailed(e);
                }

            }
        });


    }
}
