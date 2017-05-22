package com.zju.campustour.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zju.campustour.R;
import com.zju.campustour.model.database.models.NewsModule;

import java.util.List;

/**
 * Created by HeyLink on 2017/5/20.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>{

    private List<NewsModule> mNewsModules;
    private onCardViewItemClickListener mListener;


    public NewsAdapter(List<NewsModule> mNewsModules) {
        this.mNewsModules = mNewsModules;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        View mView = mInflater.inflate(R.layout.template_recomment_info_cardview, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NewsModule mModule  = mNewsModules.get(position);
        holder.newsPic.setImageURI(mModule.getImgUrl());
        holder.newsContent.setText(mModule.getText());

    }

    @Override
    public int getItemCount() {
        return mNewsModules.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        SimpleDraweeView newsPic;
        TextView newsContent;

        public ViewHolder(View itemView) {
            super(itemView);
            newsPic = (SimpleDraweeView) itemView.findViewById(R.id.news_pic);
            newsContent = (TextView) itemView.findViewById(R.id.news_describe);

            //9.设置监听事件
            itemView.setOnClickListener(a->{
                if (mListener != null){
                    mListener.onClick(a, mNewsModules.get(getLayoutPosition()).getLinkUrl());
                }
            });
        }
    }

    public interface onCardViewItemClickListener{
        void onClick(View v,String url);
    }

    public void setOnCardViewItemClickListener(onCardViewItemClickListener listener){
        this.mListener = listener;
    }


}
