package com.zju.campustour.model.database.data;

import java.util.List;

/**
 * Created by HeyLink on 2017/5/15.
 */

public class ProvinceWithCollegeModel {
    private String name;
    private List<SchoolModel> schoolList;

    public ProvinceWithCollegeModel() {
        super();
    }

    public ProvinceWithCollegeModel(String name, List<SchoolModel> schoolList) {
        super();
        this.name = name;
        this.schoolList = schoolList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SchoolModel> getSchoolList() {
        return schoolList;
    }

    public void setSchoolList(List<SchoolModel> schoolList) {
        this.schoolList = schoolList;
    }

    @Override
    public String toString() {
        return "ProvinceWithCollegeModel [name=" + name + ", schoolList=" + schoolList + "]";
    }




}
