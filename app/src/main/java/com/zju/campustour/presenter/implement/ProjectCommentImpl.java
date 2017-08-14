package com.zju.campustour.presenter.implement;

import android.content.Context;
import android.graphics.Color;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.zju.campustour.model.database.models.Comment;
import com.zju.campustour.model.util.DbUtils;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.presenter.ipresenter.IProjectCommentPresenter;
import com.zju.campustour.view.iview.ICommentView;
import com.zju.campustour.view.iview.IProjectCommentView;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by HeyLink on 2017/5/20.
 */

public class ProjectCommentImpl implements IProjectCommentPresenter {

    private Context mContext;
    private ICommentView mICommentView;
    private List<Comment> mComments;

    public ProjectCommentImpl(ICommentView commentPresenter, Context context) {
        mICommentView = commentPresenter;
        mContext = context;
    }

    @Override
    public void postComment(Comment comment) {
        if (!NetworkUtil.isNetworkAvailable(mContext))
            return;
        SweetAlertDialog pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("评价提交中");
        pDialog.setCancelable(false);
        pDialog.show();

        ParseObject mComments = new ParseObject("ProjectComments");
        mComments.put("projectId", comment.getProjectId());
        mComments.put("userId",comment.getCommentUserId());
        mComments.put("score", comment.getCommentScore());
        mComments.put("content",comment.getCommentContent());
        mComments.put("commentTime",comment.getCommentTime());

        mComments.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    pDialog.setTitleText("评价提交成功").setConfirmText("确定");
                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                            IProjectCommentView mIProjectCommentView = (IProjectCommentView)mICommentView;
                            mIProjectCommentView.onCommentSuccess();
                        }
                    });

                }
                else {
                    pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    pDialog.setTitleText("评价提交失败").setConfirmText("确定");
                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                }
            }
        });

    }

    @Override
    public void queryComment(String projectId) {
        if (!NetworkUtil.isNetworkAvailable(mContext) || projectId == null)
            return;

        ParseQuery query = ParseQuery.getQuery("ProjectComments");
        query.whereEqualTo("projectId",projectId);

        IProjectCommentView mIProjectCommentView = (IProjectCommentView)mICommentView;
        mComments = new ArrayList<>();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() != 0){
                    for (ParseObject mObject:objects){
                        Comment mComment = DbUtils.getComment(mObject);
                        mComments.add(mComment);
                    }
                    mIProjectCommentView.onQueryCommentsDone(mComments);
                }
                else
                    mIProjectCommentView.onQueryCommentsDone(mComments);
            }
        });

    }
}
