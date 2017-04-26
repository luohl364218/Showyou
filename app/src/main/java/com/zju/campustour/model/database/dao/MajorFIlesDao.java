package com.zju.campustour.model.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.database.data.MajorData;
import com.zju.campustour.model.database.models.Major;


public class MajorFIlesDao {
    private String TAG = getClass().getSimpleName();

    public DBHelper dbHelper;
    private SQLiteDatabase db;

    public MajorFIlesDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void init(){
        if (isEqual(new int[]{MajorData.allMajorAbstracts.length,MajorData.allMajorClass.length,MajorData.allMajorCodes.length,MajorData.allMajorNames.length})){
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
        }

        Log.d("databaseinit","major database init");

//        if (isEqual(new int[]{MajorData.zhexueMajorNames.length,MajorData.zhexueMajorAbstracts.length,MajorData.zhexueMajorClass.length,MajorData.zhexueMajorcodes.length})) {
//            for (int i = 0; i < MajorData.zhexueMajorNames.length; i++) {
//                Major major = new Major(MajorData.zhexueMajorAbstracts[i],MajorData.zhexueMajorClass[i],MajorData.zhexueMajorcodes[i],MajorData.zhexueMajorNames[i]);
//
//                //add(major,Constants.zhexue);
//            }
//        }

    }

    /**
     * 添加专业
     * @param
     */
    public void add(Major major,String tableName){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        //开始组装第一条数据
        values.put("major_class",major.getMajorClass());
        values.put("major_name",major.getMajorName());
        values.put("major_code",major.getMajorCode());
        values.put("major_abstract",major.getMajorAbstract());

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

    public Boolean update(Major major,String tableName){
        try {
            //得到数据库
            db = dbHelper.getWritableDatabase();
            String sql = "update " + tableName + " set major_class = ?,major_code = ?,major_abstract=? where major_name = ?";
            db.execSQL(sql, new Object[]{major.getMajorClass(),major.getMajorCode(),major.getMajorAbstract(),major.getMajorName()});

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
