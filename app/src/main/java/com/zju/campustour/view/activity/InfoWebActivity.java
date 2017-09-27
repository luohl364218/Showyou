package com.zju.campustour.view.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicEngine;
import com.tencent.sonic.sdk.SonicSession;
import com.tencent.sonic.sdk.SonicSessionConfig;
import com.zju.campustour.R;
import com.zju.campustour.model.util.HostSonicRuntime;
import com.zju.campustour.model.util.SonicJavaScriptInterface;
import com.zju.campustour.model.util.SonicSessionClientImpl;
import com.zju.campustour.view.widget.ProgressWebView;


public class InfoWebActivity extends BaseActivity{
    private String web;

    ProgressWebView mWebView;
    WebAppInterface mWebAppInterface;
    private SonicSession sonicSession;
    ImageButton mReturnBtn;
    TextView mTitle;
    Intent mIntent;
    String titleTxt = "校游";
    SonicSessionClientImpl sonicSessionClient = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIntent = getIntent();
        web = mIntent.getStringExtra("web");
        titleTxt = mIntent.getStringExtra("title");
        if (web == null)
            finish();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        // step 1: Initialize sonic engine if necessary, or maybe u can do this when application created
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(new HostSonicRuntime(getApplication()), new SonicConfig.Builder().build());
        }

        // step 2: Create SonicSession
        sonicSession = SonicEngine.getInstance().createSession(web,  new SonicSessionConfig.Builder().build());
        if (null != sonicSession) {
            sonicSession.bindClient(sonicSessionClient = new SonicSessionClientImpl());
        } else {
            // this only happen when a same sonic session is already running,
            // u can comment following codes to feedback as a default mode.
            throw new UnknownError("create session fail!");
        }

        // step 3: BindWebView for sessionClient and bindClient for SonicSession
        // in the real world, the init flow may cost a long time as startup
        // runtime、init configs....
        setContentView(R.layout.activity_infoweb);


        initViewsAndEvents();
    }

    protected void initViewsAndEvents() {
        mWebView = (ProgressWebView) findViewById(R.id.activity_infoweb_webview);
        mReturnBtn = (ImageButton) findViewById(R.id.return_btn);
        mTitle = (TextView) findViewById(R.id.title);

        mReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTitle.setText(titleTxt);

        //在App内部打开链接  不调用系统浏览器
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //隐藏浏览器内的运营商悬浮图标
                view.loadUrl("javascript:document.getElementById('statusholder').remove();");
                view.loadUrl("javascript:document.getElementById('progresstextholder').remove();");
                view.loadUrl("javascript:document.getElementById('ftsiappholder').remove();");
                view.loadUrl("javascript:document.getElementById('tlbstoolbar').remove();");
                if (sonicSession != null) {
                    sonicSession.getSessionClient().pageFinish(url);
                }
                super.onPageFinished(view, url);
            }

            @TargetApi(21)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return shouldInterceptRequest(view, request.getUrl().toString());
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (sonicSession != null) {
                    //step 6: Call sessionClient.requestResource when host allow the application
                    // to return the local data .
                    return (WebResourceResponse) sonicSession.getSessionClient().requestResource(url);
                }
                return null;
            }
        });

        // step 4: bind javascript
        // note:if api level lower than 17(android 4.2), addJavascriptInterface has security
        // issue, please use x5 or see https://developer.android.com/reference/android/webkit/
        // WebView.html#addJavascriptInterface(java.lang.Object, java.lang.String)
        //设置支持js  默认为false (解决了一部分显示不全的问题)
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebAppInterface = new WebAppInterface();
        mWebView.addJavascriptInterface(mWebAppInterface,"app");
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");

        mIntent.putExtra(SonicJavaScriptInterface.PARAM_LOAD_URL_TIME, System.currentTimeMillis());
        mWebView.addJavascriptInterface(new SonicJavaScriptInterface(sonicSessionClient, mIntent), "sonic");

        webSettings.setDomStorageEnabled(true);
        //调用页面如何使页面适应控件大小 ((解决了一部分显示不全的问题))
        // init webview settings
        webSettings.setAllowContentAccess(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBlockNetworkImage(false);
        //加载指定url链接

        // step 5: webview is ready now, just tell session client to bind
        if (sonicSessionClient != null) {
            sonicSessionClient.bindWebView(mWebView);
            sonicSessionClient.clientReady();
        } else { // default mode
            if (isNetworkUseful)
                mWebView.loadUrl(web);
        }

    }


    class WebAppInterface{

        @JavascriptInterface
        public void sayHello(){
            showToast("hello");
        }
    }

    /**
     *
     使点击回退按钮不会直接退出整个应用程序而是返回上一个页面
     *
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()){
            mWebView.goBack();//返回上个页面
            return
                    true;
        }
        return super.onKeyDown(keyCode, event);//退出整个应用程序
    }

    @Override
    protected void onDestroy() {
        if (null != sonicSession) {
            sonicSession.destroy();
            sonicSession = null;
        }
        super.onDestroy();
    }

}
