package com.zju.campustour.presenter.ipresenter;

/**
 * Created by HeyLink on 2017/8/17.
 */

public interface ILocationProviderPresenter {

    void requestUserLocation();

    void searchNearbyLocation();

    void onClose();
}
