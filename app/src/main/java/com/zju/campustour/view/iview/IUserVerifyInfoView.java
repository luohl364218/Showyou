package com.zju.campustour.view.iview;

import com.zju.campustour.model.database.models.VerifyInfo;

/**
 * Created by WuyuShan on 2017/7/25.
 */

public interface IUserVerifyInfoView extends IUserView {

    void onSubmitVerifyInfoSuccess(boolean isRefresh);

    void onSubmitVerifyInfoFailed(Exception e);

    void onQueryVerifyInfoDone(VerifyInfo verifyInfo);

}
