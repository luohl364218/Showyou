package com.zju.campustour.view.widget;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.zju.campustour.view.widget.wheelview.OnWheelChangedListener;
import com.zju.campustour.view.widget.wheelview.WheelView;
import com.zju.campustour.view.widget.wheelview.adapters.ArrayWheelAdapter;

import static com.zju.campustour.model.common.Constants.studentGrades;

/**
 * Created by HeyLink on 2017/5/14.
 */

public class GradeSelectDialog extends Dialog {
    public GradeSelectDialog(@NonNull Context context) {
        this(context,false,null);
    }

    public GradeSelectDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected GradeSelectDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder implements OnWheelChangedListener {

        private final String[] mGrades = studentGrades;
        private WheelView mViewGrade;
        private Button mBtnConfirm;
        private Context mContext;
        private OnGradeSelectClickListener mClickListener;
        protected String mCurrentGrade;
        private int pCurrent;
        //用户当前的年级，用于修改功能
        private int mUserGrade = 9;


        public Builder(Context context) {
            mContext = context;

        }

        public void setUserGrade(int userGrade){
            mUserGrade = userGrade;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @return
         */
        public GradeSelectDialog.Builder setPositiveButtonListener(OnGradeSelectClickListener listener) {
            this.mClickListener = listener;
            return this;
        }

        public GradeSelectDialog create(){

            LayoutInflater mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            WindowManager windowMgr = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            final GradeSelectDialog mDialog = new GradeSelectDialog(mContext, R.style.Dialog);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            View gradeSelectView = mInflater.inflate(R.layout.grade_select_wheel_view, null);

            Display d = windowMgr.getDefaultDisplay(); // 获取屏幕宽、高用

            mDialog.addContentView(gradeSelectView, new ActionBar.LayoutParams(
                    (int) (d.getWidth() * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            setUpViews(gradeSelectView);

            mViewGrade.addChangingListener(this);

            mBtnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null){
                        //return the student's id, so we can get the exact student info
                        try{
                            mClickListener.onClick(mDialog, mCurrentGrade, pCurrent);
                        }catch (Exception e){
                            mDialog.dismiss();
                        }
                    }
                }
            });

            setUpData();

            return mDialog;

        }

        private void setUpViews(View gradeView) {
            mViewGrade = (WheelView) gradeView.findViewById(R.id.id_grade);
            mBtnConfirm = (Button) gradeView.findViewById(R.id.grade_btn_confirm);
        }

        private void setUpData() {

            mViewGrade.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mGrades));
            // 设置可见条目数量
            mViewGrade.setVisibleItems(7);
            mViewGrade.setCurrentItem(mUserGrade);
        }


        public interface OnGradeSelectClickListener{
            void onClick(DialogInterface dialog, String grade, int gradeIndex);
        }

        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            if (wheel == mViewGrade) {
                pCurrent = mViewGrade.getCurrentItem();
                mCurrentGrade = mGrades[pCurrent];
            }
        }



    }
}
