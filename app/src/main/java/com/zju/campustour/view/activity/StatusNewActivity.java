package com.zju.campustour.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baidu.location.BDLocation;
import com.zju.campustour.R;
import com.zju.campustour.presenter.implement.LocationProvider;
import com.zju.campustour.view.iview.ILocationConsumerView;

public class StatusNewActivity extends BaseActivity implements ILocationConsumerView{


    private LocationProvider mLocationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_new);


        mLocationProvider = new LocationProvider(this,this);
        mLocationProvider.requestUserLocation();
    }

    @Override
    public void onLocationInfoGotSuccess(BDLocation mLocation) {
        if (mLocation == null)
            return;
        showToast(mLocation.getCountry()+mLocation.getProvince()+mLocation.getCity()+mLocation.getDistrict());

    }

    @Override
    public void onLocationRequestRefused() {
        showToast("地理位置请求被拒绝，请手动开启");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationProvider.onClose();
    }
}
