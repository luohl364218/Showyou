package com.zju.campustour.presenter.ipresenter;

/**
 * Created by HeyLink on 2017/6/24.
 */

public interface IMajorInfoPresenter {

    void getAllMajorInfo();

    void getUpdateMajorInfo();

    void getMajorInterest(String majorName);

    void addMajorInterests(String majorName);
}
