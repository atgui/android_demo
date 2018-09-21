package com.xcore.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.data.bean.ThemeRecommendBean;
import com.xcore.ext.ImageViewExt;
import com.xcore.utils.CacheFactory;

import java.util.ArrayList;
import java.util.List;

public class RThemeAdapter extends BaseAdapter {
    public List<ThemeRecommendBean.ThemeData> dataList=new ArrayList<>();
    private Context context;

    public RThemeAdapter(Context _context){
        this.context=_context;
    }
    //设置数据
    public void setData(List<ThemeRecommendBean.ThemeData> sources){
        this.dataList=sources;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public ThemeRecommendBean.ThemeData getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view= LayoutInflater.from(context).inflate(R.layout.adapter_recommend_theme,parent,false);

        ThemeRecommendBean.ThemeData mod = dataList.get(position);

        TextView txtTitle= view.findViewById(R.id.theme_title);

        String titleStr=mod.getName();
        if(TextUtils.isEmpty(titleStr)){
            titleStr=mod.getTitle();
        }
        txtTitle.setText(titleStr);

        ImageViewExt imageView= view.findViewById(R.id.theme_conver);
        imageView.loadUrl(mod.getConverUrl());
//        CacheFactory.getInstance().getImage(context,imageView,mod.getConverUrl());

        return view;
    }
}
