package com.xcore.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.xcore.R;
import com.xcore.base.BaseListViewAdapter;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.data.bean.HomeBean;
import com.xcore.data.bean.TypeListDataBean;
import com.xcore.ext.ImageViewExt;
import com.xcore.ui.touch.IHomeOnClick;
import com.xcore.utils.ViewUtils;
import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends BaseListViewAdapter<HomeBean.HomeDataItem> {
    private IHomeOnClick iHomeOnClick;

    public HomeAdapter(Context context,IHomeOnClick homeOnClick){
        super(context);
        this.iHomeOnClick=homeOnClick;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        convertView = inflater.inflate(R.layout.home_item_content, null);
        ViewHolder holder = null;//new ViewHolder(convertView);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.home_item_content, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HomeItemAdapter typeItemAdapter=new HomeItemAdapter(mContext);
        RecyclerView recyclerView= holder.itemView.findViewById(R.id.recyclerView);
        //recyclerView.removeAllViews();
        recyclerView.setLayoutManager(new GridLayoutManager(mContext,2));
        //recyclerView.addItemDecoration(new GridDividerItemDecoration(1,context.getResources().getColor(R.color.color_white)));
        recyclerView.setAdapter(typeItemAdapter);
//
        final HomeBean.HomeDataItem homeDataItem= dataList.get(position);
        List<TypeListDataBean> typeListDataBeanList=new ArrayList<>();
        TextView titleText = holder.itemView.findViewById(R.id.title);
        titleText.setText(homeDataItem.getName());
//
        holder.itemView.findViewById(R.id.xLayout).setVisibility(View.GONE);
        if(position==0) {
            typeItemAdapter.setData(homeDataItem.getList());
        }else{
            holder.itemView.findViewById(R.id.xLayout).setVisibility(View.VISIBLE);
            List<TypeListDataBean> typeListDataBeans=homeDataItem.getList();

            if(typeListDataBeans.size()<=0){
                return convertView;
            }

            final TypeListDataBean typeListDataBean = typeListDataBeans.get(0);

            final ImageViewExt conver = holder.itemView.findViewById(R.id.conver);
            TextView title = holder.itemView.findViewById(R.id.txtTitle);

            title.setText(typeListDataBean.getTitle());
            //CacheFactory.getInstance().getImage(context,conver, typeListDataBean.getConverUrl());
            conver.loadUrl(typeListDataBean.getConverUrl());

            conver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent=new Intent(mContext, VideoActivity.class);
//                    intent.putExtra("shortId",typeListDataBean.getShortId());
//                    mContext.startActivity(intent);
                    ViewUtils.toPlayer((Activity) mContext,conver,typeListDataBean.getShortId(),null,null);
                }
            });
            for(int i=0;i<typeListDataBeans.size();i++){
                if(i==0){
                    continue;
                }
                typeListDataBeanList.add(homeDataItem.getList().get(i));
            }
            typeItemAdapter.setData(typeListDataBeanList);
        }

        typeItemAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<TypeListDataBean>() {
            @Override
            public void onItemClick(TypeListDataBean item, int position) {
//                Intent intent=new Intent(mContext, VideoActivity.class);
//                intent.putExtra("shortId",item.getShortId());
//                mContext.startActivity(intent);
            }
        });
        holder.itemView.findViewById(R.id.btn_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iHomeOnClick!=null){
                    iHomeOnClick.onClickMore(homeDataItem);
                }
            }
        });
        holder.itemView.findViewById(R.id.btn_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iHomeOnClick!=null){
                    iHomeOnClick.onClickChange(homeDataItem);
                }
            }
        });
        return convertView;
    }
}
