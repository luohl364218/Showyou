package com.zju.campustour.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.ParseUser;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yalantis.ucrop.UCrop;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zju.campustour.MainActivity;
import com.zju.campustour.R;
import com.zju.campustour.model.chatting.utils.BitmapLoader;
import com.zju.campustour.model.chatting.utils.DialogCreator;
import com.zju.campustour.model.chatting.utils.FileHelper;
import com.zju.campustour.model.chatting.utils.HandleResponseCode;
import com.zju.campustour.model.chatting.utils.IdHelper;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.util.SharePreferenceManager;
import com.zju.campustour.presenter.protocal.event.UserPictureUploadDone;
import com.zju.campustour.view.chatting.widget.CircleImageView;
import com.zju.campustour.view.widget.GifSizeFilter;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FixProfileActivity extends BaseActivity {

    private static final String TAG = "FixProfileActivity";

    private Button mFinishBtn;
    private EditText mNickNameEt;
    private ImageView mAvatarIv;
    private String mPath;
    private ProgressDialog mDialog;
    private Dialog mSetAvatarDialog;
    private Context mContext;
    // 裁剪后图片的宽(X)和高(Y), 720 X 720的正方形。
    private static int OUTPUT_X = 720;
    private static int OUTPUT_Y = 720;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != savedInstanceState) {
            String nickName = savedInstanceState.getString("savedNickName");
            mNickNameEt.setText(nickName);
        }
        setContentView(R.layout.activity_fix_profile);
        mContext = this;
        mNickNameEt = (EditText) findViewById(R.id.nick_name_et);
        mAvatarIv = (CircleImageView) findViewById(R.id.jmui_avatar_iv);
        mFinishBtn = (Button) findViewById(R.id.finish_btn);
        mAvatarIv.setOnClickListener(listener);
        mFinishBtn.setOnClickListener(listener);
        JMessageClient.getUserInfo(JMessageClient.getMyInfo().getUserName(), null);
        SharePreferenceManager.setCachedFixProfileFlag(mContext,true);
        mNickNameEt.requestFocus();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstancedState) {
        savedInstancedState.putString("savedNickName", mNickNameEt.getText().toString());
        super.onSaveInstanceState(savedInstancedState);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.jmui_avatar_iv:
                    showSetAvatarDialog();
                    break;
                case R.id.finish_btn:
                    String nickName = mNickNameEt.getText().toString().trim();
                    if (nickName != null && !nickName.equals("")) {
                        final ProgressDialog dialog = new ProgressDialog(mContext);
                        dialog.setMessage(mContext.getString(R.string.saving_hint));
                        dialog.show();
                        dialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
                        UserInfo myUserInfo = JMessageClient.getMyInfo();
                        myUserInfo.setNickname(nickName);
                        JMessageClient.updateMyInfo(UserInfo.Field.nickname, myUserInfo, new BasicCallback() {
                            @Override
                            public void gotResult(final int status, String desc) {
                                //更新跳转标志
                                SharePreferenceManager.setCachedFixProfileFlag(mContext,false);
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                if (status != 0) {
                                    showToast(mContext.getString(R.string.nickname_save_failed));
                                }
                                startMainActivity();
                            }
                        });
                    }else {
                        showToast(FixProfileActivity.this.getString(R.string.nickname_not_null_toast));
                        return;
                    }
                    break;
            }
        }
    };

    public void showSetAvatarDialog() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.jmui_take_photo_btn:
                        mSetAvatarDialog.cancel();
                        takePhoto();
                        break;
                    case R.id.jmui_pick_picture_btn:
                        mSetAvatarDialog.cancel();
                        selectImageFromLocal();
                        break;
                }
            }
        };
        mSetAvatarDialog = DialogCreator.createSetAvatarDialog(this, listener);
        mSetAvatarDialog.show();
        mSetAvatarDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void startMainActivity() {
        Intent intent = new Intent();
        intent.setClass(FixProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void takePhoto() {
        if (FileHelper.isSdCardExist()) {
            mPath = FileHelper.createAvatarPath(JMessageClient.getMyInfo().getUserName());
            File file = new File(mPath);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            try {
                RxPermissions rxPermissions = new RxPermissions(this);
                rxPermissions.request(Manifest.permission.CAMERA)
                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                if (aBoolean) {
                                    startActivityForResult(intent, Constants.REQUEST_CODE_TAKE_PHOTO);
                                } else {
                                    showToast("权限请求被拒绝");
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
                showToast( IdHelper.getString(mContext, "camera_not_prepared"));
            }
        }else {
            Toast.makeText(this, this.getString(R.string.jmui_sdcard_not_exist_toast), Toast.LENGTH_SHORT).show();
        }
    }


    public void selectImageFromLocal(){
         /*Intent mIntent = new Intent(this, RegisterInfoOneActivity.class);
                    mIntent.putExtra("isEditMode",true);
                    startActivity(mIntent);*/
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            Matisse.from(FixProfileActivity.this)
                                    .choose(MimeType.ofAll(), false)
                                    .countable(true)
                                    .maxSelectable(1)
                                    .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                                    .gridExpectedSize(
                                            getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                    .thumbnailScale(0.85f)
                                    .imageEngine(new GlideEngine())
                                    .forResult(Constants.REQUEST_CODE_SELECT_PICTURE);
                        } else {
                            showToast("权限请求被拒绝");
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode){
            case Constants.REQUEST_CODE_TAKE_PHOTO:
                if (mPath != null) {
                    startCrop(mPath);
                }
                break;

            case Constants.REQUEST_CODE_SELECT_PICTURE:
                if (data != null) {

                    List<Uri> mUriList = Matisse.obtainResult(data);
                    Uri mUri = mUriList.get(0);

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(mUri,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    final String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    startCrop(picturePath);
                }
                break;

            case UCrop.REQUEST_CROP:
                if (data == null)
                    break;
                Uri croppedFileUri = UCrop.getOutput(data);

                if (croppedFileUri != null) {
                    imageUpLoad(croppedFileUri.getPath());
                }

                break;
            default:


        }

    }

    /**
     * 上传头像
     * @param localPath 要上传的文件路径
     */
    public void imageUpLoad(String localPath) {

        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpeg");
        OkHttpClient client = new OkHttpClient();
        String suffix = localPath.substring(localPath.lastIndexOf("."));

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        File f = new File(localPath);

        Glide.with(mContext).load(Uri.fromFile(f)).into(mAvatarIv);
        //上传极光聊天头像
        JMessageClient.updateUserAvatar(f,null);
        builder.addFormDataPart("file", f.getName(), RequestBody.create(MEDIA_TYPE_PNG, f));
        Date mDate = new Date();
        ParseUser currentLoginUser = ParseUser.getCurrentUser();
        String uriSuffix = currentLoginUser.getObjectId()+suffix + mDate.getTime();

        final MultipartBody requestBody = builder.addFormDataPart("name",uriSuffix).build();
        //构建请求
        final Request request = new Request.Builder()
                .url("http://119.23.248.205:8080")//地址
                .post(requestBody)//添加请求体
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("同学，不好意思，照片上传失败啦");
                    }
                }));

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.code() == 500){
                    runOnUiThread(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("同学，照片太大，上传失败啦");
                        }
                    }));
                }
                else {
                    runOnUiThread(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("报告同学，照片上传成功");
                            String imgUri = "http://119.23.248.205:8080/pictures/" + uriSuffix;
                            EventBus.getDefault().post(new UserPictureUploadDone(imgUri,localPath));

                        }
                    }));
                }

            }
        });

    }

    /**
     * 裁剪图片
     */
    private void startCrop(String url) {
        Uri sourceUri = Uri.fromFile(new File(url));
        //裁剪后保存到文件中
        Date mDate = new Date();
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), mDate.getTime()+"_showyou.jpeg"));
        UCrop.of(sourceUri, destinationUri).withAspectRatio(1, 1).withMaxResultSize(800, 800).start(this);
    }
}
