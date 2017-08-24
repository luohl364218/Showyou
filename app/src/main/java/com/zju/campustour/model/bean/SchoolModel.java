package com.zju.campustour.model.bean;

/**
 * Created by HeyLink on 2017/5/15.
 */

public class SchoolModel {

    private String name;
    private String tag;

    public SchoolModel() {
        super();
    }

    public SchoolModel(String name, String tag) {
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
        return "SchoolModel [name=" + name + ", tag=" + tag + "]";
    }

}
