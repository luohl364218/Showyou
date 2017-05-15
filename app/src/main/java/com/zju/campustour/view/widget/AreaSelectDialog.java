package com.zju.campustour.view.widget;

import android.app.ActionBar;
import android.app.AlertDialog;
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
import com.zju.campustour.model.area.CityModel;
import com.zju.campustour.model.area.DistrictModel;
import com.zju.campustour.model.area.ProvinceModel;
import com.zju.campustour.presenter.handler.XmlParserHandler;
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
 * Created by HeyLink on 2017/5/14.
 */

public class AreaSelectDialog extends Dialog {

    //todo 大坑啊，要继承Dialog 不是AlertDialog!!
    public AreaSelectDialog(@NonNull Context context) {

        this(context,false,null);
    }

    public AreaSelectDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected AreaSelectDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

    }

    @Override
    public void setContentView(@NonNull View view) {
        super.setContentView(view);
    }

    public static class Builder implements OnWheelChangedListener {
        private WheelView mViewProvince;
        private WheelView mViewCity;
        private WheelView mViewDistrict;
        private Button mBtnConfirm;
        private Context mContext;
        private OnAreaSelectClickListener mClickListener;


        public Builder(Context context) {
            mContext = context;

        }

        /**
         * Set the positive button resource and it's listener
         *
         * @return
         */
        public Builder setPositiveButtonListener(OnAreaSelectClickListener listener) {
            this.mClickListener = listener;
            return this;
        }


        public AreaSelectDialog create(){
            LayoutInflater mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            WindowManager windowMgr = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            final AreaSelectDialog mDialog = new AreaSelectDialog(mContext,R.style.Dialog);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            View areaSelectView = mInflater.inflate(R.layout.area_select_wheel_view, null);

            Display d = windowMgr.getDefaultDisplay(); // 获取屏幕宽、高用

            mDialog.addContentView(areaSelectView, new ActionBar.LayoutParams(
                    (int) (d.getWidth() * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            setUpViews(areaSelectView);

            // 添加change事件
            mViewProvince.addChangingListener(this);
            // 添加change事件
            mViewCity.addChangingListener(this);
            // 添加change事件
            mViewDistrict.addChangingListener(this);
            // 添加onclick事件
            mBtnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null){
                        //return the student's id, so we can get the exact student info
                        try{
                            mClickListener.onClick(mDialog, mCurrentProvinceName,mCurrentCityName,mCurrentDistrictName,mCurrentZipCode);
                        }catch (Exception e){
                            mDialog.dismiss();
                        }
                    }
                }
            });

            setUpData();

          /*  mDialog.setContentView(areaSelectView);*/

            return mDialog;

        }




        private void setUpViews(View areaView) {
            mViewProvince = (WheelView) areaView.findViewById(R.id.id_province);
            mViewCity = (WheelView) areaView.findViewById(R.id.id_city);
            mViewDistrict = (WheelView) areaView.findViewById(R.id.id_district);
            mBtnConfirm = (Button) areaView.findViewById(R.id.btn_confirm);
        }

        private void setUpData() {
            initProvinceDatas();
            mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mProvinceDatas));
            // 设置可见条目数量
            mViewProvince.setVisibleItems(7);
            mViewCity.setVisibleItems(7);
            mViewDistrict.setVisibleItems(7);
            updateCities();
            updateAreas();
        }

        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            // TODO Auto-generated method stub
            if (wheel == mViewProvince) {
                updateCities();
            } else if (wheel == mViewCity) {
                updateAreas();
            } else if (wheel == mViewDistrict) {
                mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
                mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
            }
        }

        /**
         * 根据当前的市，更新区WheelView的信息
         */
        private void updateAreas() {
            int pCurrent = mViewCity.getCurrentItem();
            mCurrentCityName = mCitisDatasMap.get(mCurrentProvinceName)[pCurrent];
            String[] areas = mDistrictDatasMap.get(mCurrentCityName);

            if (areas == null) {
                areas = new String[] { "" };
            }
            mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(mContext, areas));
            mViewDistrict.setCurrentItem(0);
            //这句话千万不能忘记
            mCurrentDistrictName = areas[0];
        }

        /**
         * 根据当前的省，更新市WheelView的信息
         */
        private void updateCities() {
            int pCurrent = mViewProvince.getCurrentItem();
            mCurrentProvinceName = mProvinceDatas[pCurrent];
            String[] cities = mCitisDatasMap.get(mCurrentProvinceName);
            if (cities == null) {
                cities = new String[] { "" };
            }
            mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(mContext, cities));
            mViewCity.setCurrentItem(0);
            updateAreas();
        }

        public interface OnAreaSelectClickListener{
            void onClick(DialogInterface dialog,String currentProvince,String currentCity,String currentDistrict,String currentZipCode);
        }


        /**
         * 所有省
         */
        protected String[] mProvinceDatas;
        /**
         * key - 省 value - 市
         */
        protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
        /**
         * key - 市 values - 区
         */
        protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();

        /**
         * key - 区 values - 邮编
         */
        protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();

        /**
         * 当前省的名称
         */
        protected String mCurrentProvinceName;
        /**
         * 当前市的名称
         */
        protected String mCurrentCityName;
        /**
         * 当前区的名称
         */
        protected String mCurrentDistrictName ="";

        /**
         * 当前区的邮政编码
         */
        protected String mCurrentZipCode ="";

        /**
         * 解析省市区的XML数据
         */

        protected void initProvinceDatas()
        {
            List<ProvinceModel> provinceList = null;
            AssetManager asset = mContext.getAssets();
            try {
                InputStream input = asset.open("province_data.xml");
                // 创建一个解析xml的工厂对象
                SAXParserFactory spf = SAXParserFactory.newInstance();
                // 解析xml
                SAXParser parser = spf.newSAXParser();
                XmlParserHandler handler = new XmlParserHandler();
                parser.parse(input, handler);
                input.close();
                // 获取解析出来的数据
                provinceList = handler.getDataList();
                //*/ 初始化默认选中的省、市、区
                if (provinceList!= null && !provinceList.isEmpty()) {
                    mCurrentProvinceName = provinceList.get(0).getName();
                    List<CityModel> cityList = provinceList.get(0).getCityList();
                    if (cityList!= null && !cityList.isEmpty()) {
                        mCurrentCityName = cityList.get(0).getName();
                        List<DistrictModel> districtList = cityList.get(0).getDistrictList();
                        mCurrentDistrictName = districtList.get(0).getName();
                        mCurrentZipCode = districtList.get(0).getZipcode();
                    }
                }
                //*/
                mProvinceDatas = new String[provinceList.size()];
                for (int i=0; i< provinceList.size(); i++) {
                    // 遍历所有省的数据
                    mProvinceDatas[i] = provinceList.get(i).getName();
                    List<CityModel> cityList = provinceList.get(i).getCityList();
                    String[] cityNames = new String[cityList.size()];
                    for (int j=0; j< cityList.size(); j++) {
                        // 遍历省下面的所有市的数据
                        cityNames[j] = cityList.get(j).getName();
                        List<DistrictModel> districtList = cityList.get(j).getDistrictList();
                        String[] distrinctNameArray = new String[districtList.size()];
                        DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
                        for (int k=0; k<districtList.size(); k++) {
                            // 遍历市下面所有区/县的数据
                            DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
                            // 区/县对于的邮编，保存到mZipcodeDatasMap
                            mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
                            distrinctArray[k] = districtModel;
                            distrinctNameArray[k] = districtModel.getName();
                        }
                        // 市-区/县的数据，保存到mDistrictDatasMap
                        mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
                    }
                    // 省-市的数据，保存到mCitisDatasMap
                    mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {

            }
        }

    }


}
