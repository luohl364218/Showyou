package com.zju.campustour.presenter.ipresenter;

/**
 * 对话题标签进行操作,创建话题以及由后台执行
 * Created by HeyLink on 2017/8/23.
 */

public interface ILabelInfoOpPresenter {

    void getRecommendLabel(int start, int count);

    void searchLabel(String label);


}
