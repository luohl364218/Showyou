package com.zju.campustour.view.chatting.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.zju.campustour.R;
import com.zju.campustour.presenter.chatting.tools.NativeImageLoader;
import com.zju.campustour.view.chatting.MyImageView;

import java.util.ArrayList;
import java.util.List;


public class PickPictureAdapter extends BaseAdapter {
    /**
     * 用来存储图片的选中情况
     */
    private SparseBooleanArray mSelectMap = new SparseBooleanArray();
    private GridView mGridView;
    private List<String> mList;
    protected LayoutInflater mInflater;
    private Context mContext;
    private Button mSendBtn;
    private float mDensity;
    private boolean mChecked;

    public PickPictureAdapter(Context context, List<String> list, GridView mGridView, float density) {
        this.mContext = context;
        this.mList = list;
        this.mGridView = mGridView;
        mInflater = LayoutInflater.from(context);
        Activity activity = (Activity) mContext;
        mSendBtn = (Button) activity.findViewById(R.id.pick_picture_send_btn);
        this.mDensity = density;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        String path = mList.get(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_pick_picture_detail, null);
            viewHolder = new ViewHolder();
            viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.child_image);
            viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.child_checkbox);
            viewHolder.mCheckBoxLl = (LinearLayout) convertView.findViewById(R.id.checkbox_ll);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.mImageView.setImageResource(R.drawable.jmui_picture_not_found);
        }
        viewHolder.mImageView.setTag(path);
        //增加选中checkbox面积
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mChecked) {
                    if (mSelectMap.size() < 9) {
                        mSelectMap.put(position, true);
                        viewHolder.mCheckBox.setChecked(true);
                        addAnimation(viewHolder.mCheckBox);
                        mChecked = true;
                    } else {
                        mChecked = false;
                        Toast.makeText(mContext, mContext.getString(R.string.picture_num_limit_toast),
                                Toast.LENGTH_SHORT).show();
                        viewHolder.mCheckBox.setChecked(mSelectMap.get(position));
                    }
                } else if (mSelectMap.size() <= 9) {
                    mSelectMap.delete(position);
                    mChecked = false;
                    viewHolder.mCheckBox.setChecked(false);
                }

                if (mSelectMap.size() > 0) {
                    mSendBtn.setClickable(true);
                    mSendBtn.setText(mContext.getString(R.string.jmui_send) + "(" + mSelectMap.size() + "/" + "9)");
                } else {
                    mSendBtn.setText(mContext.getString(R.string.jmui_send));
                    mSendBtn.setClickable(false);
                }
            }
        };
        viewHolder.mCheckBoxLl.setOnClickListener(listener);
        viewHolder.mCheckBox.setOnClickListener(listener);

        viewHolder.mCheckBox.setChecked(mSelectMap.get(position));

        //利用NativeImageLoader类加载本地图片
        Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, (int) (80 * mDensity),
                new NativeImageLoader.NativeImageCallBack() {

                    @Override
                    public void onImageLoader(Bitmap bitmap, String path) {
                        ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
                        if (bitmap != null && mImageView != null) {
                            mImageView.setImageBitmap(bitmap);
                        }
                    }
                });

        if (bitmap != null) {
            viewHolder.mImageView.setImageBitmap(bitmap);
        } else {
            viewHolder.mImageView.setImageResource(R.drawable.jmui_picture_not_found);
        }

        return convertView;
    }

    /**
     * 给CheckBox加点击动画，利用开源库nineoldandroids设置动画
     */
    private void addAnimation(View view) {
        float[] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
                ObjectAnimator.ofFloat(view, "scaleY", vaules));
        set.setDuration(150);
        set.start();
    }


    /**
     * 获取选中的Item的position
     *
     * @return 选中的图片路径集合
     */
    public List<Integer> getSelectItems() {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < mSelectMap.size(); i++) {
            list.add(mSelectMap.keyAt(i));
        }

        return list;
    }

    /*
    获得选中的图片，用于点击图片进入BrowserViewPagerActivity的初始化
     */
    public int[] getSelectedArray() {
        int pathArray[] = new int[mList.size()];
        for (int i = 0; i < pathArray.length; i++) {
            pathArray[i] = 0;
        }
        for (int i = 0; i < mSelectMap.size(); i++) {
            pathArray[mSelectMap.keyAt(i)] = 1;
        }
        return pathArray;
    }

    public void refresh(int[] pathArray) {
        mSelectMap.clear();
        for (int i = 0; i < pathArray.length; i++) {
            if (pathArray[i] == 1) {
                mSelectMap.put(i, true);
            }
        }
        notifyDataSetChanged();
    }


    private static class ViewHolder {
        MyImageView mImageView;
        CheckBox mCheckBox;
        LinearLayout mCheckBoxLl;
    }
}
