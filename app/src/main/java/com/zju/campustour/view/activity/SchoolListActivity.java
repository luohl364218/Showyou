package com.zju.campustour.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.zju.campustour.MainActivity;
import com.zju.campustour.R;
import com.zju.campustour.model.database.data.SchoolData;
import com.zju.campustour.presenter.protocal.event.ToolbarTitleChangeEvent;
import com.zju.campustour.presenter.protocal.event.onAreaAndSchoolSelectedEvent;
import com.zju.campustour.view.adapter.MyExpandableListAdapter;

import org.greenrobot.eventbus.EventBus;

import static com.zju.campustour.MainActivity.areaGroup;
import static com.zju.campustour.MainActivity.schoolItemsList;

public class SchoolListActivity extends AppCompatActivity {

    private TextView nationwideTextView;
    ExpandableListView expandableListView;
    private Toolbar toolbar;
    private String searchArea;
    private String searchSchool;
    private String selectedText;
    private int position;

    private String TAG = getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_list);
        nationwideTextView = (TextView)findViewById(R.id.search_school_nationwide);
        toolbar = (Toolbar) findViewById(R.id.school_list_toolbar);

        toolbar.setNavigationOnClickListener(a->{
            finish();
        });
        nationwideTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedText = "全国所有大学";
                searchArea = null;
                searchSchool = null;
                searchProviderWithConditions(-1, searchSchool);
            }
        });


        expandableListView = (ExpandableListView) findViewById(R.id.search_school_expandablelistview);
        expandableListView.setAdapter(new MyExpandableListAdapter(this, areaGroup,schoolItemsList) {
            @Override
            public void onItemSelected(View v) {
                position = Integer.valueOf(((TextView) v.findViewById(R.id.activity_major_list_item_child_groupID)).getText().toString());
                searchArea = SchoolData.allAreaGroup[position];
                selectedText = ((TextView) v.findViewById(R.id.activity_major_list_item_child_textview)).getText().toString();
                if ("全部".equals(selectedText)){
                    selectedText = searchArea+"所有大学";
                }
                else {
                    searchSchool = selectedText;
                }

                searchProviderWithConditions(position,searchSchool);
            }
        });

    }

    private void searchProviderWithConditions(int mArea, String mSearchSchool) {
        EventBus.getDefault().post(new ToolbarTitleChangeEvent(selectedText));
        EventBus.getDefault().post(new onAreaAndSchoolSelectedEvent(mArea, mSearchSchool));
        finish();
    }
}
