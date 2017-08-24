package com.zju.campustour.view.activity;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import com.zju.campustour.R;
import com.zju.campustour.model.bean.LabelInfo;
import com.zju.campustour.model.bean.StatusInfoModel;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.presenter.implement.LabelInfoOperator;
import com.zju.campustour.presenter.listener.MyTextWatch;
import com.zju.campustour.presenter.protocal.event.LabelSelectEvent;
import com.zju.campustour.view.adapter.HotUserStatusAdapter;
import com.zju.campustour.view.adapter.LabelInfoAdapter;
import com.zju.campustour.view.iview.ILabelInfoView;
import com.zju.campustour.view.widget.DividerItemDecortion;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LabelSelectActivity extends BaseActivity implements ILabelInfoView {

    @BindView(R.id.search_et)
    EditText searchContent;
    @BindView(R.id.recommend_label_sv)
    ScrollView recommendSv;
    @BindView(R.id.search_label_sv)
    ScrollView searchSv;
    @BindView(R.id.recommend_rv)
    RecyclerView recommendRv;
    @BindView(R.id.search_rv)
    RecyclerView searchRv;
    @BindView(R.id.label_new_btn)
    CardView labelNewBtn;
    @BindView(R.id.new_label_content)
    TextView labelNewContent;
    int state = Constants.STATE_NORMAL;
    boolean isRecommendState = true;

    LabelInfoOperator mLabelInfoOperator;
    LabelInfoAdapter mLabelInfoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_select);
        ButterKnife.bind(this);


        changePrimaryDarkColor();

        mLabelInfoOperator = new LabelInfoOperator(this,this);
        mLabelInfoOperator.getRecommendLabel(0,10);


        searchContent.addTextChangedListener(new MyTextWatch() {
            @Override
            public void afterTextChanged(Editable s) {
                isRecommendState = TextUtils.isEmpty(s.toString().trim());
                state = Constants.STATE_REFRESH;
                if (isRecommendState){
                    mLabelInfoOperator.getRecommendLabel(0,10);
                }
                else {
                    mLabelInfoOperator.searchLabel(s.toString().trim());
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void changePrimaryDarkColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            /*window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);*/
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.darkColorPrimary));   //这里动态修改颜色
        }
    }

    @Override
    public void onLabelInfoGotSuccess(List<LabelInfo> mLabelInfos) {

        if (mLabelInfoAdapter == null)
            mLabelInfoAdapter = new LabelInfoAdapter(mLabelInfos);

        if (state == Constants.STATE_NORMAL){
            mLabelInfoAdapter.setOnItemClickListener(new LabelInfoAdapter.onItemClickListener() {
                @Override
                public void onClick(View v, int position, LabelInfo label) {
                    EventBus.getDefault().post(new LabelSelectEvent(label));
                    finish();
                }
            });
            //初始化推荐
            LinearLayoutManager layoutManager = new LinearLayoutManager(this){
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };

            recommendRv.setLayoutManager(layoutManager);
            recommendRv.setAdapter(mLabelInfoAdapter);
            recommendRv.addItemDecoration(new DividerItemDecortion());
            //初始化搜索

            //初始化推荐
            LinearLayoutManager layoutMgr = new LinearLayoutManager(this){
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            searchRv.setLayoutManager(layoutMgr);
            searchRv.setAdapter(mLabelInfoAdapter);
            searchRv.addItemDecoration(new DividerItemDecortion());


        }

        if (isRecommendState){
            //显示推荐，隐藏搜索
            recommendSv.setVisibility(View.VISIBLE);
            searchSv.setVisibility(View.GONE);

            showRecommendRecycleView(mLabelInfos);
        }
        else {
            recommendSv.setVisibility(View.GONE);
            searchSv.setVisibility(View.VISIBLE);

            showSearchRecycleView(mLabelInfos);
        }

    }

    public void showRecommendRecycleView(List<LabelInfo> mLabelInfos){

        switch (state){

            case Constants.STATE_NORMAL:

                break;

            case Constants.STATE_REFRESH:

                mLabelInfoAdapter.clearData();
                mLabelInfoAdapter.addData(mLabelInfos);
                recommendRv.scrollToPosition(0);

                break;

        }
    }

    public void showSearchRecycleView(List<LabelInfo> mLabelInfos){

        if (mLabelInfos == null || mLabelInfos.size() == 0){
            labelNewBtn.setVisibility(View.VISIBLE);
            labelNewContent.setText(searchContent.getText().toString().trim());
        }
        else {
            labelNewBtn.setVisibility(View.GONE);
        }

        switch (state){

            case Constants.STATE_NORMAL:

                break;

            case Constants.STATE_REFRESH:

                mLabelInfoAdapter.clearData();
                mLabelInfoAdapter.addData(mLabelInfos);
                recommendRv.scrollToPosition(0);

                break;

        }
    }

    @OnClick(R.id.label_new_btn)
    public void onLabelNewBtnClicked(){
        LabelInfo label = new LabelInfo(null,null,labelNewContent.getText().toString().trim(),0);
        EventBus.getDefault().post(new LabelSelectEvent(label));
        finish();
    }


    @Override
    public void onLabelInfoGotFailed(Exception e) {
        showToast("获取话题失败，请稍后再试");
        finish();
    }

    @OnClick(R.id.return_btn)
    public void onReturnBtnClicked(){
        finish();
    }

}
