package com.zju.campustour.presenter.ipresenter;

import android.content.Intent;

import com.zju.campustour.presenter.protocal.enumerate.UploadImgType;

/**
 * Created by HeyLink on 2017/8/18.
 */

public interface IImageUploadPresenter {

    void takePhoto(UploadImgType type);

    void chooseUserImg(UploadImgType type);

    void startCrop(Intent data);

    void startCrop();

    void imageUpLoad(Intent data);
}
