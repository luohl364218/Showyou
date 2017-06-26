package com.zju.campustour.presenter.implement;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.database.data.MajorModel;
import com.zju.campustour.model.util.DbUtils;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.ipresenter.IMajorInfoPresenter;
import com.zju.campustour.view.iview.IMajorInfoView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by HeyLink on 2017/6/24.
 */

public class MajorInfoPresenterImpl implements IMajorInfoPresenter {

    //读取和记录是否初始化过
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    String TAG = getClass().getSimpleName();
    private IMajorInfoView mIMajorInfoView;
    private Context mContext;

    public MajorInfoPresenterImpl(Context mContext,IMajorInfoView mIMajorInfoView) {
        this.mContext = mContext;
        this.mIMajorInfoView = mIMajorInfoView;
    }

    @Override
    public void getAllMajorInfo() {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;

        List<MajorModel> mMajorModelList = new ArrayList<>();

        ParseQuery<ParseObject> mQuery = ParseQuery.getQuery(Constants.MajorInfo_tableName);
        mQuery.setLimit(300);
        mQuery.findInBackground((mObjectList,mException)->{
            if (mException == null && mObjectList.size() > 0){

                for (ParseObject major: mObjectList){
                    MajorModel mMajorModel = DbUtils.getMajorInfo(major);
                    mMajorModelList.add(mMajorModel);
                }

                mIMajorInfoView.onAllMajorInfoGot(mMajorModelList);
            }
            else if (mException !=null){
                mIMajorInfoView.onGetMajorInfoError(mException);
            }
            else {
                //专业size为0，数据库数据被清空的时候
                mIMajorInfoView.onAllMajorInfoGot(mMajorModelList);
            }

        });

    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void getUpdateMajorInfo() {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;

        sharedPreferences = mContext.getSharedPreferences("loginInfo",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        long lastUpdateTime = sharedPreferences.getLong("update_time",new Date(2000,1,1).getTime());

        List<MajorModel> mMajorModelList = new ArrayList<>();

        ParseQuery<ParseObject> mQuery = ParseQuery.getQuery(Constants.MajorInfo_tableName);
        //选择更新标志位为true，同时专业资讯更新时间大于上一次更新时间
        mQuery.whereEqualTo(Constants.MajorInfo_isUpdate,true);
        //mQuery.whereGreaterThanOrEqualTo("updateAt",lastUpdateTime);
        mQuery.setLimit(300);
        mQuery.findInBackground((mObjectList,mException)-> {
            if (mException == null && mObjectList.size() > 0) {

                for (ParseObject major: mObjectList){
                    long updateTime = major.getUpdatedAt().getTime();
                    if (updateTime> lastUpdateTime){
                        MajorModel mMajorModel = DbUtils.getMajorInfo(major);
                        mMajorModelList.add(mMajorModel);
                    }
                }

                mIMajorInfoView.onUpdateMajorInfoGot(mMajorModelList);
            }
            else if (mException !=null){
                mIMajorInfoView.onGetMajorInfoError(mException);
            }
            else {
                //专业size为0，数据库数据被清空的时候
                mIMajorInfoView.onUpdateMajorInfoGot(mMajorModelList);
            }

        });
    }
}
