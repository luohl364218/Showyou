package com.zju.campustour.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Toast;

import com.zju.campustour.model.util.NetworkUtil;

/**
 * Created by HeyLink on 2017/5/17.
 */

public class BaseFragment extends Fragment {

    private Toast mToast = null;
    protected   boolean isNetworkUseful = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isNetworkUseful = NetworkUtil.isNetworkAvailable(getContext());

    }

    public void showToast(Context mContext, String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(mContext, text,
                        Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

    public void showToast(Context mContext,int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, resId,
                    Toast.LENGTH_SHORT);
        } else {
            mToast.setText(resId);
        }
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }
}
