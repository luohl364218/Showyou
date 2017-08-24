package com.zju.campustour.presenter.implement;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zju.campustour.presenter.ipresenter.ILocationProviderPresenter;
import com.zju.campustour.view.iview.ILocationConsumerView;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by HeyLink on 2017/8/17.
 */

public class LocationProvider implements ILocationProviderPresenter {

    private Activity mActivity;
    private ILocationConsumerView mConsumerView;
    private LocationClient mLocationClient;

    public LocationProvider(Activity mContext, ILocationConsumerView mConsumerView) {
        this.mActivity = mContext;
        this.mConsumerView = mConsumerView;
    }

    @Override
    public void requestUserLocation() {

        RxPermissions rxPermissions = new RxPermissions(mActivity);
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {

                            if (mLocationClient == null)
                                mLocationClient = new LocationClient(mActivity);
                            mLocationClient.registerLocationListener(new MyLocationListener());

                            LocationClientOption option = new LocationClientOption();

                            option.setIsNeedAddress(true);
                            option.setIsNeedLocationDescribe(true);
                            option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
                            mLocationClient.setLocOption(option);

                            mLocationClient.start();

                        } else {
                            mConsumerView.onLocationRequestRefused();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onClose() {
        if (mLocationClient != null)
            mLocationClient.stop();
    }


    class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation mBDLocation) {
            mConsumerView.onLocationInfoGotSuccess(mBDLocation);
        }
    }


}
