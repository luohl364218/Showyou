package com.zju.campustour.view.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zju.campustour.R;
import com.zju.campustour.model.bean.ServiceItemInfo;
import com.zju.campustour.model.util.DbUtils;

import java.util.List;

/**
 * Created by HeyLink on 2017/4/24.
 */

public class ServiceItemInfoAdapter extends RecyclerView.Adapter<ServiceItemInfoAdapter.ViewHolder> {


    private List<ServiceItemInfo> mServiceItemInfos;
    private onCardViewItemClickListener mListener;

    public ServiceItemInfoAdapter(List<ServiceItemInfo> mServiceItemInfos) {
        this.mServiceItemInfos = mServiceItemInfos;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView studentIdView;
        SimpleDraweeView studentImgView;
        TextView studentNameView;
        TextView shortDescriptionView;
        TextView studentCollegeView;
        TextView studentMajorView;
        TextView studentGradeView;
        TextView studentFansNumView;

        public ViewHolder(View itemView) {
            super(itemView);

            studentIdView = (TextView) itemView.findViewById(R.id.cardview_user_id);
            studentImgView = (SimpleDraweeView) itemView.findViewById(R.id.cardview_user_image);
            studentNameView = (TextView) itemView.findViewById(R.id.cardview_user_name);
            shortDescriptionView = (TextView) itemView.findViewById(R.id.cardview_describe);
            studentCollegeView = (TextView) itemView.findViewById(R.id.cardview_user_college);
            studentMajorView = (TextView)itemView.findViewById(R.id.cardview_user_major);
            studentGradeView = (TextView) itemView.findViewById(R.id.cardview_user_grade);
            studentFansNumView = (TextView) itemView.findViewById(R.id.cardview_user_fans_num);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        //return the student's id, so we can get the exact student info
                        mListener.onClick(v, getLayoutPosition(), mServiceItemInfos.get(getLayoutPosition()).getStudentId());
                    }
                }
            });

        }
    }


    @Override
    public ServiceItemInfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        View mView = mInflater.inflate(R.layout.template_info_cardview, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(ServiceItemInfoAdapter.ViewHolder holder, int position) {

        ServiceItemInfo mItemInfo = mServiceItemInfos.get(position);
        holder.studentIdView.setText(mItemInfo.getStudentId());
        String url = mItemInfo.getStudentImg();
        if (url == null)
            url = "http://image.bitauto.com/dealer/news/100057188/145a7c3a-6230-482b-b050-77a40c1571fd.jpg";
        DbUtils.setImg(holder.studentImgView,url,70);

        holder.studentNameView.setText(mItemInfo.getStudentName());
        holder.shortDescriptionView.setText(mItemInfo.getShortDescription());
        holder.studentCollegeView.setText(mItemInfo.getStudentCollege());
        holder.studentMajorView.setText(mItemInfo.getStudentMajor());
        holder.studentGradeView.setText(mItemInfo.getStudentGrade());
        holder.studentFansNumView.setText(mItemInfo.getFansNum() + "人关注");
    }

    @Override
    public int getItemCount() {
        return mServiceItemInfos.size();
    }

    interface onCardViewItemClickListener{
        void onClick(View v,int position, String studentId);
    }

    public void setOnCardViewItemClickListener(onCardViewItemClickListener listener){
        this.mListener = listener;
    }

    public void clearData(){
        int length = mServiceItemInfos.size();
        mServiceItemInfos.clear();
        notifyItemRangeRemoved(0,length);
    }

    public void addData(List<ServiceItemInfo> serviceInfos){
        this.addData(0, serviceInfos);
    }


    public void addData(int position, List<ServiceItemInfo> serviceInfos){
        if (serviceInfos != null && serviceInfos.size() > 0){
            mServiceItemInfos.addAll(serviceInfos);
            notifyItemRangeChanged(position, mServiceItemInfos.size());
        }
    }

    public List<ServiceItemInfo> getDatas(){
        return mServiceItemInfos;
    }

}
