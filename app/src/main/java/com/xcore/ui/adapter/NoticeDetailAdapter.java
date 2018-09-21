package com.xcore.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.data.bean.NoticeBean;

public class NoticeDetailAdapter extends BaseRecyclerAdapter<NoticeBean.NoticeItem,NoticeDetailAdapter.ViewHolder>{

    public NoticeDetailAdapter(Context mContext) {
        super(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.notice_detail_adapter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        NoticeBean.NoticeItem noticeItem= dataList.get(position);

        TextView txtTitle=holder.itemView.findViewById(R.id.txt_title);
        txtTitle.setText(noticeItem.getTitle());

        TextView txtDesc=holder.itemView.findViewById(R.id.txt_desc);
        txtDesc.setText(noticeItem.getText());

        TextView txtDate=holder.itemView.findViewById(R.id.txt_date);
        txtDate.setText(noticeItem.getReleasetime());
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
