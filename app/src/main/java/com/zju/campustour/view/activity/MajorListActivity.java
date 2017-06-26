package com.zju.campustour.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.zju.campustour.MainActivity;
import com.zju.campustour.R;
import com.zju.campustour.view.adapter.MyExpandableListAdapter;


/**
 * Created by Administrator on 2016/7/8.
 */
public class MajorListActivity extends BaseActivity{

    ExpandableListView expandableListView;

    private Toolbar toolbar;
    //传递过来的专业类型
    private int position = 0;
    //可展开列表adapter
    private ExpandableListAdapter expandableListAdapter;

    private String TAG = getClass().getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_major_list);
        //如果专业信息未初始化完成，返回
        if (MainActivity.itemsList.size() == 0)
            return;

        expandableListView = (ExpandableListView) findViewById(R.id.activity_major_list_expandablelistview);
        toolbar = (Toolbar) this.findViewById(R.id.major_list_toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            getBundleExtras(extras);
        }

        initViewsAndEvents();
    }




    protected void getBundleExtras(Bundle extras) {
        position = extras.getInt("position");
    }

    protected void initViewsAndEvents() {
        expandableListAdapter = new MyExpandableListAdapter(this, MainActivity.groupList,MainActivity.itemsList) {
            @Override
            public void onItemSelected(View v) {
                Log.d(TAG, "onItemSelected");
                String majorName = ((TextView)v.findViewById(R.id.activity_major_list_item_child_textview)).getText().toString();
                String groupID = ((TextView)v.findViewById(R.id.activity_major_list_item_child_groupID)).getText().toString();

                Intent intent = new Intent(MajorListActivity.this, MajorInfoActivity.class);
                intent.putExtra("major_name",majorName);
                intent.putExtra("group_id",Integer.valueOf(groupID));

                startActivity(intent);

            }
        };

        expandableListView.setAdapter(expandableListAdapter);

        if (position != 100){
            //定位至某一行
            expandableListView.setSelection(position);
            //展开某一行
            expandableListView.expandGroup(position);
        }

    }


}
