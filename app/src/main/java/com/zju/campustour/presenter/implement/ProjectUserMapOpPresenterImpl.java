package com.zju.campustour.presenter.implement;

import android.content.Context;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.zju.campustour.model.database.models.Project;
import com.zju.campustour.model.database.models.ProjectUserMap;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.ipresenter.IProjectUserMapOpPresenter;
import com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType;
import com.zju.campustour.view.IView.IProjectCollectorView;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by HeyLink on 2017/4/24.
 */

public class ProjectUserMapOpPresenterImpl implements IProjectUserMapOpPresenter {

    IProjectCollectorView mCollectorView = null;
    private List<ProjectUserMap> mProjectUserMapList;
    String TAG = getClass().getSimpleName();
    Context mContext;

    public ProjectUserMapOpPresenterImpl(IProjectCollectorView mProjectCollectorView,Context context){
        mCollectorView = mProjectCollectorView;
        this.mContext = context;
    }


    @Override
    public void getBookedNum(String projectId) {

    }

    @Override
    public void getDealNum(String userId) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        if (userId == null)
            return;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ProjectUserMap");
        query.whereEqualTo("userId",userId);
        query.whereEqualTo("userProjectState",4);
        try {
            int dealNum = query.count();
            mCollectorView.onGetDealNumDone(dealNum);
        } catch (ParseException mE) {
            mE.printStackTrace();
        }

    }

    @Override
    public void getUserProjectState(String userId, String projectId) {

    }

    @Override
    public void put(String userId, String projectId, UserProjectStateType type){
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        ParseObject collector = new ParseObject("ProjectUserMap");
        collector.put("projectId", projectId);
        collector.put("userId",userId);
        collector.put("userProjectState", type.getIndex());
        collector.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null)
                    mCollectorView.onChangeCollectStateError(true);
            }
        });

        if (projectId != null){
            ParseQuery<ParseObject> query_project = ParseQuery.getQuery("Project")
                    .whereEqualTo("objectId",projectId)
                    .selectKeys(asList("collectorNum"));

            query_project.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null && objects.size() != 0){
                        for (ParseObject mObject:objects){
                            mObject.increment("collectorNum");
                            mObject.saveEventually();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void delete(String userId, String projectId, UserProjectStateType type){
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ProjectUserMap");
        if (userId != null)
            query.whereEqualTo("userId",userId);

        if (projectId != null){
            query.whereEqualTo("projectId", projectId);
        }

        if (type != null)
            query.whereEqualTo("userProjectState",type.getIndex());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() != 0){
                    for (ParseObject mObject:objects)
                        mObject.deleteEventually(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null)
                                    mCollectorView.onChangeCollectStateError(false);
                            }
                        });
                }
            }
        });


        if (projectId != null){
            ParseQuery<ParseObject> query_project = ParseQuery.getQuery("Project")
                    .whereEqualTo("objectId",projectId)
                    .selectKeys(asList("collectorNum"));

            query_project.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null && objects.size() != 0){
                        for (ParseObject mObject:objects){
                            mObject.increment("collectorNum",-1);
                            mObject.saveEventually();
                        }

                    }
                }
            });

        }

    }

    @Override
    public void query(String userId, String projectId, UserProjectStateType type) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ProjectUserMap");
        if (userId != null)
            query.whereEqualTo("userId",userId);
        if (projectId != null)
            query.whereEqualTo("projectId", projectId);
        if (type != null)
            query.whereEqualTo("userProjectState",type.getIndex());

        mProjectUserMapList = new ArrayList<>();

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() != 0){
                    for (ParseObject object:objects){
                        ProjectUserMap map = new ProjectUserMap();
                        map.setProjectId(object.getString("projectId"));
                        map.setUserId(object.getString("userId"));
                        map.setUserProjectState(UserProjectStateType.values()[object.getInt("userProjectState")]);
                        mProjectUserMapList.add(map);
                    }
                    mCollectorView.onQueryProjectCollectorStateDone(true,mProjectUserMapList);
                }
                else
                    mCollectorView.onQueryProjectCollectorStateDone(false,mProjectUserMapList);
            }
        });
    }

}
