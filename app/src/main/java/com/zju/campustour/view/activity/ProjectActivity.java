package com.zju.campustour.view.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.database.models.Project;
import com.zju.campustour.model.database.models.ProjectSaleInfo;
import com.zju.campustour.model.database.models.ProjectUserMap;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.presenter.implement.ProjectInfoOpPresenterImpl;
import com.zju.campustour.presenter.implement.ProjectUserMapOpPresenterImpl;
import com.zju.campustour.presenter.protocal.enumerate.SexType;
import com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType;
import com.zju.campustour.presenter.protocal.event.RecycleViewRefreshEvent;
import com.zju.campustour.view.IView.IProjectCollectorView;
import com.zju.campustour.view.IView.ISearchProjectInfoView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import static com.zju.campustour.model.common.Constants.URL_DEFAULT_MAN_IMG;
import static com.zju.campustour.model.common.Constants.URL_DEFAULT_PROJECT_IMG;
import static com.zju.campustour.model.common.Constants.URL_DEFAULT_WOMAN_IMG;
import static com.zju.campustour.model.util.PreferenceUtils.ConvertDateToString;

public class ProjectActivity extends BaseActivity implements ISearchProjectInfoView, IProjectCollectorView{

    @BindView(R.id.activity_project_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.activity_project_collapsingToolbar)
    CollapsingToolbarLayout collapsToolbarLayout;
    @BindView(R.id.activity_project_image)
    SimpleDraweeView projectImg;
    @BindView(R.id.project_real_title)
    TextView projectRealTitle;
    @BindView(R.id.activity_project_content)
    TextView projectContent;
    @BindView(R.id.cardview_project_accept_num)
    TextView projectAcceptNum;
    @BindView(R.id.activity_project_start_time)
    TextView projectStartTime;
    @BindView(R.id.activity_project_enroll_num)
    TextView projectEnrollNum;
    @BindView(R.id.project_tinted_decimal_ratingbar)
    MaterialRatingBar mRatingBar;
    @BindView(R.id.activity_project_score)
    TextView projectScore;
    @BindView(R.id.activity_project_comment_num)
    TextView projectComment;
    @BindView(R.id.activity_project_provider_image)
    ImageView providerImg;
    @BindView(R.id.activity_project_provider_name)
    TextView providerName;
    @BindView(R.id.activity_project_provider_describe)
    TextView providerDesc;
    @BindView(R.id.activity_project_provider_college)
    TextView providerCollege;
    @BindView(R.id.activity_project_provider_grade)
    TextView providerGrade;
    @BindView(R.id.activity_project_provider_major)
    TextView providerMajor;
    @BindView(R.id.activity_project_fans_num)
    TextView providerFansNum;
    @BindView(R.id.activity_project_chat_button)
    TextView projectChatBtn;
    @BindView(R.id.activity_project_detail)
    TextView projectHint;
    @BindView(R.id.activity_project_btn4more)
    RelativeLayout btn4More;
    @BindView(R.id.activity_project_price)
    TextView projectPrice;
    @BindView(R.id.activity_project_original_price)
    TextView projectOriginalPrice;
    @BindView(R.id.activity_project_btn_book)
    Button projectBookBtn;
    @BindView(R.id.project_refundable)
    LinearLayout refundable;
    @BindView(R.id.project_identified)
    LinearLayout identified;
    @BindView(R.id.project_provider_body)
    LinearLayout providerBody;
    @BindView(R.id.activity_project_selected_comments)
    TextView selectComment;

    private ProjectInfoOpPresenterImpl mProjectInfoOpPresenter;
    private String selectedProviderId;
    private ProjectUserMapOpPresenterImpl mCollectorPresenter;
    private int position;
    private String projectId;
    private boolean isFavor;
    private Project currentProject;
    private MenuItem selectedItem;

    //todo 这里默认一个用户，以后要改为当前登录用户
    private ParseUser currentLoginUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        ButterKnife.bind(this);
        ShareSDK.initSDK(this);
        Intent mIntent = getIntent();
        Project mProject = (Project) mIntent.getSerializableExtra("project");
        position = mIntent.getIntExtra("position", -1);

        if (mProject == null){
            finish();
        }

        initViews(mProject);

        mCollectorPresenter = new ProjectUserMapOpPresenterImpl(this);

        currentLoginUser = ParseUser.getCurrentUser();
        if (currentLoginUser != null)
            mCollectorPresenter.query(currentLoginUser.getObjectId(),mProject.getId(), UserProjectStateType.COLLECT);

