package com.zju.campustour.presenter.implement;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.zju.campustour.model.database.models.Project;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.model.util.DbUtils;
import com.zju.campustour.presenter.ipresenter.IProjectInfoOpPresenter;
import com.zju.campustour.presenter.protocal.enumerate.ProjectStateType;
import com.zju.campustour.presenter.protocal.enumerate.SexType;
import com.zju.campustour.presenter.protocal.enumerate.UserType;
import com.zju.campustour.view.IView.ISearchProjectInfoView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.zju.campustour.presenter.protocal.enumerate.ProjectStateType.BOOK_ACCEPT;
import static com.zju.campustour.presenter.protocal.enumerate.ProjectStateType.BOOK_STOP;
import static com.zju.campustour.presenter.protocal.enumerate.ProjectStateType.PROJECT_RUNNING;
import static com.zju.campustour.presenter.protocal.enumerate.ProjectStateType.PROJECT_STOP;

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
    public void queryProjectWithUserId(int userId) {

    }

    @Override
    public void setProjectState(int projectId, ProjectStateType state) {

    }

    @Override
    public void getLimitProjectInfo(int start, int count) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Project")
                .setSkip(start).setLimit(count).include("favorites").include("provider");
        mProjects = new ArrayList<>();

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> projectList, ParseException e) {
                if (e == null) {
                     /*信息转换*/
                    for(ParseObject project: projectList) {
                        String id = project.getObjectId();
                        String userId = project.getString("userId");
                        ParseObject providerObject = project.getParseObject("provider");
                        User provider = getUser(providerObject);
                        String title = project.getString("title");
                        Date startTime = project.getDate("startTime");
                        String imgUrl = project.getString("imgUrl");
                        long price = project.getLong("price");
                        String description = project.getString("description");
                        int acceptNum = project.getInt("acceptNum");
                        ProjectStateType projectState;
                        switch (project.getInt("projectState")) {
                            case 0:
                                projectState = BOOK_ACCEPT;
                                break;
                            case 1:
                                projectState = BOOK_STOP;
                                break;
                            case 2:
                                projectState = PROJECT_RUNNING;
                                break;
                            case 3:
                                projectState = PROJECT_STOP;
                                continue;
                            default:
                                projectState = BOOK_ACCEPT;
                        }

                        List<ParseObject> jsonFavorites = project.getList("favorites");
                        List<User> favorites;
                        if (jsonFavorites != null){
                            favorites = new ArrayList<User>();

                            for(ParseObject user: jsonFavorites){
                                User mUser = getUser(user);
                                favorites.add(mUser);
                            }
                        }
                        else {
                            favorites = null;
                        }

                        Project mProject = new Project(id, provider, title, startTime,
                                imgUrl, price, description, acceptNum, projectState, favorites);

                        mProjects.add(mProject);
                    }
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }

                mProjectInfoView.onGetProjectInfoDone(mProjects);

            }
        });
    }

    @NonNull
    public User getUser(ParseObject user) {
        String user_id = user.getObjectId();
        String userName = user.getString("userName");
        String loginName = user.getString("loginName");
        String password =user.getString("password");
        SexType sex = user.getInt("sex") == 0? SexType.MALE : SexType.FEMALE;
        String school = user.getString("school");
        String major = user.getString("major");
        String grade = user.getString("grade");
        int fansNum = user.getInt("fansNum");
        boolean online = user.getBoolean("online");
        String user_imgUrl = user.getString("imgUrl");
        ;
        String phoneNum = user.getString("phoneNum");
        String emailAddr = user.getString("emailAddr");
        UserType userType = user.getInt("userType") == 0? UserType.USER: UserType.PROVIDER;
        String user_description = user.getString("description");
        String shortDescription = user.getString("shortDescription");

        return new User(user_id,userName, loginName, password, sex,
                school, major, grade, fansNum, online, user_imgUrl, phoneNum, emailAddr,
                userType, user_description, shortDescription);
    }


}
