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
import com.xcore.data.bean.FeedbackRecodeBean;
import com.xcore.data.bean.PlayerBean;
import com.xcore.data.bean.UserInfo;
import com.xcore.data.utils.DataUtils;
import com.xcore.utils.CacheFactory;
import com.xcore.utils.DateUtils;

public class FeedbackAdapter extends BaseRecyclerAdapter<FeedbackRecodeBean.RecodeItem,FeedbackAdapter.ViewHolder>{

    public FeedbackAdapter(Context mContext) {
        super(mContext);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.feedback_adapter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        FeedbackRecodeBean.RecodeItem item= dataList.get(position);

        PlayerBean playerBean= DataUtils.playerBean;
        UserInfo userInfo= playerBean.getData();
        ImageView avatar=holder.itemView.findViewById(R.id.item_avatar);
        CacheFactory.getInstance().getImage(mContext,avatar,userInfo.getHeadUrl());

        TextView uname=holder.itemView.findViewById(R.id.uname);
        uname.setText(userInfo.getName());

        TextView typeTxt=holder.itemView.findViewById(R.id.txt_typeName);
        TextView contentTxt= holder.itemView.findViewById(R.id.txt_content);
        TextView dateTxt= holder.itemView.findViewById(R.id.txt_date);
        TextView reContent=holder.itemView.findViewById(R.id.txt_reContent);

        reContent.setText(item.getReplyContent());

        typeTxt.setText("类型:"+item.getGuestBookTypeName());
        contentTxt.setText(item.getContent());

        String dateStr=DateUtils.getDate("yyyy-MM-dd HH:mm:ss",item.getReleasetime());
        dateTxt.setText(dateStr);
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
