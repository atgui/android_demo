package com.xcore.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.cache.CacheModel;
import com.xcore.ext.ImageViewExt;
import com.xcore.utils.CacheFactory;
import com.xcore.utils.ViewUtils;

import java.lang.reflect.GenericSignatureFormatError;

import cn.dolit.utils.common.Utils;

public class RunAdapter extends BaseRecyclerAdapter<CacheModel,RunAdapter.ViewHolder>{

    public RunAdapter(Context mContext) {
        super(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.adapter_run,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        final CacheModel cacheModel= dataList.get(position);

        final ImageViewExt imgConver= holder.itemView.findViewById(R.id.img_conver);
        final RadioButton radioButton= holder.itemView.findViewById(R.id.radioBtn);
        TextView txtTitle=holder.itemView.findViewById(R.id.txt_title);
        TextView txtState= holder.itemView.findViewById(R.id.txt_state);
        TextView txtTotal=holder.itemView.findViewById(R.id.txt_total);
        ProgressBar progressBar= holder.itemView.findViewById(R.id.pro_progressbar);

        radioButton.setChecked(cacheModel.isChecked());
        if(cacheModel.isShowCheck()){
            radioButton.setVisibility(View.VISIBLE);
        }else{
            radioButton.setVisibility(View.GONE);
        }
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean boo=!cacheModel.isChecked();
                cacheModel.setChecked(boo);
                radioButton.setChecked(boo);
            }
        });

//        CacheFactory.getInstance().getImage(mContext,imgConver,cacheModel.getConverUrl());
        imgConver.loadUrl(cacheModel.getConverUrl());
        txtTitle.setText(cacheModel.getName());

        imgConver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//暂时不判断
//                if(cacheModel.getComplete()==1) {
                    ViewUtils.toPlayer((Activity) mContext, v, cacheModel.getShortId(), cacheModel.getUrl(), null);
//                }
            }
        });

        int total=Integer.valueOf(cacheModel.getTotalSize());
        if(total<=0){
            txtTotal.setText("未知");
        }else{
            String tStr=Utils.getDisplayFileSize(total);
            txtTotal.setText(tStr);
        }

        progressBar.setMax(100);
        int proValue=Integer.valueOf(cacheModel.getPercent());
        progressBar.setProgress(proValue);

        if(cacheModel.getComplete()==1||proValue>=100){
            txtState.setText("已完成");
            progressBar.setVisibility(View.GONE);
            return;
        }
        txtState.setText("未开始");
        if(cacheModel.getStatus()!=1){
            txtState.setText("已下载...  "+cacheModel.getPercent()+"%");
        }else if(cacheModel.getStatus()==1){//下载中..
            if(cacheModel.getStreamInfo()!=null){
                String speedStr=Utils.getDisplayFileSize(cacheModel.getStreamInfo().getDownloadSpeed());
                txtState.setText("下载中."+cacheModel.getPercent()+"% ["+speedStr+"/S]");
            }else{
                txtState.setText("下载中."+cacheModel.getPercent()+"% [0B/S]");
            }
        }
    }
    //全选或全不选
    public void selectAll(boolean boo){
        for(CacheModel cacheModel:dataList){
            cacheModel.setChecked(boo);
        }
        notifyDataSetChanged();
    }
    //显示或不显示
    public void showAll(boolean boo){
        for(CacheModel cacheModel:dataList){
            cacheModel.setShowCheck(boo);
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
