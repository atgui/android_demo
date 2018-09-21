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

public class TypeTabAdapter extends BaseRecyclerAdapter<CategoriesBean,TypeTabAdapter.ViewHolder> {

    public TypeTabAdapter(Context mContext) {
        super(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.type_tab_adapter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);

        final CategoriesBean typeItemBean=dataList.get(position);

        TextView txtInfo =holder.itemView.findViewById(R.id.txt_item_info);
        txtInfo.setMinimumHeight(20);
        txtInfo.setText(typeItemBean.getName());

        if(typeItemBean.isChecked()){
            txtInfo.setTextColor(mContext.getResources().getColor(R.color.title_color));
        }else{
            txtInfo.setTextColor(mContext.getResources().getColor(R.color.item_txt_color));
        }
    }

    /**
     * 点击改变状态
     * @param position
     */
    public void changeSelect(int position){
        dataList.get(position).setChecked(true);
        for(int i=0;i<dataList.size();i++){
            if(position!=i){
                dataList.get(i).setChecked(false);
            }
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
