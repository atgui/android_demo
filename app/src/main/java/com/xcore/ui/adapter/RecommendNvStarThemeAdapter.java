package com.xcore.ui.adapter;

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
import com.xcore.data.bean.NvStar;
import com.xcore.data.bean.ThemeRecommendBean;
import com.xcore.ext.ImageViewExt;
import com.xcore.utils.CacheFactory;

public class RecommendNvStarThemeAdapter extends BaseRecyclerAdapter<ThemeRecommendBean.ThemeData,RecommendNvStarThemeAdapter.ViewHolder>{

    public RecommendNvStarThemeAdapter(Context mContext) {
        super(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.adapter_recommend_theme,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        ThemeRecommendBean.ThemeData mod = dataList.get(position);

        TextView txtTitle= holder.itemView.findViewById(R.id.theme_title);
        txtTitle.setText(mod.getName());

        ImageViewExt imageView= holder.itemView.findViewById(R.id.theme_conver);
        imageView.loadUrl(mod.getConverUrl());
//        CacheFactory.getInstance().getImage(mContext,imageView,mod.getConverUrl());
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
