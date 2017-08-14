package com.zju.campustour.presenter.implement;

import android.content.Context;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.database.models.VerifyInfo;
import com.zju.campustour.model.util.DbUtils;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.ipresenter.IUserVerifyInfoPresenter;
import com.zju.campustour.presenter.protocal.enumerate.IdentityType;
import com.zju.campustour.view.iview.IUserVerifyInfoView;
import com.zju.campustour.view.iview.IUserView;

import java.util.List;

/**
 * Created by WuyuShan on 2017/7/25.
 */

public class UserVerifyInfoImpl implements IUserVerifyInfoPresenter {

    private String TAG = "user verify Info impl ";
    private Context mContext;
    private IUserView mUserView;


    public UserVerifyInfoImpl(Context mContext, IUserView mUserView) {
        this.mContext = mContext;
        this.mUserView = mUserView;
    }

    @Override
    public void submitVerifyInfo(VerifyInfo verifyInfo) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;

        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseObject object = new ParseObject(Constants.UserIdVerifyInfo_TableName);
        object.put(Constants.UserIdVerifyInfo_User, currentUser);
        object.put(Constants.UserIdVerifyInfo_UserId,verifyInfo.getSubmitUserId());
        object.put(Constants.UserIdVerifyInfo_ImgUrl,verifyInfo.getSubmitImgUrl());
        object.put(Constants.UserIdVerifyInfo_Description,verifyInfo.getSubmitDescription());
        object.put(Constants.UserIdVerifyInfo_VerifyStateType,verifyInfo.getSubmitVerifyStateType().getVerifyId());
        object.put(Constants.User_identityType,verifyInfo.getIdentityType().getIdentityId());
        object.put(Constants.UserIdVerifyInfo_SubmitTime,verifyInfo.getSubmitTime());
        IUserVerifyInfoView userVerifyInfoView = (IUserVerifyInfoView) mUserView;

        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    userVerifyInfoView.onSubmitVerifyInfoSuccess(false);
                }
                else {
                    userVerifyInfoView.onSubmitVerifyInfoFailed(e);
                }
            }
        });
    }

    @Override
    public void queryVerifyInfoState(String userId, IdentityType identityType) {
        if (!NetworkUtil.isNetworkAvailable(mContext) || userId == null)
            return;

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.UserIdVerifyInfo_TableName);
        query.whereEqualTo(Constants.UserIdVerifyInfo_UserId,userId)
             .whereEqualTo(Constants.UserIdVerifyInfo_IdentityType, identityType.getIdentityId())
             .setLimit(1);

        IUserVerifyInfoView userVerifyInfoView = (IUserVerifyInfoView) mUserView;


        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    if (objects != null && objects.size() > 0)
                        userVerifyInfoView.onQueryVerifyInfoDone(DbUtils.getVerifyInfo(objects.get(0)));
                    else
                        userVerifyInfoView.onQueryVerifyInfoDone(new VerifyInfo());
                }
                else
                    userVerifyInfoView.onQueryVerifyInfoDone(null);


            }
        });

    }

    @Override
    public void refreshVerifyInfo(VerifyInfo verifyInfo) {
        if (!NetworkUtil.isNetworkAvailable(mContext) || verifyInfo == null)
            return;

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.UserIdVerifyInfo_TableName);

        IUserVerifyInfoView userVerifyInfoView = (IUserVerifyInfoView) mUserView;


        query.getInBackground(verifyInfo.getObjectId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null && object != null){
                    object.put(Constants.UserIdVerifyInfo_ImgUrl,verifyInfo.getSubmitImgUrl());
                    object.put(Constants.UserIdVerifyInfo_Description,verifyInfo.getSubmitDescription());
                    object.put(Constants.UserIdVerifyInfo_VerifyStateType,verifyInfo.getSubmitVerifyStateType().getVerifyId());
                    object.put(Constants.UserIdVerifyInfo_SubmitTime,verifyInfo.getSubmitTime());
                    object.put(Constants.User_identityType,verifyInfo.getIdentityType().getIdentityId());
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){
                                userVerifyInfoView.onSubmitVerifyInfoSuccess(true);
                            }
                            else {
                                userVerifyInfoView.onSubmitVerifyInfoFailed(e);
                            }
                        }
                    });
                }
                else
                    userVerifyInfoView.onQueryVerifyInfoDone(null);
            }
        });

    }
}
