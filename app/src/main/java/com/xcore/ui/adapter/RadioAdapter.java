package com.xcore.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.data.bean.FeedbackBean;

import static android.media.CamcorderProfile.get;

public class RadioAdapter extends BaseRecyclerAdapter<FeedbackBean.FeedbackItem,RadioAdapter.ViewHolder>{

    public RadioAdapter(Context mContext) {
        super(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.adapter_radio,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        FeedbackBean.FeedbackItem feedbackItem= dataList.get(position);

        ImageView radioButton= holder.itemView.findViewById(R.id.radioBtn);
        if(feedbackItem.selected){
            radioButton.setImageResource(R.drawable.radio_checked);
        }else{
            radioButton.setImageResource(R.drawable.radio_check);
        }

        TextView radioTxt= holder.itemView.findViewById(R.id.radioTxt);
        radioTxt.setText(feedbackItem.getName());

        holder.itemView.findViewById(R.id.radioLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick1(position);
            }
        });
    }
    private void onClick1(int position){
        FeedbackBean.FeedbackItem item= dataList.get(position);
        item.selected=true;
        for(FeedbackBean.FeedbackItem xitem:dataList){
            if(item==xitem){
                continue;
            }
            xitem.selected=false;
        }
        notifyDataSetChanged();
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
