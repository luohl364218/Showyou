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
import com.zju.campustour.model.database.models.Project;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.presenter.protocal.enumerate.ProjectStateType;
import com.zju.campustour.presenter.protocal.enumerate.SexType;
import com.zju.campustour.presenter.protocal.enumerate.UserType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.zju.campustour.presenter.protocal.enumerate.ProjectStateType.BOOK_ACCEPT;
import static com.zju.campustour.presenter.protocal.enumerate.ProjectStateType.BOOK_STOP;
import static com.zju.campustour.presenter.protocal.enumerate.ProjectStateType.PROJECT_RUNNING;
import static com.zju.campustour.presenter.protocal.enumerate.ProjectStateType.PROJECT_STOP;

/**
 * Created by HeyLink on 2017/4/26.
 */

public class DbUtils {

    @NonNull
    public static User getUser(ParseObject user) {
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

    @NonNull
    public static Project getProject(ParseObject project) {
        String id = project.getObjectId();
        User provider;
        ParseObject providerObject = project.getParseObject("provider");
        if (providerObject != null)
            provider = getUser(providerObject);
        else
            provider = null;
        String title = project.getString("title");
        Date startTime = project.getDate("startTime");
        String imgUrl = project.getString("imgUrl");
        long price = project.getLong("price");
        String description = project.getString("description");
        int acceptNum = project.getInt("acceptNum");
        ProjectStateType projectState;
        switch (project.getInt("projectState")) {
            case 0:
                projectState = BOOK_ACCEPT;
                break;
            case 1:
                projectState = BOOK_STOP;
                break;
            case 2:
                projectState = PROJECT_RUNNING;
                break;
            case 3:
                projectState = PROJECT_STOP;
                break;
            default:
                projectState = BOOK_ACCEPT;
        }

        List<ParseObject> jsonFavorites = project.getList("favorites");
        List<User> favorites;
        if (jsonFavorites != null){
            favorites = new ArrayList<User>();

            for(ParseObject user: jsonFavorites){
                User mUser = getUser(user);
                favorites.add(mUser);
            }
        }
        else {
            favorites = null;
        }

        return new Project(id, provider, title, startTime,
                imgUrl, price, description, acceptNum, projectState, favorites);
    }
}
