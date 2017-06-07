package com.zju.campustour.view.chatting.adapter;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.picasso.Picasso;
import com.zju.campustour.R;
import com.zju.campustour.model.chatting.entity.FileItem;
import com.zju.campustour.model.chatting.entity.FileType;
import com.zju.campustour.view.chatting.MyImageView;
import com.zju.campustour.view.chatting.fragment.ImageFragment;
import com.zju.campustour.view.chatting.listener.UpdateSelectedStateListener;

import java.io.File;
import java.util.List;


public class ImageAdapter extends BaseAdapter {

    private ImageFragment mFragment;
    private List<FileItem> mList;
    private LayoutInflater mInflater;
    private SparseBooleanArray mSelectMap = new SparseBooleanArray();
    private UpdateSelectedStateListener mListener;

    public ImageAdapter(ImageFragment fragment, List<FileItem> list) {
        this.mFragment = fragment;
        this.mList = list;
        this.mInflater = LayoutInflater.from(fragment.getContext());
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        final FileItem item = mList.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_pick_picture_detail, null);
            holder = new ViewHolder();
            holder.icon = (MyImageView) convertView.findViewById(R.id.child_image);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.child_checkbox);
            holder.checkBoxLl = (LinearLayout) convertView.findViewById(R.id.checkbox_ll);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked()) {
                    if (mFragment.getTotalCount() < 5) {
                        if (mFragment.getTotalSize() + item.getLongFileSize() < 10485760.0) {
                            holder.checkBox.setChecked(true);
                            mSelectMap.put(position, true);
                            mListener.onSelected(item.getFilePath(), item.getLongFileSize(), FileType.image);
                            addAnimation(holder.checkBox);
                        } else {
                            holder.checkBox.setChecked(false);
                            Toast.makeText(mFragment.getContext(), mFragment.getString(R.string
                                    .file_size_over_limit_hint), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        holder.checkBox.setChecked(false);
                        Toast.makeText(mFragment.getContext(), mFragment.getString(R.string
                                .size_over_limit_hint), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mSelectMap.delete(position);
                    mListener.onUnselected(item.getFilePath(), item.getLongFileSize(), FileType.image);
                }
            }
        });

        holder.checkBox.setChecked(mSelectMap.get(position));
        try {
            Picasso.with(mFragment.getContext()).load(new File(item.getFilePath())).into(holder.icon);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private void addAnimation(View view) {
        float[] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
                ObjectAnimator.ofFloat(view, "scaleY", vaules));
        set.setDuration(150);
        set.start();
    }

    public void setUpdateListener(UpdateSelectedStateListener listener) {
        this.mListener = listener;
    }

    private class ViewHolder {
        CheckBox checkBox;
        ImageView icon;
        LinearLayout checkBoxLl;
    }
}
