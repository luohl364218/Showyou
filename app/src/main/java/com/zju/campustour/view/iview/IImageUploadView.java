package com.zju.campustour.view.iview;

import android.net.Uri;

/**
 * Created by HeyLink on 2017/8/18.
 */

public interface IImageUploadView {

    void imagePermissionRefused();

    void imageUploadSuccess(String imgUrl,Uri localPath);

    void imageUploadFailed(Exception e);


}
