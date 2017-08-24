package com.zju.campustour.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.zju.campustour.R;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.bean.Project;
import com.zju.campustour.model.bean.ProjectUserMap;
import com.zju.campustour.model.bean.User;
import com.zju.campustour.model.bean.UserFocusMap;
import com.zju.campustour.presenter.implement.FocusMapOpPresenterImpl;
import com.zju.campustour.presenter.implement.ProjectInfoOpPresenterImpl;
import com.zju.campustour.presenter.implement.ProjectUserMapOpPresenterImpl;
import com.zju.campustour.presenter.implement.UserInfoOpPresenterImpl;
import com.zju.campustour.presenter.protocal.enumerate.FocusStateType;
import com.zju.campustour.presenter.protocal.enumerate.SexType;
import com.zju.campustour.presenter.protocal.enumerate.UserProjectStateType;
import com.zju.campustour.presenter.protocal.enumerate.UserType;
import com.zju.campustour.view.iview.IProjectCollectorView;
import com.zju.campustour.view.iview.ISearchProjectInfoView;
import com.zju.campustour.view.iview.ISearchUserInfoView;
import com.zju.campustour.view.iview.IUserFocusView;
import com.zju.campustour.view.adapter.ProjectInfoAdapter;
import com.zju.campustour.view.widget.DividerItemDecortion;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends BaseActivity implements ISearchUserInfoView,
        ISearchProjectInfoView,IUserFocusView ,IProjectCollectorView {

    @BindView(R.id.activity_major_provider_page_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.activity_major_provider_page_collapsing_toolbar)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.major_provider_page_userimage)
    SimpleDraweeView userImage;
    @BindView(R.id.major_provider_page_providerName)
    TextView providerName;
    @BindView(R.id.major_provider_page_sex_man)
    CircleImageView iconMan;
    @BindView(R.id.major_provider_page_sex_woman)
    CircleImageView iconWoman;
    @BindView(R.id.icon_provider_college)
    ImageView iconCollege;
    @BindView(R.id.major_provider_page_college)
    TextView college;
    @BindView(R.id.icon_provider_major)
    ImageView iconMajor;
    @BindView(R.id.major_provider_page_major)
    TextView major;
    @BindView(R.id.icon_provider_grade)
    ImageView iconGrade;
    @BindView(R.id.major_provider_page_grade)
    TextView grade;
    @BindView(R.id.major_provider_page_online)
    ImageView online;
    @BindView(R.id.major_provider_page_disOnline)
    ImageView disOnline;
    @BindView(R.id.major_provider_page_online_txt)
    TextView onlineTxt;
    @BindView(R.id.major_provider_page_total_deals)
    TextView dealNum;
    @BindView(R.id.major_provider_page_attention_amount)
    TextView focusNum;
    @BindView(R.id.major_provider_page_chat_button)
    Button chatOnline;
    @BindView(R.id.major_provider_page_project_list)
    RecyclerView projectList;
    @BindView(R.id.major_provider_page_noResult_hint)
    TextView noResultHint;
    @BindView(R.id.major_provider_page_about)
    TextView aboutHim;
    @BindView(R.id.contact_ta)
    CardView contactTA;
    @BindView(R.id.between_1)
    ImageView between_1;
    @BindView(R.id.between_2)
    ImageView between_2;
    @BindView(R.id.project_items)
    CardView projects;
    @BindView(R.id.project_wrapper)
    RelativeLayout projectWrapper;
    @BindView(R.id.user_contact_txt)
    TextView contactTxt;

    private String currentUserId;
    private UserInfoOpPresenterImpl mUserInfoOpPresenter;
    private ProjectInfoOpPresenterImpl mProjectInfoOpPresenter;
    private User defaultUser;
    private ProjectInfoAdapter mProjectAdapter;
    private MenuItem selectedItem;
    private ParseUser currentLoginUser;
    private UserType currentUserType;
    private int position;
    private int mFansNum = 0;
    private boolean isMyselfPage = false;
    private boolean isFans;
    private FocusMapOpPresenterImpl mFocusMapOpPresenter;
    private ProjectUserMapOpPresenterImpl mProjectUserMapOpPresenter;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);
        ShareSDK.initSDK(this);
        Intent mIntent = getIntent();
        defaultUser = (User) mIntent.getSerializableExtra("provider");
        position = mIntent.getIntExtra("position", -1);
        if (defaultUser == null){
            finish();
        }


        currentUserId = defaultUser.getId();
        currentUserType = defaultUser.getUserType();
        if (currentUserType == UserType.PROVIDER){
            mProjectInfoOpPresenter = new ProjectInfoOpPresenterImpl(this,this);
            mProjectInfoOpPresenter.queryProjectWithUserId(currentUserId,0,10);
            mProjectUserMapOpPresenter = new ProjectUserMapOpPresenterImpl(this, this);
            mProjectUserMapOpPresenter.getDealNum(currentUserId);
        }
        mUserInfoOpPresenter = new UserInfoOpPresenterImpl(this,this);
        mFocusMapOpPresenter = new FocusMapOpPresenterImpl(this,this);
        mFocusMapOpPresenter.queryFansNum(currentUserId);

        mUserInfoOpPresenter.queryUserInfoWithId(currentUserId);


        initViewsAndEvents(defaultUser);

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentLoginUser = ParseUser.getCurrentUser();
        if (currentLoginUser != null)
            mFocusMapOpPresenter.query(currentUserId,currentLoginUser.getObjectId(), FocusStateType.FOCUS);

        /*如果是当前用户进自己的界面，不显示关注按钮,不显示聊天界面*/
        if (defaultUser != null
                && currentLoginUser != null
                && currentLoginUser.getObjectId().equals(currentUserId)){
            isMyselfPage = true;
            contactTA.setVisibility(View.GONE);
            between_1.setVisibility(View.GONE);

        }
        else
            isMyselfPage = false;
    }

    protected void initViewsAndEvents(User user) {

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initBasicView();
        initUserInfoView(user);

    }

    private void initBasicView() {

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setDisplayShowTitleEnabled(false);
        }

    }

    @Override
    public void onGetProviderUserDone(List<User> mUsers) {
        if (mUsers.get(0).isOnline()){
            online.setVisibility(View.VISIBLE);
            disOnline.setVisibility(View.GONE);
            onlineTxt.setText("在线");
        }
        else {
            onlineTxt.setText("下线");
            disOnline.setVisibility(View.VISIBLE);
            online.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGetProviderUserError(ParseException e) {

    }

    @Override
    public void refreshUserOnlineState(boolean isOnline) {
         /*只更新在线与否的状态*/
        if (isOnline){
            online.setVisibility(View.VISIBLE);
            disOnline.setVisibility(View.GONE);
            onlineTxt.setText("在线");
        }
        else {
            onlineTxt.setText("下线");
            disOnline.setVisibility(View.VISIBLE);
            online.setVisibility(View.GONE);
        }

    }

    private void initUserInfoView(User user) {
        String url = user.getImgUrl();
        providerName.setVisibility(View.VISIBLE);
        providerName.setText(user.getRealName());
        mToolbarLayout.setTitle(user.getRealName());
        mToolbarLayout.setCollapsedTitleGravity(View.SCROLL_INDICATOR_START);
        mToolbarLayout.setExpandedTitleGravity(View.SCROLL_INDICATOR_TOP);
        mToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        mToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.transparent));
        if (user.getSex() == SexType.MALE){
            if (url == null)
                url = Constants.URL_DEFAULT_MAN_IMG;
            iconMan.setVisibility(View.VISIBLE);
            iconWoman.setVisibility(View.GONE);
        }
        else {
            if (url == null)
                url = Constants.URL_DEFAULT_WOMAN_IMG;
            iconMan.setVisibility(View.GONE);
            iconWoman.setVisibility(View.VISIBLE);
        }

        Uri uri = Uri.parse(url);
        userImage.setImageURI(uri);

        String school = user.getSchool();
        if (!TextUtils.isEmpty(school)){
            iconCollege.setVisibility(View.VISIBLE);
            college.setVisibility(View.VISIBLE);
            college.setText(school);
        }

        String majorName = user.getMajor();
        if (!TextUtils.isEmpty(majorName)){
            iconMajor.setVisibility(View.VISIBLE);
            major.setVisibility(View.VISIBLE);
            major.setText(majorName);
        }

        String gradeName = user.getGrade();
        if (!TextUtils.isEmpty(gradeName)){
            iconGrade.setVisibility(View.VISIBLE);
            grade.setVisibility(View.VISIBLE);
            grade.setText(gradeName);
        }

        if (user.isOnline()){
            online.setVisibility(View.VISIBLE);
            disOnline.setVisibility(View.GONE);
            onlineTxt.setText("在线");
        }
        else {
            onlineTxt.setText("下线");
            disOnline.setVisibility(View.VISIBLE);
            online.setVisibility(View.GONE);
        }

        if (currentUserType == UserType.USER){
            contactTxt.setText("了解不同学校的学习情况");
            between_2.setVisibility(View.GONE);
            projects.setVisibility(View.GONE);
            projectWrapper.setVisibility(View.GONE);
        }

        String selfDesc = user.getDescription();
        if (TextUtils.isEmpty(selfDesc))
            selfDesc = "我对自己无话可说，如果你真的想听，那么，请联系我吧";
        aboutHim.setText(selfDesc);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onGetProjectInfoDone(List<? extends Object> mProjects) {
        if (mProjects.size() != 0){
            noResultHint.setVisibility(View.GONE);
            showProjectRecycleView((List<Project>) mProjects);
        }
        else {
            noResultHint.setVisibility(View.VISIBLE);
            noResultHint.setText(R.string.userProjectHint);
            //showLocalProjectItemInfoData();
        }
    }

    @Override
    public void onGetProjectInfoError(Exception e) {

    }

    private void showProjectRecycleView(List<Project> mProjectList) {
        projectList.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        if (mProjectList == null)
            return;
        mProjectAdapter = new ProjectInfoAdapter(mProjectList, Constants.PART_VIEW ,getBaseContext());

        mProjectAdapter.setOnProjectItemClickListener(new ProjectInfoAdapter.onProjectItemClickListener() {
            @Override
            public void onClick(View v, int position, Project project) {
                Intent mIntent = new Intent(UserActivity.this, ProjectActivity.class);
                mIntent.putExtra("project", project);
                mIntent.putExtra("position",position);
                startActivity(mIntent);
            }
        });
        projectList.setLayoutManager(layoutManager);
        projectList.setAdapter(mProjectAdapter);
        projectList.addItemDecoration(new DividerItemDecortion());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem mItem = menu.findItem(R.id.user_focus_icon);
        selectedItem = mItem;
         /*如果是当前用户进自己的界面，不显示关注按钮*/
        if (isMyselfPage){
            menu.findItem(R.id.user_focus_icon).setVisible(false);
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
        //点击收藏
        if (id == R.id.user_focus_icon) {
            if (isFans){
                showToast("取消关注");
                mFocusMapOpPresenter.delete(currentUserId,currentLoginUser.getObjectId(), FocusStateType.FOCUS);
                item.setIcon(R.mipmap.icon_user_focus_default);
                mFansNum--;
                if (mFansNum >= 0)
                    focusNum.setText(mFansNum+"人关注");
                isFans = false;
            }
            else {
                showToast("关注成功");
                mFocusMapOpPresenter.put(currentUserId,currentLoginUser.getObjectId(), FocusStateType.FOCUS);
                item.setIcon(R.mipmap.icon_user_focus_select);
                mFansNum++;
                focusNum.setText(mFansNum+"人关注");
                isFans = true;
            }
            return true;
        }
        else if (id == R.id.user_share_icon){
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
        oks.setTitle(defaultUser.getSchool()+ " " + defaultUser.getRealName());

        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        String url ="http://119.23.248.205:8080/user?objectId="+defaultUser.getId();
        oks.setTitleUrl(url);

        // text是分享文本，所有平台都需要这个字段
        oks.setText(defaultUser.getShortDescription());

        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        oks.setImageUrl(defaultUser.getImgUrl());

        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);

        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我在校游中找到了一位优秀的同学，你们也一起来校游吧！");

        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));

        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);

        // 启动分享GUI
        oks.show(this);
    }

    @Override
    public void onFocusActionError(boolean flag) {
        String text;
        if (flag){
            text = "关注失败";
            selectedItem.setIcon(R.mipmap.icon_user_focus_default);
        }
        else {
            text = "取消关注失败";
            selectedItem.setIcon(R.mipmap.icon_user_focus_select);
        }

        showToast(text);
    }

    @Override
    public void onQueryFansOrFocusDone(boolean isFocus, List<UserFocusMap> userFocusList) {
        //判断是否已经关注
        isFans = isFocus;

        if (isFocus && selectedItem != null)
            selectedItem.setIcon(R.mipmap.icon_user_focus_select);
        else
            selectedItem.setIcon(R.mipmap.icon_user_focus_default);
    }

    @Override
    public void onGetFansNumDone(int fansNum) {
        mFansNum = fansNum;
        focusNum.setText(fansNum+"人关注");

    }

    @Override
    public void onQueryMyFansDone(List<User> fansList) {

    }

    @Override
    public void onQueryMyFocusDone(List<User> focusList) {

    }

    @Override
    public void onQueryProjectCollectorStateDone(boolean state, List<ProjectUserMap> mProjectUserMapList) {

    }

    @Override
    public void onChangeCollectStateError(boolean isFavor, UserProjectStateType type) {

    }

    @Override
    public void onGetDealNumDone(int deal) {
        dealNum.setText(deal+"人成交");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ShareSDK.stopSDK(this);
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    @OnClick(R.id.major_provider_page_chat_button)
    public void talkToSomeone(){

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

}
