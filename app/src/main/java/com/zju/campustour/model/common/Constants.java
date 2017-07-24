package com.zju.campustour.model.common;

import com.zju.campustour.R;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by HeyLink on 2017/4/22.
 */

public class Constants {


    public static final String bmobAppKey = "1dc82556be41f";
    public static final String bmobAppPwd = "8303895984769248cae27a25055ba839";


    public static final String DB_USERNAME = "userName";
    public static final String DB_USERIMG = "userImgUrl";
    public static final String DB_USERIMG_ONLINE = "userImgOnline";

    public static final int FULL_VIEW = 0;
    public static final int PART_VIEW = 1;
    public static final int COLLECT_VIEW = 2;

    public static final int TAG_DEFAULT_SEARCH = 0;
    public static final int TAG_HOT_RECOMMEND = 1;
    public static final int TAG_SAME_PROVINCE = 2;
    public static final int TAG_LATEST = 3;

    public static final int TAG_RECOMMEND = 1;
    public static final int TAG_STUDY = 2;
    public static final int TAG_SCIENCE = 3;
    public static final int TAG_NEWS = 4;

    public static final int TAG_MINE = 4;
    public static final int TAG_FAVOR = 5;
    public static final int TAG_BOOKED = 6;
    public static final int TAG_FINISHED = 7;

    public static final int TAG_FANS = 0;
    public static final int TAG_FOCUS = 1;

    public static final int ACTION_LIST = 1;
    public static final int ACTION_GIRD = 2;

    public static final int GRADE_PRIMARY_SCHOOL = 5;
    public static final int GRADE_JUNIOR_HIGH_SCHOOL = 8;
    public static final int GRADE_HIGH_SCHOOL = 12;
    public static final int GRADE_COLLEGE_SCHOOL =27;


    public static final String First_Open = "isFirstOpen";
    public static final int STATE_NORMAL = 0;
    public static final int STATE_REFRESH = 1;
    public static final int STATE_MORE = 2;

    public static final int DEFAULT_GET_NUM = 10;

    public static final String URL_DEFAULT_PROJECT_BG =  "http://119.23.248.205:8080/pictures/20170511162220.jpg";

    public static final String URL_DEFAULT_MAN_IMG = "http://imgtu.5011.net/uploads/content/20170323/8197471490255172.jpg";

    public static final String URL_DEFAULT_WOMAN_IMG = "https://a-ssl.duitang.com/uploads/item/201605/24/20160524072338_CVLZB.thumb.700_0.jpeg";

    public static final String URL_DEFAULT_PROJECT_IMG = "http://mmbiz.qpic.cn/mmbiz/TT9v08cQf2Svq1UswDp5NCQu3Q14cDtWDCicLk8icBbkmDhvxic8peFmyKtiakDWTXicZfkA3KOSkK7ukIoxE7uECUw/640?wx_fmt=jpeg";

    public static final String URL_VERIFIED_ID_BG = "http://campustour.oss-cn-shenzhen.aliyuncs.com/verify_id_bg.jpg";

    public static final int[] imageUrls = {
            R.drawable.home1,
            R.drawable.home2,
            R.drawable.home3,
            R.drawable.home4,
            R.drawable.home5
    };
    //图片说明文字集
    public static final String[] imageContentDescs = {
            "教育部：2016高考关键词",
            "2016年艺术类专业报考指南",
            "清华大学：首推手机版报考指南",
            "教育部：高校毕业生就业创业政策百问",
            "北大工学院在超分辨显微方面取得突破"
    };
    //图片对应的网页
    public static final String[] imagewebs = {
            "http://www.moe.edu.cn/jyb_zwfw/zwfw_ytkd/201606/t20160607_248471.html",
            "http://gaokao.chsi.com.cn/gkzt/ysl2016",
            "http://news.tsinghua.edu.cn/publish/thunews/9945/2016/20160624115927201461764/20160624115927201461764_.html",
            "http://www.moe.gov.cn/jyb_xwfb/xw_zt/moe_357/jyzt_2016nztzl/2016_zt12/",
            "http://pkunews.pku.edu.cn/xxfz/2016-06/22/content_294169.htm"

    };

    /*数据库有关*/
    public final static String DB_NAME = "CampusTour";

    public final static String MAJOR_ZHEXUE_TABLE_NAME = "zhexueFiles";
    public final static String MAJOR_JINGJIXUE_TABLE_NAME = "jingjiFiles";
    public final static String MAJOR_FAXUE_TABLE_NAME = "faxueFiles";
    public final static String MAJOR_JIAOYUXUE_TABLE_NAME = "jiaoyuxueFiles";
    public final static String MAJOR_WENXUE_TABLE_NAME = "wenxueFiles";
    public final static String MAJOR_LISHIXUE_TABLE_NAME = "lishixueFiles";
    public final static String MAJOR_LIXUE_TABLE_NAME = "lixueFiles";
    public final static String MAJOR_GONGXUE_TABLE_NAME = "gongxueFiles";
    public final static String MAJOR_NONGXUE_TABLE_NAME = "nongxueFiles";
    public final static String MAJOR_YIXUE_TABLE_NAME = "yixueFiles";
    public final static String MAJOR_GUANLIXUE_TABLE_NAME = "guanlixueFiles";
    public final static String MAJOR_YISHUXUE_TABLE_NAME = "yishuxueFiles";

    public final static String[] majorsTableName = new String[]{MAJOR_ZHEXUE_TABLE_NAME, MAJOR_JINGJIXUE_TABLE_NAME, MAJOR_FAXUE_TABLE_NAME,
            MAJOR_JIAOYUXUE_TABLE_NAME, MAJOR_WENXUE_TABLE_NAME, MAJOR_LISHIXUE_TABLE_NAME, MAJOR_LIXUE_TABLE_NAME,
            MAJOR_GONGXUE_TABLE_NAME, MAJOR_NONGXUE_TABLE_NAME, MAJOR_YIXUE_TABLE_NAME, MAJOR_GUANLIXUE_TABLE_NAME, MAJOR_YISHUXUE_TABLE_NAME};



