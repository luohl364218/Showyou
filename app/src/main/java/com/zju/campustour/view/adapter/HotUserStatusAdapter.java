package com.zju.campustour.view.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zju.campustour.R;
import com.zju.campustour.model.bean.StatusInfoModel;

import java.util.List;

/**
 * Created by HeyLink on 2017/8/15.
 */

public class HotUserStatusAdapter extends RecyclerView.Adapter<HotUserStatusAdapter.ViewHolder> {


    private List<StatusInfoModel> mStatusInfoModelList;
    private StatusInfoItemListener mListener;

    public HotUserStatusAdapter(List<StatusInfoModel> mStatusInfoModelList) {
        this.mStatusInfoModelList = mStatusInfoModelList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        View mView = mInflater.inflate(R.layout.template_status_grid_show_cardview,parent,false);

        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        StatusInfoModel mStatusInfoModel = mStatusInfoModelList.get(position);
        String imgUrl = mStatusInfoModel.getImgUrl();
        if (imgUrl != null){
            Uri mUri = Uri.parse(imgUrl);
            holder.statusImg.setImageURI(mUri);
        }
        else {
            holder.statusImg.setVisibility(View.GONE);
        }

        holder.statusContent.setText(mStatusInfoModel.getContent());
        holder.userName.setText(mStatusInfoModel.getUser().getRealName());
        holder.favourNum.setText(""+mStatusInfoModel.getFavourCount());
        Uri mUri = Uri.parse(mStatusInfoModel.getUser().getImgUrl());
        holder.userImg.setImageURI(mUri);

    }

    @Override
    public int getItemCount() {
        return mStatusInfoModelList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        SimpleDraweeView statusImg;
        TextView statusContent;
        SimpleDraweeView userImg;
        TextView userName;
        TextView favourNum;

        public ViewHolder(View itemView) {
            super(itemView);

            statusImg = (SimpleDraweeView) itemView.findViewById(R.id.status_img);
            statusContent = (TextView) itemView.findViewById(R.id.status_content);
            userImg = (SimpleDraweeView) itemView.findViewById(R.id.user_img);
            userName = (TextView)itemView.findViewById(R.id.status_user_name);
            favourNum = (TextView)itemView.findViewById(R.id.favour_num);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.onClick(v, getLayoutPosition(), mStatusInfoModelList.get(getLayoutPosition()));
                    }
                }
            });
        }
    }

    public interface StatusInfoItemListener{
        void onClick(View v,int position,StatusInfoModel status);
    }

    public void setOnStatusInfoItemClickListener(StatusInfoItemListener mItemListener){
        this.mListener = mItemListener;
    }

    public void clearData(){
        int length = mStatusInfoModelList.size();
        mStatusInfoModelList.clear();
        notifyItemRangeRemoved(0,length);
    }

    public void addData(List<StatusInfoModel> mInfoModelList){
        this.addData(0, mInfoModelList);
    }

    public void addData(int position, List<StatusInfoModel> mStatusInfoModels){
        if (mStatusInfoModels != null && mStatusInfoModels.size() > 0){
            mStatusInfoModelList.addAll(mStatusInfoModels);
            notifyItemRangeChanged(position, mStatusInfoModelList.size());
        }
    }
}
