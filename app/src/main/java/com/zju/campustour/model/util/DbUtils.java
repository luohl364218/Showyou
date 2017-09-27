package com.zju.campustour.model.util;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.zju.campustour.model.bean.LabelInfo;
import com.zju.campustour.model.bean.PositionModel;
import com.zju.campustour.model.bean.StatusInfoModel;
import com.zju.campustour.model.common.Constants;
import com.zju.campustour.model.bean.MajorModel;
import com.zju.campustour.model.bean.Comment;
import com.zju.campustour.model.bean.Project;
import com.zju.campustour.model.bean.ProjectSaleInfo;
import com.zju.campustour.model.bean.User;
import com.zju.campustour.model.bean.VerifyInfo;
import com.zju.campustour.presenter.protocal.enumerate.IdentityType;
import com.zju.campustour.presenter.protocal.enumerate.ProjectStateType;
import com.zju.campustour.presenter.protocal.enumerate.SexType;
import com.zju.campustour.presenter.protocal.enumerate.UserType;
import com.zju.campustour.presenter.protocal.enumerate.VerifyStateType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

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
        String userName = user.getString(Constants.User_userName);
        String realName = user.getString(Constants.User_realName);
        String password = "";
        SexType sex = SexType.values()[user.getInt(Constants.User_sex)];

        String major = user.getString(Constants.User_major);
        int fansNum = user.getInt(Constants.User_fansNum);
        boolean online = user.getBoolean(Constants.User_online);
        String user_imgUrl = user.getString(Constants.User_imgUrl);
        String phoneNum = user.getString(Constants.User_phoneNum);
        String emailAddr = user.getString(Constants.User_emailAddr);
        UserType userType = UserType.values()[user.getInt("userType")];
        IdentityType identityType = IdentityType.values()[user.getInt(Constants.User_identityType)];
        String user_description = user.getString("description");
        String shortDescription = user.getString("shortDescription");
        int categoryId = user.getInt("categoryId");
        String country = user.getString(Constants.User_country);
        String province = user.getString("province");
        String city = user.getString("city");
        String district = user.getString("district");
        String street = user.getString(Constants.User_street);
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

        if (TextUtils.isEmpty(realName))
            realName =  "匿名校友";

        if (TextUtils.isEmpty(school))
            school =  "神秘学校";

        if (TextUtils.isEmpty(major))
            major =  "神秘专业";

        if (TextUtils.isEmpty(shortDescription))
            shortDescription =  "初来乍到，请多多指教";

        if (identityType == IdentityType.LOOK_AROUND_USER){
            school =  "游客";
            major =  "名校体验";
            grade = "VIP";
            shortDescription =  "上校游，找校友";
        }

        String collegeTag = user.getString("collegeTag");
        boolean isVerified = user.getBoolean(Constants.User_isVerified);

        return new User(user_id,userName, realName, password, sex,
                school, major, grade, fansNum, online, user_imgUrl, phoneNum, emailAddr,
                userType,identityType, user_description, shortDescription,categoryId,country,province,city,
                district,street,gradeId,collegeTag,isVerified);
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
        IdentityType identityType = IdentityType.values()[user.getInt(Constants.User_identityType)];
        String user_description = user.getString("description");
        String shortDescription = user.getString("shortDescription");
        int categoryId = user.getInt("categoryId");
        String country = user.getString(Constants.User_country);
        String province = user.getString("province");
        String city = user.getString("city");
        String district = user.getString("district");
        String street = user.getString(Constants.User_street);
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

        if (TextUtils.isEmpty(realName))
            realName =  "匿名校友";

        if (TextUtils.isEmpty(school))
            school =  "神秘学校";

        if (TextUtils.isEmpty(major))
            major =  "神秘专业";

        if (TextUtils.isEmpty(shortDescription))
            shortDescription =  "初来乍到，请多多指教";

        String collegeTag = user.getString("collegeTag");
        boolean isVerified = user.getBoolean(Constants.User_isVerified);

        return new User(user_id,userName, realName, password, sex,
                school, major, grade, fansNum, online, user_imgUrl, phoneNum, emailAddr,
                userType, identityType,user_description, shortDescription,categoryId,country,province,city,
                district,street,gradeId,collegeTag,isVerified);
    }

    public static User getUser(HashMap user) {
        String user_id = (String) user.get("objectId");
        String userName = (String) user.get(Constants.User_userName);
        String realName = (String) user.get(Constants.User_realName);
        String password = "";
        SexType sex = SexType.values()[(int) user.get(Constants.User_sex)];

        String major = (String) user.get(Constants.User_major);
        int fansNum = (int) user.get(Constants.User_fansNum);
        boolean online = (boolean) user.get(Constants.User_online);
        String user_imgUrl = (String) user.get(Constants.User_imgUrl);
        String phoneNum = (String) user.get(Constants.User_phoneNum);
        String emailAddr = (String) user.get(Constants.User_emailAddr);
        UserType userType = UserType.values()[(int) user.get("userType")];
        IdentityType identityType = IdentityType.values()[(int) user.get(Constants.User_identityType)];
        String user_description = (String) user.get("description");
        String shortDescription = (String) user.get("shortDescription");
        int categoryId = (int) user.get("categoryId");
        String country = (String) user.get(Constants.User_country);
        String province = (String) user.get("province");
        String city = (String) user.get("city");
        String district = (String) user.get("district");
        String street = (String) user.get(Constants.User_street);
        int gradeId = (int) user.get("gradeId");
        String grade = Constants.studentGrades[gradeId];

        String school = "";
        if (gradeId <= GRADE_PRIMARY_SCHOOL){
            school = (String) user.get("primarySchool");
        }
        else if (gradeId <= GRADE_JUNIOR_HIGH_SCHOOL){
            school = (String) user.get("juniorHighSchool");
        }
        else if (gradeId <= GRADE_HIGH_SCHOOL){
            school = (String) user.get("highSchool");
        }
        else
            school = (String) user.get("school");

        if (TextUtils.isEmpty(realName))
            realName =  "匿名校友";

        if (TextUtils.isEmpty(school))
            school =  "神秘学校";

        if (TextUtils.isEmpty(major))
            major =  "神秘专业";

        if (TextUtils.isEmpty(shortDescription))
            shortDescription =  "初来乍到，请多多指教";

        String collegeTag = (String) user.get("collegeTag");
        boolean isVerified = (boolean) user.get(Constants.User_isVerified);

        return new User(user_id,userName, realName, password, sex,
                school, major, grade, fansNum, online, user_imgUrl, phoneNum, emailAddr,
                userType,identityType, user_description, shortDescription,categoryId,country,province,city,
                district,street,gradeId,collegeTag,isVerified);
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
        boolean isOffline = project.getBoolean("isOffline");


        return new Project(id, provider, title, startTime,
                imgUrl, price,salePrice, description, acceptNum, projectState, collectorNum,booekdNum,tips,isOffline);
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


    public static VerifyInfo getVerifyInfo(ParseObject object){
        try {
            String objectId = object.getObjectId();
            String submitUserId = object.getString(Constants.UserIdVerifyInfo_UserId);
            String submitImgUrl = object.getString(Constants.UserIdVerifyInfo_ImgUrl);
            String submitDescription = object.getString(Constants.UserIdVerifyInfo_Description);
            VerifyStateType submitVerifyStateType = VerifyStateType
                    .values()[object.getInt(Constants.UserIdVerifyInfo_VerifyStateType)];
            Date submitTime = object.getDate(Constants.UserIdVerifyInfo_SubmitTime);
            Date verifiedTime = object.getDate(Constants.UserIdVerifyInfo_VerifiedTime);
            String replyComment = object.getString(Constants.UserIdVerifyInfo_ReplyComment);

            return new VerifyInfo(objectId,submitUserId,
                    submitImgUrl,
                    submitDescription,
                    submitVerifyStateType,
                    submitTime,
                    verifiedTime,
                    replyComment);
        }
        catch (Exception e){
            return null;
        }


    }

    public static StatusInfoModel getStatusInfo(ParseObject object){
        try{
            String objectId = object.getObjectId();
            String imgUrl = object.getString(Constants.StatusInfo_ImgUrl);
            String content = object.getString(Constants.StatusInfo_Content);
            String userId = object.getString(Constants.StatusInfo_UserId);
            User provider;
            ParseObject providerObject = object.getParseObject(Constants.StatusInfo_User);
            if (providerObject != null)
                provider = getUser(providerObject);
            else
                return null;

            Date createdTime = object.getCreatedAt();
            int favourCount = object.getInt(Constants.StatusInfo_FavorCount);
            int commentCount = object.getInt(Constants.StatusInfo_CommentCount);
            boolean isDeleted = object.getBoolean(Constants.StatusInfo_IsDeleted);
            String labelId = object.getString(Constants.StatusInfo_LabelId);
            String labelContent = object.getString(Constants.StatusInfo_LabelContent);
            String province = object.getString(Constants.StatusInfo_Province);
            String city = object.getString(Constants.StatusInfo_City);
            String district = object.getString(Constants.StatusInfo_District);
            String street = object.getString(Constants.StatusInfo_Street);
            String detailLocation = object.getString(Constants.StatusInfo_DetailLocation);
            String diyLocation = object.getString(Constants.StatusInfo_DiyLocation);
            boolean hidePosition = object.getBoolean(Constants.StatusInfo_HidePosition);

            return new StatusInfoModel(objectId,
                    imgUrl,content,userId,provider,
                    createdTime,favourCount,commentCount,
                    isDeleted,labelId,labelContent,province,city,
                    district,street,detailLocation,diyLocation,hidePosition);

        }
        catch (Exception e){
            return null;
        }
    }

    @Nullable
    public static StatusInfoModel getStatusInfo(HashMap object){
        try{
            String objectId = (String) object.get("objectId");

            String imgUrl = "";
            if (object.get(Constants.StatusInfo_ImgUrl) != null) {
                imgUrl = (String) object.get(Constants.StatusInfo_ImgUrl);
            }
            String content = (String) object.get(Constants.StatusInfo_Content);
            String userId = (String) object.get(Constants.StatusInfo_UserId);
            User provider;
            ParseUser providerObject = (ParseUser) object.get(Constants.StatusInfo_User);
            if (providerObject != null)
                provider = getUser(providerObject);
            else
                return null;

            String strDate = (String) object.get("createdAt");;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date createdTime = sdf.parse(strDate);
            long rightTime = (long) (createdTime.getTime() + 8 * 60 * 60 * 1000);
            Date realTime = new Date(rightTime);
            int favourCount = (int) object.get(Constants.StatusInfo_FavorCount);
            int commentCount = (int) object.get(Constants.StatusInfo_CommentCount);
            boolean isDeleted = (boolean) object.get(Constants.StatusInfo_IsDeleted);
            String labelId = (String) object.get(Constants.StatusInfo_LabelId);
            String labelContent = (String) object.get(Constants.StatusInfo_LabelContent);
            String province = (String) object.get(Constants.StatusInfo_Province);
            String city = (String) object.get(Constants.StatusInfo_City);
            String district = (String) object.get(Constants.StatusInfo_District);
            String street = (String) object.get(Constants.StatusInfo_Street);
            String detailLocation = (String) object.get(Constants.StatusInfo_DetailLocation);
            String diyLocation = (String) object.get(Constants.StatusInfo_DiyLocation);
            boolean hidePosition = false;
            if (object.get(Constants.StatusInfo_HidePosition) != null)
                hidePosition = (boolean) object.get(Constants.StatusInfo_HidePosition);

            StatusInfoModel statusInfo = new StatusInfoModel(objectId,
                    imgUrl,content,userId,provider,
                    realTime,favourCount,commentCount,
                    isDeleted,labelId,labelContent,province,city,
                    district,street,detailLocation,diyLocation,hidePosition);

            statusInfo.setFavorited((boolean) object.get(Constants.StatusInfo_IsFavorited));

            return statusInfo;

        }
        catch (Exception e){
            return null;
        }
    }

    public static LabelInfo getLabelInfo(ParseObject object){

        String labelId = object.getObjectId();
        User provider;
        ParseObject providerObject = object.getParseObject(Constants.LabelInfo_User);
        if (providerObject != null)
            provider = getUser(providerObject);
        else
            return null;

        String content = object.getString(Constants.LabelInfo_Content);
        int joinNum = object.getInt(Constants.LabelInfo_JoinNum);

        return new LabelInfo(labelId,provider,content,joinNum);
    }



}
