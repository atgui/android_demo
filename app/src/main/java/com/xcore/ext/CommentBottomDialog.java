package com.xcore.ext;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.R;
import com.xcore.ui.adapter.CommentAdapter;

import java.util.Arrays;
import java.util.List;

import me.shaohui.bottomdialog.BaseBottomDialog;

public class CommentBottomDialog extends BaseBottomDialog {

    private RefreshLayout refreshLayout;
    public CommentAdapter commentAdapter;

    @Override
    public int getLayoutRes() {
        return R.layout.layout_pvideo_dialog;
    }

    @Override
    public void bindView(View v) {
        ImageView img_close=v.findViewById(R.id.btn_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentBottomDialog.this.dismiss();
            }
        });

        refreshLayout= v.findViewById(R.id.refreshLayout);

        commentAdapter=new CommentAdapter(CommentBottomDialog.this.getActivity());
        RecyclerView recyclerView=v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(CommentBottomDialog.this.getActivity(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(commentAdapter);

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                setData(Arrays.asList("fdasfd","fdsafds","fdasfds"));
                refreshLayout.finishLoadMore(2000);
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
            }
        });
        setData(Arrays.asList("fdasfd","fdsafds","fdasfds"));
    }

   public void setData(List<String> sourcs){
        commentAdapter.dataList.addAll(sourcs);
        commentAdapter.notifyDataSetChanged();
//        commentAdapter.setData(sourcs);
   }


}
