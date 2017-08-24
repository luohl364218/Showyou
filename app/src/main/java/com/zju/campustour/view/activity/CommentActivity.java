package com.zju.campustour.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.bean.Comment;
import com.zju.campustour.model.bean.Project;
import com.zju.campustour.presenter.implement.ProjectCommentImpl;
import com.zju.campustour.presenter.listener.MyTextWatch;
import com.zju.campustour.presenter.protocal.event.CommentSuccessEvent;
import com.zju.campustour.view.iview.IProjectCommentView;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommentActivity extends BaseActivity implements IProjectCommentView {

    @BindView(R.id.project_rate)
    RatingBar mRatingBar;
    @BindView(R.id.project_score)
    TextView score;
    @BindView(R.id.project_comments)
    EditText comments;
    @BindView(R.id.btn_post)
    Button mButton;
    @BindView(R.id.comment_toolbar)
    Toolbar mToolbar;

    int rateScore = 10;
    boolean isCommentNotNull = false;
    ProjectCommentImpl mProjectComment;
    Project currentProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);

        Intent mIntent = getIntent();
        currentProject = (Project) mIntent.getSerializableExtra("project");
        if (currentProject == null || currentProject.getId() == null)
            return;

        mRatingBar.setNumStars(10);
        mRatingBar.setMax(10);
        mRatingBar.setStepSize(1);
        mRatingBar.setRating(10);
        score.setText("10.0分");

        mProjectComment = new ProjectCommentImpl(this,this);

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                score.setText(""+rating+"分");
                rateScore = (int) rating;
            }
        });

        //让按钮随着输入内容有效而使能
        comments.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isCommentNotNull = !TextUtils.isEmpty(s.toString());
                mButton.setEnabled(isCommentNotNull);
            }
        });


        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }



    @OnClick(R.id.btn_post)
    public void postComment(){
        String content = comments.getText().toString().trim();
        if (TextUtils.isEmpty(content)){
            showToast("评价内容不能为空");
            return;
        }

        Comment mComment = new Comment();
        mComment.setProjectId(currentProject.getId());
        mComment.setCommentUserId(ParseUser.getCurrentUser().getObjectId());
        mComment.setCommentContent(content);
        mComment.setCommentScore(rateScore);
        mComment.setCommentTime(new Date());

        mProjectComment.postComment(mComment);
    }

    @Override
    public void onCommentSuccess() {
        EventBus.getDefault().post(new CommentSuccessEvent(true));
        finish();
    }

    @Override
    public void onQueryCommentsDone(List<Comment> mComments) {

    }
}
