package com.zju.campustour.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.zju.campustour.R;
import com.zju.campustour.model.bean.ProjectItemInfo;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.database.models.Project;
import com.zju.campustour.presenter.implement.ProjectInfoOpPresenterImpl;
import com.zju.campustour.presenter.protocal.event.onLoadingDone;
import com.zju.campustour.presenter.protocal.event.onNetworkChangeEvent;
import com.zju.campustour.view.IView.ISearchProjectInfoView;
import com.zju.campustour.view.activity.InfoWebActivity;
import com.zju.campustour.view.activity.MajorListActivity;
import com.zju.campustour.view.activity.ProviderHomePageActivity;
import com.zju.campustour.view.adapter.RecommendProjectAdapter;
import com.zju.campustour.view.widget.DividerItemDecortion;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.zju.campustour.model.common.Constants.STATE_MORE;
import static com.zju.campustour.model.common.Constants.STATE_NORMAL;
import static com.zju.campustour.model.common.Constants.STATE_REFRESH;
import static com.zju.campustour.model.common.Constants.imageUrls;

/**
 * Created by HeyLink on 2017/4/1.
 */

public class HomeFragment extends Fragment implements View.OnClickListener, ISearchProjectInfoView{

    private View mRootView;
    private SliderLayout mSliderShow;
    //首页科目分类按钮
    private LinearLayout renwenBtn;
    private LinearLayout gongxueBtn;
    private LinearLayout lixueBtn;
    private LinearLayout yixueBtn;
    private LinearLayout nongxueBtn;
    private LinearLayout jingjixueBtn;
    private LinearLayout yishuxueBtn;
    private LinearLayout allSubjectsBtn;
    //两大话题按钮入口
    private Button hotMajorBtn;
    private Button remoteChatBtn;
    //专家推荐列表
    private RecyclerView mRecommendList;
    private RecommendProjectAdapter mProjectAdapter;

    private TextView noResultHint;
    private int state = STATE_NORMAL;

    //获取project信息的接口实现
    private ProjectInfoOpPresenterImpl mProjectInfoPresenter;