        //异步请求项目的销售界面信息
        mProjectInfoOpPresenter = new ProjectInfoOpPresenterImpl(this);
        mProjectInfoOpPresenter.queryProjectSaleInfoWithId(mProject.getId());

    }

    private void initViews(Project mProject) {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFinish();
            }
        });
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
        projectId = mProject.getId();
        currentProject = mProject;
        collapsToolbarLayout.setTitle("");
        projectRealTitle.setText(mProject.getTitle());
        projectContent.setText(mProject.getDescription());
        String url = mProject.getImgUrl();
        if (url==null)
            url = URL_DEFAULT_PROJECT_IMG;
        Uri uri = Uri.parse(url);
        projectImg.setImageURI(uri);
        providerDesc.setText(mProject.getDescription());
        projectAcceptNum.setText("上限"+mProject.getAcceptNum()+"人");
        projectStartTime.setText(ConvertDateToString(mProject.getStartTime()));
        //todo 增加报名人数字段
        projectEnrollNum.setText(mProject.getAcceptNum()+"人报名");

        User provider = mProject.getProvider();
        url = provider.getImgUrl();

        if (url==null)
            url = provider.getSex() == SexType.MALE ? URL_DEFAULT_MAN_IMG: URL_DEFAULT_WOMAN_IMG;
        uri = Uri.parse(url);
        providerImg.setImageURI(uri);
        providerName.setText(provider.getUserName());
        providerDesc.setText(provider.getShortDescription());
        providerCollege.setText(provider.getSchool());
        providerGrade.setText(provider.getGrade());
        providerMajor.setText(provider.getMajor());
        providerFansNum.setText(provider.getFansNum()+"人关注");
        //todo 增加温馨提示
        projectHint.setText(mProject.getDescription());
        //todo 增加精选评论
        projectPrice.setText(mProject.getPrice()+"");

        providerBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(ProjectActivity.this, ProviderHomePageActivity.class);
                mIntent.putExtra("provider",currentProject.getProvider());
                startActivity(mIntent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.project, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem mItem = menu.findItem(R.id.project_favor_icon);
        selectedItem = mItem;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        currentLoginUser = ParseUser.getCurrentUser();
        if (currentLoginUser == null){
            Intent mIntent = new Intent(this,LoginActivity.class);
            startActivity(mIntent);
            return true;
        }
        //点击收藏
        if (id == R.id.project_favor_icon) {
            if (isFavor){
                showToast("收藏已取消");
                mCollectorPresenter.delete(currentLoginUser.getObjectId(), projectId, UserProjectStateType.COLLECT);
                item.setIcon(R.mipmap.icon_favor_default);
                isFavor = false;
            }
            else {
                showToast("收藏成功");
                mCollectorPresenter.put(currentLoginUser.getObjectId(), currentProject.getId(), UserProjectStateType.COLLECT);
                item.setIcon(R.mipmap.icon_favor_selected);
                isFavor = true;
            }
            return true;
        }
        else if (id == R.id.project_share_icon){
            showShare();
            return true;
        }
        return true;
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle(currentProject.getTitle());
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(currentProject.getDescription());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        oks.setImageUrl(currentProject.getImgUrl());
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(selectComment.getText().toString().trim());
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ShareSDK.stopSDK(this);
    }

    @Override
    public void onGetProjectInfoDone(List<? extends Object> mProjects) {

        if (mProjects.size() != 0){
            ProjectSaleInfo mProjectSaleInfo = (ProjectSaleInfo) mProjects.get(0);
            refundable.setVisibility(mProjectSaleInfo.isRefundable()? View.VISIBLE : View.GONE);
            identified.setVisibility(mProjectSaleInfo.isIdentified()? View.VISIBLE : View.GONE);
            //todo 增加分数字段
            mRatingBar.setNumStars(5);
            mRatingBar.setMax(mProjectSaleInfo.getCommentNum() * 5);
            double progress = mProjectSaleInfo.getTotalScore() * 5.0 /mRatingBar.getMax();
            mRatingBar.setProgress(mProjectSaleInfo.getTotalScore());
            projectScore.setText(String.format("%.1f", progress)+"分");

            projectOriginalPrice.setText(""+mProjectSaleInfo.getOriginalPrice());
            projectOriginalPrice.setPaintFlags(projectOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }


    }

    @Override
    public void onGetProjectInfoError(Exception e) {

    }

    @Override
    public void onQueryProjectCollectorStateDone(boolean state, List<ProjectUserMap> mProjectUserMapList) {
        //todo 判断是否已经收藏
        isFavor = state;

        if (isFavor)
            selectedItem.setIcon(R.mipmap.icon_favor_selected);
        else
            selectedItem.setIcon(R.mipmap.icon_favor_default);

    }

    @Override
    public void onChangeCollectStateError(boolean isFavor) {

        String text;
        if (isFavor){
            text = "收藏失败";
            selectedItem.setIcon(R.mipmap.icon_favor_default);
        }
        else {
            text = "取消收藏失败";
            selectedItem.setIcon(R.mipmap.icon_favor_selected);
        }

        showToast(text);
    }

    @Override
    public void onBackPressed() {
        myFinish();
    }

    private void myFinish(){
        EventBus.getDefault().post(new RecycleViewRefreshEvent(position));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentLoginUser = ParseUser.getCurrentUser();
        if (currentLoginUser != null)
            mCollectorPresenter.query(currentLoginUser.getObjectId(),projectId, UserProjectStateType.COLLECT);
    }
}
