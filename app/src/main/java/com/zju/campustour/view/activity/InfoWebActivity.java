package com.zju.campustour.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zju.campustour.R;


public class InfoWebActivity extends BaseActivity{
    private String web;

    WebView mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infoweb);
        mWebView = (WebView) findViewById(R.id.activity_infoweb_webview);
        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            getBundleExtras(extras);
        }

        initViewsAndEvents();
    }


    protected void getBundleExtras(Bundle extras) {
        web = extras.getString("web");
    }

    protected void initViewsAndEvents() {

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
        //加载指定url链接
        mWebView.loadUrl(web);

    }

}
