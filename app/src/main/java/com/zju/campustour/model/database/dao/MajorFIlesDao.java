package com.zju.campustour.model.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.bean.MajorModel;

import java.util.List;


public class MajorFIlesDao {
    private String TAG = getClass().getSimpleName();

    public DBHelper dbHelper;
    private SQLiteDatabase db;

    public MajorFIlesDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void init(List<MajorModel> mMajorModelList){
       /* if (isEqual(new int[]{MajorData.allMajorAbstracts.length,MajorData.allMajorClass.length,MajorData.allMajorCodes.length,MajorData.allMajorNames.length})){
            for (int i = 0;i<MajorData.allMajorNames.length;i++){

                String[] majorNames = (String[])MajorData.allMajorNames[i];
                String[] majorCodes = (String[])MajorData.allMajorCodes[i];
                int[] majorClass = (int[])MajorData.allMajorClass[i];
                String[] majorAbstracts = (String[])MajorData.allMajorAbstracts[i];

                if (isEqual(new int[]{majorAbstracts.length,majorClass.length,majorCodes.length,majorNames.length})){
                    for (int j=0;j<majorNames.length;j++){
                        Major major = new Major(majorAbstracts[j],majorClass[j],majorCodes[j],majorNames[j]);

                        add(major, Constants.majorsTableName[i]);
                    }
                }
            }
        }*/

        for (int i = 0; i < mMajorModelList.size(); i++){
            MajorModel mMajorModel = mMajorModelList.get(i);
            add(mMajorModel, Constants.majorsTableName[mMajorModel.getMajorClass()]);
        }

        Log.d("databaseinit","major database init");

    }

    public void updateMajorInfo(List<MajorModel> mMajorModelList){
        for (int i = 0; i < mMajorModelList.size(); i++){
            MajorModel mMajorModel = mMajorModelList.get(i);
            update(mMajorModel, Constants.majorsTableName[mMajorModel.getMajorClass()]);
        }

        Log.d("databaseinit","major database update");
    }

    /**
     * 添加专业
     * @param
     */
    public void add(MajorModel major,String tableName){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        //开始组装第一条数据
        values.put("major_class",major.getMajorClass());
        values.put("major_name",major.getName());
        values.put("major_code",major.getMajorCode());
        values.put("major_abstract",major.getMajorAbstract());
        values.put("major_img",major.getImgUrl());
        values.put("major_interests",major.getInterests());

        db.insert(tableName, null, values);

        Log.d(TAG, "---insert data success!---");
    }

    /**
     * 删除专业
     * @param
     */
    public void delete(String majorName,String tableName){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "delete from "+tableName+" where major_name = ?";

        db.execSQL(sql, new String[]{majorName});

        Log.d(TAG, "---delete major success!---");
    }

    public Boolean update(MajorModel major,String tableName){
        try {
            //得到数据库
            db = dbHelper.getWritableDatabase();

            String querySql = "select * from "+ tableName+" where major_name = ?";
            Cursor cursor = db.rawQuery(querySql, new String[]{major.getName()});

            if (cursor != null && cursor.getCount() > 0) {
                //更新操作
                String sql = "update " + tableName + " set major_class = ?,major_code = ?,major_abstract=?,major_img=?, major_interests = ? where major_name = ?";
                db.execSQL(sql, new Object[]{major.getMajorClass(),major.getMajorCode(),major.getMajorAbstract(),major.getImgUrl(),major.getInterests(),major.getName()});

            }
            else{
                //插入操作
                add(major,tableName);
            }

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public Cursor selectByMajorName(String majorName,String tableName){
        db = dbHelper.getReadableDatabase();
        String sql = "select * from "+ tableName+" where major_name = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{majorName});

        return cursor;
    }

    public Cursor selectByMajorClass(String tableName){
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(tableName,null,null,null,null,null,null);
        return cursor;
    }

    private Boolean isEqual(int[] a){
        for (int i = 1;i<a.length;i++){
            if (a[i] != a[0]){
                Log.d(TAG,"the lengts are not equal");
                return false;
            }
        }
        return true;
    }
}
