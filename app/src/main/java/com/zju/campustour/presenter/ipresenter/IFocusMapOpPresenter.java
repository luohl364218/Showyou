package com.zju.campustour.presenter.ipresenter;

import com.zju.campustour.presenter.protocal.enumerate.FocusStateType;

/**
 * Created by HeyLink on 2017/4/24.
 */

public interface IFocusMapOpPresenter {

    public void put(String providerId, String fansId, FocusStateType type);
    public void delete(String providerId, String fansId, FocusStateType type);
    public void queryFansNum(String providerId);
    public void query(String providerId, String fansId, FocusStateType type);

}
