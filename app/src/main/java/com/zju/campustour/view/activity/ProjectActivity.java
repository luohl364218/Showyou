package com.zju.campustour.view.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zju.campustour.R;
import com.zju.campustour.model.database.models.Project;
import com.zju.campustour.model.database.models.ProjectSaleInfo;
import com.zju.campustour.model.database.models.ProjectUserMap;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.presenter.implement.ProjectInfoOpPresenterImpl;
import com.zju.campustour.presenter.implement.ProjectUserMapOpPresenterImpl;
import com.zju.campustour.presenter.protocal.enumerate.SexType;
import com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType;
import com.zju.campustour.presenter.protocal.event.onRecycleViewRefreshEvent;
import com.zju.campustour.view.IView.IProjectCollectorView;
import com.zju.campustour.view.IView.ISearchProjectInfoView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import static com.zju.campustour.model.common.Constants.URL_DEFAULT_MAN_IMG;
import static com.zju.campustour.model.common.Constants.URL_DEFAULT_PROJECT_IMG;
import static com.zju.campustour.model.common.Constants.URL_DEFAULT_WOMAN_IMG;
import static com.zju.campustour.model.util.SpUtils.ConvertDateToString;

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

    private ProjectInfoOpPresenterImpl mProjectInfoOpPresenter;
    private String selectedProviderId;
    private ProjectUserMapOpPresenterImpl mCollectorPresenter;
    private int position;
    private String projectId;
    private boolean isFavor;
    private Project currentProject;
    private MenuItem selectedItem;
    private Toast toast = null;
    //todo 这里默认一个用户，以后要改为当前登录用户
    private String currentLoginUser = "BmzEVHaG4A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        ButterKnife.bind(this);
        Intent mIntent = getIntent();
        Project mProject = (Project) mIntent.getSerializableExtra("project");
        position = mIntent.getIntExtra("position", -1);

        if (mProject == null){
            finish();
        }

        initViews(mProject);

        mCollectorPresenter = new ProjectUserMapOpPresenterImpl(this);

        mCollectorPresenter.query(currentLoginUser,mProject.getId(), UserProjectStateType.COLLECT);

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

        //点击收藏
        if (id == R.id.project_favor_icon) {
            if (isFavor){
                if (toast != null) {
                    toast.setText("收藏已取消");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();

                } else {
                    toast = Toast.makeText(this, "收藏已取消", Toast.LENGTH_SHORT);
                    toast.show();
                }
                mCollectorPresenter.delete(currentLoginUser, projectId, UserProjectStateType.COLLECT);
                item.setIcon(R.mipmap.icon_favor_default);
                isFavor = false;
            }
            else {
                if (toast != null) {
                    toast.setText("收藏成功");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    toast = Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT);
                    toast.show();
                }
                mCollectorPresenter.put(currentLoginUser, currentProject.getId(), UserProjectStateType.COLLECT);
                item.setIcon(R.mipmap.icon_favor_selected);
                isFavor = true;
            }
            return true;
        }
        else if (id == R.id.project_share_icon){
            return true;
        }
        return true;
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

        if (toast != null) {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onBackPressed() {
        myFinish();
    }

    private void myFinish(){
        EventBus.getDefault().post(new onRecycleViewRefreshEvent(position));
        finish();
    }

}
