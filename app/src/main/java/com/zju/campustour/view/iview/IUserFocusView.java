package com.zju.campustour.view.iview;

import com.zju.campustour.model.database.models.User;
import com.zju.campustour.model.database.models.UserFocusMap;

import java.util.List;

/**
 * Created by HeyLink on 2017/5/17.
 */

public interface IUserFocusView extends IUserView {

    public void onFocusActionError(boolean flag);

    public void onQueryFansOrFocusDone(boolean isFocus,List<UserFocusMap> userFocusList);

    public void onGetFansNumDone(int fansNum);

    public void onQueryMyFansDone(List<User> fansList);

    public void onQueryMyFocusDone(List<User> focusList);


}
