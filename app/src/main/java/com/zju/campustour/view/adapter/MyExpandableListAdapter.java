package com.zju.campustour.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zju.campustour.R;

import java.util.List;

public abstract class MyExpandableListAdapter extends BaseExpandableListAdapter{

    private Context mContext = null;

    private List<String> groupList = null;
    private List<List> itemList = null;

    public MyExpandableListAdapter(Context mContext, List<String> groupList, List<List> itemList) {
        this.mContext = mContext;
        this.groupList = groupList;
        this.itemList = itemList;
    }

    /**
     * 设置子节点对象，在事件处理时返回的对象，可存放一些数据
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return itemList.get(groupPosition).get(childPosition);
    }

    /**
     * 分组的个数
     */
    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (itemList.get(groupPosition)!= null)
            return itemList.get(groupPosition).size();
        else
            return 0;
    }

    /**
     * 返回分组对象，用于一些数据传递，在事件处理时可直接取得和分组相关的数据
     */
    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * 判断分组是否为空，本示例中数据是固定的，所以不会为空，我们返回false
     * 如果数据来自数据库，网络时，可以把判断逻辑写到这个方法中，如果为空
     * 时返回true
     */
    public boolean isEmpty() {
        return false;
    }

    /**
     * 收缩列表时要处理的东西都放这儿
     */
    public void onGroupCollapsed(int groupPosition) {

    }

    /**
     * 展开列表时要处理的东西都放这儿
     */
    public void onGroupExpanded(int groupPosition) {


    }

    /**
     * 分组视图，这里也是一个文本视图
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        TextView text;
        ImageView imageView;
        if (convertView == null){
            convertView = View.inflate(mContext, R.layout.activity_major_list_item_group,null);
        }
        text = (TextView)convertView.findViewById(R.id.activity_major_list_item_group_textview);
        imageView = (ImageView)convertView.findViewById(R.id.activity_major_list_item_group_indicator);

        String name = (String) groupList.get(groupPosition);

        text.setText(name);

        if (isExpanded){
            text.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            imageView.setImageResource(R.drawable.icon_unfold);
        }else {
            text.setTextColor(mContext.getResources().getColor(R.color.black));
            imageView.setImageResource(R.drawable.icon_folder);
        }

        return convertView;
    }
    /**
     * 子节点视图，这里我们显示一个文本对象
     */
    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TextView text;
        TextView groupID;
        if (convertView == null){
            convertView = View.inflate(mContext,R.layout.activity_major_list_item_child,null);
        }
        text = (TextView)convertView.findViewById(R.id.activity_major_list_item_child_textview);
        //此textview不显示  只为了在点击事件中传递信息
        groupID = (TextView)convertView.findViewById(R.id.activity_major_list_item_child_groupID);


        // 获取子节点要显示的名称
        String name = (String)itemList.get(groupPosition).get(childPosition);

        text.setText(name);
        groupID.setText(groupPosition+"");


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemSelected(v);
            }
        });

        return convertView;
    }

    public abstract void onItemSelected(View v);

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
