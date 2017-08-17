package com.zju.campustour.view.iview;

import com.baidu.location.BDLocation;

/**
 * Created by HeyLink on 2017/8/17.
 */

public interface ILocationConsumerView {

    void onLocationInfoGotSuccess(BDLocation mLocation);

    void onLocationRequestRefused();
}
