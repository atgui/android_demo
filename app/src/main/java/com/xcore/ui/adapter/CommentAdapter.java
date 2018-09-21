package com.xcore.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;

public class CommentAdapter extends BaseRecyclerAdapter<String,CommentAdapter.HolderView> {

    public CommentAdapter(Context mContext) {
        super(mContext);
    }

    @NonNull
    @Override
    public HolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.adapter_comment,parent,false);
        return new HolderView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderView holder, int position) {
        super.onBindViewHolder(holder, position);

    }

    class HolderView extends RecyclerView.ViewHolder{
        private View itemView;
        public HolderView(View itemView) {
            super(itemView);
            this.itemView=itemView;
        }
    }
}
