package com.zju.campustour.presenter.implement;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.zju.campustour.model.database.models.Project;
import com.zju.campustour.model.util.DbUtils;
import com.zju.campustour.presenter.ipresenter.IProjectInfoOpPresenter;
import com.zju.campustour.presenter.protocal.enumerate.ProjectStateType;
import com.zju.campustour.view.IView.ISearchProjectInfoView;


import java.util.ArrayList;
import java.util.List;


/**
 * Created by HeyLink on 2017/4/24.
 */

public class ProjectInfoOpPresenterImpl implements IProjectInfoOpPresenter {

    ISearchProjectInfoView mProjectInfoView;
    List<Project> mProjects;
    String TAG = getClass().getSimpleName();

    public ProjectInfoOpPresenterImpl(ISearchProjectInfoView mProjectInfoView) {
        this.mProjectInfoView = mProjectInfoView;
    }

    @Override
    public void addOrUpdateProject(Project mProject) {

    }

    @Override
    public void queryProjectWithUserId(String userId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Project")
                .whereEqualTo("userId",userId).include("provider").include("favorites");
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

    }

    @Override
    public void getLimitProjectInfo(int start, int count) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Project").whereNotEqualTo("projectState",3)
                .setSkip(start).setLimit(count).include("favorites").include("provider");
        mProjects = new ArrayList<>();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> projectList, ParseException e) {
                if (e == null) {
                     /*信息转换*/
                    for(ParseObject project: projectList) {
                        Project mProject = DbUtils.getProject(project);
                        mProjects.add(mProject);
                    }
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }

                mProjectInfoView.onGetProjectInfoDone(mProjects);

            }
        });
    }




}
