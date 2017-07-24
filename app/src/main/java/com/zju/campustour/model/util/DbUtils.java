package com.zju.campustour.model.util;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.database.data.MajorModel;
import com.zju.campustour.model.database.models.Comment;
import com.zju.campustour.model.database.models.Project;
import com.zju.campustour.model.database.models.ProjectSaleInfo;
import com.zju.campustour.model.database.models.User;
import com.zju.campustour.presenter.protocal.enumerate.ProjectStateType;
import com.zju.campustour.presenter.protocal.enumerate.SexType;
import com.zju.campustour.presenter.protocal.enumerate.UserType;

import java.util.Date;

import static com.zju.campustour.model.common.Constants.GRADE_HIGH_SCHOOL;
import static com.zju.campustour.model.common.Constants.GRADE_JUNIOR_HIGH_SCHOOL;
import static com.zju.campustour.model.common.Constants.GRADE_PRIMARY_SCHOOL;

/**
 * Created by HeyLink on 2017/4/26.
 */

public class DbUtils {

    @NonNull
    public static User getUser(ParseObject user) {
        String user_id = user.getObjectId();
        String userName = user.getString("username");
        String realName = user.getString("realname");
        String password = "";
        SexType sex = SexType.values()[user.getInt("sex")];

        String major = user.getString("major");
        int fansNum = user.getInt("fansNum");
        boolean online = user.getBoolean("online");
        String user_imgUrl = user.getString("imgUrl");
        String phoneNum = user.getString("phoneNum");
        String emailAddr = user.getString("emailAddr");
        UserType userType = UserType.values()[user.getInt("userType")];
        String user_description = user.getString("description");
        String shortDescription = user.getString("shortDescription");
        int categoryId = user.getInt("categoryId");
        String province = user.getString("province");
        String city = user.getString("city");
        String district = user.getString("district");
        int gradeId = user.getInt("gradeId");
        String grade = Constants.studentGrades[gradeId];

        String school = "";
        if (gradeId <= GRADE_PRIMARY_SCHOOL){
            school = user.getString("primarySchool");
        }
        else if (gradeId <= GRADE_JUNIOR_HIGH_SCHOOL){
            school = user.getString("juniorHighSchool");
        }
        else if (gradeId <= GRADE_HIGH_SCHOOL){
            school = user.getString("highSchool");
        }
        else
            school = user.getString("school");

        if (TextUtils.isEmpty(school))
            school =  "未填写";

        String collegeTag = user.getString("collegeTag");

        return new User(user_id,userName, realName, password, sex,
                school, major, grade, fansNum, online, user_imgUrl, phoneNum, emailAddr,
                userType, user_description, shortDescription,categoryId,province,city,
                district,gradeId,collegeTag);
    }

    @NonNull
    public static User getUser(ParseUser user, boolean is) {
        String user_id = user.getObjectId();
        String userName = user.getString("username");
        String realName = user.getString("realname");
        String password = "";
        SexType sex = SexType.values()[user.getInt("sex")];

        String major = user.getString("major");

        int fansNum = user.getInt("fansNum");
        boolean online = user.getBoolean("online");
        String user_imgUrl = user.getString("imgUrl");
        String phoneNum = user.getString("phoneNum");
        String emailAddr = user.getString("emailAddr");
        UserType userType = UserType.values()[user.getInt("userType")];
        String user_description = user.getString("description");
        String shortDescription = user.getString("shortDescription");
        int categoryId = user.getInt("categoryId");
        String province = user.getString("province");
        String city = user.getString("city");
        String district = user.getString("district");
        int gradeId = user.getInt("gradeId");
        String grade = Constants.studentGrades[gradeId];

        String school = "";
        if (gradeId <= GRADE_PRIMARY_SCHOOL){
            school = user.getString("primarySchool");
        }
        else if (gradeId <= GRADE_JUNIOR_HIGH_SCHOOL){
            school = user.getString("juniorHighSchool");
        }
        else if (gradeId <= GRADE_HIGH_SCHOOL){
            school = user.getString("highSchool");
        }
        else
            school = user.getString("school");

        if (TextUtils.isEmpty(school))
            school =  "未填写";

        String collegeTag = user.getString("collegeTag");

        return new User(user_id,userName, realName, password, sex,
                school, major, grade, fansNum, online, user_imgUrl, phoneNum, emailAddr,
                userType, user_description, shortDescription,categoryId,province,city,
                district,gradeId,collegeTag);
    }

