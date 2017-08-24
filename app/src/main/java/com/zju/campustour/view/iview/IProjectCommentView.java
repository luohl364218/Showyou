package com.zju.campustour.view.iview;

import com.zju.campustour.model.bean.Comment;

import java.util.List;

/**
 * Created by HeyLink on 2017/5/20.
 */

public interface IProjectCommentView extends ICommentView {

    public void onCommentSuccess();

    public void onQueryCommentsDone(List<Comment> mComments);
}
