package com.zju.campustour.model.util;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.parse.ParseObject;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.presenter.protocal.enumerate.SexType;
import com.zju.campustour.presenter.protocal.enumerate.UserType;

/**
 * Created by HeyLink on 2017/4/26.
 */

public class DbUtils {

    @NonNull
    public User getUser(ParseObject user) {
        String user_id = user.getObjectId();
        String userName = user.getString("userName");
        String loginName = user.getString("loginName");
        String password =user.getString("password");
        SexType sex = user.getInt("sex") == 0? SexType.MALE : SexType.FEMALE;
        String school = user.getString("school");
        String major = user.getString("major");
        String grade = user.getString("grade");
        int fansNum = user.getInt("fansNum");
        boolean online = user.getBoolean("online");
        String user_imgUrl = user.getString("imgUrl");
        ;
        String phoneNum = user.getString("phoneNum");
        String emailAddr = user.getString("emailAddr");
        UserType userType = user.getInt("userType") == 0? UserType.USER: UserType.PROVIDER;
        String user_description = user.getString("description");
        String shortDescription = user.getString("shortDescription");

        return new User(user_id,userName, loginName, password, sex,
                school, major, grade, fansNum, online, user_imgUrl, phoneNum, emailAddr,
                userType, user_description, shortDescription);
    }

    public static void setImg(SimpleDraweeView mImg, String url, int length)
    {
        Uri uri = Uri.parse(url);

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(length,length))
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(mImg.getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>())
                .setImageRequest(request).build();
        mImg.setController(controller);
    }
}
