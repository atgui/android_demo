package com.xcore.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.SearchBean;

public class SearchAdapter extends BaseRecyclerAdapter<SearchBean.SearchItem,SearchAdapter.ViewHolder> {

    public SearchAdapter(Context mContext) {
        super(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.adapter_search,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        SearchBean.SearchItem item= dataList.get(position);

        TextView txtSort= holder.itemView.findViewById(R.id.txt_sort);
        TextView txtKey=holder.itemView.findViewById(R.id.txt_key);
        TextView txtChange=holder.itemView.findViewById(R.id.txt_change);

        txtKey.setText(item.getName());

        txtSort.setText(item.getSort()+"");
        txtSort.setTextColor(mContext.getResources().getColor(R.color.item_txt_color));
        if(item.getSort()==1){
            txtSort.setBackgroundResource(R.drawable.search_adapter_txt_red_bg);
            txtSort.setTextColor(mContext.getResources().getColor(R.color.color_ff3158));
        }else if(item.getSort()==2){
            txtSort.setBackgroundResource(R.drawable.search_adapter_txt_two_bg);
            txtSort.setTextColor(mContext.getResources().getColor(R.color.color_ff9600));
        }else if(item.getSort()==3){
            txtSort.setBackgroundResource(R.drawable.search_adapter_txt_yellow__bg);
            txtSort.setTextColor(mContext.getResources().getColor(R.color.title_color));
        }else{
            txtSort.setBackgroundResource(R.drawable.search_adapter_txt_white__bg);
            txtSort.setTextColor(mContext.getResources().getColor(R.color.color_9c9c9c));
        }

        txtChange.setText("一");
        txtChange.setTextColor(mContext.getResources().getColor(R.color.item_txt_color));
        if(item.getChange()==1){
            txtChange.setText("↑");
            txtChange.setTextColor(mContext.getResources().getColor(R.color.red_color));
        }else if(item.getChange()==-1){
            txtChange.setText("↓");
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
