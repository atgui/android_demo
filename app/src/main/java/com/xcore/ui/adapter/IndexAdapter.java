package com.xcore.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.data.bean.HomeBean;
import com.xcore.data.bean.TypeListDataBean;
import com.xcore.ext.ImageViewExt;
import com.xcore.ui.decorations.GridDividerItemDecoration;
import com.xcore.ui.touch.IHomeOnClick;
import com.xcore.utils.ViewUtils;
import java.util.ArrayList;
import java.util.List;

public class IndexAdapter extends BaseAdapter{
    public List<HomeBean.HomeDataItem> dataList=new ArrayList<>();

    private Context context;
    private IHomeOnClick iHomeOnClick;

    public IndexAdapter(Context context,IHomeOnClick homeOnClick){
        this.context=context;
        this.iHomeOnClick=homeOnClick;
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
    //刷新数据
    public void setData(List<HomeBean.HomeDataItem> sources){
        this.dataList=sources;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=LayoutInflater.from(context).inflate(R.layout.home_item_content,parent,false);
        HomeItemAdapter typeItemAdapter=new HomeItemAdapter(context);
        RecyclerView recyclerView= view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(context,2));
        recyclerView.addItemDecoration(new GridDividerItemDecoration(20,context.getResources().getColor(R.color.black_1A)));
        recyclerView.setAdapter(typeItemAdapter);


        final HomeBean.HomeDataItem homeDataItem= dataList.get(position);
        List<TypeListDataBean> typeListDataBeanList=new ArrayList<>();
        TextView titleText = view.findViewById(R.id.title);
        titleText.setText(homeDataItem.getName());

        if(position==-1) {
            view.findViewById(R.id.xLayout).setVisibility(View.GONE);
            typeItemAdapter.setData(homeDataItem.getList());
        }else{
            view.findViewById(R.id.xLayout).setVisibility(View.VISIBLE);
            List<TypeListDataBean> typeListDataBeans=homeDataItem.getList();

            final TypeListDataBean typeListDataBean = typeListDataBeans.get(0);

            final ImageViewExt conver = view.findViewById(R.id.conver);
            TextView title = view.findViewById(R.id.txtTitle);

            title.setText(typeListDataBean.getTitle());
            //CacheFactory.getInstance().getImage(context,conver, typeListDataBean.getConverUrl());
            conver.loadUrl(typeListDataBean.getConverUrl());

            conver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent=new Intent(context, VideoActivity.class);
//                    intent.putExtra("shortId",typeListDataBean.getShortId());
//                    context.startActivity(intent);
                    ViewUtils.toPlayer((Activity) context,conver,typeListDataBean.getShortId(),null,null);
                }
            });
            for(int i=0;i<homeDataItem.getList().size();i++){
                if(i==0&&position!=0){
                    continue;
                }
                typeListDataBeanList.add(homeDataItem.getList().get(i));
            }
            typeItemAdapter.setData(typeListDataBeanList);
        }

        typeItemAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<TypeListDataBean>() {
            @Override
            public void onItemClick(TypeListDataBean item, int position) {
//                Intent intent=new Intent(context, VideoActivity.class);
//                intent.putExtra("shortId",item.getShortId());
//                context.startActivity(intent);
            }
        });
        view.findViewById(R.id.btn_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iHomeOnClick!=null){
                    iHomeOnClick.onClickMore(homeDataItem);
                }
            }
        });
        view.findViewById(R.id.btn_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iHomeOnClick!=null){
                    iHomeOnClick.onClickChange(homeDataItem);
                }
            }
        });
        return view;
    }


}
