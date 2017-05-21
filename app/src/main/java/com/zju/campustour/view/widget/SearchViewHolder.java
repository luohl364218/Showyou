package com.zju.campustour.view.widget;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hongkl on 16/8/13.
 * 通用的ViewHolder,通过控件的ID查找控件
 */
public class SearchViewHolder {
    private View mConvertView;
    //1.效率较高
    //2.key值只能是integer
    SparseArray<View> mViews = null;

    public SearchViewHolder(Context context, int layoutID, ViewGroup parent) {

        mViews = new SparseArray<>();
        mConvertView = LayoutInflater.from(context).inflate(layoutID, parent,false);
        mConvertView.setTag(this);
    }

    /***
     * 获取一个ViewHolder的对象
     * @param context
     * @param layoutID
     * @param convertView
     * @param parent
     * @return
     */
    public static SearchViewHolder get(Context context, int layoutID, View convertView, ViewGroup parent){

        if (convertView == null){
            return new SearchViewHolder(context,layoutID,parent);
        }
        return (SearchViewHolder) convertView.getTag();
    }


    public <T extends View> T getView(int viewID){

        View view = mViews.get(viewID);
        if (view == null){
            view = mConvertView.findViewById(viewID);
            mViews.put(viewID,view);
        }
        return (T)view;
    }

    public View getConvertView(){
        return mConvertView;
    }


}
