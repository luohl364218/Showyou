package com.zju.campustour.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.chatting.utils.IdHelper;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.bean.Comment;
import com.zju.campustour.model.bean.Project;
import com.zju.campustour.model.bean.ProjectSaleInfo;
import com.zju.campustour.model.bean.ProjectUserMap;
import com.zju.campustour.model.bean.User;
import com.zju.campustour.presenter.implement.ProjectCommentImpl;
import com.zju.campustour.presenter.implement.ProjectInfoOpPresenterImpl;
import com.zju.campustour.presenter.implement.ProjectUserMapOpPresenterImpl;
import com.zju.campustour.presenter.protocal.enumerate.ProjectStateType;
import com.zju.campustour.presenter.protocal.enumerate.SexType;
import com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType;
import com.zju.campustour.presenter.protocal.event.CommentSuccessEvent;
import com.zju.campustour.presenter.protocal.event.ProjectDeleteEvent;
import com.zju.campustour.presenter.protocal.event.RecycleViewRefreshEvent;
import com.zju.campustour.view.iview.IProjectCollectorView;
import com.zju.campustour.view.iview.IProjectCommentView;
import com.zju.campustour.view.iview.IProjectInfoOperateView;
import com.zju.campustour.view.iview.ISearchProjectInfoView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import static com.zju.campustour.model.common.Constants.URL_DEFAULT_MAN_IMG;
import static com.zju.campustour.model.common.Constants.URL_DEFAULT_PROJECT_IMG;
import static com.zju.campustour.model.common.Constants.URL_DEFAULT_WOMAN_IMG;
import static com.zju.campustour.model.util.SharePreferenceManager.ConvertDateToDetailString;
import static com.zju.campustour.model.util.SharePreferenceManager.ConvertDateToString;
import static com.zju.campustour.presenter.protocal.enumerate.ProjectStateType.BOOK_ACCEPT;
import static com.zju.campustour.presenter.protocal.enumerate.ProjectStateType.PROJECT_STOP;
import static com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType.BOOK_SUCCESS;
import static com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType.FINISHED;

public class ProjectActivity extends BaseActivity implements IProjectInfoOperateView,ISearchProjectInfoView, IProjectCollectorView, View.OnClickListener,IProjectCommentView {

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
    @BindView(R.id.project_more_arrow)
    ImageView moreComment;
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
    Button projectChatBtn;
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
    @BindView(R.id.project_official)
    LinearLayout official;
    @BindView(R.id.project_provider_body)
    LinearLayout providerBody;
    @BindView(R.id.activity_project_selected_comments)
    TextView selectComment;
    @BindView(R.id.selected_comments_time)
    TextView selectCommentTime;
    @BindView(R.id.project_flag)
    LinearLayout projectFlag;
    @BindView(R.id.project_book_interface)
    CardView bookInterface;
    @BindView(R.id.project_manager_interface)
    CardView managerInterface;
    @BindView(R.id.project_contact_ta)
    CardView contactTa;
    @BindView(R.id.between_3)
    ImageView betweenLine;
    @BindView(R.id.cardview_provider_info)
    CardView providerInfo;
    @BindView(R.id.project_manager_btn_left)
    Button buttonLeft;
    @BindView(R.id.project_manager_btn_right)
    Button buttonRight;

    private ProjectInfoOpPresenterImpl mProjectInfoOpPresenter;
    private String selectedProviderId;
    private ProjectUserMapOpPresenterImpl mCollectorPresenter;
    private int position;
    private String projectId;
    private boolean isFavor;
    private Project currentProject;
    private ProjectSaleInfo currentProjectSaleInfo;
    private ProjectCommentImpl mProjectComment;
    private User defaultUser;
    private MenuItem selectedItem;
    private boolean isMyOwnProject = false;

    private ParseUser currentLoginUser;
    private UserProjectStateType userBookedState;
    private Context mContext;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        mContext = this;
        ButterKnife.bind(this);
        ShareSDK.initSDK(this);
        EventBus.getDefault().register(this);
        Intent mIntent = getIntent();
        currentProject = (Project) mIntent.getSerializableExtra("project");
        projectId = currentProject.getId();

        position = mIntent.getIntExtra("position", -1);
        if (currentProject == null){
            finish();
        }
        defaultUser = currentProject.getProvider();
        if (defaultUser == null){
            finish();
        }
        if (!isNetworkUseful){
            projectFlag.setVisibility(View.GONE);
        }

