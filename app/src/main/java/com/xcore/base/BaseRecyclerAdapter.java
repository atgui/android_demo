package com.xcore.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public abstract class BaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>{
        public Context mContext;

        public BaseRecyclerAdapter(Context mContext){
            this.mContext=mContext;
        }

        //ItemClick事件
        public OnItemClickListener<T> mItemClickListener= null;

        //数据集合
        public List<T> dataList = new ArrayList<>();

    /*
        设置数据
        Presenter处理过为null的情况，所以为不会为Null
     */
    public void setData(List<T> sources){
        if(sources==null){
            dataList.clear();
        }else{
            dataList=sources;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemClickListener!=null){
                    mItemClickListener.onItemClick(dataList.get(position),position);
                }
            }
        });
    }

    /*
        ItemClick事件声明
     */
    public interface OnItemClickListener<T> {
        void onItemClick(T item, int position);
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.mItemClickListener = listener;
    }


    /**
     * 根据时间戳得到日期
     * @param time
     * @return
     */
    public String getDate(String time){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String sd = sdf.format(new Date(Long.parseLong(time)));      // 时间戳转换成时间
        return sd;
    }
    /**
     * 根据时间戳得到日期+时间
     * @param time
     * @return
     */
    public String getDatetime(long time){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(time))));      // 时间戳转换成时间
        return sd;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
