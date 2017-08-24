package com.zju.campustour.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zju.campustour.R;
import com.zju.campustour.model.bean.NewsModule;

import java.util.List;

import static com.zju.campustour.model.util.SharePreferenceManager.ConvertDateToDetailString;

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
        holder.newsTime.setText(ConvertDateToDetailString(mModule.getNewsTime()));

    }

    @Override
    public int getItemCount() {
        return mNewsModules.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        SimpleDraweeView newsPic;
        TextView newsContent;
        TextView newsTime;

        public ViewHolder(View itemView) {
            super(itemView);
            newsPic = (SimpleDraweeView) itemView.findViewById(R.id.news_pic);
            newsContent = (TextView) itemView.findViewById(R.id.news_describe);
            newsTime = (TextView) itemView.findViewById(R.id.news_time);

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

    public List<NewsModule> getDatas(){
        return mNewsModules;
    }

    public void clearData(){
        int length = mNewsModules.size();
        mNewsModules.clear();
        notifyItemRangeRemoved(0,length);
    }

    public void addData(List<NewsModule> mNewsModuleList){
        this.addData(0, mNewsModuleList);
    }

    public void addData(int position, List<NewsModule> mNewsModuleList){
        if (mNewsModuleList != null && mNewsModuleList.size() > 0){
            mNewsModules.addAll(mNewsModuleList);
            notifyItemRangeChanged(position, mNewsModules.size());
        }
    }

}
