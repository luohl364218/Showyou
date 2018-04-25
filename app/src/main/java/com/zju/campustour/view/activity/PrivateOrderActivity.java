package com.zju.campustour.view.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.zju.campustour.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PrivateOrderActivity extends BaseActivity {

    @BindView(R.id.private_order_rv)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_order);

        ButterKnife.bind(this);

    }
}
