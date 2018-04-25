package com.zju.campustour.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by HeyLink on 2017/10/24.
 */

public class PrivateOrderItemAdapter extends RecyclerView.Adapter<PrivateOrderItemAdapter.ViewHolder> {


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView itemImg;
        TextView itemContent;
        TextView itemHotDegree;

        public ViewHolder(View itemView) {
            super(itemView);




        }


        }


}
