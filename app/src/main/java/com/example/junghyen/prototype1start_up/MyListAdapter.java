package com.example.junghyen.prototype1start_up;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jungh on 2017-02-23.
 */

public class MyListAdapter extends BaseAdapter {
    Context context;
    ArrayList<ListItemActivity> list_itemArrayList;
    ViewHolder viewHolder;

    class ViewHolder{
        ImageView item_imageView;
        TextView item_nickname_textView;
        TextView item_left_person_textView;
        TextView item_recent_time_textView;
    }

    public MyListAdapter(Context context, ArrayList<ListItemActivity> list_itemArrayList){
        this.context = context;
        this.list_itemArrayList = list_itemArrayList;
    }

    @Override
    public int getCount() {
        return this.list_itemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list_itemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.list_view_item,null);
            viewHolder = new ViewHolder();
            viewHolder.item_imageView = (ImageView) convertView.findViewById(R.id.item_imageView);
            viewHolder.item_nickname_textView = (TextView) convertView.findViewById(R.id.item_nickname_textView);
            viewHolder.item_left_person_textView = (TextView) convertView.findViewById(R.id.item_left_person_textView);
            viewHolder.item_recent_time_textView = (TextView) convertView.findViewById(R.id.item_recent_time_textView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.item_imageView.setImageResource(list_itemArrayList.get(position).getIcon_image());
        viewHolder.item_nickname_textView.setText(list_itemArrayList.get(position).getNickname());
        viewHolder.item_left_person_textView.setText(list_itemArrayList.get(position).getLeft_person());
        viewHolder.item_recent_time_textView.setText(list_itemArrayList.get(position).getRecent_time());

        return convertView;
    }
}
