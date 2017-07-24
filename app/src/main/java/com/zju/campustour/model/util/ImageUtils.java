package com.zju.campustour.model.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.yalantis.ucrop.UCrop;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.presenter.protocal.event.UserPictureUploadDone;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Date;

import cn.jpush.im.android.api.JMessageClient;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.zju.campustour.model.common.Constants.AccessKeyId;
import static com.zju.campustour.model.common.Constants.AccessKeySecret;

/**
 * Created by HeyLink on 2017/7/19.
 * 专门处理图片的上传和裁剪
 */

public class ImageUtils {

    /**
     * 裁剪照片
     * */
    public static void startCrop(String url, Activity mActivity) {
        Uri sourceUri = Uri.fromFile(new File(url));
        //裁剪后保存到文件中
        Date mDate = new Date();
        Uri destinationUri = Uri.fromFile(new File(mActivity.getCacheDir(), mDate.getTime() + "_showyou.jpeg"));
        UCrop.of(sourceUri, destinationUri).withAspectRatio(1, 1).withMaxResultSize(800, 800).start(mActivity);
    }

    /**
     * 上传照片
     * */
    public void imageUpLoad(String localPath, String userId, Activity mActivity) {

        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpeg");

        String suffix = localPath.substring(localPath.lastIndexOf("."));
        /*创建一个独一无二的key*/
        Date mDate = new Date();
        String uriSuffix = userId + mDate.getTime() + suffix;

        File f = new File(localPath);
        //上传极光聊天头像
        JMessageClient.updateUserAvatar(f, null);


        String endpoint = Constants.AliEndPoint;
       /* OSSCredentialProvider credentialProvider = new OSSCustomSignerCredentialProvider() {
            @Override
            public String signContent(String content) {
                // 您需要在这里依照OSS规定的签名算法，实现加签一串字符内容，并把得到的签名传拼接上AccessKeyId后返回
                // 一般实现是，将字符内容post到您的业务服务器，然后返回签名
                // 如果因为某种原因加签失败，描述error信息后，返回nil
                // 以下是用本地算法进行的演示

                return "OSS " + AccessKeyId + ":" + base64(hmac-sha1(AccessKeySecret, content));
            }
        };*/

        OSSPlainTextAKSKCredentialProvider credentialProvider =
                new OSSPlainTextAKSKCredentialProvider(Constants.AccessKeyId,Constants.AccessKeySecret);

        OSS oss = new OSSClient(mActivity.getApplicationContext(), endpoint, credentialProvider);

        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(Constants.BucketName, uriSuffix, localPath);

        // 异步上传时可以设置进度回调
        ProgressDialog mProgressDialog = new ProgressDialog(mActivity.getBaseContext());
        mProgressDialog.setMax(100);
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                mProgressDialog.setProgress((int) (currentSize/totalSize));
            }
        });
        //// TODO: 2017/7/21
        /*异步上传任务*/
        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
            }
            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });

        String imgUri = "http://119.23.248.205:8080/pictures/" + uriSuffix;
        EventBus.getDefault().post(new UserPictureUploadDone(imgUri, localPath));

    }

}
