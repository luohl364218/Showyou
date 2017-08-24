package com.zju.campustour;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabSelectListener;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.yalantis.ucrop.UCrop;
import com.zju.campustour.model.chatting.utils.DialogCreator;
import com.zju.campustour.model.chatting.utils.FileHelper;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.database.dao.MajorFIlesDao;
import com.zju.campustour.model.database.data.MajorData;
import com.zju.campustour.model.bean.MajorModel;
import com.zju.campustour.model.database.data.SchoolData;
import com.zju.campustour.model.bean.User;
import com.zju.campustour.model.bean.UserFocusMap;
import com.zju.campustour.model.util.NetworkUtil;
import com.zju.campustour.model.util.SharePreferenceManager;
import com.zju.campustour.presenter.chatting.tools.NativeImageLoader;
import com.zju.campustour.presenter.implement.FocusMapOpPresenterImpl;
import com.zju.campustour.presenter.implement.ImageUploader;
import com.zju.campustour.presenter.implement.MajorInfoPresenterImpl;
import com.zju.campustour.presenter.ipresenter.IMajorInfoPresenter;
import com.zju.campustour.presenter.protocal.enumerate.UploadImgType;
import com.zju.campustour.presenter.protocal.enumerate.UserType;
import com.zju.campustour.presenter.protocal.event.EditUserInfoDone;
import com.zju.campustour.presenter.protocal.event.LoginDoneEvent;
import com.zju.campustour.presenter.protocal.event.LogoutEvent;
import com.zju.campustour.presenter.protocal.event.UnreadFriendVerifyNum;
import com.zju.campustour.presenter.protocal.event.UnreadMsgEvent;
import com.zju.campustour.presenter.protocal.event.UserPictureUploadDone;
import com.zju.campustour.presenter.protocal.event.ToolbarItemClickEvent;
import com.zju.campustour.presenter.protocal.event.NetworkChangeEvent;
import com.zju.campustour.presenter.protocal.event.UserTypeChangeEvent;
import com.zju.campustour.view.activity.FixProfileActivity;
import com.zju.campustour.view.activity.MeInfoActivity;
import com.zju.campustour.view.activity.ReloginActivity;
import com.zju.campustour.view.activity.SettingActivity;
import com.zju.campustour.view.fragment.ChatFragment;
import com.zju.campustour.view.fragment.ContactsFragment;
import com.zju.campustour.view.fragment.StatusInfoFragment;
import com.zju.campustour.view.iview.IImageUploadView;
import com.zju.campustour.view.iview.IMajorInfoUpdateView;
import com.zju.campustour.view.iview.IUserFocusView;
import com.zju.campustour.view.activity.BaseMainActivity;
import com.zju.campustour.view.activity.LoginActivity;
import com.zju.campustour.view.activity.MyProjectActivity;
import com.zju.campustour.view.activity.MySchoolmateActivity;
import com.zju.campustour.view.activity.ProjectNewActivity;
import com.zju.campustour.view.adapter.FragmentAdapter;
import com.zju.campustour.view.fragment.HomeFragment;
import com.zju.campustour.view.fragment.MessageFragment;
import com.zju.campustour.view.fragment.SearchFragment;
import com.zju.campustour.view.widget.ActivityCollector;
import com.zju.campustour.view.widget.viewpager.SuperViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jiguang.api.JCoreInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends BaseMainActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, IUserFocusView ,
        IMajorInfoUpdateView,IImageUploadView {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private ImageView mAvatarIv;
    private SimpleDraweeView userImg;
    private CircleImageView userEditLogo;
    private Context mContext = this;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    private View headerLayout;
    private TextView loginHint;
    private TextView userName;
    private TextView userType;

    @BindView(R.id.bottomBar)
    BottomBar mBottomBar;
    BottomBarTab chatTab;
    BottomBarTab contactTab;

    @BindView(R.id.viewPager)
    SuperViewPager mViewPager;

    private List<Fragment> fragmentList;
    //点击两次返回才退出程序
    private long lastPressTime = 0;
    //定义二维码扫描请求
    private int REQUEST_CODE = 1;
    private int CAMERA_REQUEST_CODE = 2;

    //专业列表相关信息
    public static List<String> groupList = new ArrayList<>();
    public static List<List> itemsList = new ArrayList<>();
    //学校列表相关信息
    public static List<String> areaGroup = new ArrayList<>();
    public static List<List> schoolItemsList = new ArrayList<>();

    //监听网络变化
    private IntentFilter mIntentFilter;
    private NetworkChangeReceiver mNetworkChangeReceiver;

    //读取和记录是否初始化过
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //初始化fragment
    private HomeFragment mHomeFragment;
    private SearchFragment mSearchFragment;
    private MessageFragment mMessageFragment;
    private ChatFragment mChatFragment;
    private ContactsFragment mContactsFragment;
    private StatusInfoFragment mStatusInfoFragment;

    private ParseUser currentLoginUser;
    private Dialog mDialog;
    private int unreadMsg = 0;
    private ImageUploader mImageUploader;

    private String TAG = "mainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        initLayoutElements();

        initSchoolListViewData();
        initDatabase();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mNetworkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(mNetworkChangeReceiver, mIntentFilter);
        //huanxin
      /*  //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new MyConnectionListener(this));*/
      //准备好负责图片上传的工具
        mImageUploader = new ImageUploader(this,this);


    }

    @Override
    protected void onResume() {
        JCoreInterface.onResume(this);
        UserInfo myInfo = JMessageClient.getMyInfo();
        if (myInfo != null){
            //已经登录过但是没设置头像,就跳转到设置头像界面
            if (TextUtils.isEmpty(myInfo.getNickname())) {
                Intent intent = new Intent();
                intent.setClass(this, FixProfileActivity.class);
                startActivity(intent);
            }
        }
        mChatFragment.sortConvList();
        super.onResume();
    }

    @Override
    protected void onPause() {
        JCoreInterface.onPause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        unregisterReceiver(mNetworkChangeReceiver);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 初始化数据库,本地化存储是否有初始过，保证只初始化一次
     */
    private void initDatabase() {

        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Boolean hasInited = sharedPreferences.getBoolean("init_database",false);
        IMajorInfoPresenter mMajorInfoPresenter = new MajorInfoPresenterImpl(this, this);
        if (!hasInited) {
            mMajorInfoPresenter.getAllMajorInfo();
        }
        else {
            mMajorInfoPresenter.getUpdateMajorInfo();
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    public  void initMajorListViewData(List<MajorModel> mMajorModelList){

        groupList = new ArrayList<>(Arrays.asList(MajorData.majorGroup));

        Map<String, List<String>> majorGroup = new HashMap<>(groupList.size());

        for (MajorModel major : mMajorModelList){

            if (majorGroup.get(major.getMajorType()) == null){
                List<String> majorList = new ArrayList<>();
                majorList.add(major.getName());
                majorGroup.put(major.getMajorType(),majorList);
            }
            else
            {
                majorGroup.get(major.getMajorType()).add(major.getName());
            }

        }

        for (String majorName : groupList){
            itemsList.add(majorGroup.get(majorName));
        }

        //android暂时不支持stream用法
    /*    for (int i = 0; i < groupList.size(); i++){
            String majorType = groupList.get(i);
            List<String> majorList = mMajorModelList.stream()
                    .filter(a->a.getMajorType().equals(majorType))
                    .map(MajorModel::getName)
                    .distinct()
                    .collect(Collectors.toList());

            itemsList.add(majorList);
        }*/
    }


    public  void initSchoolListViewData(){
        areaGroup = new ArrayList<>(Arrays.asList(SchoolData.allAreaGroup));
        for (int i = 0; i< SchoolData.allAreaSchoolList.length; i++){
            List<String> schoolList = new ArrayList<>(Arrays.asList((String[])SchoolData.allAreaSchoolList[i]));
            schoolItemsList.add(schoolList);
        }
    }

    private void initLayoutElements(){

        mNavigationView.setNavigationItemSelectedListener(this);
        headerLayout = mNavigationView.getHeaderView(0);
        userImg = (SimpleDraweeView) headerLayout.findViewById(R.id.current_user_img);
        userImg.setOnClickListener(this);
        mAvatarIv = (ImageView)headerLayout.findViewById(R.id.my_avatar_iv);
        loginHint = (TextView) headerLayout.findViewById(R.id.login_hint);
        loginHint.setOnClickListener(this);
        userEditLogo = (CircleImageView) headerLayout.findViewById(R.id.user_edit);
        userEditLogo.setOnClickListener(this);
        userName = (TextView) headerLayout.findViewById(R.id.username);
        userType = (TextView) headerLayout.findViewById(R.id.user_type);
        currentLoginUser = ParseUser.getCurrentUser();

        if (currentLoginUser == null){
            //双重防护， 只有同时登陆成功才能进入
            userImg.setImageURI(Uri.parse("www.cxx"));
            loginHint.setVisibility(View.VISIBLE);
            userEditLogo.setVisibility(View.GONE);

        }
        else{
            initUserCloudInfo();

            userEditLogo.setVisibility(View.VISIBLE);
            String userImgUrl = currentLoginUser.getString("imgUrl");
            userImg.setImageURI(Uri.parse(userImgUrl == null? "www.cxx" :userImgUrl));
            loginHint.setVisibility(View.GONE);
            userName.setText(currentLoginUser.getUsername());
            userType.setText(UserType.values()[currentLoginUser.getInt("userType")].getName());
            currentLoginUser.put("online",true);
            currentLoginUser.saveInBackground();

        }

        mBottomBar.setDefaultTabPosition(0);
        //初始化几个Fragment
        initViewPager();

        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_home:
                        Log.d(TAG,"tab home click--------------------");
                        mViewPager.setCurrentItemByTab(0, true);
                        /*mToolbar.setNavigationIcon(R.mipmap.icon_user_default);
                        mToolbar.setTitle("校游 Show You");*/
                        break;

                    case R.id.tab_search:
                        Log.d(TAG,"tab search click--------------------");
                        mViewPager.setCurrentItemByTab(1, true);
                        /*mToolbar.setTitle("发现");*/
                        break;

                    case R.id.tab_info:
                        Log.d(TAG,"tab info click--------------------");
                        mViewPager.setCurrentItemByTab(2, true);
                        /*mToolbar.setTitle("消息");
                        mToolbar.setNavigationIcon(R.mipmap.icon_user_default);*/
                        break;
                    case R.id.tab_chat:
                        mViewPager.setCurrentItemByTab(3,true);
                      /*  if (chatTab != null)
                            chatTab.removeBadge();*/
                        break;
                    case R.id.tab_contact:
                        mViewPager.setCurrentItemByTab(4,true);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    private void initViewPager() {
        fragmentList = new ArrayList<>();
        // 这里的添加顺序是否对 tab 页的前后顺序有影响
        //fragmentList.add(fragmentTabHost.getTabWidget().)
        mHomeFragment = new HomeFragment();
        mSearchFragment = new SearchFragment();
        mMessageFragment = new MessageFragment();
        mChatFragment = new ChatFragment();
        mContactsFragment = new ContactsFragment();
        mStatusInfoFragment = new StatusInfoFragment();

        fragmentList.add(mHomeFragment);
        fragmentList.add(mSearchFragment);
        fragmentList.add(mStatusInfoFragment);
        //fragmentList.add(mMessageFragment);
        fragmentList.add(mChatFragment);
        fragmentList.add(mContactsFragment);



        //让viewPager缓存一定的页面，不要销毁
        mViewPager.setOffscreenPageLimit(3);


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mBottomBar.selectTabAtPosition(position, true);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(),
                fragmentList));
        mViewPager.setCurrentItem(0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            case R.id.edit_info:
                //编辑我的信息
                //// TODO: 2017/5/31 要把编辑信息的范围扩大
                /*Intent mIntent = new Intent(this,RegisterInfoOneActivity.class);
                mIntent.putExtra("isEditMode",true);
                //修复BUG，需要登录验证
                startActivity(mIntent,true);*/

                startActivity(new Intent(this, MeInfoActivity.class), true);
                break;
            case R.id.my_project:
                //打开我的活动

                Intent mIntent_1 = new Intent(this,MyProjectActivity.class);
                currentLoginUser = ParseUser.getCurrentUser();
                if (currentLoginUser != null)
                    mIntent_1.putExtra("userType",currentLoginUser.getInt("userType"));
                else
                    mIntent_1.putExtra("userType",0);

                startActivity(mIntent_1,true);
                break;

            case R.id.my_schoolmate:
                //打开我的校友
                startActivity(new Intent(this,MySchoolmateActivity.class),true);
                break;

            case R.id.my_setting:
                startActivity(new Intent(this, SettingActivity.class),true);
                break;

            case R.id.logout:
                //退出登录
                currentLoginUser = ParseUser.getCurrentUser();
            /*退出登录也要网络可用*/
                if (currentLoginUser != null){

                    View.OnClickListener listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            switch (view.getId()) {
                                case R.id.jmui_cancel_btn:
                                    mDialog.cancel();
                                    break;
                                case R.id.jmui_commit_btn:
                                    currentLoginUser.put("online",false);
                                    currentLoginUser.saveInBackground();

                                    Logout();
                                    cancelNotification();
                                    NativeImageLoader.getInstance().releaseCache();
                                    finish();
                                    mDialog.cancel();
                                    break;
                            }
                        }
                    };
                    mDialog = DialogCreator.createLogoutDialog(mContext, listener);
                    mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
                    mDialog.show();


                }
                break;
            case R.id.build_project:
                //发布活动

                currentLoginUser = ParseUser.getCurrentUser();
                //修复Bug，未登录状态下不能发布活动
                if (currentLoginUser != null) {
                    UserType mUserType = UserType.values()[currentLoginUser.getInt("userType")];
                    if (mUserType == UserType.PROVIDER) {
                        Intent mIntent_2 = new Intent(this, ProjectNewActivity.class);
                        mIntent_2.putExtra("isEditMode", false);
                        startActivity(mIntent_2, true);
                    } else {
                        showToast("同学你是普通用户，没有权限创建活动哦");
                    }
                }
                break;
            /*case R.id.test_enter:
                startActivity(new Intent(this, IdentityConfirmActivity.class));
                break;*/

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //退出登录
    private void Logout() {



        EventBus.getDefault().post(new LogoutEvent(true));
        // TODO Auto-generated method stub
        final Intent intent = new Intent();
        UserInfo info = JMessageClient.getMyInfo();
        if (null != info) {
            intent.putExtra("userName", info.getUserName());
            File file = info.getAvatarFile();
            if (file != null && file.isFile()) {
                intent.putExtra("avatarFilePath", file.getAbsolutePath());
            } else {
                String path = FileHelper.getUserAvatarPath(info.getUserName());
                file = new File(path);
                if (file.exists()) {
                    intent.putExtra("avatarFilePath", file.getAbsolutePath());
                }
            }
            SharePreferenceManager.putString(this,Constants.DB_USERNAME,info.getUserName());
            SharePreferenceManager.putString(this,Constants.DB_USERIMG,file.getAbsolutePath());

            if (NetworkUtil.isNetworkAvailable(mContext)){
                JMessageClient.logout();
                ParseUser.logOut();
            }

            intent.setClass(mContext, ReloginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Log.d(TAG, "user info is null!");
        }
    }

    public void cancelNotification() {
        NotificationManager manager = (NotificationManager) this.getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_edit:
                editUserImg();
                break;
            case R.id.login_hint:
            case R.id.current_user_img:
                currentLoginUser = ParseUser.getCurrentUser();
                if (currentLoginUser == null) {
                    Intent mIntent = new Intent(this, LoginActivity.class);
                    startActivity(mIntent);
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                break;

            default:
                break;
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onLoginDoneEvent(LoginDoneEvent event) {
        if (event.isLogin()){
            try{
                currentLoginUser = ParseUser.getCurrentUser();
                userName.setText(currentLoginUser.getUsername());
                loginHint.setVisibility(View.GONE);
                userType.setText(UserType.values()[currentLoginUser.getInt("userType")].getName());
                String img = currentLoginUser.getString("imgUrl");
                if (img == null){
                    img = Constants.URL_DEFAULT_MAN_IMG;
                }
                userImg.setImageURI(Uri.parse(img));
                userEditLogo.setVisibility(View.VISIBLE);
                SharePreferenceManager.putString(mContext,Constants.DB_USERIMG_ONLINE, img);
                SharePreferenceManager.putString(mContext,Constants.DB_USERNAME, currentLoginUser.getUsername());

                FocusMapOpPresenterImpl mFocusMapOpPresenter = new FocusMapOpPresenterImpl(this,this);
                mFocusMapOpPresenter.queryFansNum(currentLoginUser.getObjectId());


            }catch (Exception e){

            }

        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onPictureUploadDoneEvent(UserPictureUploadDone event) {
        currentLoginUser = ParseUser.getCurrentUser();
        if (event != null && currentLoginUser != null){
                Uri mUri = Uri.fromFile(new File(event.getLocalImgUrl()));
                userImg.setImageURI(mUri);

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onLogoutEvent(LogoutEvent event) {
        if (event.isLogout()){
            try{
                userImg.setImageURI(Uri.parse("www.cxx"));
                loginHint.setVisibility(View.VISIBLE);
                userEditLogo.setVisibility(View.GONE);
                userName.setText("第0行代码");
                userType.setText("1124281072@qq.com");
                currentLoginUser = null;
            }catch (Exception e){

            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserTypeChangeEvent(UserTypeChangeEvent event){
        if (event.isCommonUser()){
            userType.setText(UserType.USER.getName());
        }
        else {
            userType.setText(UserType.PROVIDER.getName());
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onEditUserInfoDoneEvent(EditUserInfoDone event) {
        if (event.isDone()){
            try{

                currentLoginUser = ParseUser.getCurrentUser();
                userName.setText(currentLoginUser.getUsername());
                loginHint.setVisibility(View.GONE);
                userType.setText(UserType.values()[currentLoginUser.getInt("userType")].getName());
                String img = currentLoginUser.getString("imgUrl");
                if (img == null){
                    img = Constants.URL_DEFAULT_MAN_IMG;
                }
                userImg.setImageURI(Uri.parse(img));

                FocusMapOpPresenterImpl mFocusMapOpPresenter = new FocusMapOpPresenterImpl(this,this);
                mFocusMapOpPresenter.queryFansNum(currentLoginUser.getObjectId());
            }catch (Exception e){

            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onToolbarItemClickEvent(ToolbarItemClickEvent event) {
        int id = event.getItemId();

        //点击二维码扫描
        if (id == R.id.fragment_home_right_btn) {

            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            }else {
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivedUnreadMsg(UnreadMsgEvent event){
      /*  int count = event.getUnreadMsg();
        if (count > 0 ) {
            chatTab = mBottomBar.getTabWithId(R.id.tab_chat);
            chatTab.setBadgeCount("");
        }
        else {
            if (chatTab != null)
                chatTab.removeBadge();
        }*/
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivedFriendVerifyMsg(UnreadFriendVerifyNum event){
        int count = event.getFriendVerifyNum();
        if (count > 0 && count < 100){
            contactTab = mBottomBar.getTabWithId(R.id.tab_contact);
            contactTab.setBadgeCount(count);
        }
        else if (count >= 100 ){
            contactTab = mBottomBar.getTabWithId(R.id.tab_chat);
            contactTab.setBadgeCount(99);
        }
        else {
            if (contactTab != null)
                contactTab.removeBadge();
        }
    }


    @Override
    public void onFocusActionError(boolean flag) {

    }

    @Override
    public void onQueryFansOrFocusDone(boolean isFocus, List<UserFocusMap> userFocusList) {

    }

    @Override
    public void onGetFansNumDone(int fansNum) {
        currentLoginUser.put("fansNum",fansNum);
        currentLoginUser.saveInBackground();
    }

    @Override
    public void onQueryMyFansDone(List<User> fansList) {

    }

    @Override
    public void onQueryMyFocusDone(List<User> focusList) {

    }

    @Override
    public void onAllMajorInfoGot(List<MajorModel> mMajorModelList) {
        if (mMajorModelList.size() == 0){
            //todo 如果获取的专业数据为空，则要禁止首页的专业点击跳转动作

            return;
        }

        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        MajorFIlesDao majorFIlesDao = new MajorFIlesDao(this);

        majorFIlesDao.init(mMajorModelList);

        Log.d(TAG,"init database");

        editor.putBoolean("init_database",true);
        Date updateTime = new Date();
        editor.putLong("update_time", updateTime.getTime());
        editor.apply();


        initMajorListViewData(mMajorModelList);

    }

    @Override
    public void onUpdateMajorInfoGot(List<MajorModel> mMajorModelList) {
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        MajorFIlesDao majorFIlesDao = new MajorFIlesDao(this);

        if (mMajorModelList.size() != 0){
            //如果获取的专业数据为空，没有需要更新的专业
            majorFIlesDao.updateMajorInfo(mMajorModelList);

            Log.d(TAG,"update database");

            editor.putBoolean("init_database",true);
            Date updateTime = new Date();
            editor.putLong("update_time", updateTime.getTime());
            editor.apply();
        }

        //初始化grouplist和更新后的itemList
        groupList = new ArrayList<>(Arrays.asList(MajorData.majorGroup));
        if (itemsList.size() > 0){
            itemsList.clear();
        }

        for (int i = 0; i < groupList.size(); i++) {
            List<String> majorList = new ArrayList<>();
            Cursor majorCursor = majorFIlesDao.selectByMajorClass(Constants.majorsTableName[i]);
            if (majorCursor.moveToFirst()){
                do {
                    String majorName = majorCursor.getString(majorCursor.getColumnIndex("major_name"));
                    majorList.add(majorName);
                }while (majorCursor.moveToNext());

                itemsList.add(majorList);
            }

            if (null != majorCursor) {
                majorCursor.close();
            }
        }


    }

    @Override
    public void onGetMajorInfoError(Exception e) {
        //todo 如果获取的专业数据失败，则要禁止首页的专业点击跳转动作

    }


    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetworkUtil.isNetworkAvailable(mContext)){

                // 当前所连接的网络可用
                isNetworkUseful = true;
                EventBus.getDefault().post(new NetworkChangeEvent(true));
                initUserCloudInfo();

            }
            else {
                isNetworkUseful = false;
                EventBus.getDefault().post(new NetworkChangeEvent(false));
            }

        }


    }

    private void initUserCloudInfo() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isNetworkUseful && currentLoginUser != null) {
                /*刷新粉丝数量*/
                    FocusMapOpPresenterImpl mFocusMapOpPresenter = new FocusMapOpPresenterImpl(MainActivity.this, mContext);
                    mFocusMapOpPresenter.queryFansNum(currentLoginUser.getObjectId());
                    try {
                        currentLoginUser.fetch();
                        String img = currentLoginUser.getString("imgUrl");
                        SharePreferenceManager.putString(mContext,Constants.DB_USERIMG_ONLINE, img);
                    } catch (ParseException mE) {
                        mE.printStackTrace();
                    }
                }
            }
        }).start();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if (new Date().getTime() - lastPressTime < 2000)
                ActivityCollector.finishAll();
            else{
                lastPressTime = new Date().getTime();
                showToast("再按一次返回键退出");
            }
            return true;

        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                }
                else {
                    showToast("相机被禁用，二维码扫描失败");
                }
                break;
            default:
                break;
        }
    }

    public void editUserImg(){
        showImgSelectDialog();
    }

    private void showImgSelectDialog() {

        final Dialog dialog = new Dialog(this, R.style.jmui_default_dialog_style);
        final LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_img_select, null);
        dialog.setContentView(view);
        dialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        RelativeLayout albumBtn = (RelativeLayout) view.findViewById(R.id.album_btn);
        RelativeLayout cameraBtn = (RelativeLayout) view.findViewById(R.id.camera_btn);

        View.OnClickListener listener = new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                switch (v.getId()){

                    case R.id.album_btn:

                        mImageUploader.chooseUserImg(UploadImgType.IMG_AVATAR);
                        dialog.dismiss();

                        break;

                    case R.id.camera_btn:
                        mImageUploader.takePhoto(UploadImgType.IMG_AVATAR);
                        dialog.dismiss();
                        break;

                }
            }
        };


        albumBtn.setOnClickListener(listener);
        cameraBtn.setOnClickListener(listener);
    }

    @Override
    public void imagePermissionRefused() {
        showToast("照片获取请求被拒绝，请手动开启");
    }

    @Override
    public void imageUploadSuccess(String imgUrl, Uri localPath) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(getApplicationContext()).load(localPath).into(userImg);
            }
        });


    }

    @Override
    public void imageUploadFailed(Exception e) {
        showToast("图片上传失败");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case Constants.REQUEST_CODE_CHOOSE:
                if (data == null)
                    break;
                mImageUploader.startCrop(data);
                break;
            case UCrop.REQUEST_CROP:
                if (data == null)
                    break;
                mImageUploader.imageUpLoad(data);
                break;
            case Constants.REQUEST_CODE_TAKE_PHOTO:
                mImageUploader.startCrop();
                break;

            case 1:
                /**
                 * 处理二维码扫描结果
                 */
                //处理扫描结果（在界面上显示）
                if (null != data) {
                    Bundle bundle = data.getExtras();
                    if (bundle == null) {
                        return;
                    }
                    if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                        String result = bundle.getString(CodeUtils.RESULT_STRING);
                        showToast("解析结果:" + result);
                    } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                        showToast("解析二维码失败");
                    }
                }
                break;
            default:
        }


    }
}
