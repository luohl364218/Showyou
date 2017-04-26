package com.zju.campustour.presenter.implement;

import android.support.annotation.NonNull;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.model.util.DbUtils;
import com.zju.campustour.presenter.ipresenter.IUserInfoOpPresenter;
import com.zju.campustour.presenter.protocal.enumerate.SexType;
import com.zju.campustour.presenter.protocal.enumerate.UserType;
import com.zju.campustour.view.IView.ISearchUserInfoView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HeyLink on 2017/4/24.
 */

public class UserInfoOpPresenterImpl implements IUserInfoOpPresenter {

    private String TAG = "User Info Operation ";
    private ISearchUserInfoView mSearchInfoView;
    List<User> userResults;

    public UserInfoOpPresenterImpl(ISearchUserInfoView mSearchInfoView) {
        this.mSearchInfoView = mSearchInfoView;
    }

    @Override
    public void initialUserInfo() {

    }

    @Override
    public void addOrUpdateUser(User mUser) {

    }

    @Override
    public void queryUserInfoWithId(String userId) {

    }

    @Override
    public void userLogin(String loginName, String password) {

    }

    @Override
    public void userLogout(String userId) {

    }

    @Override
    public void userDelete(String userId) {

    }

    @Override
    public void queryProviderUserWithConditions(String mSchool, String mMajor,int start) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("User2")
                .setSkip(start).setLimit(10);
        if (mSchool != null)
            query.whereEqualTo("school",mSchool);
        if (mMajor != null)
            query.whereEqualTo("major",mMajor);


        userResults = new ArrayList<>();

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> userList, ParseException e) {
                if (e == null) {
                    /*信息转换*/
                    for(ParseObject user: userList){
                        User provider = getUser(user);
                        userResults.add(provider);
                    }

                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }

                mSearchInfoView.onGetProviderUserDone(userResults);

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
