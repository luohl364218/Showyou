package com.zju.campustour.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.zju.campustour.R;

public class ProviderHomePageActivity extends AppCompatActivity {
    private String selectedProviderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_major_provider_home_page);

        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            getBundleExtras(extras);
        }


    }


    protected void getBundleExtras(Bundle extras) {

        selectedProviderId = extras.getString("provider_id");

    }
}
