package com.zju.campustour.presenter.protocal.event;

/**
 * Created by WuyuShan on 2017/7/27.
 */

public class ProjectDeleteEvent {

    private boolean isDelete;

    public ProjectDeleteEvent(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }
}
