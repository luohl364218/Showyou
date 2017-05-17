package com.zju.campustour.model.database.data;

import java.util.List;

/**
 * Created by HeyLink on 2017/5/16.
 */

public class MajorModel {

    private String name;
    private String tag;

    public MajorModel() {
        super();
    }

    public MajorModel(String name, String tag) {
        this.name = name;
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "MajorModel [name=" + name + ", tag=" + tag + "]";
    }



}
