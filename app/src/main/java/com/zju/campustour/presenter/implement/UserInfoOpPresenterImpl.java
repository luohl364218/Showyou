package com.zju.campustour.presenter.implement;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import java.util.List;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.ipresenter.IUserInfoOpPresenter;
import com.zju.campustour.presenter.protocal.enumerate.UserType;
import com.zju.campustour.view.IView.ISearchUserInfoView;
import com.zju.campustour.view.IView.IUserLoginView;
import com.zju.campustour.view.IView.IUserRegisterView;
import com.zju.campustour.view.IView.IUserView;
import com.zju.campustour.view.activity.ProjectActivity;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.zju.campustour.model.database.data.SchoolData.allAreaSchoolList;
import static com.zju.campustour.model.util.DbUtils.getUser;

/**
 * Created by HeyLink on 2017/4/24.
 */

public class UserInfoOpPresenterImpl implements IUserInfoOpPresenter {

    private String TAG = "user Info Operation ";
    private Context mContext;
    private IUserView mUserView;
    private ISearchUserInfoView mSearchUserInfoView;
    private IUserLoginView mIUserLoginView;
    private IUserRegisterView mUserRegisterView;
    List<User> userResults;

    public UserInfoOpPresenterImpl(IUserView mSearchInfoView, Context context) {
        this.mContext = context;
        this.mUserView = mSearchInfoView;
    }

    @Override
    public void initialUserInfo() {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;

    }

    @Override
    public void registerUser(String userName, String password) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        SweetAlertDialog mDialog = new SweetAlertDialog(mContext,SweetAlertDialog.PROGRESS_TYPE);
        mDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mDialog.setTitleText("正在注册");
        mDialog.show();

        ParseUser user = new ParseUser();
        user.setUsername(userName);
        user.setPassword(password);
        user.put("online",true);
        user.put("imgUrl","");
        user.put("fansNum",0);

