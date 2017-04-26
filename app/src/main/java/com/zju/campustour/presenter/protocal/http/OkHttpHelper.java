package com.zju.campustour.presenter.protocal.http;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by HeyLink on 2017/4/18.
 */

public class OkHttpHelper {

    private static OkHttpClient sOkHttpClient;
    private Gson mGson;
    private Handler mHandler;

    private OkHttpHelper(){

        sOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10,TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .build();

        mGson = new Gson();
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static OkHttpHelper getInstance(){
        return new OkHttpHelper();
    }

    public void get(String url, BaseHttpCallback callback){
        Request mRequest = buildRequest(url,null,HttpMethodType.GET);
        doRequest(mRequest, callback);
    }

    public void post(String url, Map<String,String> params, BaseHttpCallback callback){
        Request mRequest = buildRequest(url, params,HttpMethodType.POST);
        doRequest(mRequest, callback);

    }

    private void doRequest(Request request, BaseHttpCallback callback){

        //弹出loading对话框等
        callback.onRequestBefore(request);

        sOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                callbackFailure(callback,call.request(),e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()){
                    String resultStr = response.body().string();
                    if (callback.mType == String.class){

                        callbackSuccess(callback, response, resultStr);
                    }
                    else {
                        try {
                            Object object = mGson.fromJson(resultStr, callback.mType);
                            callbackSuccess(callback, response, object);

                        } catch (JsonParseException e) {

                            callbackError(callback,response,e);

                        }
                    }
                }
                else {
                    callbackError(callback,response,null);
                }
            }
        });
    }


    private Request buildRequest(String url, Map<String, String> params, HttpMethodType mType){

        Request.Builder mBuilder = new Request.Builder();

        mBuilder.url(url);

        if (mType == HttpMethodType.GET){
            mBuilder.get();
        }else if (mType == HttpMethodType.POST){

            FormBody mBody = buildFormBody(params);
            mBuilder.post(mBody);
        }

        return mBuilder.build();
    }

    private FormBody buildFormBody(Map<String, String> params){

        FormBody.Builder mBuilder = new FormBody.Builder();

        if ( params!= null)
            params.forEach(mBuilder::add);

        return mBuilder.build();
    }

    private void callbackSuccess(BaseHttpCallback callback, Response response, Object object){
        mHandler.post(()->{
           callback.onSuccess(response,object);
        });
    }

    private void callbackError(BaseHttpCallback callback, Response response, Exception e){
        mHandler.post(()->{
            callback.onError(response, response.code(), e);
        });
    }

    private void callbackFailure(BaseHttpCallback callback, Request request, Exception e){
        mHandler.post(()->{
            callback.onFailure(request, e);
        });
    }


    private enum HttpMethodType{
        GET,
        POST
    }

}
