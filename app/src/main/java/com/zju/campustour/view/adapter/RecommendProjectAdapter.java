package com.zju.campustour.view.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import com.parse.Parse;
import com.zju.campustour.R;
import com.zju.campustour.model.bean.ProjectItemInfo;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.util.DbUtils;

import java.util.List;

/**
 * Created by HeyLink on 2017/4/24.
 */

public class RecommendProjectAdapter extends RecyclerView.Adapter<RecommendProjectAdapter.ViewHolder> {


    private List<ProjectItemInfo> mProjectItemInfos;
    private onProjectItemClickListener mListener;
    private int state = Constants.FULL_VIEW;

    public RecommendProjectAdapter(List<ProjectItemInfo> mProjectItemInfos, int mState) {
        this.mProjectItemInfos = mProjectItemInfos;
        this.state = mState;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        SimpleDraweeView providerImgView;
        TextView projectTitleView;
        TextView projectTimeView;
        TextView projectAcceptNumView;
        TextView projectDescView;
        SimpleDraweeView projectImgView;
        TextView projectFavoritesNumView;
        TextView projectPriceView;
        TextView projectEnrollNumView;

        public ViewHolder(View itemView) {
            super(itemView);

            providerImgView = (SimpleDraweeView) itemView.findViewById(R.id.cardview_project_user_img);
            projectTitleView = (TextView) itemView.findViewById(R.id.cardview_project_title);
            projectTimeView = (TextView) itemView.findViewById(R.id.cardview_project_start_time);
            projectAcceptNumView = (TextView) itemView.findViewById(R.id.cardview_project_accept_num);
            projectDescView = (TextView) itemView.findViewById(R.id.cardview_project_description);
            projectImgView = (SimpleDraweeView) itemView.findViewById(R.id.cardview_project_img);
            projectFavoritesNumView = (TextView) itemView.findViewById(R.id.cardview_project_favor_num);
            projectPriceView = (TextView) itemView.findViewById(R.id.cardview_project_price);
            projectEnrollNumView = (TextView) itemView.findViewById(R.id.cardview_project_join);
            if (state == Constants.PART_VIEW){
                providerImgView.setVisibility(View.GONE);
                projectDescView.setMaxLines(100);
            }


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        //return the student's id, so we can get the exact student info
                        mListener.onClick(v, getLayoutPosition(), mProjectItemInfos.get(getLayoutPosition()).getProviderId());
                    }
                }
            });

        }
    }


    @Override
    public RecommendProjectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        View mView = mInflater.inflate(R.layout.template_project_cardview, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(RecommendProjectAdapter.ViewHolder holder, int position) {

        ProjectItemInfo mItemInfo = mProjectItemInfos.get(position);
        String url = mItemInfo.getProviderImg();
        if (url == null)
            url = "http://image.bitauto.com/dealer/news/100057188/145a7c3a-6230-482b-b050-77a40c1571fd.jpg";
        Uri uri = Uri.parse(url);
        holder.providerImgView.setImageURI(uri);
        holder.projectTitleView.setText(mItemInfo.getProjectTitle());
        holder.projectTimeView.setText(mItemInfo.getProjectTime());
        holder.projectAcceptNumView.setText("上限"+mItemInfo.getProjectAcceptNum()+"人");
        holder.projectDescView.setText(mItemInfo.getProjectInfo());

        String url_1 = mItemInfo.getProjectImg();
        if (url_1 == null)
            url_1 = "http://www.huedu.net/UploadFiles_2233/201505/2015050508445878.jpg";

        DbUtils.setImg(holder.projectImgView,url_1,200);

        holder.projectFavoritesNumView.setText(mItemInfo.getFavoritesNum()+"人收藏");
        holder.projectPriceView.setText(mItemInfo.getProjectPrice()+"元/人");
        holder.projectEnrollNumView.setText(mItemInfo.getProjectEnrollNum()+"人报名");
    }

    @Override
    public int getItemCount() {
        return mProjectItemInfos.size();
    }

    public interface onProjectItemClickListener{
        void onClick(View v, int position, String providerId);
    }

    public void setOnProjectItemClickListener(onProjectItemClickListener listener){
        this.mListener = listener;
    }

    public void clearData(){
        int length = mProjectItemInfos.size();
        mProjectItemInfos.clear();
        notifyItemRangeRemoved(0,length);
    }

    public void addData(List<ProjectItemInfo> serviceInfos){
        this.addData(0, serviceInfos);
    }


    public void addData(int position, List<ProjectItemInfo> serviceInfos){
        if (serviceInfos != null && serviceInfos.size() > 0){
            mProjectItemInfos.addAll(serviceInfos);
            notifyItemRangeChanged(position, mProjectItemInfos.size());
        }
    }

    public List<ProjectItemInfo> getDatas(){
        return mProjectItemInfos;
    }

}
