package com.zju.campustour.view.chatting.listener;


import com.zju.campustour.model.chatting.entity.FileType;

public interface UpdateSelectedStateListener {
    public void onSelected(String path, long fileSize, FileType type);
    public void onUnselected(String path, long fileSize, FileType type);
}
