package com.xcore.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.cache.CacheManager;
import com.xcore.cache.CacheModel;
import com.xcore.cache.DownModel;
import com.xcore.cache.TableConfig;
import com.xcore.ext.ImageViewExt;
import com.xcore.utils.CacheFactory;

import cn.dolit.p2ptrans.P2PTrans;

public class DownAdapter extends BaseRecyclerAdapter<CacheModel,DownAdapter.ViewHolder>{

    public DownAdapter(Context mContext) {
        super(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.adapter_down,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        final CacheModel cacheModel=dataList.get(position);

        ImageViewExt conver=holder.itemView.findViewById(R.id.conver);
        conver.loadUrl(cacheModel.getConverUrl());
//        CacheFactory.getInstance().getImage(mContext,conver,cacheModel.getConverUrl());

        TextView titleTxt=holder.itemView.findViewById(R.id.title);
        titleTxt.setText(cacheModel.getName());

        ProgressBar pro=holder.itemView.findViewById(R.id.pro_progress);
        TextView proTxt=holder.itemView.findViewById(R.id.proTxt);
        int proValue=Integer.valueOf(cacheModel.getPercent());
        pro.setProgress(proValue);
        pro.setMax(100);

        proTxt.setText(cacheModel.getPercent()+"%");
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