    private String TAG = "HomeFragment";
    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null){
            mRootView = inflater.inflate(R.layout.fragment_home, container, false);
            initView();
            initSlideImages();
            mProjectInfoPresenter = new ProjectInfoOpPresenterImpl(this);
            mProjectInfoPresenter.getLimitProjectInfo(0,10);
            EventBus.getDefault().register(this);


        }

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null){
            parent.removeView(mRootView);
        }
        Log.d(TAG,"second create view--------------------");
        return mRootView;
    }

    private void initView() {
        renwenBtn = (LinearLayout)mRootView.findViewById(R.id.fragment_home_renwen);
        gongxueBtn = (LinearLayout)mRootView.findViewById(R.id.fragment_home_gongxue);
        lixueBtn = (LinearLayout)mRootView.findViewById(R.id.fragment_home_lixue);
        yixueBtn = (LinearLayout)mRootView.findViewById(R.id.fragment_home_yixue);
        nongxueBtn = (LinearLayout)mRootView.findViewById(R.id.fragment_home_nongxue);
        jingjixueBtn = (LinearLayout)mRootView.findViewById(R.id.fragment_home_jingjixue);
        yishuxueBtn = (LinearLayout)mRootView.findViewById(R.id.fragment_home_yishuxue);
        allSubjectsBtn = (LinearLayout)mRootView.findViewById(R.id.fragment_home_allsubjects);
        //两大话题按钮入口
        hotMajorBtn = (Button)mRootView.findViewById(R.id.fragment_home_hotmajor_btn);
        remoteChatBtn = (Button)mRootView.findViewById(R.id.fragment_home_remotechat_btn);
        noResultHint = (TextView) mRootView.findViewById(R.id.fragment_home_noResult_hint);
        //专家推荐列表
        mRecommendList = (RecyclerView)mRootView.findViewById(R.id.fragment_home_recycle_view);

        renwenBtn.setOnClickListener(this);
        gongxueBtn.setOnClickListener(this);
        lixueBtn.setOnClickListener(this);
        nongxueBtn.setOnClickListener(this);
        jingjixueBtn.setOnClickListener(this);
        yishuxueBtn.setOnClickListener(this);
        yixueBtn.setOnClickListener(this);
        allSubjectsBtn.setOnClickListener(this);

        hotMajorBtn.setOnClickListener(this);
        remoteChatBtn.setOnClickListener(this);
    }

    private void initSlideImages() {
        mSliderShow = (SliderLayout) mRootView.findViewById(R.id.slider);

        for (int i = 0; i < Constants.imageUrls.length; i++) {
            TextSliderView slideView = new TextSliderView(this.getActivity());
            slideView.image(Constants.imageUrls[i]);
            slideView.description(Constants.imageContentDescs[i]);
            slideView.setScaleType(BaseSliderView.ScaleType.Fit);
            int position = i;
            slideView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    Intent intent = new Intent(getActivity(), InfoWebActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("web",Constants.imagewebs[(position + imageUrls.length )%imageUrls.length]);
                    intent.putExtras(bundle);

                    getActivity().startActivity(intent);
                }
            });
            mSliderShow.addSlider(slideView);
        }

        mSliderShow.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        mSliderShow.startAutoCycle();
        mSliderShow.setDuration(3000);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mSliderShow!=null){
            mSliderShow.startAutoCycle();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mSliderShow!=null){
            mSliderShow.stopAutoCycle();
        }
    }

    @Override
    public void onClick(View v) {
        int btnId = v.getId();

        switch (btnId){
            case R.id.fragment_home_renwen:
                startActivityWithData(4);
                break;
            case R.id.fragment_home_gongxue:
                startActivityWithData(7);
                break;
            case R.id.fragment_home_lixue:
                startActivityWithData(6);
                break;
            case R.id.fragment_home_nongxue:
                startActivityWithData(8);
                break;
            case R.id.fragment_home_yixue:
                startActivityWithData(9);
                break;
            case R.id.fragment_home_jingjixue:
                startActivityWithData(1);
                break;
            case R.id.fragment_home_yishuxue:
                startActivityWithData(11);
                break;
            case R.id.fragment_home_allsubjects:
                //全部专业
                startActivityWithData(100);
                break;

            case R.id.fragment_home_hotmajor_btn:
                Toast.makeText(getActivity(), "Sorry 此功能我们将在5月初完善", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fragment_home_remotechat_btn:
                Toast.makeText(getActivity(), "Sorry 此功能我们将在5月初完善", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;

        }
    }


    /**
     * 跳转到对应的专业列表处
     * @param position
     */
    void startActivityWithData(int position){

        Intent intent = new Intent(getActivity(),MajorListActivity.class);
        intent.putExtra("position",position);

        startActivity(intent);
    }

    /**
     * 云端获取project数据后显示
     * @param mProjects
     */
    @Override
    public void onGetProjectInfoDone(List<Project> mProjects) {
        //mRecommendList
        if (mProjects.size() != 0){
            noResultHint.setVisibility(View.GONE);
            if (mProjectAdapter != null)
                state = STATE_REFRESH;
            else
                state = STATE_NORMAL;
            showProjectRecycleView(mProjects);
        }
        else {
            //showLocalProjectItemInfoData();
            noResultHint.setVisibility(View.VISIBLE);
            noResultHint.setText("请检查网络连接是否断开");

        }
    }

    private void showProjectRecycleView(List<Project> projectList) {


        switch (state) {


            case Constants.STATE_NORMAL:
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()) {
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                };
                if (projectList == null)
                    return;

                mProjectAdapter = new RecommendProjectAdapter(projectList, Constants.FULL_VIEW);
                mProjectAdapter.setOnProjectItemClickListener(new RecommendProjectAdapter.onProjectItemClickListener() {
                    @Override
                    public void onClick(View v, int position, Project project) {
                        Intent mIntent = new Intent(getActivity(), ProviderHomePageActivity.class);
                        mIntent.putExtra("provider", project.getProvider());
                        startActivity(mIntent);
                    }
                });
                mRecommendList.setLayoutManager(layoutManager);
                mRecommendList.setAdapter(mProjectAdapter);
                mRecommendList.addItemDecoration(new DividerItemDecortion());
                Log.d(TAG, "------------loading project info done----------");
                EventBus.getDefault().post(new onLoadingDone(true));
                break;
            case STATE_REFRESH:
                mProjectAdapter.clearData();
                mProjectAdapter.addData(projectList);
                mRecommendList.scrollToPosition(0);
                break;
            case STATE_MORE:
                break;
            default:
                break;

        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onNetworkChangeEvent(onNetworkChangeEvent event) {
        if (event.isValid()){
            noResultHint.setVisibility(View.GONE);

           /* mProjectInfoPresenter.getLimitProjectInfo(0,10);*/
        }
        else {
            noResultHint.setVisibility(View.VISIBLE);
            noResultHint.setText("网络连接已经断开");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//解除订阅
    }
}
