package com.xcore.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nex3z.flowlayout.FlowLayout;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.cache.beans.CacheBean;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.ext.ImageViewExt;
import com.xcore.ui.activity.TagActivity;
import com.xcore.utils.ViewUtils;
import java.util.List;

public class Recode1Adapter extends BaseRecyclerAdapter<CacheBean,Recode1Adapter.ViewHolder>{

    public Recode1Adapter(Context mContext) {
        super(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.adapter_recode,parent,false);
        return new ViewHolder(view);
    }
    private TextView getText(final String txt,int res){
        TextView textView=ViewUtils.getText(mContext,txt,R.drawable.tag_feedback_tiwen);//new TextView(mContext);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, TagActivity.class);
                intent.putExtra("tag",txt);
                mContext.startActivity(intent);
            }
        });
        return textView;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final CacheBean cacheBean=dataList.get(position);

        final ImageViewExt conver=holder.itemView.findViewById(R.id.conver);
        conver.loadUrl(cacheBean.getConverUrl());
        conver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.toPlayer((Activity) mContext,conver,cacheBean.getShortId(),null,cacheBean.getPlayPosition());
//                Intent intent=new Intent(mContext, VideoActivity.class);
//                intent.putExtra("position",cacheBean.getPlayPosition());
//                intent.putExtra("shortId",cacheBean.getShortId());
//                mContext.startActivity(intent);
            }
        });

//        CacheFactory.getInstance().getImage(mContext,conver,);

        TextView titleTxt=holder.itemView.findViewById(R.id.title);
        titleTxt.setText(cacheBean.getTitle());
        titleTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(mContext, VideoActivity.class);
//                intent.putExtra("position",cacheBean.getPlayPosition());
//                intent.putExtra("shortId",cacheBean.getShortId());
//                mContext.startActivity(intent);
//                ViewUtils.toPlayer((Activity) mContext,conver,cacheBean.getShortId(),null,cacheBean.getPlayPosition());
            }
        });


        final RadioButton radioButton= holder.itemView.findViewById(R.id.radioBtn);
        radioButton.setChecked(cacheBean.selected);
        if(cacheBean.showed){
            radioButton.setVisibility(View.VISIBLE);
        }else{
            radioButton.setVisibility(View.GONE);
        }
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cacheBean.selected=!cacheBean.selected;
                radioButton.setChecked(cacheBean.selected);
            }
        });

        FlowLayout flowLayout= holder.itemView.findViewById(R.id.bqFlowLayout);
        flowLayout.removeAllViews();
        String tags=cacheBean.getTags();
        if(tags.length()>0) {
            List<CategoriesBean> categoriesBeans =
                    new Gson().fromJson(tags, new TypeToken<List<CategoriesBean>>() {
                    }.getType());
            int i=0;
            for(CategoriesBean item:categoriesBeans){
                TextView txt=getText(item.getName(),R.drawable.type_tag_play);

                flowLayout.addView(txt);
                i++;
                if(i>3){
                    break;
                }
            }
        }
    }
    //全部显示或不显示
    public void allShow(boolean boo){
        for(CacheBean item:dataList){
            item.showed=boo;
        }
        notifyDataSetChanged();
    }
    //全部选中或不选中
    public void allSelect(boolean boo){
        for(CacheBean item:dataList){
            item.selected=boo;
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
