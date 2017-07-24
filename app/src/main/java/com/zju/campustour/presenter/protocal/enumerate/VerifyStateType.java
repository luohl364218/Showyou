package com.zju.campustour.presenter.protocal.enumerate;

/**
 * Created by HeyLink on 2017/7/23.
 */

public enum VerifyStateType {

    VERIFY_NOT_YET("未认证",0),
    VERIFY_DONE("已认证",1),
    VERIFY_ING("审核中",2),
    VERIFY_FAILED("审核失败",3);

    private String verifyState;
    private int verifyId;

    VerifyStateType(String verify,int verifyId) {
        this.verifyState = verify;
        this.verifyId = verifyId;
    }

    public String getVerifyState() {
        return verifyState;
    }

   public void setVerifyState(String mVerifyState) {
        verifyState = mVerifyState;
    }

    public int getVerifyId() {
        return verifyId;
    }

    public void setVerifyId(int mVerifyId) {
        verifyId = mVerifyId;
    }
}
