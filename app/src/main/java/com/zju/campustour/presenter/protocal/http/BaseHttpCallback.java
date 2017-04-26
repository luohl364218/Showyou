package com.zju.campustour.presenter.protocal.http;

import com.google.gson.internal.$Gson$Types;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by HeyLink on 2017/4/18.
 */

public abstract class BaseHttpCallback<T> {

    Type mType;

    public BaseHttpCallback(){
        mType = getSuperclassTypeParameter(getClass());
    }

    /**
     * 得到泛型T的实际Type
     */
    static Type getSuperclassTypeParameter(Class<?> subclass){

        /*
         得到带有泛型的类
         返回表示由此类表示的实体（类，接口，原始类型或void）的直接超类的类型。
         如果超类是参数化类型，则返回的Type对象必须准确地反映源代码中使用的实际类型参数。
         如果以前没有创建表示超类的参数化类型。
         请参阅java.lang.reflect.ParameterizedType的声明参数化类型的参数化类型的创建过程的语义。
         如果此类表示Object类，接口，原始类型或void，则返回null。
         如果此对象表示数组类，则返回表示Object类的Class对象。
         */
        Type superClass = subclass.getGenericSuperclass();
        // 如果 superclass指向的对象是Class类的实例，为true
        if (superClass instanceof Class){
            throw new RuntimeException("Missing type parameter.");
        }
        //取出当前类的泛型
        ParameterizedType mParameterizedType = (ParameterizedType) superClass;
        //getActualTypeArguments()[0]中 ‘0’ 代表的是第一个泛型类型。
        return $Gson$Types.canonicalize(mParameterizedType.getActualTypeArguments()[0]);
    }

    public abstract void onRequestBefore(Request request);

    public abstract void onFailure(Request request, Exception e);

    public abstract void onSuccess(Response response, T t);

    public abstract void onError(Response response, int code, Exception e);

}
