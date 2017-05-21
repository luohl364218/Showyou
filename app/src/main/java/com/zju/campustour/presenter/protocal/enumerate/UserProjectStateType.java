package com.zju.campustour.presenter.protocal.enumerate;

/**
 * Created by HeyLink on 2017/4/24.
 */

public enum UserProjectStateType {

    COLLECT("收藏",0),

    WAIT_TO_PAY("等待确认",1),

    CANCEL_SUCCESS("取消成功",2),

    BOOK_SUCCESS("预约成功",3),

    WAIT_CANCEL_CONFIRM("申请取消",4),

    WAIT_TO_COMMENT("评价活动",5),

    FINISHED("已完成",6),

    INTERRUPT("中断",7);

    private String name;
    private int index;

    UserProjectStateType(String mName, int mIndex){
        this.name = mName;
        this.index = mIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String mName) {
        name = mName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int mIndex) {
        index = mIndex;
    }

    public int getValue(){
        return this.ordinal();
    }
}
