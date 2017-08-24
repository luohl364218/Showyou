package com.zju.campustour.presenter.implement;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.parse.ParseUser;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yalantis.ucrop.UCrop;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zju.campustour.R;
import com.zju.campustour.model.chatting.utils.FileHelper;
import com.zju.campustour.model.chatting.utils.IdHelper;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.util.SharePreferenceManager;
import com.zju.campustour.presenter.ipresenter.IImageUploadPresenter;
import com.zju.campustour.presenter.protocal.enumerate.UploadImgType;
import com.zju.campustour.presenter.protocal.event.UserPictureUploadDone;
import com.zju.campustour.view.activity.RegisterLookAroundActivity;
import com.zju.campustour.view.iview.IImageUploadView;
import com.zju.campustour.view.widget.GifSizeFilter;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Date;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;

/**
 * Created by HeyLink on 2017/8/18.
 */

public class ImageUploader implements IImageUploadPresenter {

    private Activity mContext;
    private IImageUploadView mImageUploadView;
    private UploadImgType mUploadImgType;
    String mPhotoPath;


    public ImageUploader(Activity mContext, IImageUploadView mImageUploadView) {
        this.mContext = mContext;
        this.mImageUploadView = mImageUploadView;
    }


    public void takePhoto(UploadImgType type) {

        mUploadImgType = type;
        if (FileHelper.isSdCardExist()) {
            mPhotoPath = FileHelper.createAvatarPath(null);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mPhotoPath)));
            try {
                RxPermissions rxPermissions = new RxPermissions(mContext);
                rxPermissions.request(Manifest.permission.CAMERA)
                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                if (aBoolean) {
                                    mContext.startActivityForResult(intent, Constants.REQUEST_CODE_TAKE_PHOTO);
                                } else {
                                    mImageUploadView.imagePermissionRefused();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });

            } catch (ActivityNotFoundException anf) {
                mImageUploadView.imagePermissionRefused();

            }
        } else {
            mImageUploadView.imagePermissionRefused();
        }
    }

    @Override
    public void chooseUserImg(UploadImgType type) {

        mUploadImgType = type;

        RxPermissions rxPermissions = new RxPermissions(mContext);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            Matisse.from(mContext)
                                    .choose(MimeType.ofAll(), false)
                                    .countable(true)
                                    .maxSelectable(1)
                                    .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                                    .gridExpectedSize(
                                            mContext.getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                    .thumbnailScale(0.85f)
                                    .imageEngine(new GlideEngine())
                                    .forResult(Constants.REQUEST_CODE_CHOOSE);
                        } else {
                            mImageUploadView.imagePermissionRefused();
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
    public void startCrop(Intent data) {

        List<Uri> mUriList = Matisse.obtainResult(data);
        Uri mUri = mUriList.get(0);

        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = mContext.getContentResolver().query(mUri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        final String url = cursor.getString(columnIndex);
        cursor.close();

        Uri sourceUri = Uri.fromFile(new File(url));
        //裁剪后保存到文件中
        Date mDate = new Date();
        String date = SharePreferenceManager.ConvertDateToSimpleString(mDate);
        Uri destinationUri = Uri.fromFile(new File(mContext.getCacheDir(), date+"_showyou.jpeg"));
        UCrop.of(sourceUri, destinationUri).withAspectRatio(1, 1).withMaxResultSize(800, 800).start(mContext);
    }

    @Override
    public void startCrop() {
        if (mPhotoPath == null)
            return;
        Uri sourceUri = Uri.fromFile(new File(mPhotoPath));
        //裁剪后保存到文件中
        Date mDate = new Date();
        String date = SharePreferenceManager.ConvertDateToSimpleString(mDate);
        Uri destinationUri = Uri.fromFile(new File(mContext.getCacheDir(), date+"_showyou.jpeg"));
        UCrop.of(sourceUri, destinationUri).withAspectRatio(1, 1).withMaxResultSize(800, 800).start(mContext);
    }

    @Override
    public void imageUpLoad(Intent data) {
        //MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpeg");

        Uri croppedFileUri = UCrop.getOutput(data);

        if (croppedFileUri == null)
            return;

        String localPath = croppedFileUri.getPath();

        String suffix = localPath.substring(localPath.lastIndexOf("."));
        /*创建一个独一无二的key*/
        Date mDate = new Date();
        String date = SharePreferenceManager.ConvertDateToSimpleString(mDate);
        ParseUser currentUser = ParseUser.getCurrentUser();
        /*用户ID+时间+后缀*/
        final String uriSuffix = mUploadImgType.getFilePrefix() + currentUser.getObjectId() + "_"+ date + suffix;

        //如果是更新用户头像，则上传极光聊天头像

        if (mUploadImgType == UploadImgType.IMG_AVATAR){
            File f = new File(localPath);
            JMessageClient.updateUserAvatar(f, null);

        }

        //上传照片到服务器
        String endpoint = Constants.AliEndPoint;

        OSSPlainTextAKSKCredentialProvider credentialProvider =
                new OSSPlainTextAKSKCredentialProvider(Constants.AccessKeyId,Constants.AccessKeySecret);

        OSS oss = new OSSClient(mContext, endpoint, credentialProvider);

        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(Constants.BucketName, uriSuffix, localPath);

        /*异步上传任务*/
        oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {

                String imgUrl = Constants.URL_PREFIX_ALIYUN + uriSuffix;
                Uri localUrl= Uri.fromFile(new File(localPath));

                //如果是更新表情，则要同步聊天的第三方服务器和自己的Parse服务器
                if (mUploadImgType == UploadImgType.IMG_AVATAR){
                    SharePreferenceManager.putString(mContext, Constants.DB_USERIMG,localPath);
                    currentUser.put("imgUrl",imgUrl);
                    currentUser.saveInBackground();
                    EventBus.getDefault().post(new UserPictureUploadDone(imgUrl,localPath));
                }

                mImageUploadView.imageUploadSuccess(imgUrl,localUrl);
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
                mImageUploadView.imageUploadFailed(clientExcepion);
            }
        });



    }
}