        mUserRegisterView = (IUserRegisterView)mUserView;
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    mDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    mDialog.getProgressHelper().setBarColor(Color.parseColor("#eb4f38"));
                    mDialog.setTitleText("注册成功")
                            .setContentText("欢迎你加入校游，去寻找校友吧!")
                            .setConfirmText("下一步")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    mDialog.dismissWithAnimation();
                                    mUserRegisterView.userSignedUpSuccessfully(userName,password);
                                }
                            });
                    mDialog.setCancelable(false);


                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    mDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    mDialog.getProgressHelper().setBarColor(Color.parseColor("#dc143c"));
                    mDialog.setTitleText("注册失败");
                    mDialog.setContentText("同学，该用户名已经存在");
                    mDialog.setConfirmText("朕知道了")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    mDialog.dismissWithAnimation();
                                    mUserRegisterView.userSignUpDidNotSucceed(e);
                                }
                            });
                }
            }
        });
    }


    @Override
    public void queryUserInfoWithId(String userId) {
        if (userId == null)
            return;
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        userResults = new ArrayList<>();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(userId, new GetCallback<ParseUser>() {
            public void done(ParseUser object, ParseException e) {
                if (e == null) {

                    User provider = getUser(object);
                    userResults.add(provider);
                    mSearchUserInfoView = (ISearchUserInfoView) mUserView;
                    mSearchUserInfoView.onGetProviderUserDone(userResults);
                } else {
                    Log.d(TAG,"get user error!!!!");
                    mSearchUserInfoView = (ISearchUserInfoView) mUserView;
                    mSearchUserInfoView.onGetProviderUserError(e);
                }
            }
        });

    }

    @Override
    public void userLogin(String loginName, String password) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        SweetAlertDialog mDialog = new SweetAlertDialog(mContext,SweetAlertDialog.PROGRESS_TYPE);
        mDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mDialog.setTitleText("登录中");
        mDialog.show();
        mIUserLoginView = (IUserLoginView)mUserView;
        ParseUser.logInInBackground(loginName, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                mDialog.dismissWithAnimation();
                if (e == null && user != null) {
                    //设置用户的登录标志为true
                    user.put("online",true);
                    user.saveEventually();
                    mIUserLoginView.loginSuccessful();
                } else if (user == null) {
                    mIUserLoginView.usernameOrPasswordIsInvalid();
                } else {
                    mIUserLoginView.loginError(e);
                }
            }
        });
    }

    @Override
    public void userLogout(String userId) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
    }

    @Override
    public void userDelete(String userId) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;

    }

    @Override
    public void queryProviderUserWithConditions(String school, String major,int start, int area, int categoryId) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
       this.queryProviderUserWithConditions(school,major,start,area,categoryId,false,false,true);
    }

    @Override
    public void queryProviderUserWithConditions(String school, String major, int start, int area, int categoryId, boolean isOrderByFansNum, boolean isOrderByLatest, boolean isMajorNotCommon) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        ParseQuery<ParseUser> query = ParseUser.getQuery().setSkip(start).setLimit(10);

        if (school != null)
            query.whereEqualTo("school",school);
        if (major != null)
            query.whereEqualTo("major",major);
        if (categoryId >= 0){
            query.whereEqualTo("categoryId",categoryId);
        }
        if (school == null && area >= 0){
            String[] schools = (String[]) allAreaSchoolList[area];
            List<String> schoolList = new ArrayList<>();
            for (int i = 1; i < schools.length; i++)
                schoolList.add(schools[i]);

            query.whereContainedIn("school",schoolList);
        }
        if (isOrderByFansNum)
            query.orderByDescending("fansNum");
        if (isOrderByLatest)
            query.orderByDescending("createdAt");

        if (isMajorNotCommon)
            query.whereEqualTo("userType", UserType.PROVIDER.getValue());
        else
            query.whereEqualTo("userType",UserType.USER.getValue());

        userResults = new ArrayList<>();
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> userList, ParseException e) {
                if (e == null) {
                    /*信息转换*/
                    for(ParseUser user: userList){
                        User provider = getUser(user);
                        userResults.add(provider);
                    }
                    Log.d(TAG, "find user-----------------: " + userList.size());
                    mSearchUserInfoView = (ISearchUserInfoView) mUserView;
                    mSearchUserInfoView.onGetProviderUserDone(userResults);

                } else {
                    Log.d(TAG,"get user error!!!!");
                    mSearchUserInfoView = (ISearchUserInfoView) mUserView;
                    mSearchUserInfoView.onGetProviderUserError(e);
                }



            }
        });
    }

    @Override
    public void searchRelativeUserWithConditions(String condition, int start) {
        if (!NetworkUtil.isNetworkAvailable(mContext) || condition==null)
            return;
        ArrayList<ParseQuery<ParseUser>> mList = new ArrayList<>();
        ParseQuery<ParseUser> query_1 = ParseUser.getQuery().whereContains("school",condition);
        ParseQuery<ParseUser> query_2 = ParseUser.getQuery().whereContains("major",condition);
        ParseQuery<ParseUser> query_3 = ParseUser.getQuery().whereContains("highSchool",condition);
        ParseQuery<ParseUser> query_4 = ParseUser.getQuery().whereContains("juniorHighSchool",condition);
        ParseQuery<ParseUser> query_5 = ParseUser.getQuery().whereContains("primarySchool",condition);
        ParseQuery<ParseUser> query_6 = ParseUser.getQuery().whereContains("realname",condition);
        ParseQuery<ParseUser> query_7 = ParseUser.getQuery().whereContains("username",condition);
        ParseQuery<ParseUser> query_8 = ParseUser.getQuery().whereContains("shortDescription",condition);
        mList.add(query_1);
        mList.add(query_2);
        mList.add(query_3);
        mList.add(query_4);
        mList.add(query_5);
        mList.add(query_6);
        mList.add(query_7);
        mList.add(query_8);

        ParseQuery<ParseUser> query = ParseQuery.or(mList);

        /*if (major != null)
            query.whereContains("major",major);*/
        userResults = new ArrayList<>();
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> userList, ParseException e) {
                if (e == null) {

                    /*信息转换*/
                    for(ParseUser user: userList){
                        User provider = getUser(user);
                        userResults.add(provider);
                    }
                    Log.d(TAG, "find user-----------------: " + userList.size());
                    mSearchUserInfoView = (ISearchUserInfoView) mUserView;
                    mSearchUserInfoView.onGetProviderUserDone(userResults);

                } else {
                    Log.d(TAG,"get user error!!!!");
                    mSearchUserInfoView = (ISearchUserInfoView) mUserView;
                    mSearchUserInfoView.onGetProviderUserError(e);
                }

            }
        });


    }

    @Override
    public void queryProviderUserWithConditions(String school, String major, int start, int area, int categoryId, boolean isOrderByFansNum, boolean isOrderByLatest){
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        ParseQuery<ParseUser> query = ParseUser.getQuery().setSkip(start).setLimit(10);

        if (school != null)
            query.whereEqualTo("school",school);
        if (major != null)
            query.whereEqualTo("major",major);
        if (categoryId >= 0){
            query.whereEqualTo("categoryId",categoryId);
        }
        if (school == null && area >= 0){
            String[] schools = (String[]) allAreaSchoolList[area];
            List<String> schoolList = new ArrayList<>();
            for (int i = 1; i < schools.length; i++)
                schoolList.add(schools[i]);

            query.whereContainedIn("school",schoolList);
        }
        if (isOrderByFansNum)
            query.orderByDescending("fansNum");
        if (isOrderByLatest)
            query.orderByDescending("createdAt");

        userResults = new ArrayList<>();
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> userList, ParseException e) {
                if (e == null) {
                    /*信息转换*/
                    for(ParseUser user: userList){
                        User provider = getUser(user);
                        userResults.add(provider);
                    }
                    Log.d(TAG, "find user-----------------: " + userList.size());
                    mSearchUserInfoView = (ISearchUserInfoView) mUserView;
                    mSearchUserInfoView.onGetProviderUserDone(userResults);

                } else {
                    Log.d(TAG,"get user error!!!!");
                    mSearchUserInfoView = (ISearchUserInfoView) mUserView;
                    mSearchUserInfoView.onGetProviderUserError(e);
                }



            }
        });
    }


}
