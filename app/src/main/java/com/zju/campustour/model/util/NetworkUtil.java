package com.zju.campustour.model.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by HeyLink on 2017/5/17.
 */

public class NetworkUtil {

    /**
     * 检测当的网络（WLAN、3G/2G）状态
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络连接，判断是否可达
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNetWorkAvailable() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process pingProcess = runtime.exec("ping www.baidu.com");
            int exitCode = pingProcess.waitFor();
            return (exitCode == 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