        initViews();

        mCollectorPresenter = new ProjectUserMapOpPresenterImpl(this,this);

        currentLoginUser = ParseUser.getCurrentUser();
        if (currentLoginUser != null)
            mCollectorPresenter.query(currentLoginUser.getObjectId(),currentProject.getId(),null);

        //异步请求项目的销售界面信息
        mProjectInfoOpPresenter = new ProjectInfoOpPresenterImpl(this,this);
        mProjectInfoOpPresenter.queryProjectSaleInfoWithId(currentProject.getId());
        //异步请求评论信息
        mProjectComment = new ProjectCommentImpl(this,this);
        mProjectComment.queryComment(projectId);

        initBtnView();

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    public void initBtnView(){
        /*初始化*/
        currentLoginUser = ParseUser.getCurrentUser();
        if (currentLoginUser != null)
            mCollectorPresenter.query(currentLoginUser.getObjectId(),projectId,null);

        /*如果是当前用户进自己的界面，不显示关注按钮,不显示聊天界面*/
        if (defaultUser != null && currentLoginUser != null
                && currentLoginUser.getObjectId().equals(defaultUser.getId())){
            contactTa.setVisibility(View.GONE);
            betweenLine.setVisibility(View.GONE);

            /*开启管理员模式*/
            openManagerMode();
        }
        else{
            isMyOwnProject = false;
            projectBookBtn.setText("立即预约");
            projectBookBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(ProjectActivity.this,LoginActivity.class);
                    startActivity(mIntent);
                }
            });

        }
    }


    private void openManagerMode() {
        isMyOwnProject = true;
        managerInterface.setVisibility(View.VISIBLE);
        bookInterface.setVisibility(View.GONE);
        buttonLeft.setOnClickListener(this);
        buttonRight.setOnClickListener(this);
        providerInfo.setVisibility(View.GONE);
        contactTa.setVisibility(View.GONE);
        switch (currentProject.getProjectState()){
            case BOOK_ACCEPT:
                buttonLeft.setText("报名详情");
                buttonRight.setText("结束报名");
                break;

            case BOOK_STOP:
                buttonLeft.setText("报名详情");
                buttonRight.setText("开始活动");
                break;

            case PROJECT_RUNNING:
                buttonLeft.setText("用户详情");
                buttonRight.setText("结束活动");
                break;

            case PROJECT_STOP:
                buttonLeft.setText("用户详情");
                buttonRight.setText("重启活动");
                break;
            default:
                break;
        }

    }

    private void openUserMode() {

        managerInterface.setVisibility(View.GONE);
        bookInterface.setVisibility(View.VISIBLE);

        if (userBookedState == null){
            switch (currentProject.getProjectState()){
                case BOOK_ACCEPT:
                    projectBookBtn.setText("立即预约");
                    projectBookBtn.setOnClickListener(this);
                    break;
                case BOOK_STOP:
                    projectBookBtn.setText("报名结束");
                    projectBookBtn.setEnabled(false);
                    break;
                case PROJECT_RUNNING:
                    projectBookBtn.setText("活动进行中");
                    projectBookBtn.setEnabled(false);
                    break;
                case PROJECT_STOP:
                    projectBookBtn.setText("活动已结束");
                    projectBookBtn.setEnabled(false);
                    break;

            }

            return;
        }

        switch (userBookedState){
            case BOOK_SUCCESS:
                if (currentProject.getProjectState() == BOOK_ACCEPT){
                    projectBookBtn.setText("取消预约");
                    projectBookBtn.setOnClickListener(this);
                }
                else if (currentProject.getProjectState() == PROJECT_STOP){
                    projectBookBtn.setText("填写评价");
                    projectBookBtn.setOnClickListener(this);
                }
                else if (currentProject.getProjectState() ==ProjectStateType.PROJECT_RUNNING){
                    projectBookBtn.setText("活动进行中");
                    projectBookBtn.setEnabled(false);
                }
                else if (currentProject.getProjectState() ==ProjectStateType.BOOK_STOP){
                    projectBookBtn.setText("预约成功");
                    projectBookBtn.setEnabled(false);
                }
                break;
            case FINISHED:
                projectBookBtn.setText("活动结束");
                projectBookBtn.setEnabled(false);
                break;
            default:
                break;

        }




    }


    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id){
            case R.id.activity_project_comment_num:
            case R.id.project_more_arrow:
                Intent intent = new Intent(this, InfoWebActivity.class);
                String url = "http://119.23.248.205:8080/comment?projectId="+projectId;
                intent.putExtra("web",url);
                startActivity(intent);

                break;
            case R.id.activity_project_btn_book:
                //用户模式
                if (!isMyOwnProject) {
                    if (userBookedState == null && currentProject.getProjectState() == BOOK_ACCEPT) {
                        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
                        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                        dialog.setCancelable(false);
                        dialog.setTitleText("预约成功")
                                .setContentText("活动预约成功，你可以在“我的活动”中查看，活动发起人会在近期与您联系")
                                .setConfirmText("确定")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                        currentProject.setBookedNum(currentProject.getBookedNum() + 1);
                                        projectEnrollNum.setText(currentProject.getBookedNum() + "人报名");

                                        projectBookBtn.setText("取消预约");
                                        userBookedState = BOOK_SUCCESS;
                                        mCollectorPresenter.put(currentLoginUser.getObjectId(), currentProject.getId(), BOOK_SUCCESS);
                                    }
                                })
                                .show();
                    } else if (userBookedState == BOOK_SUCCESS && currentProject.getProjectState() == BOOK_ACCEPT) {
                        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
                        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                        dialog.setCancelable(false);
                        dialog.setTitleText("取消预约")
                                .setContentText("您真的要取消这次活动么？")
                                .setConfirmText("确定")
                                .setCancelText("取消")
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.cancel();
                                    }
                                })
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                        if (currentProject.getBookedNum() >= 1)
                                            currentProject.setBookedNum(currentProject.getBookedNum() - 1);
                                        projectEnrollNum.setText(currentProject.getBookedNum() + "人报名");
                                        projectBookBtn.setText("立即预约");
                                        userBookedState = null;
                                        mCollectorPresenter.delete(currentLoginUser.getObjectId(), currentProject.getId(), BOOK_SUCCESS);
                                    }
                                })
                                .show();
                    } else if (userBookedState == BOOK_SUCCESS && currentProject.getProjectState() == PROJECT_STOP) {
                        //评价活动

                        Intent mIntent = new Intent(ProjectActivity.this, CommentActivity.class);
                        mIntent.putExtra("project", currentProject);
                        startActivity(mIntent, true);
                    }

                }
                break;
            case R.id.project_manager_btn_left:
            case R.id.project_manager_btn_right:
                //管理员模式
                if (isMyOwnProject) {
                    switch (currentProject.getProjectState()) {
                        case BOOK_ACCEPT:
                            if (id == R.id.project_manager_btn_left) {
                                Intent mIntent = new Intent(ProjectActivity.this, BookedStudentActivity.class);
                                mIntent.putExtra("projectId", currentProject.getId());
                                startActivity(mIntent, true);
                            }
                            if (id == R.id.project_manager_btn_right) {
                                SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
                                dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                dialog.setCancelable(false);
                                dialog.setTitleText("你确定吗?")
                                        .setContentText("结束报名后将无法再接收有意向参加的学生!")
                                        .setConfirmText("确定")
                                        .setCancelText("取消")
                                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.cancel();
                                            }
                                        })
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismissWithAnimation();
                                                buttonRight.setText("开始活动");
                                                currentProject.setProjectState(ProjectStateType.BOOK_STOP);
                                                mProjectInfoOpPresenter.setProjectState(currentProject.getId(), ProjectStateType.BOOK_STOP);
                                            }
                                        })
                                        .show();
                            }
                            break;

                        case BOOK_STOP:
                            if (id == R.id.project_manager_btn_left) {
                                Intent mIntent = new Intent(ProjectActivity.this, BookedStudentActivity.class);
                                mIntent.putExtra("projectId", currentProject.getId());
                                startActivity(mIntent, true);
                            }
                            if (id == R.id.project_manager_btn_right) {
                                SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
                                dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                dialog.setCancelable(false);
                                dialog.setTitleText("你确定吗?")
                                        .setContentText("活动即将开始，请确认参加人员到齐！")
                                        .setConfirmText("确定")
                                        .setCancelText("取消")
                                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.cancel();
                                            }
                                        })
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismissWithAnimation();
                                                buttonRight.setText("结束活动");
                                                currentProject.setProjectState(ProjectStateType.PROJECT_RUNNING);
                                                mProjectInfoOpPresenter.setProjectState(currentProject.getId(), ProjectStateType.PROJECT_RUNNING);
                                            }
                                        })
                                        .show();
                            }

                            break;

                        case PROJECT_RUNNING:
                            if (id == R.id.project_manager_btn_left) {
                                Intent mIntent = new Intent(ProjectActivity.this, BookedStudentActivity.class);
                                mIntent.putExtra("projectId", currentProject.getId());
                                startActivity(mIntent, true);
                            }
                            if (id == R.id.project_manager_btn_right) {
                                SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
                                dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                dialog.setCancelable(false);
                                dialog.setTitleText("恭喜你完成活动")
                                        .setContentText("辛苦啦~希望你多多组织活动让更多学生参与进来哈")
                                        .setConfirmText("好的")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismissWithAnimation();

                                                buttonRight.setText("重启活动");
                                                currentProject.setProjectState(PROJECT_STOP);
                                                mProjectInfoOpPresenter.setProjectState(currentProject.getId(), PROJECT_STOP);
                                            }
                                        })
                                        .show();
                            }
                            break;

                        case PROJECT_STOP:
                            if (id == R.id.project_manager_btn_left) {
                                Intent mIntent = new Intent(ProjectActivity.this, BookedStudentActivity.class);
                                mIntent.putExtra("projectId", currentProject.getId());
                                startActivity(mIntent, true);
                            }
                            if (id == R.id.project_manager_btn_right) {
                                SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
                                dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                dialog.setCancelable(false);
                                dialog.setTitleText("重启活动")
                                        .setContentText("活动重启后将接受学生报名")
                                        .setConfirmText("确定")
                                        .setCancelText("取消")
                                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.cancel();
                                            }
                                        })
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismissWithAnimation();
                                                buttonRight.setText("结束报名");
                                                currentProject.setProjectState(BOOK_ACCEPT);
                                                mProjectInfoOpPresenter.setProjectState(currentProject.getId(), BOOK_ACCEPT);
                                            }
                                        })
                                        .show();
                            }
                            break;

                        default:
                            break;
                    }
                }
                break;


        }




    }

    private void initViews() {
        /*初始化toolbar*/
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

        collapsToolbarLayout.setTitle("");
        projectRealTitle.setText(currentProject.getTitle());
        projectContent.setText(currentProject.getDescription());
        String url = currentProject.getImgUrl();
        if (url==null)
            url = URL_DEFAULT_PROJECT_IMG;
        Uri uri = Uri.parse(url);
        projectImg.setImageURI(uri);
        providerDesc.setText(currentProject.getDescription());
        projectAcceptNum.setText("上限"+currentProject.getAcceptNum()+"人");
        projectStartTime.setText(ConvertDateToString(currentProject.getStartTime()));
        projectEnrollNum.setText(currentProject.getBookedNum()+"人报名");

        User provider = currentProject.getProvider();

        url = provider.getImgUrl();

        if (url==null)
            url = provider.getSex() == SexType.MALE ? URL_DEFAULT_MAN_IMG: URL_DEFAULT_WOMAN_IMG;
        uri = Uri.parse(url);
        providerImg.setImageURI(uri);
        providerName.setText(provider.getRealName());
        providerDesc.setText(provider.getShortDescription());
        providerCollege.setText(provider.getSchool());
        providerGrade.setText(provider.getGrade());
        providerMajor.setText(provider.getMajor());
        providerFansNum.setText(provider.getFansNum()+"人关注");

        projectHint.setText(currentProject.getTips());
        projectOriginalPrice.setText(currentProject.getPrice()+"");
        projectOriginalPrice.setPaintFlags(projectOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        projectPrice.setText(""+currentProject.getSalePrice());

        projectComment.setOnClickListener(this);
        moreComment.setOnClickListener(this);

        providerBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(ProjectActivity.this, UserActivity.class);
                mIntent.putExtra("provider",currentProject.getProvider());
                startActivity(mIntent);
            }
        });



    }

    /*启动时初始化一次*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.project, menu);
        return true;
    }

    /*每次点击一个Menu的时候，它就改变一次*/
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem mItem = menu.findItem(R.id.project_favor_icon);
        selectedItem = mItem;
        /*如果是当前用户进自己的界面，不显示关注按钮*/
        if (isMyOwnProject){
            menu.findItem(R.id.project_favor_icon).setVisible(false);
            menu.findItem(R.id.project_edit_icon).setVisible(true);
        }

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

        if (!isNetworkUseful){
            showToast("当前网络不可用");
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
        //点击分享
        else if (id == R.id.project_share_icon){
            showShare();
            return true;
        }
        //点击编辑
        else if (id == R.id.project_edit_icon){
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.jmui_cancel_btn:
                            mDialog.cancel();
                            View.OnClickListener listener = new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    switch (view.getId()) {
                                        case R.id.jmui_cancel_btn:
                                            mDialog.cancel();
                                            break;
                                        case R.id.jmui_commit_btn:
                                            mProjectInfoOpPresenter.deleteProjectWithId(projectId);
                                            mDialog.cancel();
                                            break;
                                    }
                                }
                            };

                            mDialog = createDeleteDialog(mContext, listener);
                            mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
                            mDialog.show();

                            break;
                        case R.id.jmui_commit_btn:

                            showToast("准备编辑");
                            Intent mIntent = new Intent(ProjectActivity.this, ProjectNewActivity.class);
                            mIntent.putExtra("isEditMode",true);
                            mIntent.putExtra("project",currentProject);
                            startActivity(mIntent,true);


                            mDialog.cancel();
                            break;
                    }
                }
            };

            mDialog = createDeleteOrEditDialog(mContext, listener);
            mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
            mDialog.show();





        }
        return true;
    }


    public Dialog createDeleteDialog(Context context, View.OnClickListener listener){
        Dialog dialog = new Dialog(context, IdHelper.getStyle(context, "jmui_default_dialog_style"));
        View view = LayoutInflater.from(context).inflate(IdHelper.getLayout(context,
                "jmui_dialog_base_with_button"), null);
        dialog.setContentView(view);
        TextView title = (TextView) view.findViewById(IdHelper.getViewID(context, "jmui_title"));
        title.setText(IdHelper.getString(context, "delete_comfirm"));
        final Button cancel = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_cancel_btn"));
        final Button commit = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_commit_btn"));
        cancel.setOnClickListener(listener);
        cancel.setTextColor(getResources().getColor(R.color.colorPrimary));
        commit.setOnClickListener(listener);
        commit.setText(IdHelper.getString(context, "jmui_confirm"));
        commit.setTextColor(getResources().getColor(R.color.black));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public Dialog createDeleteOrEditDialog(Context context, View.OnClickListener listener){
        Dialog dialog = new Dialog(context, IdHelper.getStyle(context, "jmui_default_dialog_style"));
        View view = LayoutInflater.from(context).inflate(IdHelper.getLayout(context,
                "jmui_dialog_base_with_button"), null);
        dialog.setContentView(view);
        TextView title = (TextView) view.findViewById(IdHelper.getViewID(context, "jmui_title"));
        title.setText(IdHelper.getString(context, "choose_deleteOrEdit"));
        final Button cancel = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_cancel_btn"));
        final Button commit = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_commit_btn"));
        cancel.setOnClickListener(listener);
        cancel.setText(IdHelper.getString(context,"delete"));
        commit.setOnClickListener(listener);
        commit.setText(IdHelper.getString(context, "edit"));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle(currentProject.getTitle());
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        String url = "http://119.23.248.205:8080/project?objectId=" + currentProject.getId();
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(currentProject.getDescription());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        oks.setImageUrl(currentProject.getImgUrl());
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(selectComment.getText().toString().trim());
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);

        // 启动分享GUI
        oks.show(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ShareSDK.stopSDK(this);
    }

    @Override
    public void onGetProjectInfoDone(List<? extends Object> mProjects) {


        if (mProjects.size() != 0){
            currentProjectSaleInfo = (ProjectSaleInfo) mProjects.get(0);
            if (currentProjectSaleInfo.isRefundable()){
                projectFlag.setVisibility(View.VISIBLE);
                refundable.setVisibility(View.VISIBLE);
            }

            if(currentLoginUser.getBoolean(Constants.User_isVerified) ){
                projectFlag.setVisibility(View.VISIBLE);
                identified.setVisibility(View.VISIBLE);
            }
            if (currentProjectSaleInfo.isOfficial()){
                projectFlag.setVisibility(View.VISIBLE);
                official.setVisibility(View.VISIBLE);
            }

            //todo 增加分数字段
            mRatingBar.setNumStars(10);
            mRatingBar.setMax(currentProjectSaleInfo.getCommentNum() * 10);
            double progress = currentProjectSaleInfo.getTotalScore() * 10.0 /mRatingBar.getMax();
            mRatingBar.setProgress(currentProjectSaleInfo.getTotalScore());
            projectScore.setText(String.format("%.1f", progress)+"分");
        }


    }

    @Override
    public void onGetProjectInfoError(Exception e) {

    }

    @Override
    public void onQueryProjectCollectorStateDone(boolean state, List<ProjectUserMap> mProjectUserMapList) {
        //判断是否已经收藏
        isFavor = state;
        if (mProjectUserMapList != null && mProjectUserMapList.size() != 0){
            userBookedState = mProjectUserMapList.get(0).getUserProjectState();
        }
        else
            userBookedState = null;

        //此时用户不是关注活动状态，而是已经与活动有交互了，如果当前是用户进入，开启用户模式
        if (!isMyOwnProject)
            openUserMode();

        if (isFavor && selectedItem != null)
            selectedItem.setIcon(R.mipmap.icon_favor_selected);
        else if (selectedItem != null)
            selectedItem.setIcon(R.mipmap.icon_favor_default);

    }




    @Override
    public void onChangeCollectStateError(boolean isFavor, UserProjectStateType type) {

        String text;
        switch (type){
            case COLLECT:
                if (isFavor){
                    text = "收藏失败";
                    selectedItem.setIcon(R.mipmap.icon_favor_default);
                }
                else {
                    text = "取消收藏失败";
                    selectedItem.setIcon(R.mipmap.icon_favor_selected);
                }

                showToast(text);
                break;
            default:
                break;
        }

    }

    @Override
    public void onGetDealNumDone(int deal) {

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
    public void onCommentSuccess() {
        //提交评论
    }

    @Override
    public void onQueryCommentsDone(List<Comment> mComments) {
        //获得当前活动的评论
        int count = mComments.size();
        projectComment.setText(count+"条评价");

        if (mComments.size()>0){
            try{
                selectComment.setText(mComments.get(0).getCommentContent());
                selectCommentTime.setText(ConvertDateToDetailString(mComments.get(0).getCommentTime()));
            }catch (Exception e){

            }

        }
//【21】修复用户评论活动中，评论时间没有提交导致查看评论奔溃，评分默认不为true等BUG
    }

    @OnClick(R.id.activity_project_chat_button)
    public void onChatClickListener(){

        //获取极光用户信息
        JMessageClient.getUserInfo(defaultUser.getUserName(), new GetUserInfoCallback() {
            @Override
            public void gotResult(int status, String desc, UserInfo userInfo) {

                if (status == 0) {
                    Intent intent = new Intent();

                    if (userInfo.isFriend()) {
                        intent.setClass(mContext, FriendInfoActivity.class);
                        intent.putExtra("fromContact", true);
                    } else {
                        intent.setClass(mContext, SearchFriendDetailActivity.class);
                    }
                    intent.putExtra(Constants.TARGET_ID, userInfo.getUserName());
                    intent.putExtra(Constants.TARGET_APP_KEY, userInfo.getAppKey());
                    mContext.startActivity(intent);
                } else {
                    showToast("该用户尚未开通聊天功能");
                }
            }
        });

    }


    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onCommentDone(CommentSuccessEvent event){
        if (event.isCommentSuccess()){
            try {
                mCollectorPresenter.delete(currentLoginUser.getObjectId(),currentProject.getId(),BOOK_SUCCESS);
                mCollectorPresenter.put(currentLoginUser.getObjectId(),currentProject.getId(),FINISHED);
                projectBookBtn.setText("活动结束");
                projectBookBtn.setEnabled(false);
            }catch (Exception e){

            }

        }


    }

    @Override
    public void onDeleteProjectSuccess() {
        showToast("删除成功");
        EventBus.getDefault().post(new ProjectDeleteEvent(true));
        finish();
    }

    @Override
    public void onDeleteProjectFailed(Exception e) {
        showToast("删除失败，请稍后再试");
    }
}
