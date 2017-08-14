package com.zju.campustour.presenter.implement;

import android.content.Context;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.zju.campustour.model.database.models.ProjectUserMap;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.ipresenter.IProjectUserMapOpPresenter;
import com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType;
import com.zju.campustour.view.iview.IProjectCollectorView;
import com.zju.campustour.view.iview.IProjectUserInfoView;
import com.zju.campustour.view.iview.IProjectView;

import java.util.ArrayList;
import java.util.List;


import static com.zju.campustour.model.util.DbUtils.getUser;
import static java.util.Arrays.asList;

/**
 * Created by HeyLink on 2017/4/24.
 */

public class ProjectUserMapOpPresenterImpl implements IProjectUserMapOpPresenter {

    IProjectView mCollectorView = null;
    private List<ProjectUserMap> mProjectUserMapList;
    String TAG = getClass().getSimpleName();
    Context mContext;

    public ProjectUserMapOpPresenterImpl(IProjectView mProjectCollectorView,Context context){
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
            IProjectCollectorView mIProjectCollectorView = (IProjectCollectorView) mCollectorView;
            mIProjectCollectorView.onGetDealNumDone(dealNum);
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
        collector.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){

                }
                else {
                    IProjectCollectorView mIProjectCollectorView = (IProjectCollectorView) mCollectorView;
                    mIProjectCollectorView.onChangeCollectStateError(true,type);
                }

            }
        });

        if (projectId != null && type == UserProjectStateType.COLLECT ){
            ParseQuery<ParseObject> query_project = ParseQuery.getQuery("Project")
                    .whereEqualTo("objectId",projectId)
                    .selectKeys(asList("collectorNum"));

            query_project.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null && objects.size() != 0){
                        for (ParseObject mObject:objects){
                            mObject.increment("collectorNum");
                            mObject.saveInBackground();
                        }
                    }
                }
            });
        }

        if (projectId != null && type == UserProjectStateType.BOOK_SUCCESS ){
            ParseQuery<ParseObject> query_project = ParseQuery.getQuery("Project")
                    .whereEqualTo("objectId",projectId)
                    .selectKeys(asList("bookedNum"));

            query_project.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null && objects.size() != 0){
                        for (ParseObject mObject:objects){
                            mObject.increment("bookedNum");
                            mObject.saveInBackground();
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
                                if (e != null){
                                    IProjectCollectorView mIProjectCollectorView = (IProjectCollectorView) mCollectorView;
                                    mIProjectCollectorView.onChangeCollectStateError(false,type);
                                }
                            }
                        });
                }
            }
        });


        if (projectId != null && type == UserProjectStateType.COLLECT){
            ParseQuery<ParseObject> query_project = ParseQuery.getQuery("Project")
                    .whereEqualTo("objectId",projectId)
                    .selectKeys(asList("collectorNum"));

            query_project.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null && objects.size() != 0){
                        for (ParseObject mObject:objects){
                            mObject.increment("collectorNum",-1);
                            mObject.saveInBackground();
                        }

                    }
                }
            });

        }
        if (projectId != null && type == UserProjectStateType.BOOK_SUCCESS){
            ParseQuery<ParseObject> query_project = ParseQuery.getQuery("Project")
                    .whereEqualTo("objectId",projectId)
                    .selectKeys(asList("bookedNum"));

            query_project.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null && objects.size() != 0){
                        for (ParseObject mObject:objects){
                            mObject.increment("bookedNum",-1);
                            mObject.saveInBackground();
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
        IProjectCollectorView mIProjectCollectorView = (IProjectCollectorView) mCollectorView;

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() != 0){
                    boolean isFavor = false;
                    for (ParseObject object:objects){
                        ProjectUserMap map = new ProjectUserMap();
                        map.setProjectId(object.getString("projectId"));
                        map.setUserId(object.getString("userId"));
                        UserProjectStateType mType = UserProjectStateType.values()[object.getInt("userProjectState")];
                        if (mType.getValue() == 0){
                            isFavor = true;
                            continue;
                        }
                        map.setUserProjectState(mType);
                        mProjectUserMapList.add(map);
                    }
                    mIProjectCollectorView.onQueryProjectCollectorStateDone(isFavor,mProjectUserMapList);
                }
                else
                    mIProjectCollectorView.onQueryProjectCollectorStateDone(false,mProjectUserMapList);
            }
        });
    }

    @Override
    public void queryUserInfo(String projectId, UserProjectStateType type,int start, int count) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        if (projectId == null)
            return;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ProjectUserMap");

        query.whereEqualTo("projectId", projectId);
        if (type != null)
            query.whereEqualTo("userProjectState",type.getIndex());
        query.setSkip(start).setLimit(count).selectKeys(asList("userId"));

        IProjectUserInfoView mIProjectUserInfoView = (IProjectUserInfoView) mCollectorView;
        List<User> userResults = new ArrayList<>();

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() != 0) {
                    List<String> userList = new ArrayList<String>();

                    for (ParseObject object : objects) {
                        userList.add(object.getString("userId"));
                    }

                    ParseQuery<ParseUser> queryTwo = ParseUser.getQuery().setSkip(start).setLimit(count);
                    queryTwo.whereContainedIn("objectId", userList);

                    queryTwo.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (e == null && objects.size() != 0) {
                                for (ParseUser user : objects) {
                                    User fans = getUser(user);
                                    userResults.add(fans);
                                }
                            }

                            mIProjectUserInfoView.onGetProjectUserInfoDone(userResults);

                        }
                    });
                }
                else
                    mIProjectUserInfoView.onGetProjectUserInfoDone(userResults);
            }
        });


    }

}
