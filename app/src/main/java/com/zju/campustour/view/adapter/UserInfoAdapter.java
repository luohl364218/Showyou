package com.zju.campustour.view.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.bean.User;
import com.zju.campustour.model.util.DbUtils;


import java.util.List;

import static com.zju.campustour.model.common.Constants.GRADE_HIGH_SCHOOL;

/**
 * Created by HeyLink on 2017/4/24.
 */

public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.ViewHolder> {


    private List<User> mProviderUserItemInfos;
    private onCardViewItemClickListener mListener;
    private boolean isManager = false;

    public void setBooked(boolean mBooked) {
        isBooked = mBooked;
    }

    private boolean isBooked = false;

    public UserInfoAdapter(List<User> mProviderUserItemInfos) {
        this.mProviderUserItemInfos = mProviderUserItemInfos;
    }

    public UserInfoAdapter(List<User> mProviderUserItemInfos, boolean isProjectManager) {
        this.mProviderUserItemInfos = mProviderUserItemInfos;
        isManager = isProjectManager;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView studentIdView;
        ImageView studentIsVerified;
        SimpleDraweeView studentImgView;
        TextView studentNameView;
        TextView shortDescriptionView;
        TextView studentCollegeView;
        TextView studentMajorView;
        TextView studentGradeView;
        TextView studentTypeView;
        ImageView studentTypeImgView;
        ImageView studentMajorImgView;

        public ViewHolder(View itemView) {
            super(itemView);

            studentIdView = (TextView) itemView.findViewById(R.id.cardview_user_id);
            studentIsVerified = (ImageView) itemView.findViewById(R.id.user_is_verified);
            studentImgView = (SimpleDraweeView) itemView.findViewById(R.id.cardview_user_image);
            studentNameView = (TextView) itemView.findViewById(R.id.cardview_user_name);
            shortDescriptionView = (TextView) itemView.findViewById(R.id.cardview_describe);
            studentCollegeView = (TextView) itemView.findViewById(R.id.cardview_user_college);
            studentMajorView = (TextView)itemView.findViewById(R.id.cardview_user_major);
            studentGradeView = (TextView) itemView.findViewById(R.id.cardview_user_grade);
            studentTypeView = (TextView) itemView.findViewById(R.id.cardview_user_type_txt);
            studentTypeImgView = (ImageView) itemView.findViewById(R.id.cardview_user_type_icon);
            studentMajorImgView = (ImageView) itemView.findViewById(R.id.cardview_user_major_icon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        //return the student's id, so we can get the exact student info
                        try{
                            mListener.onClick(v, getLayoutPosition(), mProviderUserItemInfos.get(getLayoutPosition()));
                        }catch (Exception e){
                            e.printStackTrace();
                            return;
                        }

                    }
                }
            });

        }
    }


    @Override
    public UserInfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        View mView = mInflater.inflate(R.layout.template_info_cardview, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(UserInfoAdapter.ViewHolder holder, int position) {

        User mItemInfo = mProviderUserItemInfos.get(position);
        holder.studentIdView.setText(mItemInfo.getObjectId());
        String url = mItemInfo.getImgUrl();
        boolean isFromNewServer = url.startsWith(Constants.URL_PREFIX_ALIYUN);
        //作过滤，如果是新服务器上的头像，则加载小图片
        if (isFromNewServer) {
            url += "!header";
            Uri mUri = Uri.parse(url);
            holder.studentImgView.setImageURI(mUri);
        }
        else {
            //如果是旧服务器上的，保持原样
            if (url == null)
                url = "http://image.bitauto.com/dealer/news/100057188/145a7c3a-6230-482b-b050-77a40c1571fd.jpg";
            DbUtils.setImg(holder.studentImgView,url,150,150);
        }

        holder.studentNameView.setText(mItemInfo.getRealName());

        //如果用户已经认证，显示已认证标志
        if (mItemInfo.isVerified())
            holder.studentIsVerified.setVisibility(View.VISIBLE);
        else
            holder.studentIsVerified.setVisibility(View.GONE);

        if (isManager && isBooked){
            String phoneNum = mItemInfo.getPhoneNum();
            if (phoneNum == null){
                phoneNum = mItemInfo.getEmailAddr();
                if (phoneNum == null)
                    phoneNum = mItemInfo.getUserName();
            }

            holder.shortDescriptionView.setText("联系方式："+phoneNum);
        }
        else
            holder.shortDescriptionView.setText(mItemInfo.getShortDescription());

        if (mItemInfo.getGradeId() > GRADE_HIGH_SCHOOL){
            String major = mItemInfo.getMajor();
            if (TextUtils.isEmpty(major))
                major = "未填写";
            holder.studentMajorImgView.setVisibility(View.VISIBLE);
            holder.studentMajorView.setVisibility(View.VISIBLE);
            holder.studentMajorView.setText(major);
        }
        else {
            holder.studentMajorImgView.setVisibility(View.INVISIBLE);
            holder.studentMajorView.setVisibility(View.INVISIBLE);
        }

        holder.studentCollegeView.setText(mItemInfo.getSchool());

        holder.studentGradeView.setText(mItemInfo.getGrade());
        holder.studentTypeView.setText(mItemInfo.getUserType().getName());
        if (mItemInfo.getUserType().getUserTypeId() == 1)
            holder.studentTypeImgView.setImageResource(R.drawable.icon_major_user);
        else
            holder.studentTypeImgView.setImageResource(R.drawable.icon_common_user);
    }

    @Override
    public int getItemCount() {
        return mProviderUserItemInfos.size();
    }

    public interface onCardViewItemClickListener{
        void onClick(View v,int position, User provider);
    }

    public void setOnCardViewItemClickListener(onCardViewItemClickListener listener){
        this.mListener = listener;
    }

    public void clearData(){
        int length = mProviderUserItemInfos.size();
        mProviderUserItemInfos.clear();
        notifyItemRangeRemoved(0,length);
    }

    public void addData(List<User> serviceInfos){
        this.addData(0, serviceInfos);
    }


    public void addData(int position, List<User> serviceInfos){
        if (serviceInfos != null && serviceInfos.size() > 0){
            mProviderUserItemInfos.addAll(serviceInfos);
            notifyItemRangeChanged(position, mProviderUserItemInfos.size());
        }
    }

    public List<User> getDatas(){
        return mProviderUserItemInfos;
    }

    public void refreshSelectedData(int position, User mUser){
        mProviderUserItemInfos.get(position).update(mUser);
        notifyItemChanged(position);
    }

}
