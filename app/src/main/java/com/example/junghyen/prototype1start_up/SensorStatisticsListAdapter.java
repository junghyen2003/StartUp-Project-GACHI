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

public class SensorStatisticsListAdapter extends BaseAdapter {
    Context context;
    ArrayList<SensorStatisticsListItemActivity> sensor_statistics_list_itemArrayList;
    ViewHolder viewHolder;

    class ViewHolder{
        TextView statistics_list_date_textView;
        TextView statistics_list_incount_textView;
        TextView statistics_list_outcount_textView;
    }

    public SensorStatisticsListAdapter(Context context, ArrayList<SensorStatisticsListItemActivity> sensor_statistics_list_itemArrayList){
        this.context = context;
        this.sensor_statistics_list_itemArrayList = sensor_statistics_list_itemArrayList;
    }

    @Override
    public int getCount() {
        return this.sensor_statistics_list_itemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.sensor_statistics_list_itemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.statistics_list_view_item,null);
            viewHolder = new SensorStatisticsListAdapter.ViewHolder();
            viewHolder.statistics_list_date_textView = (TextView) convertView.findViewById(R.id.statistics_list_date_textView);
            viewHolder.statistics_list_incount_textView = (TextView) convertView.findViewById(R.id.statistics_list_incount_textView);
            viewHolder.statistics_list_outcount_textView = (TextView) convertView.findViewById(R.id.statistics_list_outcount_textView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SensorStatisticsListAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.statistics_list_date_textView.setText(sensor_statistics_list_itemArrayList.get(position).getDate());
        viewHolder.statistics_list_incount_textView.setText(sensor_statistics_list_itemArrayList.get(position).getIncount());
        viewHolder.statistics_list_outcount_textView.setText(sensor_statistics_list_itemArrayList.get(position).getOutcount());

        return convertView;
    }

}
