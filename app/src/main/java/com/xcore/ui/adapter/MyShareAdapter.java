package com.xcore.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.data.bean.MyShareBean;
import com.xcore.utils.DateUtils;

public class MyShareAdapter extends BaseRecyclerAdapter<MyShareBean.MyShareBeanItem,MyShareAdapter.ViewHolder>{

    public MyShareAdapter(Context mContext) {
        super(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.adapter_my_share,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        MyShareBean.MyShareBeanItem beanItem= dataList.get(position);

        LinearLayout linearLayout= holder.itemView.findViewById(R.id.contentLayout);
        if(position%2==0){
            linearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.black_1A));
        }else{
            linearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.color_0f0f0f));
        }

        TextView ustate= holder.itemView.findViewById(R.id.ustate);
        TextView uName= holder.itemView.findViewById(R.id.uname);
        TextView uTime= holder.itemView.findViewById(R.id.utime);

        ustate.setText(beanItem.getStatus());
        uName.setText(beanItem.getAppuserName());

        String timeStr=DateUtils.getDate("yy-MM-dd",beanItem.getCreateTime());
        uTime.setText(timeStr);
        if(beanItem.getState()==1){//成功
            ustate.setTextColor(mContext.getResources().getColor(R.color.color_green));
        }else{
            ustate.setTextColor(mContext.getResources().getColor(R.color.red_color));
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
