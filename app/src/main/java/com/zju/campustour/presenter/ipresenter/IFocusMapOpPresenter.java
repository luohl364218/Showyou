package com.zju.campustour.presenter.ipresenter;

/**
 * Created by HeyLink on 2017/4/24.
 */

public interface IFocusMapOpPresenter {

    public void addFocusOn(int fromId, int toId);
    public void cancelFocusOn(int fromId, int toId);
    public int getFansNum(int userId);

}
