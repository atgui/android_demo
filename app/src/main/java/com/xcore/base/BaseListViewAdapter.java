package com.xcore.base;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.xcore.R;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListViewAdapter<T> extends BaseAdapter {
    public List<T> dataList=new ArrayList<>();
    public Context mContext;
    public LayoutInflater inflater;

    public BaseListViewAdapter(Context context){
        mContext=context;
        inflater=LayoutInflater.from(context);
    }


    public void setData(List<T> dataList){
        this.dataList=dataList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public T getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    //更新某个ITEM
    public void notifyDataSetChanged(int position,ListView listView){
//        T v=dataList.get(position);
//        v=t;
        int firstVisiblePosition = listView.getFirstVisiblePosition();//获得可见的第一个item的position
        int lastVisiblePosition = listView.getLastVisiblePosition();//获得可见的最后一个item的position
        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
            View view = listView.getChildAt(position - firstVisiblePosition);
            getView(position, view, listView);
        }
    }


    public static class ViewHolder {
        public View itemView;
        public ViewHolder(View view) {
            this.itemView=view;
        }
    }

}
