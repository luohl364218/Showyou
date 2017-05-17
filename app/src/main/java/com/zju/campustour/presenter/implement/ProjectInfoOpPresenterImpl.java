package com.zju.campustour.presenter.implement;

import android.content.Context;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.database.models.Project;
import com.zju.campustour.model.database.models.ProjectSaleInfo;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.model.util.DbUtils;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.ipresenter.IProjectInfoOpPresenter;
import com.zju.campustour.presenter.protocal.enumerate.ProjectStateType;
import com.zju.campustour.view.IView.ISearchProjectInfoView;


import java.util.ArrayList;
import java.util.List;

import static com.zju.campustour.model.util.DbUtils.getUser;
import static java.util.Arrays.asList;


/**
 * Created by HeyLink on 2017/4/24.
 */

public class ProjectInfoOpPresenterImpl implements IProjectInfoOpPresenter {

    ISearchProjectInfoView mProjectInfoView;
    List<Project> mProjects;
    Context mContext;
    String TAG = getClass().getSimpleName();

    public ProjectInfoOpPresenterImpl(ISearchProjectInfoView mProjectInfoView, Context context) {
        this.mProjectInfoView = mProjectInfoView;
        mContext = context;
    }

    @Override
    public void addOrUpdateProject(Project mProject) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
    }

    @Override
    public void queryProjectWithUserId(String userId) {
        if (userId == null)
            return;
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Project")
                .whereEqualTo("userId",userId)
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

                mProjectInfoView.onGetProjectInfoDone(mProjects);

            }
        });


    }




    @Override
    public void setProjectState(int projectId, ProjectStateType state) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
    }

    @Override
    public void getLimitProjectInfo(int start, int count) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Project").whereNotEqualTo("projectState",3)
                .setSkip(start).setLimit(count).include("providerV2").selectKeys(Constants.projectDefaultKeys);
        mProjects = new ArrayList<>();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> projectList, ParseException e) {
                if (e == null) {
                     /*信息转换*/
                    for(ParseObject project: projectList) {
                        Project mProject = DbUtils.getProject(project);
                        mProjects.add(mProject);
                    }
                    mProjectInfoView.onGetProjectInfoDone(mProjects);
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                    mProjectInfoView.onGetProjectInfoError(e);
                }



            }
        });
    }

    @Override
    public void queryProjectWithId(String projectId) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        mProjects = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Project").selectKeys(Constants.projectDefaultKeys);
        query.getInBackground(projectId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {

                    Project mProject = DbUtils.getProject(object);
                    mProjects.add(mProject);
                    mProjectInfoView.onGetProjectInfoDone(mProjects);
                } else {
                    Log.d(TAG,"get user error!!!!");
                    mProjectInfoView.onGetProjectInfoError(e);
                }
            }
        });
    }

    @Override
    public void queryProjectSaleInfoWithId(String projectId) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Project").selectKeys(Constants.projectSaleKeys);
        query.getInBackground(projectId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {

                    ProjectSaleInfo mProjectSaleInfo = DbUtils.getProjectSaleInfo(object);
                    mProjectInfoView.onGetProjectInfoDone(asList(mProjectSaleInfo));
                } else {
                    Log.d(TAG,"get user error!!!!");
                    mProjectInfoView.onGetProjectInfoError(e);
                }
            }
        });

    }


}
