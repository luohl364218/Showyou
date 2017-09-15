package com.zju.campustour.presenter.ipresenter;


/**
 * Created by HeyLink on 2017/4/24.
 */

public interface IUserInfoOpPresenter {

    public void initialUserInfo();

    public void registerUser(String userName, String password);

    public void queryUserInfoWithId(String userId);

    public void queryUserInfoWithUserName(String userName);

    public void userLogin(String loginName, String password);

    public void userLogout(String userId);

    public void userDelete(String userId);

    public void queryMajorStudent(String majorName, int categoryId);

    public void queryUserWithConditions(int start, int startGrade, int endGrade,boolean isOrderByFansNum, boolean isOrderByLatest, boolean isMajorNotCommon);

    public void queryUserWithConditions(String school, String major, int start, int area, int categoryId);

    public void queryUserWithConditions(String school, String major, int start, int area, int categoryId, boolean isOrderByFansNum, boolean isOrderByLatest);

    public void queryUserWithConditions(String school, String major, int start, int area, int categoryId, boolean isOrderByFansNum, boolean isOrderByLatest, boolean isMajorNotCommon);

    public void searchRelativeUserWithConditions(String condition,int start);

}
