package com.zju.campustour.view.IView;

import com.zju.campustour.model.database.models.UserFocusMap;

import java.util.List;

/**
 * Created by HeyLink on 2017/5/17.
 */

public interface IUserFocusView extends IUserView {

    public void onFocusActionError(boolean flag);

    public void onQueryFansOrFocusDone(boolean isFocus,List<UserFocusMap> userFocusList);

    public void onGetFansNumDone(int fansNum);

    public void onGetDealNumDone(int dealNum);

}
