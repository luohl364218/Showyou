package com.zju.campustour.model.database.models;

import java.io.Serializable;

/**
 * Created by HeyLink on 2017/5/3.
 */

public class Comment implements Serializable {

    private String projectId;
    private String commentUserId;
    private String commentContent;
    private double commentScore;
}
