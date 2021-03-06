package com.zju.campustour.view.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zju.campustour.R;
import com.zju.campustour.model.chatting.entity.ImageBean;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.presenter.chatting.tools.SortPictureList;
import com.zju.campustour.view.chatting.adapter.AlbumListAdapter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PickPictureTotalActivity extends BaseActivity {

    private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();
    private List<ImageBean> list = new ArrayList<ImageBean>();
    private final static int SCAN_OK = 1;
    private final static int SCAN_ERROR = 2;
    private ProgressDialog mProgressDialog;
    private AlbumListAdapter adapter;
    private ListView mListView;
    private ImageButton mReturnBtn;
    private TextView mTitle;
    private ImageButton mMenuBtn;
    private Intent mIntent;
    private final MyHandler myHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<PickPictureTotalActivity> mActivity;

        public MyHandler(PickPictureTotalActivity activity) {
            mActivity = new WeakReference<PickPictureTotalActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PickPictureTotalActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case SCAN_OK:
                        //关闭进度条
                        activity.mProgressDialog.dismiss();
                        activity.adapter = new AlbumListAdapter(activity, activity.list = activity
                                .subGroupOfImage(activity.mGruopMap), activity.mDensity);
                        activity.mListView.setAdapter(activity.adapter);
                        break;
                    case SCAN_ERROR:
                        activity.mProgressDialog.dismiss();
                        activity.showToast(activity.getString(R.string.sdcard_not_prepare_toast));
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_picture_total);
        mReturnBtn = (ImageButton) findViewById(R.id.return_btn);
        mTitle = (TextView) findViewById(R.id.title);
        mMenuBtn = (ImageButton) findViewById(R.id.right_btn);
//		mGroupGridView = (GridView) findViewById(R.id.pick_picture_total_grid_view);
        mListView = (ListView) findViewById(R.id.pick_picture_total_list_view);
        mTitle.setText(this.getString(R.string.choose_album_title));
        mMenuBtn.setVisibility(View.GONE);
        getImages();
        mIntent = this.getIntent();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<String> childList = mGruopMap.get(list.get(position).getFolderName());
                mIntent.setClass(PickPictureTotalActivity.this, PickPictureActivity.class);
                mIntent.putStringArrayListExtra("data", (ArrayList<String>)childList);
                startActivityForResult(mIntent, Constants.REQUEST_CODE_SELECT_ALBUM);

            }
        });

        mReturnBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }

        });

    }

    @Override
    protected void onPause() {
        mProgressDialog.dismiss();
        super.onPause();
    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
     */
    private void getImages() {
        //显示进度条
        mProgressDialog = ProgressDialog.show(this, null, PickPictureTotalActivity.this.getString(R.string.jmui_loading));

        new Thread(new Runnable() {

            @Override
            public void run() {
                Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = PickPictureTotalActivity.this.getContentResolver();
                String[] projection = new String[]{ MediaStore.Images.ImageColumns.DATA };
                Cursor cursor = contentResolver.query(imageUri, projection, null, null,
                        MediaStore.Images.Media.DATE_MODIFIED);
                if (cursor == null || cursor.getCount() == 0) {
                    myHandler.sendEmptyMessage(SCAN_ERROR);
                } else {
                    while (cursor.moveToNext()) {
                        //获取图片的路径
                        String path = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Images.Media.DATA));

                        try{
                            //获取该图片的父路径名
                            String parentName = new File(path).getParentFile().getName();
                            //根据父路径名将图片放入到mGruopMap中
                            if (!mGruopMap.containsKey(parentName)) {
                                List<String> chileList = new ArrayList<String>();
                                chileList.add(path);
                                mGruopMap.put(parentName, chileList);
                            } else {
                                mGruopMap.get(parentName).add(path);
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    cursor.close();
                    //通知Handler扫描图片完成
                    myHandler.sendEmptyMessage(SCAN_OK);
                }
            }
        }).start();

    }


    /**
     * 组装分组界面GridView的数据源，因为我们扫描手机的时候将图片信息放在HashMap中
     * 所以需要遍历HashMap将数据组装成List
     *
     * @param mGruopMap 相册HashMap
     * @return List<ImageBean>
     */
    private List<ImageBean> subGroupOfImage(HashMap<String, List<String>> mGruopMap) {
        if (mGruopMap.size() == 0) {
            return null;
        }
        List<ImageBean> list = new ArrayList<ImageBean>();

        Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<String>> entry = it.next();
            ImageBean mImageBean = new ImageBean();
            String key = entry.getKey();
            List<String> value = entry.getValue();
            SortPictureList sortList = new SortPictureList();
            Collections.sort(value, sortList);
            mImageBean.setFolderName(key);
            mImageBean.setImageCounts(value.size());
            mImageBean.setTopImagePath(value.get(0));//获取该组的第一张图片

            list.add(mImageBean);
        }

        //对相册进行排序，最近修改的相册放在最前面
        SortImageBeanComparator sortComparator = new SortImageBeanComparator(list);
        Collections.sort(list, sortComparator);

        return list;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (resultCode == Constants.RESULT_CODE_SELECT_ALBUM) {
            setResult(Constants.RESULT_CODE_SELECT_PICTURE, data);
            finish();
        }
    }

    public static class SortImageBeanComparator implements Comparator<ImageBean> {

        List<ImageBean> list;

        public SortImageBeanComparator(List<ImageBean> list){
            this.list = list;
        }

        //根据相册的第一张图片进行排序，最近修改的放在前面
        public int compare(ImageBean arg0, ImageBean arg1) {
            String path1 = arg0.getTopImagePath();
            String path2 = arg1.getTopImagePath();
            File f1 = new File(path1);
            File f2 = new File(path2);
            if (f1.lastModified() < f2.lastModified()) {
                return 1;
            }else {
                return -1;
            }
        }
    }
}
