package com.xcore.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.nex3z.flowlayout.FlowLayout;
import com.xcore.R;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.RelatedBean;
import com.xcore.ext.ImageViewExt;
import com.xcore.ui.activity.TagActivity;
import com.xcore.utils.ViewUtils;
import java.util.ArrayList;
import java.util.List;


public class RelatedAdapter extends BaseAdapter {
    private Context _context;
    public List<RelatedBean> dataList=new ArrayList<>();

    public RelatedAdapter(Context context){
        _context=context;
    }

    /**
     * 设置数据
     * @param sources
     */
    public void setData(List<RelatedBean> sources){
        this.dataList=sources;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=LayoutInflater.from(_context).inflate(R.layout.adapter_related,parent,false);
         final RelatedBean relatedBean= dataList.get(position);

        final ImageViewExt icon=view.findViewById(R.id.image_icon);
        icon.loadUrl(relatedBean.getConverUrl());
//        CacheFactory.getInstance().getImage(_context,icon,relatedBean.getConverUrl());

        TextView txtTitle=view.findViewById(R.id.txt_title);
        txtTitle.setText(relatedBean.getName());

        TextView txtTime=view.findViewById(R.id.txt_time);
        txtTime.setText(relatedBean.getDate());

        TextView pTxt=view.findViewById(R.id.txt_playCount);
        pTxt.setText(relatedBean.getPlayCount());

        FlowLayout flowLayout= view.findViewById(R.id.flowLayout);
        flowLayout.removeAllViews();
        int i=0;
        for(CategoriesBean item:relatedBean.getTagslist()){
            TextView textView=getText(item);
            textView.setMaxLines(1);
            textView.setTextColor(_context.getResources().getColor(R.color.title_color));
            flowLayout.addView(textView);
            i++;
            if(i>1){
                break;
            }
        }
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//这里要给一个ID
//                Intent intent=new Intent(_context, VideoActivity.class);
//                intent.putExtra("shortId",relatedBean.getShortId());
//                _context.startActivity(intent);
                ViewUtils.toPlayer((Activity) _context,icon,relatedBean.getShortId(),null,null);
            }
        });

        return view;
    }

    private TextView getText(final CategoriesBean item){
        TextView textView=ViewUtils.getText(_context,item.getName(),R.drawable.tag_feedback_tiwen);
        textView.setMaxEms(7);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(_context, TagActivity.class);
                intent.putExtra("tag",item.getName());
                _context.startActivity(intent);
            }
        });
        return textView;
    }
}
