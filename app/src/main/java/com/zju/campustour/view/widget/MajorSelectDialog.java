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
import com.zju.campustour.model.database.data.MajorClass;
import com.zju.campustour.model.database.data.MajorModel;
import com.zju.campustour.presenter.handler.MajorXmlParserHandler;
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

public class MajorSelectDialog extends Dialog {
    public MajorSelectDialog(@NonNull Context context) {
        super(context);
    }

    public MajorSelectDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected MajorSelectDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder implements OnWheelChangedListener {

        private WheelView mViewClass;
        private WheelView mViewMajor;

        private String mCurrentMajorClass;
        private String mCurrentMajor;
        private String mCurrentTag;//1 2 3 4
        private Button mBtnConfirm;
        private Context mContext;
        private OnMajorSelectDialog mClickListener;

        public Builder(Context context){
            this.mContext = context;
        }

        public Builder setPositiveButtonListener(OnMajorSelectDialog listener) {
            this.mClickListener = listener;
            return this;
        }


        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            if (wheel == mViewClass) {
                updateSchools();
            } else if (wheel == mViewMajor) {
                mCurrentMajor = mClassMajorMap.get(mCurrentMajorClass)[newValue];
                mCurrentTag = mTagDatasMap.get(mCurrentMajor);
            }
        }

        public interface OnMajorSelectDialog {
            void onClick(DialogInterface dialog, String currentMajorClass, String currentMajor, String currentTag);
        }


        public CollegeSelectDialog create(){
            LayoutInflater mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            WindowManager windowMgr = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            final CollegeSelectDialog mDialog = new CollegeSelectDialog(mContext, R.style.Dialog);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            View schoolSelectView = mInflater.inflate(R.layout.major_select_wheel_view, null);

            Display d = windowMgr.getDefaultDisplay(); // 获取屏幕宽、高用

            mDialog.addContentView(schoolSelectView, new ActionBar.LayoutParams(
                    (int) (d.getWidth() * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            setUpViews(schoolSelectView);

            // 添加change事件
            mViewClass.addChangingListener(this);
            // 添加change事件
            mViewMajor.addChangingListener(this);

            // 添加onclick事件
            mBtnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null){
                        //return the student's id, so we can get the exact student info
                        try{
                            mClickListener.onClick(mDialog, mCurrentMajorClass, mCurrentMajor, mCurrentTag);
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
            mViewClass = (WheelView) areaView.findViewById(R.id.id_major_class);
            mViewMajor = (WheelView) areaView.findViewById(R.id.id_major_name);
            mBtnConfirm = (Button) areaView.findViewById(R.id.major_btn_confirm);
        }

        private void setUpData() {
            initProvinceDatas();
            mViewClass.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mMajorClassDatas));
            // 设置可见条目数量
            mViewClass.setVisibleItems(7);
            mViewMajor.setVisibleItems(7);
            mViewMajor.setCurrentItem(1);
            updateSchools();

        }


        /**
         * 根据当前的省，更新学校WheelView的信息
         */
        private void updateSchools() {
            int pCurrent = mViewClass.getCurrentItem();
            mCurrentMajorClass = mMajorClassDatas[pCurrent];
            String[] schools = mClassMajorMap.get(mCurrentMajorClass);
            if (schools == null) {
                schools = new String[] { "" };
            }
            mViewMajor.setViewAdapter(new ArrayWheelAdapter<String>(mContext, schools));
            mViewMajor.setCurrentItem(1);
            mCurrentMajor = schools[1];
        }



        /**
         * 所有专业分类
         */
        protected String[] mMajorClassDatas;
        /**
         * key - 分类 value - 专业
         */
        protected Map<String, String[]> mClassMajorMap = new HashMap<String, String[]>();

        /**
         * key - 专业 values - 1234
         */
        protected Map<String, String> mTagDatasMap = new HashMap<String, String>();


        protected void initProvinceDatas()
        {
            List<MajorClass> mMajorClassList = null;
            AssetManager asset = mContext.getAssets();
            try {
                InputStream input = asset.open("major_data.xml");
                // 创建一个解析xml的工厂对象
                SAXParserFactory spf = SAXParserFactory.newInstance();
                // 解析xml
                SAXParser parser = spf.newSAXParser();
                MajorXmlParserHandler handler = new MajorXmlParserHandler();
                parser.parse(input, handler);
                input.close();
                // 获取解析出来的数据
                mMajorClassList = handler.getDataList();
                ///* 初始化默认选中的专业类别，专业
                if (mMajorClassList!= null && !mMajorClassList.isEmpty()) {
                    mCurrentMajorClass = mMajorClassList.get(0).getName();
                    List<MajorModel> schoolList = mMajorClassList.get(0).getMajorList();
                    if (schoolList!= null && !schoolList.isEmpty()) {
                        mCurrentMajor = schoolList.get(0).getName();
                        mCurrentTag = schoolList.get(0).getTag();
                    }
                }

                mMajorClassDatas = new String[mMajorClassList.size()];
                for (int i=0; i< mMajorClassList.size(); i++) {

                    // 遍历所有

                    mMajorClassDatas[i] = mMajorClassList.get(i).getName();
                    List<MajorModel> schoolList = mMajorClassList.get(i).getMajorList();
                    String[] schoolNames = new String[schoolList.size()];
                    for (int j=0; j< schoolList.size(); j++) {

                        MajorModel schoolModel = new MajorModel(schoolList.get(j).getName(),schoolList.get(j).getTag());

                        // 985 211
                        mTagDatasMap.put(schoolList.get(j).getName(),schoolList.get(j).getTag());

                        schoolNames[j] = schoolModel.getName();
                        }

                    mClassMajorMap.put(mMajorClassDatas[i], schoolNames);
                }

            } catch (Throwable e) {
                e.printStackTrace();
            } finally {

            }
        }

    }

}