    public final static List<String> projectDefaultKeys = asList(
           "id",
           "providerV2",
           "title",
           "startTime",
           "imgUrl",
           "price",
            "salePrice",
           "description",
           "acceptNum",
            "bookedNum",
           "projectState",
           "collectorNum",
            "tips");

    public final static List<String> projectSaleKeys = asList(
            "refundable",
            "identified",
            "score",
            "official",
            "rateCount",
            "comments");

    public final static String[] studentGrades = {"小学一年级","小学二年级","小学三年级","小学四年级","小学五年级","小学六年级",
            "初中一年级","初中二年级","初中三年级","高中一年级","高中二年级","高中三年级","就业-高中","大学一年级","大学二年级","大学三年级","大学四年级",
            "大学五年级","就业-本科","研究生一年级","研究生二年级","研究生三年级","就业-硕士","研究生四年级","研究生五年级",
            "研究生五年+","就业-博士","博士后","助理研究员","讲师","副研究员","副教授","研究员","教授"};


    /*下面为与聊天相关定义常量*/
    public static final int REQUEST_CODE_TAKE_PHOTO = 4;
    public static final int REQUEST_CODE_SELECT_PICTURE = 6;
    public static final int RESULT_CODE_SELECT_PICTURE = 8;
    public static final int REQUEST_CODE_SELECT_ALBUM = 10;
    public static final int RESULT_CODE_SELECT_ALBUM = 11;
    public static final int REQUEST_CODE_BROWSER_PICTURE = 12;
    public static final int RESULT_CODE_BROWSER_PICTURE = 13;
    public static final int REQUEST_CODE_CHAT_DETAIL = 14;
    public static final int RESULT_CODE_CHAT_DETAIL = 15;
    public static final int REQUEST_CODE_FRIEND_INFO = 16;
    public static final int RESULT_CODE_FRIEND_INFO = 17;
    public static final int REQUEST_CODE_CROP_PICTURE = 18;
    public static final int REQUEST_CODE_ME_INFO = 19;
    public static final int RESULT_CODE_ME_INFO = 20;
    public static final int REQUEST_CODE_ALL_MEMBER = 21;
    public static final int RESULT_CODE_ALL_MEMBER = 22;
    public static final int RESULT_CODE_SELECT_FRIEND = 23;
    public static final int REQUEST_CODE_SEND_LOCATION = 24;
    public static final int RESULT_CODE_SEND_LOCATION = 25;
    public static final int REQUEST_CODE_SEND_FILE = 26;
    public static final int RESULT_CODE_SEND_FILE = 27;
    public static final int REQUEST_CODE_EDIT_NOTENAME = 28;
    public static final int RESULT_CODE_EDIT_NOTENAME = 29;
    public static final int REQUEST_CODE_AT_MEMBER = 30;
    public static final int RESULT_CODE_AT_MEMBER = 31;
    public static final int ON_GROUP_EVENT = 3004;

    private static final String JCHAT_CONFIGS = "JChat_configs";
    public static final String CONV_TITLE = "convTitle";
    public static final String TARGET_APP_KEY = "targetAppKey";
    public static final String TARGET_ID = "targetId";
    public static final String AVATAR = "avatar";
    public static final String NAME = "name";
    public static final String NICKNAME = "nickname";
    public static final String GROUP_ID = "groupId";
    public static final String GROUP_NAME = "groupName";
    public static final String NOTENAME = "notename";
    public static final String GENDER = "gender";
    public static final String REGION = "region";
    public static final String SIGNATURE = "signature";
    public static final String STATUS = "status";
    public static final String POSITION = "position";
    public static final String MsgIDs = "msgIDs";
    public static final String DRAFT = "draft";
    public static final String DELETE_MODE = "deleteMode";
    public static final String MEMBERS_COUNT = "membersCount";
    public static String FILE_DIR = "sdcard/showyou/recvFiles/";

    /*与parse服务端数据表有关*/

    //MajorInfo表
    public static String MajorInfo_tableName = "MajorInfo";
    public static String MajorInfo_name = "name";
    public static String MajorInfo_code = "code";
    public static String MajorInfo_class = "class";
    public static String MajorInfo_abstract = "abstract";
    public static String MajorInfo_isRecommend = "isRecommend";
    public static String MajorInfo_classType = "classType";
    public static String MajorInfo_isUpdate = "isUpdate";
    public static String MajorInfo_imgUrl = "imgUrl";
    public static String MajorInfo_interests = "interests";

    //User表
    public static String User_userType = "userType";
    public static String User_identityType = "identityType";
    public static String User_fansNum = "fansNum";
    public static String User_province = "province";
    public static String User_city = "city";
    public static String User_district = "district";
    public static String User_grade = "gradeId";
    public static String User_isVerified = "isVerified";
    public static String User_collegeSchool = "school";
    public static String User_highSchool = "highSchool";
    public static String User_juniorHighSchool = "juniorHighSchool";
    public static String User_primarySchool = "primarySchool";
    public static String User_major = "major";
    public static String User_majorCategory = "categoryId";

    public static String AliEndPoint = "http://oss-cn-shenzhen.aliyuncs.com";
    public static String AccessKeyId = "LTAIQeF79xVMLn7c";
    public static String AccessKeySecret = "Iwz0Cnh3fonteUN8QCPG3nCDC6RE7J";
    public static String BucketName = "campustour";

}
