package com.zju.campustour.view.widget;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.zju.campustour.R;
import com.zju.campustour.model.database.data.ProvinceWithCollegeModel;
import com.zju.campustour.model.database.data.SchoolModel;
import com.zju.campustour.presenter.handler.SchoolXmlParserHandler;
import com.zju.campustour.view.widget.wheelview.OnWheelChangedListener;
import com.zju.campustour.view.widget.wheelview.WheelView;
import com.zju.campustour.view.widget.wheelview.adapters.ArrayWheelAdapter;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by HeyLink on 2017/5/15.
 */

public class CollegeSelectDialog extends Dialog {
    public CollegeSelectDialog(@NonNull Context context) {
        super(context);
    }

    public CollegeSelectDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected CollegeSelectDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder implements OnWheelChangedListener {

        private WheelView mViewProvince;
        private WheelView mViewSchool;

        private String mCurrentProvinceName;
        private String mCurrentSchoolName;
        private String mCurrentTag;//985 211
        private Button mBtnConfirm;
        private Context mContext;
        private OnCollegeSelectDialog mClickListener;

        public Builder(Context context){
            this.mContext = context;
        }

        public Builder setPositiveButtonListener(OnCollegeSelectDialog listener) {
            this.mClickListener = listener;
            return this;
        }


        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            if (wheel == mViewProvince) {
                updateSchools();
            } else if (wheel == mViewSchool) {
                mCurrentSchoolName = mSchoolsDatasMap.get(mCurrentProvinceName)[newValue];
                mCurrentTag = mTagDatasMap.get(mCurrentSchoolName);
            }
        }

        public interface OnCollegeSelectDialog{
            void onClick(DialogInterface dialog, String currentProvince, String currentSchool, String currentTag);
        }


        public CollegeSelectDialog create(){
            LayoutInflater mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            WindowManager windowMgr = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            final CollegeSelectDialog mDialog = new CollegeSelectDialog(mContext, R.style.Dialog);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            View schoolSelectView = mInflater.inflate(R.layout.school_select_wheel_view, null);

            Display d = windowMgr.getDefaultDisplay(); // 获取屏幕宽、高用

            mDialog.addContentView(schoolSelectView, new ActionBar.LayoutParams(
                    (int) (d.getWidth() * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            setUpViews(schoolSelectView);

            // 添加change事件
            mViewProvince.addChangingListener(this);
            // 添加change事件
            mViewSchool.addChangingListener(this);

            // 添加onclick事件
            mBtnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null){
                        //return the student's id, so we can get the exact student info
                        try{
                            mClickListener.onClick(mDialog,mCurrentProvinceName,mCurrentSchoolName, mCurrentTag);
                        }catch (Exception e){
                            mDialog.dismiss();
                        }
                    }
                }
            });

            setUpData();

            return mDialog;

        }


        private void setUpViews(View areaView) {
            mViewProvince = (WheelView) areaView.findViewById(R.id.id_school_province);
            mViewSchool = (WheelView) areaView.findViewById(R.id.id_school_name);
            mBtnConfirm = (Button) areaView.findViewById(R.id.school_btn_confirm);
        }

        private void setUpData() {
            initProvinceDatas();
            mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mProvinceDatas));
            // 设置可见条目数量
            mViewProvince.setVisibleItems(7);
            mViewSchool.setVisibleItems(7);
            mViewSchool.setCurrentItem(1);
            updateSchools();

        }


        /**
         * 根据当前的省，更新学校WheelView的信息
         */
        private void updateSchools() {
            int pCurrent = mViewProvince.getCurrentItem();
            mCurrentProvinceName = mProvinceDatas[pCurrent];
            String[] schools = mSchoolsDatasMap.get(mCurrentProvinceName);
            if (schools == null) {
                schools = new String[] { "" };
            }
            mViewSchool.setViewAdapter(new ArrayWheelAdapter<String>(mContext, schools));
            mViewSchool.setCurrentItem(1);
            mCurrentSchoolName = schools[1];
        }


        /**
         * 解析省市区的XML数据
         */
        /**
         * 所有省
         */
        protected String[] mProvinceDatas;
        /**
         * key - 省 value - 学校
         */
        protected Map<String, String[]> mSchoolsDatasMap = new HashMap<String, String[]>();

        /**
         * key - 学校 values - 985？211
         */
        protected Map<String, String> mTagDatasMap = new HashMap<String, String>();


        protected void initProvinceDatas()
        {
            List<ProvinceWithCollegeModel> provinceList = null;
            AssetManager asset = mContext.getAssets();
            try {
                InputStream input = asset.open("school_data.xml");
                // 创建一个解析xml的工厂对象
                SAXParserFactory spf = SAXParserFactory.newInstance();
                // 解析xml
                SAXParser parser = spf.newSAXParser();
                SchoolXmlParserHandler handler = new SchoolXmlParserHandler();
                parser.parse(input, handler);
                input.close();
                // 获取解析出来的数据
                provinceList = handler.getDataList();
                ///* 初始化默认选中的省、学校
                if (provinceList!= null && !provinceList.isEmpty()) {
                    mCurrentProvinceName = provinceList.get(0).getName();
                    List<SchoolModel> schoolList = provinceList.get(0).getSchoolList();
                    if (schoolList!= null && !schoolList.isEmpty()) {
                        mCurrentSchoolName = schoolList.get(0).getName();
                        mCurrentTag = schoolList.get(0).getTag();
                    }
                }
                ///*
                mProvinceDatas = new String[provinceList.size()];
                for (int i=0; i< provinceList.size(); i++) {
                    // 遍历所有学校的数据
                    mProvinceDatas[i] = provinceList.get(i).getName();
                    List<SchoolModel> schoolList = provinceList.get(i).getSchoolList();
                    String[] schoolNames = new String[schoolList.size()];
                    for (int j=0; j< schoolList.size(); j++) {
                        // 遍历省下面的所有学校的数据
                        SchoolModel schoolModel = new SchoolModel(schoolList.get(j).getName(),schoolList.get(j).getTag());

                        // 985 211
                        mTagDatasMap.put(schoolList.get(j).getName(),schoolList.get(j).getTag());

                        schoolNames[j] = schoolModel.getName();
                        }
                    // 省-学校的数据，保存到mSchoolsDatasMap
                    mSchoolsDatasMap.put(mProvinceDatas[i], schoolNames);
                }

            } catch (Throwable e) {
                e.printStackTrace();
            } finally {

            }
        }

    }

}
