package com.zju.campustour.presenter.ipresenter;

import com.zju.campustour.model.bean.Comment;

/**
 * Created by HeyLink on 2017/5/20.
 */

public interface IProjectCommentPresenter extends ICommentPresenter {

    public void postComment(Comment comment);

    public void queryComment(String projectId);

}
