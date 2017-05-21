package com.zju.campustour.presenter.implement;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.Log;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.model.database.models.UserFocusMap;
import com.zju.campustour.model.util.DbUtils;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.ipresenter.IFocusMapOpPresenter;
import com.zju.campustour.presenter.protocal.enumerate.FocusStateType;
import com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType;
import com.zju.campustour.view.IView.ISearchUserInfoView;
import com.zju.campustour.view.IView.IUserFocusView;
import com.zju.campustour.view.IView.IUserView;

import java.util.ArrayList;
import java.util.List;

import static com.zju.campustour.model.util.DbUtils.getUser;
import static java.util.Arrays.asList;

/**
 * Created by HeyLink on 2017/4/24.
 */

public class FocusMapOpPresenterImpl implements IFocusMapOpPresenter {

    private IUserView mUserView;
    private List<UserFocusMap> mUserFocusMapList;
    private Context mContext;

    public FocusMapOpPresenterImpl(IUserView mUserView, Context context) {
        this.mUserView = mUserView;
        this.mContext = context;
    }

    @Override
    public void put(String providerId, String fansId, FocusStateType type) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        ParseObject fans = new ParseObject("UserFocusMap");
        fans.put("providerId", providerId);
        fans.put("fansId",fansId);
        fans.put("focusState", type.getStateId());
        fans.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    IUserFocusView mFocusView = (IUserFocusView)mUserView;
                    mFocusView.onFocusActionError(true);
                }
            }
        });
    }

    @Override
    public void delete(String providerId, String fansId, FocusStateType type) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserFocusMap");
        if (providerId != null)
            query.whereEqualTo("providerId",providerId);

        if (fansId != null){
            query.whereEqualTo("fansId", fansId);
        }

        if (type != null)
            query.whereEqualTo("focusState",type.getStateId());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() != 0){
                    for (ParseObject mObject:objects)
                        mObject.deleteEventually(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null){
                                    IUserFocusView mFocusView = (IUserFocusView)mUserView;
                                    mFocusView.onFocusActionError(false);
                                }
                            }
                        });
                }
            }
        });
    }

    @Override
    public void queryFansNum(String providerId) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        if (providerId == null)
            return;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserFocusMap");
        query.whereEqualTo("providerId",providerId).whereEqualTo("focusState",FocusStateType.FOCUS.getStateId());

        try {
            int fansNum = query.count();
            IUserFocusView mFocusView = (IUserFocusView)mUserView;
            mFocusView.onGetFansNumDone(fansNum);
        } catch (ParseException mE) {
            mE.printStackTrace();
        }

    }


    @Override
    public void query(String providerId, String fansId, FocusStateType type) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserFocusMap");
        if (providerId != null)
            query.whereEqualTo("providerId",providerId);

        if (fansId != null){
            query.whereEqualTo("fansId", fansId);
        }

        if (type != null)
            query.whereEqualTo("focusState",type.getStateId());
        mUserFocusMapList = new ArrayList<>();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() != 0){
                    for (ParseObject mObject:objects){
                        UserFocusMap map = new UserFocusMap();
                        map.setProviderId(mObject.getString("providerId"));
                        map.setFansId(mObject.getString("fansId"));
                        map.setFocusState(FocusStateType.values()[mObject.getInt("focusState")]);
                        mUserFocusMapList.add(map);
                    }
                    IUserFocusView mFocusView = (IUserFocusView)mUserView;
                    mFocusView.onQueryFansOrFocusDone(true, mUserFocusMapList);
                }
                else {
                    IUserFocusView mFocusView = (IUserFocusView)mUserView;
                    mFocusView.onQueryFansOrFocusDone(false, mUserFocusMapList);
                }
            }
        });
    }

    @Override
    public void queryFansOrFocus(String userId,boolean isQueryFansNotFocus, final int start, final int count) {
        if (!NetworkUtil.isNetworkAvailable(mContext) || userId == null)
            return;
        String tag = "";
        ParseQuery<ParseObject> query;
        if (isQueryFansNotFocus){
            tag = "fansId";
            query = ParseQuery.getQuery("UserFocusMap")
                    .whereEqualTo("providerId",userId)
                    .whereEqualTo("focusState",FocusStateType.FOCUS.getValue())
                    .selectKeys(asList(tag));
        }
        else {
            tag = "providerId";
            query = ParseQuery.getQuery("UserFocusMap")
                    .whereEqualTo("fansId",userId)
                    .whereEqualTo("focusState",FocusStateType.FOCUS.getValue())
                    .selectKeys(asList(tag));
        }

        final String currentTag = tag;
        final ISearchUserInfoView mSearchUserInfoView = (ISearchUserInfoView) mUserView;

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null && objects.size() != 0) {
                    List<String> fansList = new ArrayList<String>();

                    for (ParseObject object : objects) {

                        fansList.add(object.getString(currentTag));
                    }

                    ParseQuery<ParseUser> queryTwo = ParseUser.getQuery().setSkip(start).setLimit(count);
                    queryTwo.whereContainedIn("objectId", fansList);

                    queryTwo.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            List<User> userResults = new ArrayList<>();
                            if (e == null && objects.size() != 0) {
                                for (ParseUser user : objects) {
                                    User fans = getUser(user);
                                    userResults.add(fans);
                                }
                            }

                            mSearchUserInfoView.onGetProviderUserDone(userResults);

                        }
                    });
                }
                else {
                    List<User> userResults = new ArrayList<>();
                    mSearchUserInfoView.onGetProviderUserDone(userResults);
                }
            }
        });

    }
}
