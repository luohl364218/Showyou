package com.zju.campustour.view.iview;

import com.zju.campustour.model.bean.HomepageSlideImg;

import java.util.List;

/**
 * Created by HeyLink on 2017/5/20.
 */

public interface IHomepageImgLoadView extends IImgLoadView {

    public void onImgUrlGot(List<HomepageSlideImg> mHomepageSlideImgs);
}
