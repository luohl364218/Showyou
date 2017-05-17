package com.zju.campustour.model.database.data;

import java.util.List;

/**
 * Created by HeyLink on 2017/5/16.
 */

public class MajorClass {

    private String name;
    private List<MajorModel> majorList;

    public MajorClass() {
        super();
    }

    public MajorClass(String mName, List<MajorModel> mMajorList) {
        name = mName;
        majorList = mMajorList;
    }

    public String getName() {
        return name;
    }

    public void setName(String mName) {
        name = mName;
    }

    public List<MajorModel> getMajorList() {
        return majorList;
    }

    public void setMajorList(List<MajorModel> mMajorList) {
        majorList = mMajorList;
    }
}
