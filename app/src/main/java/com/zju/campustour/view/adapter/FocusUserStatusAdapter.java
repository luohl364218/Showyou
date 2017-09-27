package com.zju.campustour.view.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.bean.StatusInfoModel;
import com.zju.campustour.model.chatting.utils.IdHelper;
import com.zju.campustour.model.chatting.utils.TimeFormat;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.presenter.protocal.enumerate.IdentityType;
import com.zju.campustour.view.activity.FriendInfoActivity;
import com.zju.campustour.view.activity.IdentityConfirmActivity;
import com.zju.campustour.view.activity.SearchFriendDetailActivity;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import me.yifeiyuan.library.PeriscopeLayout;

/**
 * Created by HeyLink on 2017/8/15.
 */

public class FocusUserStatusAdapter extends RecyclerView.Adapter<FocusUserStatusAdapter.ViewHolder> {


    private List<StatusInfoModel> mStatusInfoModelList;
    private StatusInfoItemListener mListener;
    private Context mContext;

    public FocusUserStatusAdapter(Context context,List<StatusInfoModel> mStatusInfoModelList) {
        this.mContext = context;
        this.mStatusInfoModelList = mStatusInfoModelList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        View mView = mInflater.inflate(R.layout.template_status_liner_show_cardview,parent,false);

        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        StatusInfoModel mStatusInfoModel = mStatusInfoModelList.get(position);
        String imgUrl = mStatusInfoModel.getImgUrl();
        if (!TextUtils.isEmpty(imgUrl)){
            //新服务器上的图片
            //imgUrl += "!lgheader";
            holder.statusImg.setVisibility(View.VISIBLE);
            Uri mUri = Uri.parse(imgUrl);
            holder.statusImg.setImageURI(mUri);
        }
        else {
            holder.statusImg.setVisibility(View.GONE);
        }

        holder.statusContent.setText(mStatusInfoModel.getContent());


        //用户头像
        String headImgUrl = mStatusInfoModel.getUser().getImgUrl();
        boolean isFromNewServer = headImgUrl.startsWith(Constants.URL_PREFIX_ALIYUN);
        //作过滤，如果是新服务器上的头像，则加载小图片
        if (isFromNewServer)
            headImgUrl += "!header";

        Uri mUri = Uri.parse(headImgUrl);
        holder.userImg.setImageURI(mUri);
        //用户名 学校
        holder.userName.setText(mStatusInfoModel.getUser().getRealName()
                +" "+ mStatusInfoModel.getUser().getSchool());
        //发布地点
        if (mStatusInfoModel.isHidePosition() || mStatusInfoModel.getCity() == null){
            holder.statusLocation.setVisibility(View.GONE);
        }
        else {
            holder.statusLocation.setVisibility(View.VISIBLE);

            holder.statusLocation.setText(mStatusInfoModel.getCity());
        }

        //发布时间
        TimeFormat timeFormat = new TimeFormat(mContext, mStatusInfoModel.getCreatedAt().getTime());
        holder.statusDate.setText(timeFormat.getTime());

        int favourCount = mStatusInfoModel.getFavourCount();
        int commentCount = mStatusInfoModel.getCommentCount();

        //// TODO: 2017/9/20 暂且把评论功能关闭，直接打开聊天功能
       /* if (favourCount ==0 && commentCount ==0){
            holder.favorAndCommentPart.setVisibility(View.GONE);
        }
        else if (favourCount == 0){
            holder.favorAndCommentPart.setVisibility(View.VISIBLE);
            holder.commentPart.setVisibility(View.VISIBLE);
            holder.favorPart.setVisibility(View.GONE);
        }
        else if (commentCount == 0){
            holder.favorAndCommentPart.setVisibility(View.VISIBLE);
            holder.commentPart.setVisibility(View.GONE);
            holder.favorPart.setVisibility(View.VISIBLE);
        }
        else{
            holder.favorAndCommentPart.setVisibility(View.VISIBLE);
            holder.commentPart.setVisibility(View.VISIBLE);
            holder.favorPart.setVisibility(View.VISIBLE);
        }*/

        if (favourCount ==0 ){
            holder.favorAndCommentPart.setVisibility(View.GONE);
        }
        else{
            holder.favorAndCommentPart.setVisibility(View.VISIBLE);
            holder.commentPart.setVisibility(View.GONE);
            holder.favorPart.setVisibility(View.VISIBLE);
        }

        holder.favourNum.setText(""+favourCount);

        holder.commentNumTv.setText(""+commentCount);

        if (mStatusInfoModel.isFavorited()){
            holder.favorImg.setImageResource(R.mipmap.icon_favor_blue);
        }
        else {
            holder.favorImg.setImageResource(R.mipmap.icon_favor_gray);
        }

        holder.favorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null){
                    for (int i = 0; i <= 2; i++)
                        holder.periscopeLayout.addHeart();

                    if (!mStatusInfoModel.isFavorited()) {
                        int count = favourCount + 1;
                        mStatusInfoModel.setFavourCount(count);
                        holder.favorAndCommentPart.setVisibility(View.VISIBLE);
                        holder.favorPart.setVisibility(View.VISIBLE);
                        holder.favourNum.setText("" + count);
                        holder.favorImg.setImageResource(R.mipmap.icon_favor_blue);
                        mListener.onClick(v, position, mStatusInfoModel);
                        mStatusInfoModel.setFavorited(true);
                    }
                    else {
                        mListener.onClick(v, position, mStatusInfoModel);
                        mStatusInfoModel.setFavorited(false);
                    }

                }
            }
        });


        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener !=null)
                    mListener.onClick(v, position, mStatusInfoModel);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mStatusInfoModelList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{


        SimpleDraweeView userImg;
        TextView userName;
        TextView statusLocation;
        TextView statusDate;
        SimpleDraweeView statusImg;
        TextView statusContent;
        ImageView favorImg;
        LinearLayout favorBtn;
        LinearLayout commentBtn;
        LinearLayout shareBtn;
        LinearLayout reportBtn;
        LinearLayout favorAndCommentPart;
        LinearLayout favorPart;
        LinearLayout commentPart;
        TextView commentNumTv;
        TextView favourNum;
        PeriscopeLayout periscopeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            periscopeLayout = (PeriscopeLayout) itemView.findViewById(R.id.periscope);
            statusImg = (SimpleDraweeView) itemView.findViewById(R.id.status_img);
            statusContent = (TextView) itemView.findViewById(R.id.status_content);
            userImg = (SimpleDraweeView) itemView.findViewById(R.id.user_img);
            userName = (TextView)itemView.findViewById(R.id.status_user_name);
            favourNum = (TextView)itemView.findViewById(R.id.favour_num);
            statusLocation = (TextView) itemView.findViewById(R.id.status_location);
            statusDate = (TextView) itemView.findViewById(R.id.status_date);
            favorImg = (ImageView) itemView.findViewById(R.id.favour_img);
            favorBtn = (LinearLayout) itemView.findViewById(R.id.favor_btn);
            commentBtn = (LinearLayout) itemView.findViewById(R.id.comment_btn);
            shareBtn = (LinearLayout) itemView.findViewById(R.id.share_btn);
            reportBtn = (LinearLayout) itemView.findViewById(R.id.report_btn);
            favorAndCommentPart = (LinearLayout) itemView.findViewById(R.id.favor_and_comment);
            favorPart = (LinearLayout) itemView.findViewById(R.id.favor_part);
            commentPart = (LinearLayout) itemView.findViewById(R.id.comment_part);
            commentNumTv = (TextView) itemView.findViewById(R.id.comment_num_tv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.onClick(v, getLayoutPosition(), mStatusInfoModelList.get(getLayoutPosition()));
                    }
                }
            });

            statusImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.onClick(v, getLayoutPosition(), mStatusInfoModelList.get(getLayoutPosition()));
                    }
                }
            });

            userImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.onClick(v, getLayoutPosition(), mStatusInfoModelList.get(getLayoutPosition()));
                    }
                }
            });

            favorBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.onClick(v, getLayoutPosition(), mStatusInfoModelList.get(getLayoutPosition()));
                    }
                }
            });


            commentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.onClick(v, getLayoutPosition(), mStatusInfoModelList.get(getLayoutPosition()));
                    }
                }
            });
            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.onClick(v, getLayoutPosition(), mStatusInfoModelList.get(getLayoutPosition()));
                    }
                }
            });

            reportBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.onClick(v, getLayoutPosition(), mStatusInfoModelList.get(getLayoutPosition()));
                    }
                }
            });

            favorPart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.onClick(v, getLayoutPosition(), mStatusInfoModelList.get(getLayoutPosition()));
                    }
                }
            });

            commentPart.setOnClickListener(new View.OnClickListener() {
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
        void onClick(View v, int position, StatusInfoModel status);
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
