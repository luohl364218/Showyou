package com.zju.campustour.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zju.campustour.R;


public class InfoWebActivity extends BaseActivity{
    private String web;

    WebView mWebView;

    Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infoweb);
        mWebView = (WebView) findViewById(R.id.activity_infoweb_webview);
        mToolbar = (Toolbar) findViewById(R.id.webview_toolbar);

        Intent mIntent = getIntent();
        web = mIntent.getStringExtra("web");
        if (web == null)
            finish();

        initViewsAndEvents();
    }

    protected void initViewsAndEvents() {

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //设置支持js  默认为false (解决了一部分显示不全的问题)
        mWebView.getSettings().setJavaScriptEnabled(true);
        //在App内部打开链接  不调用系统浏览器
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        mWebView.getSettings().setDomStorageEnabled(true);
        //调用页面如何使页面适应控件大小 ((解决了一部分显示不全的问题))
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBlockNetworkImage(false);
        mWebView.getSettings().setBuiltInZoomControls(true);
        //加载指定url链接
        if (isNetworkUseful)
            mWebView.loadUrl(web);

    }

}
