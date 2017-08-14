package com.zju.campustour.presenter.ipresenter;

import com.zju.campustour.model.database.models.VerifyInfo;
import com.zju.campustour.presenter.protocal.enumerate.IdentityType;

/**
 * Created by WuyuShan on 2017/7/25.
 */

public interface IUserVerifyInfoPresenter {

    void submitVerifyInfo(VerifyInfo verifyInfo);

    void queryVerifyInfoState(String userId, IdentityType identityType);

    void refreshVerifyInfo(VerifyInfo verifyInfo);

}