    public static void setImg(SimpleDraweeView mImg, String url, int width, int heigth)
    {
        Uri uri = Uri.parse(url);

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width,heigth))
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
        ParseObject providerObject = project.getParseObject("providerV2");
        if (providerObject != null)
            provider = getUser(providerObject);
        else
            provider = null;
        String title = project.getString("title");
        Date startTime = project.getDate("startTime");
        String imgUrl = project.getString("imgUrl");
        long price = project.getLong("price");
        long salePrice = project.getLong("salePrice");
        String description = project.getString("description");
        int acceptNum = project.getInt("acceptNum");
        ProjectStateType projectState = ProjectStateType.values()[project.getInt("projectState")];
        int collectorNum = project.getInt("collectorNum");
        int booekdNum = project.getInt("bookedNum");
        String tips = project.getString("tips");
        if (TextUtils.isEmpty(tips))
            tips = "暂无相关提示";

        return new Project(id, provider, title, startTime,
                imgUrl, price,salePrice, description, acceptNum, projectState, collectorNum,booekdNum,tips);
    }

    @NonNull
    public static Comment getComment(ParseObject object) {

        Comment mComment = new Comment();
        mComment.setProjectId(object.getString("projectId"));
        mComment.setCommentScore(object.getInt("score"));
        mComment.setCommentContent(object.getString("content"));
        mComment.setCommentUserId(object.getString("userId"));
        mComment.setCommentTime(object.getDate("commentTime"));

        return mComment;
    }

    public static ProjectSaleInfo getProjectSaleInfo(ParseObject project){
        boolean refundable = project.getBoolean("refundable");
        boolean identified = project.getBoolean("identified");
        boolean official = project.getBoolean("official");
        int totalScore = project.getInt("score");
        int commentNum = project.getInt("rateCount");
        //todo 将评论获得后转换
        Comment comment = null;

        return new ProjectSaleInfo(refundable,identified,official,totalScore,commentNum,comment);
    }

    public static MajorModel getMajorInfo(ParseObject major){
        String name = major.getString(Constants.MajorInfo_name);
        int majorClass = major.getInt(Constants.MajorInfo_class);
        String majorCode = major.getString(Constants.MajorInfo_code);
        String majorAbstract = major.getString(Constants.MajorInfo_abstract);
        boolean isRecommend = major.getBoolean(Constants.MajorInfo_isRecommend);
        String majorType = major.getString(Constants.MajorInfo_classType);
        boolean isUpdate = major.getBoolean(Constants.MajorInfo_isUpdate);
        Date updateAt = major.getUpdatedAt();
        String imgUrl = major.getString(Constants.MajorInfo_imgUrl);
        int interests = major.getInt(Constants.MajorInfo_interests);


        return new MajorModel(
                name, majorClass,
                majorCode,majorAbstract,
                isRecommend,majorType,
                isUpdate,updateAt,
                imgUrl,interests);
    }

    public static String getSchoolTypeByGradeId(int gradeIndex){

        //1. 大学生+
        if (gradeIndex > Constants.GRADE_HIGH_SCHOOL){
            return Constants.User_collegeSchool;
        }
        else if (gradeIndex > Constants.GRADE_JUNIOR_HIGH_SCHOOL){
            return Constants.User_highSchool;
        }
        else if (gradeIndex > Constants.GRADE_PRIMARY_SCHOOL){
            return Constants.User_juniorHighSchool;
        }
        else {
            return Constants.User_primarySchool;
        }

    }


}
