package com.zju.campustour.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zju.campustour.R;
import com.zju.campustour.model.bean.LabelInfo;

import java.util.List;

/**
 * Created by HeyLink on 2017/8/23.
 */

public class LabelInfoAdapter extends RecyclerView.Adapter<LabelInfoAdapter.ViewHolder>{


    private List<LabelInfo> mLabelInfos;
    private onItemClickListener mListener;

    public LabelInfoAdapter(List<LabelInfo> mLabelInfos) {
        this.mLabelInfos = mLabelInfos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        View mView = mInflater.inflate(R.layout.template_label_cardview, parent, false);

        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LabelInfo mLabelInfo = mLabelInfos.get(position);

        holder.labelContent.setText(mLabelInfo.getContent());
        holder.joinNum.setText(""+mLabelInfo.getJoinNum());

    }

    @Override
    public int getItemCount() {
        return mLabelInfos.size();
    }

    public interface onItemClickListener{
        void onClick(View v,int position,LabelInfo label);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        mListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView labelContent;
        TextView joinNum;

        public ViewHolder(View itemView) {
            super(itemView);
            labelContent = (TextView) itemView.findViewById(R.id.label_content);
            joinNum = (TextView) itemView.findViewById(R.id.join_num);

            //9.设置监听事件
            itemView.setOnClickListener(a->{
                if (mListener != null){
                    mListener.onClick(a, getLayoutPosition(), mLabelInfos.get(getLayoutPosition()));
                }
            });
        }
    }

    public void clearData(){
        int length = mLabelInfos.size();
        mLabelInfos.clear();
        notifyItemRangeRemoved(0,length);
    }

    public void addData(List<LabelInfo> mLabelInfoList){
        this.addData(0, mLabelInfoList);
    }

    public void addData(int position,List<LabelInfo> mLabelInfoList){
        if (mLabelInfoList != null && mLabelInfoList.size() > 0){
            mLabelInfos.addAll(mLabelInfoList);
            notifyItemRangeChanged(position, mLabelInfos.size());
        }
    }


}
