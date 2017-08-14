package com.zju.campustour.presenter.implement;

import android.content.Context;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.zju.campustour.model.bean.StatusInfoModel;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.util.DbUtils;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.ipresenter.IStatusInfoPresenter;
import com.zju.campustour.view.iview.IStatusInfoView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HeyLink on 2017/8/14.
 */

public class StatusInfoOperator implements IStatusInfoPresenter {

    private Context mContext;
    private IStatusInfoView mIStatusInfoView;

    public StatusInfoOperator(Context mContext, IStatusInfoView mIStatusInfoView) {
        this.mContext = mContext;
        this.mIStatusInfoView = mIStatusInfoView;
    }

    @Override
    public void publishStatusInfo(StatusInfoModel mStatusInfoModel) {
        if (!NetworkUtil.isNetworkAvailable(mContext) || mStatusInfoModel == null)
            return;

        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseObject object = new ParseObject(Constants.StatusInfo_TableName);
        object.put(Constants.StatusInfo_User,currentUser);
        object.put(Constants.StatusInfo_Content, mStatusInfoModel.getContent());
        object.put(Constants.StatusInfo_FavorCount, mStatusInfoModel.getFavourCount());
        object.put(Constants.StatusInfo_UserId,currentUser.getObjectId());
        object.put(Constants.StatusInfo_CommentCount, mStatusInfoModel.getCommentCount());
        object.put(Constants.StatusInfo_ImgUrl, mStatusInfoModel.getImgUrl());
        object.put(Constants.StatusInfo_IsDeleted,false);

        object.saveInBackground();
    }

    @Override
    public void getHotStatusInfo(int start, int count) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.StatusInfo_TableName);
        query.setSkip(start).setLimit(count).orderByDescending(Constants.StatusInfo_FavorCount);

        List<StatusInfoModel> mStatusInfoModelList = new ArrayList<>();

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects != null && objects.size() > 0) {
                        for (ParseObject mObject : objects){
                            StatusInfoModel mStatusInfoModel = DbUtils.getStatusInfo(mObject);
                            if (mStatusInfoModel != null && !mStatusInfoModel.isDeleted()){
                                mStatusInfoModelList.add(mStatusInfoModel);
                            }
                        }
                    }

                    mIStatusInfoView.onStatusInfoGotSuccess(mStatusInfoModelList);
                }
                else
                    mIStatusInfoView.onStatusInfoGotError(e);
            }
        });

    }

    @Override
    public void getMyFocusStatusInfo(int start, int count) {

    }

    @Override
    public void getSpecifiedStatusInfo(int start, int count, String userId) {

    }
}
