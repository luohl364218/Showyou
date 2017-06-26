package com.zju.campustour.model.database.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.zju.campustour.model.common.Constants;


/**
 * Created by Administrator on 2016/7/6.
 */
public class DBHelper extends SQLiteOpenHelper {

    String TAG = getClass().getSimpleName();

    public DBHelper(Context context) {
        super(context,Constants.DB_NAME,null,1);
    }

    //为创建数据库的时候执行（数据库已存在则不执行）
    @Override
    public void onCreate(SQLiteDatabase db) {


        //第一次使用数据库时自动建表

        for (int i = 0;i<Constants.majorsTableName.length;i++) {
            String sql = "create table "+ Constants.majorsTableName[i]
                    +"(_id integer primary key," +
                    "major_class integer," +
                    "major_name String," +
                    "major_code String," +
                    "major_img String,"+
                    "major_interests integer,"+
                    "major_abstract String)";

            db.execSQL(sql);
        }

        Log.d(TAG, "数据库创建成功");
    }

    //版本更新
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG,"-----database onupgrade-----");

        for (int i = 0;i<Constants.majorsTableName.length;i++) {
            String sql = "drop table if exists "+ Constants.majorsTableName[i];
            db.execSQL(sql);
        }

        onCreate(db);
    }
}
