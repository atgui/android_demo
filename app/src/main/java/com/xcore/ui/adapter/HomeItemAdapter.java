package com.xcore.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.data.bean.TypeListDataBean;
import com.xcore.ext.ImageViewExt;
import com.xcore.utils.CacheFactory;
import com.xcore.utils.ViewUtils;

public class HomeItemAdapter extends BaseRecyclerAdapter<TypeListDataBean,HomeItemAdapter.ViewHoler>{

    public HomeItemAdapter(Context mContext) {
        super(mContext);
    }

    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.adapter_home_item,parent,false);
        return new ViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoler holder, int position) {
        super.onBindViewHolder(holder, position);

        final TypeListDataBean dataBean= dataList.get(position);
        TextView cTitle= holder.itemView.findViewById(R.id.cTitle);
        cTitle.setText(dataBean.getTitle());

        final ImageViewExt conver= holder.itemView.findViewById(R.id.imageConver);
        conver.loadUrl(dataBean.getConverUrl());

        conver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.toPlayer((Activity) mContext,conver,dataBean.getShortId(),null,null);
            }
        });
    }

    class ViewHoler extends RecyclerView.ViewHolder{
        public ViewHoler(View itemView) {
            super(itemView);
        }
    }
}
