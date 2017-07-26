package com.example.junghyen.prototype1start_up;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jungh on 2017-02-24.
 */

public class SensorInfoListAdapter extends BaseAdapter {
    Context context;
    ArrayList<SensorInfoListItemActivity> sensor_info_list_itemArrayList;
    ViewHolder viewHolder;

    class ViewHolder{
        TextView sensor_info_list_item_check;
        TextView sensor_info_list_item_time;
        TextView sensor_info_list_item_incount;
        TextView sensor_info_list_item_outcount;
        TextView sensor_info_list_item_left_person;
    }

    public SensorInfoListAdapter(Context context, ArrayList<SensorInfoListItemActivity> sensor_info_list_itemArrayList){
        this.context = context;
        this.sensor_info_list_itemArrayList = sensor_info_list_itemArrayList;
    }

    @Override
    public int getCount() {
        return this.sensor_info_list_itemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.sensor_info_list_itemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.sensor_info_list_view_item,null);
            viewHolder = new SensorInfoListAdapter.ViewHolder();
            viewHolder.sensor_info_list_item_check = (TextView) convertView.findViewById(R.id.sensor_info_list_item_check);
            viewHolder.sensor_info_list_item_time = (TextView) convertView.findViewById(R.id.sensor_info_list_item_time);
            viewHolder.sensor_info_list_item_incount = (TextView) convertView.findViewById(R.id.sensor_info_list_item_incount);
            viewHolder.sensor_info_list_item_outcount = (TextView) convertView.findViewById(R.id.sensor_info_list_item_outcount);
            viewHolder.sensor_info_list_item_left_person = (TextView)convertView.findViewById(R.id.sensor_info_list_item_left_person);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SensorInfoListAdapter.ViewHolder) convertView.getTag();
        }

        //viewHolder.sensor_info_list_item_check.setText(sensor_info_list_itemArrayList.get(position).getCheck());
        viewHolder.sensor_info_list_item_check.setText(sensor_info_list_itemArrayList.get(position).getCheck());
        viewHolder.sensor_info_list_item_time.setText(sensor_info_list_itemArrayList.get(position).getTime());
        viewHolder.sensor_info_list_item_incount.setText(sensor_info_list_itemArrayList.get(position).getIncount());
        viewHolder.sensor_info_list_item_outcount.setText(sensor_info_list_itemArrayList.get(position).getOutcount());
        viewHolder.sensor_info_list_item_left_person.setText(sensor_info_list_itemArrayList.get(position).getLeft_person());

        return convertView;
    }

}
