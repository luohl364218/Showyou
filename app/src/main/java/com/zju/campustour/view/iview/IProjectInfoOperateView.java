package com.zju.campustour.view.iview;

/**
 * Created by WuyuShan on 2017/7/27.
 */

public interface IProjectInfoOperateView extends IProjectView {

    void onDeleteProjectSuccess();
    void onDeleteProjectFailed(Exception e);


}
