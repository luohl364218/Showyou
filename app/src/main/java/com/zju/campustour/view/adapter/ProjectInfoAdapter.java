package com.zju.campustour.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.database.models.Project;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.model.util.DbUtils;
import com.zju.campustour.presenter.protocal.enumerate.IdentityType;

import java.text.SimpleDateFormat;
import java.util.List;

import static com.zju.campustour.model.common.Constants.COLLECT_VIEW;
import static com.zju.campustour.model.common.Constants.PART_VIEW;

/**
 * Created by HeyLink on 2017/4/24.
 */

public class ProjectInfoAdapter extends RecyclerView.Adapter<ProjectInfoAdapter.ViewHolder>{

    private List<Project> mProjectItemInfos;
    private onProjectItemClickListener mListener;
    private onProjectProviderClickListener mProviderClickListener;
    private int state = Constants.FULL_VIEW;
    private Context mContext;


    public ProjectInfoAdapter(List<Project> mProjectItemInfos, int mState, Context context) {
        this.mProjectItemInfos = mProjectItemInfos;
        this.state = mState;
        this.mContext = context;


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
        LinearLayout projectCardViewBody;
        LinearLayout projectCardViewTail;
        ImageView projectOfflineIv;
        ImageView projectOnlineIv;

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
            projectCardViewBody = (LinearLayout) itemView.findViewById(R.id.project_cardview_body);
            projectCardViewTail = (LinearLayout) itemView.findViewById(R.id.project_cardview_tail);
            projectOfflineIv = (ImageView) itemView.findViewById(R.id.offline_activity_iv);
            projectOnlineIv = (ImageView) itemView.findViewById(R.id.online_activity_iv);
            if (state == Constants.PART_VIEW){
                providerImgView.setVisibility(View.GONE);
                projectCardViewTail.setVisibility(View.GONE);
            }
            else if (state == COLLECT_VIEW){
                projectImgView.setVisibility(View.GONE);
                projectCardViewTail.setVisibility(View.GONE);
            }




            projectCardViewBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        //return the student's id, so we can get the exact student info
                        mListener.onClick(v, getLayoutPosition(), mProjectItemInfos.get(getLayoutPosition()));
                    }
                }
            });

            providerImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mProviderClickListener != null){
                        //return the student's id, so we can get the exact student info
                        mProviderClickListener.onClick(v, getLayoutPosition(), mProjectItemInfos.get(getLayoutPosition()).getProvider());
                    }
                }
            });

        }
    }


    @Override
    public ProjectInfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        View mView = mInflater.inflate(R.layout.template_project_cardview, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(ProjectInfoAdapter.ViewHolder holder, int position) {

        Project mItemInfo = mProjectItemInfos.get(position);
        if (state != PART_VIEW){
            String url = mItemInfo.getProvider().getImgUrl();
            if (url == null)
                url = "http://image.bitauto.com/dealer/news/100057188/145a7c3a-6230-482b-b050-77a40c1571fd.jpg";
            //Uri uri = Uri.parse(url);
            //holder.providerImgView.setImageURI(uri);
            DbUtils.setImg(holder.providerImgView,url,150,150);
        }

        holder.projectTitleView.setText(mItemInfo.getTitle());
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        holder.projectTimeView.setText(sdf.format(mItemInfo.getStartTime()));
        holder.projectAcceptNumView.setText("上限"+mItemInfo.getAcceptNum()+"人");
        holder.projectDescView.setText(mItemInfo.getDescription());

        String url_1 = mItemInfo.getImgUrl();
        if (url_1 == null)
            url_1 = "http://www.huedu.net/UploadFiles_2233/201505/2015050508445878.jpg";
        // DbUtils.setImg(holder.projectImgView,url_1,250,100);
        if (state != COLLECT_VIEW){
            Uri mUri = Uri.parse(url_1);
            holder.projectImgView.setImageURI(mUri);
        }


        if(!mItemInfo.isOffline()){
            holder.projectOfflineIv.setVisibility(View.GONE);
            holder.projectOnlineIv.setVisibility(View.VISIBLE);
        }
        else {
            holder.projectOnlineIv.setVisibility(View.GONE);
            holder.projectOfflineIv.setVisibility(View.VISIBLE);
        }


        holder.projectFavoritesNumView.setText(mItemInfo.getCollectorNum()+"人收藏");
        holder.projectPriceView.setText(mItemInfo.getPrice()+"元/人");
        holder.projectEnrollNumView.setText(mItemInfo.getBookedNum()+"人报名");
    }

    @Override
    public int getItemCount() {
        return mProjectItemInfos.size();
    }

    public interface onProjectItemClickListener{
        void onClick(View v, int position, Project project);
    }

    public interface onProjectProviderClickListener{
        void onClick(View v, int position, User user);
    }

    public void setOnProjectItemClickListener(onProjectItemClickListener listener){
        this.mListener = listener;
    }

    public void setOnProjectProviderClickListener(onProjectProviderClickListener listener){
        this.mProviderClickListener = listener;
    }

    public void clearData(){
        int length = mProjectItemInfos.size();
        mProjectItemInfos.clear();
        notifyItemRangeRemoved(0,length);
    }

    public void addData(List<Project> serviceInfos){
        this.addData(0, serviceInfos);
    }


    public void addData(int position, List<Project> serviceInfos){
        if (serviceInfos != null && serviceInfos.size() > 0){
            mProjectItemInfos.addAll(serviceInfos);
            notifyItemRangeChanged(position, mProjectItemInfos.size());
        }
    }

    public List<Project> getDatas(){
        return mProjectItemInfos;
    }

    public void refreshSelectedData(int position, Project mProject){
        mProjectItemInfos.get(position).update(mProject);
        notifyItemChanged(position);
    }

}
