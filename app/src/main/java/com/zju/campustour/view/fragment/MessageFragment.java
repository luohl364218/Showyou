package com.zju.campustour.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.zju.campustour.R;
import com.zju.campustour.model.database.models.NewsModule;
import com.zju.campustour.presenter.implement.NewsGetImpl;
import com.zju.campustour.view.IView.INewsShowView;
import com.zju.campustour.view.activity.InfoWebActivity;
import com.zju.campustour.view.adapter.NewsAdapter;
import com.zju.campustour.view.widget.DividerItemDecortion;

import java.util.List;

/**
 * Created by HeyLink on 2017/4/1.
 */

public class MessageFragment extends BaseFragment implements INewsShowView {

    private View mRootView;
    private String TAG = "MessageFragment";

    private Toolbar newsToolbar;
    private RecyclerView newsItem;
    private NewsAdapter mNewsAdapter;
    private NewsGetImpl mNewsGet;

    public MessageFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null){
            mRootView = inflater.inflate(R.layout.fragment_message, container, false);
            Log.d(TAG,"first create view--------------------");
            initFragmentView();
            mNewsGet = new NewsGetImpl(this,getContext());
            mNewsGet.getNewsInfo();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null){
            parent.removeView(mRootView);
        }

        return mRootView;
    }

    private void initFragmentView() {
        newsToolbar = (Toolbar) mRootView.findViewById(R.id.info_fragment_toolbar);
        newsItem = (RecyclerView) mRootView.findViewById(R.id.fragment_news_recycle_view);
        newsToolbar.setTitle("");
        newsToolbar.setNavigationIcon(R.mipmap.icon_user_default);
        newsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                if (!drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

    }


    @Override
    public void onGetNewsDone(List<NewsModule> mNewsModules) {

        if (mNewsModules.size() > 0){
            mNewsAdapter = new NewsAdapter(mNewsModules);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

            mNewsAdapter.setOnCardViewItemClickListener(new NewsAdapter.onCardViewItemClickListener() {
                @Override
                public void onClick(View v, String url) {
                    Intent mIntent = new Intent(getActivity(), InfoWebActivity.class);
                    mIntent.putExtra("web",url);
                    startActivity(mIntent);
                }
            });

            newsItem.setLayoutManager(layoutManager);
            newsItem.setAdapter(mNewsAdapter);
            newsItem.addItemDecoration(new DividerItemDecortion());
        }

    }
}
