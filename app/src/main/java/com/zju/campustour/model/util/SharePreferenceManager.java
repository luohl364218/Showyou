package com.zju.campustour.model.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * SharedPreference操作类
 */
public class SharePreferenceManager {
    static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    static final SimpleDateFormat sdf_detail = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static String ConvertDateToString(Date mDate){
        return sdf.format(mDate);
    }

    public static String ConvertDateToDetailString(Date mDate){
        return sdf_detail.format(mDate);
    }

    private static final String spFileName = "Xiaoyou";


    public static String getString(Context context, String strKey) {
        SharedPreferences setPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        return setPreferences.getString(strKey, null);
    }

    public static String getString(Context context, String strKey,
                                   String strDefault) {
        SharedPreferences setPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        return setPreferences.getString(strKey, strDefault);
    }

    public static void putString(Context context, String strKey, String strData) {
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putString(strKey, strData);
        editor.apply();
    }

    public static Boolean getBoolean(Context context, String strKey) {
        SharedPreferences setPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        return setPreferences.getBoolean(strKey, false);
    }

    public static Boolean getBoolean(Context context, String strKey,
                                     Boolean strDefault) {
        SharedPreferences setPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        return setPreferences.getBoolean(strKey, strDefault);
    }


    public static void putBoolean(Context context, String strKey,
                                  Boolean strData) {
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putBoolean(strKey, strData);
        editor.apply();
    }

    public static int getInt(Context context, String strKey) {
        SharedPreferences setPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        return setPreferences.getInt(strKey, -1);
    }

    public static int getInt(Context context, String strKey, int strDefault) {
        SharedPreferences setPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        return setPreferences.getInt(strKey, strDefault);
    }

    public static void putInt(Context context, String strKey, int strData) {
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putInt(strKey, strData);
        editor.apply();
    }

    public static long getLong(Context context, String strKey) {
        SharedPreferences setPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        return setPreferences.getLong(strKey, -1);
    }

    public static long getLong(Context context, String strKey, long strDefault) {
        SharedPreferences setPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        return setPreferences.getLong(strKey, strDefault);
    }

    public static void putLong(Context context, String strKey, long strData) {
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putLong(strKey, strData);
        editor.apply();
    }

    private static final String KEY_CACHED_FIX_PROFILE_FLAG = "fixProfileFlag";

    public static void setCachedFixProfileFlag(Context context, boolean value) {
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        if(null != activityPreferences){
            activityPreferences.edit().putBoolean(KEY_CACHED_FIX_PROFILE_FLAG, value).apply();
        }
    }

    public static boolean getCachedFixProfileFlag(Context context){
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        return null != activityPreferences && activityPreferences.getBoolean(KEY_CACHED_FIX_PROFILE_FLAG, false);
    }

    private static final String SOFT_KEYBOARD_HEIGHT = "SoftKeyboardHeight";

    public static void setCachedKeyboardHeight(Context context,int height){
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        if(null != activityPreferences){
            activityPreferences.edit().putInt(SOFT_KEYBOARD_HEIGHT, height).apply();
        }
    }

    public static int getCachedKeyboardHeight(Context context){
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        if(null != activityPreferences){
            return activityPreferences.getInt(SOFT_KEYBOARD_HEIGHT, 0);
        }
        return 0;
    }

    private static final String WRITABLE_FLAG = "writable";
    public static void setCachedWritableFlag(Context context,boolean value){
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        if(null != activityPreferences){
            activityPreferences.edit().putBoolean(WRITABLE_FLAG, value).apply();
        }
    }

    public static boolean getCachedWritableFlag(Context context){
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        return null == activityPreferences || activityPreferences.getBoolean(WRITABLE_FLAG, true);
    }

    private static final String CACHED_APP_KEY = "CachedAppKey";

    public static void setCachedAppKey(Context context,String appKey) {
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        if (null != activityPreferences) {
            activityPreferences.edit().putString(CACHED_APP_KEY, appKey).apply();
        }
    }

    public static String getCachedAppKey(Context context) {
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        if (null != activityPreferences) {
            return activityPreferences.getString(CACHED_APP_KEY, "default");
        }
        return "default";
    }

    private static final String CACHED_NEW_FRIEND = "CachedNewFriend";

    public static void setCachedNewFriendNum(Context context,int num) {
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        if (null != activityPreferences) {
            activityPreferences.edit().putInt(CACHED_NEW_FRIEND, num).apply();
        }
    }

    public static int getCachedNewFriendNum(Context context) {
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        if (null != activityPreferences) {
            return activityPreferences.getInt(CACHED_NEW_FRIEND, 0);
        }
        return 0;
    }
}
