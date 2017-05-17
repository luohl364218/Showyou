package com.zju.campustour.presenter.protocal.enumerate;

/**
 * Created by HeyLink on 2017/4/24.
 */

public enum UserProjectStateType {

    COLLECT("收藏",0),

    BOOK_ACCEPT("接受预定",1),

    WAIT_TO_PAY("等待付款",2),

    CANCEL_SUCCESS("取消成功",3),

    BOOK_SUCCESS("成功预定",4),

    WAIT_CANCEL_CONFIRM("申请取消",5),

    FINISHED("已完成",6);

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
