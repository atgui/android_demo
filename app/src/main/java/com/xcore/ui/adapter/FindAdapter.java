package com.xcore.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.cache.CacheManager;
import com.xcore.cache.beans.CacheBean;
import com.xcore.cache.beans.CacheType;
import com.xcore.data.bean.TypeListDataBean;
import com.xcore.ext.ImageViewExt;
import com.xcore.ui.touch.IFindTouchListener;
import com.xcore.utils.CacheFactory;
import com.xcore.utils.ViewUtils;

public class FindAdapter extends BaseRecyclerAdapter<TypeListDataBean,FindAdapter.ViewHolder>{
    private IFindTouchListener findTouchListener;

    public FindAdapter(Context mContext,IFindTouchListener touchListener) {
        super(mContext);
        this.findTouchListener=touchListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.adapter_find,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        final TypeListDataBean dataBean= dataList.get(position);

        final ImageViewExt icon=holder.itemView.findViewById(R.id.icon);
        icon.loadUrl(dataBean.getConverUrl());
//        CacheFactory.getInstance().getImage(mContext,icon,dataBean.getConverUrl());

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.toPlayer((Activity) mContext,icon,dataBean.getShortId(),null,null);
            }
        });

        TextView titleTxt=holder.itemView.findViewById(R.id.title);
        titleTxt.setText(dataBean.getTitle());

        TextView txtTime=holder.itemView.findViewById(R.id.txt_time);
        txtTime.setText(dataBean.getTime());

        TextView txtDate=holder.itemView.findViewById(R.id.txt_date);
        txtDate.setText(dataBean.getDate());

        TextView pTxt=holder.itemView.findViewById(R.id.txt_playCount);
        pTxt.setText(dataBean.getPlayCountData());

//        ImageView imageDown=holder.itemView.findViewById(R.id.image_down);
//        ImageView imageCollect=holder.itemView.findViewById(R.id.image_collect);
//        ImageView imageShare=holder.itemView.findViewById(R.id.image_share);

//        imageDown.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(findTouchListener!=null){
//                    findTouchListener.onDownClick(dataBean);
//                }
//            }
//        });
//        imageShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(findTouchListener!=null){
//                    findTouchListener.onShareClick(dataBean);
//                }
//            }
//        });

//        imageCollect.setImageResource(R.drawable.type_collect);

//        CacheBean collect1=new CacheBean();
//        collect1.settType(CacheType.CACHE_COLLECT);
//        collect1.setShortId(dataBean.getShortId());

        //查找是否有收藏
//        CacheBean cacheBean= CacheManager.getInstance().getDbHandler().query(collect1);
//        if(cacheBean!=null){
//            if(cacheBean.gettDelete().equals("1")){
//                imageCollect.setImageResource(R.drawable.type_collected);
//            }
//        }

    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
