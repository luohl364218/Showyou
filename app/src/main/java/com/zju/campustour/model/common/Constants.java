package com.zju.campustour.model.common;

import com.zju.campustour.R;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by HeyLink on 2017/4/22.
 */

public class Constants {

    public static final int FULL_VIEW = 0;
    public static final int PART_VIEW = 1;
    public static final int COLLECT_VIEW = 2;

    public static final int TAG_DEFAULT_SEARCH = 0;
    public static final int TAG_HOT_RECOMMEND = 1;
    public static final int TAG_SAME_PROVINCE = 2;
    public static final int TAG_LATEST = 3;

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


    public static final String First_Open = "isFirstOpen";
    public static final int STATE_NORMAL = 0;
    public static final int STATE_REFRESH = 1;
    public static final int STATE_MORE = 2;

    public static final int DEFAULT_GET_NUM = 10;

    public static final String URL_DEFAULT_PROJECT_BG =  "http://119.23.248.205:8080/pictures/20170511162220.jpg";

    public static final String URL_DEFAULT_MAN_IMG = "http://imgtu.5011.net/uploads/content/20170323/8197471490255172.jpg";

    public static final String URL_DEFAULT_WOMAN_IMG = "https://a-ssl.duitang.com/uploads/item/201605/24/20160524072338_CVLZB.thumb.700_0.jpeg";

    public static final String URL_DEFAULT_PROJECT_IMG = "http://mmbiz.qpic.cn/mmbiz/TT9v08cQf2Svq1UswDp5NCQu3Q14cDtWDCicLk8icBbkmDhvxic8peFmyKtiakDWTXicZfkA3KOSkK7ukIoxE7uECUw/640?wx_fmt=jpeg";

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
            "研究生五年+","博士-就业","博士后","助理研究员","讲师","副研究员","副教授","研究员","教授"};

}
