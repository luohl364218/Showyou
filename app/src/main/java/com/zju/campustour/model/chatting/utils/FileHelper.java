package com.zju.campustour.model.chatting.utils;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.view.application.CampusTourApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import cn.jpush.im.android.api.JMessageClient;


/**
 * Created by Ken on 2015/11/13.
 */
public class FileHelper {

    private static FileHelper mInstance = new FileHelper();

    public static FileHelper getInstance() {
        return mInstance;
    }

    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String createAvatarPath(String userName) {
        String dir = CampusTourApplication.PICTURE_DIR;
        File destDir = new File(dir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        File file;
        if (userName != null) {
            file = new File(dir, userName + ".png");
        }else {
            file = new File(dir, new DateFormat().format("yyyy_MMdd_hhmmss",
                    Calendar.getInstance(Locale.CHINA)) + ".png");
        }
        return file.getAbsolutePath();
    }

    public static String getUserAvatarPath(String userName) {
        return CampusTourApplication.PICTURE_DIR + userName + ".png";
    }


    public interface CopyFileCallback {
        public void copyCallback(Uri uri);
    }

    public void copyFile(final String fileName, final String filePath, final Activity context,
                         final CopyFileCallback callback) {
        if (isSdCardExist()) {
            final Dialog dialog = DialogCreator.createLoadingDialog(context,
                    context.getString(R.string.jmui_loading));
            dialog.show();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        FileInputStream fis = new FileInputStream(new File(filePath));
                        File destDir = new File(Constants.FILE_DIR);
                        if (!destDir.exists()) {
                            destDir.mkdirs();
                        }
                        final File tempFile = new File(Constants.FILE_DIR + fileName);
                        FileOutputStream fos = new FileOutputStream(tempFile);
                        byte[] bt = new byte[1024];
                        int c;
                        while((c = fis.read(bt)) > 0) {
                            fos.write(bt,0,c);
                        }
                        //关闭输入、输出流
                        fis.close();
                        fos.close();

                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.copyCallback(Uri.fromFile(tempFile));
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        });
                    }
                }
            });
            thread.start();
        }else {
            Toast.makeText(context, context.getString(R.string.jmui_sdcard_not_exist_toast), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 复制后裁剪文件
     * @param file 要复制的文件
     */
    public void copyFile(final File file, final Activity context, final CopyFileCallback callback) {
        if (isSdCardExist()) {
            final Dialog dialog = DialogCreator.createLoadingDialog(context,
                    context.getString(R.string.jmui_loading));
            dialog.show();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        String path = createAvatarPath(JMessageClient.getMyInfo().getUserName());
                        final File tempFile = new File(path);
                        FileOutputStream fos = new FileOutputStream(tempFile);
                        byte[] bt = new byte[1024];
                        int c;
                        while((c = fis.read(bt)) > 0) {
                            fos.write(bt,0,c);
                        }
                        //关闭输入、输出流
                        fis.close();
                        fos.close();

                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.copyCallback(Uri.fromFile(tempFile));
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        });
                    }
                }
            });
            thread.start();
        }else {
            Toast.makeText(context, context.getString(R.string.jmui_sdcard_not_exist_toast), Toast.LENGTH_SHORT).show();
        }
    }
}
