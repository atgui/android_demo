package com.xcore.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.base.BaseListViewAdapter;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.cache.CacheModel;
import com.xcore.ext.ImageViewExt;

import cn.dolit.utils.common.Utils;

public class Run1Adapter extends BaseListViewAdapter<CacheModel>{

    public Run1Adapter(Context mContext) {
        super(mContext);
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CacheModel cacheModel= dataList.get(position);
        BaseListViewAdapter.ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_run, null);
            holder = new BaseListViewAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
            ImageViewExt imgConver= holder.itemView.findViewById(R.id.img_conver);
            imgConver.loadUrl(cacheModel.getConverUrl());
        } else {
            holder = (BaseListViewAdapter.ViewHolder) convertView.getTag();
        }

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

        txtTitle.setText(cacheModel.getName());

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

        if(cacheModel.getComplete()==1){
            txtState.setText("已完成");
            progressBar.setVisibility(View.GONE);
        }else {
            txtState.setText("未开始");
            if (cacheModel.getStatus() != 1) {
                txtState.setText("暂停");
            } else if (cacheModel.getStatus() == 1) {//下载中..
                if (cacheModel.getStreamInfo() != null) {
                    String speedStr = Utils.getDisplayFileSize(cacheModel.getStreamInfo().getDownloadSpeed());
                    txtState.setText("" + cacheModel.getPercent() + "% [" + speedStr + "/S]");
                } else {
                    txtState.setText("" + cacheModel.getPercent() + "% [0B/S]");
                }
            }
        }
        return convertView;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
