package com.zju.campustour.presenter.implement;

import android.support.annotation.NonNull;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
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

import static com.zju.campustour.model.util.DbUtils.getUser;

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

        userResults = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("User2");
        query.getInBackground(userId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {

                    User provider = getUser(object);
                    userResults.add(provider);

                } else {
                    Log.d(TAG,"get user error!!!!");
                }

                mSearchInfoView.onGetProviderUserDone(userResults);
            }
        });

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




}
