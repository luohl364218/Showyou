package com.zju.campustour.presenter.implement;

import android.content.Context;
import android.text.TextUtils;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.zju.campustour.model.bean.PositionModel;
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
        object.put(Constants.StatusInfo_UserId,currentUser.getObjectId());
        object.put(Constants.StatusInfo_User,currentUser);
        if (!TextUtils.isEmpty(mStatusInfoModel.getImgUrl()))
            object.put(Constants.StatusInfo_ImgUrl, mStatusInfoModel.getImgUrl());
        object.put(Constants.StatusInfo_Content, mStatusInfoModel.getContent());
        object.put(Constants.StatusInfo_FavorCount, mStatusInfoModel.getFavourCount());
        object.put(Constants.StatusInfo_CommentCount, mStatusInfoModel.getCommentCount());
        object.put(Constants.StatusInfo_IsDeleted,false);
        if (!TextUtils.isEmpty(mStatusInfoModel.getLabelId()))
            object.put(Constants.StatusInfo_LabelId,mStatusInfoModel.getLabelId());
        object.put(Constants.StatusInfo_LabelContent,mStatusInfoModel.getLabelContent());
        object.put(Constants.StatusInfo_Province,mStatusInfoModel.getProvince());
        object.put(Constants.StatusInfo_City,mStatusInfoModel.getCity());
        object.put(Constants.StatusInfo_District,mStatusInfoModel.getDistrict());
        object.put(Constants.StatusInfo_Street,mStatusInfoModel.getStreet());
        PositionModel positionModel = mStatusInfoModel.getStatusPosition();
        ParseGeoPoint point = new ParseGeoPoint(positionModel.getLatitude(),positionModel.getLongitude());
        object.put(Constants.StatusInfo_StatusPosition,point);
        object.put(Constants.StatusInfo_DiyLocation,mStatusInfoModel.getDiyLocation());
        object.put(Constants.StatusInfo_DetailLocation,mStatusInfoModel.getDetailLocation());
        object.put(Constants.StatusInfo_HidePosition,mStatusInfoModel.isHidePosition());

        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                    mIStatusInfoView.onStatusInfoCommitSuccess();
                else {
                    mIStatusInfoView.onStatusInfoCommitError(e);
                }
            }
        });
    }

    @Override
    public void getHotStatusInfo(int start, int count) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.StatusInfo_TableName);
        query.setSkip(start)
                .setLimit(count)
                .whereExists(Constants.StatusInfo_ImgUrl)
                .orderByDescending(Constants.StatusInfo_FavorCount)
                .orderByDescending(Constants.StatusInfo_CreatedAt)
                .include(Constants.StatusInfo_User);

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
