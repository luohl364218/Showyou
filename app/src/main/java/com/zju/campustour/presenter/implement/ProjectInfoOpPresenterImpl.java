package com.zju.campustour.presenter.implement;

import android.content.Context;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.bean.Project;
import com.zju.campustour.model.bean.ProjectSaleInfo;
import com.zju.campustour.model.util.DbUtils;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.ipresenter.IProjectInfoOpPresenter;
import com.zju.campustour.presenter.protocal.enumerate.ProjectStateType;
import com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType;
import com.zju.campustour.view.iview.IProjectInfoOperateView;
import com.zju.campustour.view.iview.IProjectView;
import com.zju.campustour.view.iview.ISearchProjectInfoView;


import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;


/**
 * Created by HeyLink on 2017/4/24.
 */

public class ProjectInfoOpPresenterImpl implements IProjectInfoOpPresenter {

    IProjectView mProjectInfoView;
    List<Project> mProjects;
    Context mContext;
    String TAG = getClass().getSimpleName();

    public ProjectInfoOpPresenterImpl(IProjectView mProjectInfoView, Context context) {
        this.mProjectInfoView = mProjectInfoView;
        mContext = context;
    }

    @Override
    public void addOrUpdateProject(Project mProject,boolean isEditMode) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
    }

    @Override
    public void queryProjectWithUserId(String userId,int startIndex, int count) {
        if (userId == null)
            return;
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Project")
                .setSkip(startIndex)
                .setLimit(count)
                .whereEqualTo("userId",userId)
                .whereEqualTo(Constants.Project_IsDelete, false)
                .include("providerV2")
                .selectKeys(Constants.projectDefaultKeys);
        mProjects = new ArrayList<>();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> projectList, ParseException e) {
                if (e == null) {
                     /*信息转换*/
                     if (projectList.size() != 0){
                         for(ParseObject project: projectList) {
                             Project mProject = DbUtils.getProject(project);
                             mProjects.add(mProject);
                         }
                     }

                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }

                ISearchProjectInfoView mISearchProjectInfoView = (ISearchProjectInfoView)mProjectInfoView;
                mISearchProjectInfoView.onGetProjectInfoDone(mProjects);

            }
        });


    }

    @Override
    public void queryProjectWithUserIdAndState(String userId, UserProjectStateType type, int startIndex, int count) {
        if (userId == null || type == null)
            return;
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;

        List<String> projectIdList = new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ProjectUserMap");
        query.whereEqualTo("userId",userId)
                .whereEqualTo("userProjectState",type.getIndex())
                .selectKeys(asList("projectId"));
        mProjects = new ArrayList<>();
        ISearchProjectInfoView mISearchProjectInfoView = (ISearchProjectInfoView)mProjectInfoView;
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() != 0) {
                    for (ParseObject object:objects){
                        projectIdList.add(object.getString("projectId"));
                    }

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Project");
                    query.whereContainedIn("objectId",projectIdList)
                            .include("providerV2")
                            .whereEqualTo(Constants.Project_IsDelete, false)
                            .setSkip(startIndex)
                            .setLimit(count);

                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null && objects.size() != 0) {

                                for (ParseObject project:objects) {
                                    Project mProject = DbUtils.getProject(project);
                                    mProjects.add(mProject);
                                }
                            }
                            mISearchProjectInfoView.onGetProjectInfoDone(mProjects);
                        }
                    });

                }
                else
                    mISearchProjectInfoView.onGetProjectInfoDone(mProjects);

            }
        });
    }


    @Override
    public void setProjectState(String projectId, ProjectStateType state) {
        if (!NetworkUtil.isNetworkAvailable(mContext) || projectId == null)
            return;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Project");

        // Retrieve the object by id
        query.getInBackground(projectId, new GetCallback<ParseObject>() {
            public void done(ParseObject project, ParseException e) {
                if (e == null) {

                    project.put("projectState", state.getValue());
                    project.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null)
                                return;
                        }
                    });
                }
            }
        });

    }

    @Override
    public void getLimitProjectInfo(int start, int count,boolean isLatest, boolean isHotest) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Project").whereNotEqualTo("projectState",3)
                .whereEqualTo(Constants.Project_IsDelete, false)
                .setSkip(start).setLimit(count).include("providerV2").selectKeys(Constants.projectDefaultKeys);

        if (isLatest)
            query.orderByDescending("createdAt");

        if (isHotest)
            query.orderByDescending("bookedNum");

        mProjects = new ArrayList<>();
        ISearchProjectInfoView mISearchProjectInfoView = (ISearchProjectInfoView)mProjectInfoView;
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> projectList, ParseException e) {
                if (e == null) {
                     /*信息转换*/
                    for(ParseObject project: projectList) {
                        Project mProject = DbUtils.getProject(project);
                        mProjects.add(mProject);
                    }
                    mISearchProjectInfoView.onGetProjectInfoDone(mProjects);
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                    mISearchProjectInfoView.onGetProjectInfoError(e);
                }



            }
        });
    }

    @Override
    public void getLimitProjectInfo(int start, int count,boolean isLatest, boolean isHotest, boolean isRecommend) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Project").whereNotEqualTo("projectState",3)
                .whereEqualTo(Constants.Project_IsDelete, false)
                .setSkip(start).setLimit(count).include("providerV2").selectKeys(Constants.projectDefaultKeys);

        if (isLatest)
            query.orderByDescending("createdAt");

        if (isHotest)
            query.orderByDescending("bookedNum");

        if (isRecommend)
            query.whereEqualTo(Constants.Project_IsRecommend,true);

        mProjects = new ArrayList<>();
        ISearchProjectInfoView mISearchProjectInfoView = (ISearchProjectInfoView)mProjectInfoView;
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> projectList, ParseException e) {
                if (e == null) {
                     /*信息转换*/
                    for(ParseObject project: projectList) {
                        Project mProject = DbUtils.getProject(project);
                        mProjects.add(mProject);
                    }
                    mISearchProjectInfoView.onGetProjectInfoDone(mProjects);
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                    mISearchProjectInfoView.onGetProjectInfoError(e);
                }



            }
        });
    }


    @Override
    public void getLimitProjectInfo(int start, int count,boolean isLatest, boolean isHotest, boolean isRecommend, boolean isOffline) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Project").whereNotEqualTo("projectState",3)
                .whereEqualTo(Constants.Project_IsDelete, false)
                .setSkip(start).setLimit(count).include("providerV2").selectKeys(Constants.projectDefaultKeys);

        if (isLatest)
            query.orderByDescending("createdAt");

        if (isHotest)
            query.orderByDescending("bookedNum");

        if (isRecommend)
            query.whereEqualTo(Constants.Project_IsRecommend,true);

        if (isOffline)
            query.whereEqualTo(Constants.Project_IsOffline, true);
        else
            query.whereEqualTo(Constants.Project_IsOffline,false);

        mProjects = new ArrayList<>();
        ISearchProjectInfoView mISearchProjectInfoView = (ISearchProjectInfoView)mProjectInfoView;
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> projectList, ParseException e) {
                if (e == null) {
                     /*信息转换*/
                    for(ParseObject project: projectList) {
                        Project mProject = DbUtils.getProject(project);
                        mProjects.add(mProject);
                    }
                    mISearchProjectInfoView.onGetProjectInfoDone(mProjects);
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                    mISearchProjectInfoView.onGetProjectInfoError(e);
                }



            }
        });
    }

    @Override
    public void queryProjectWithId(String projectId) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        mProjects = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Project")
                .whereEqualTo(Constants.Project_IsDelete, false)
                .selectKeys(Constants.projectDefaultKeys);
        ISearchProjectInfoView mISearchProjectInfoView = (ISearchProjectInfoView)mProjectInfoView;
        query.getInBackground(projectId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {

                    Project mProject = DbUtils.getProject(object);
                    mProjects.add(mProject);
                    mISearchProjectInfoView.onGetProjectInfoDone(mProjects);
                } else {
                    Log.d(TAG,"get user error!!!!");
                    mISearchProjectInfoView.onGetProjectInfoError(e);
                }
            }
        });
    }

    @Override
    public void queryProjectSaleInfoWithId(String projectId) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Project").selectKeys(Constants.projectSaleKeys);
        ISearchProjectInfoView mISearchProjectInfoView = (ISearchProjectInfoView)mProjectInfoView;
        query.getInBackground(projectId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {

                    ProjectSaleInfo mProjectSaleInfo = DbUtils.getProjectSaleInfo(object);
                    mISearchProjectInfoView.onGetProjectInfoDone(asList(mProjectSaleInfo));
                } else {
                    Log.d(TAG,"get user error!!!!");
                    mISearchProjectInfoView.onGetProjectInfoError(e);
                }
            }
        });

    }

    @Override
    public void deleteProjectWithId(String projectId) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Project");
        IProjectInfoOperateView projectInfoOperateView = (IProjectInfoOperateView)mProjectInfoView;
        query.getInBackground(projectId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    object.put(Constants.Project_IsDelete,true);
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            projectInfoOperateView.onDeleteProjectSuccess();
                        }
                    });

                } else {
                   projectInfoOperateView.onDeleteProjectFailed(e);
                }
            }
        });
    }


}
