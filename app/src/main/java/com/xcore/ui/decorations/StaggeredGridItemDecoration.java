package com.xcore.ui.decorations;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class StaggeredGridItemDecoration extends RecyclerView.ItemDecoration {
    private int space = 0;
    private int pos;

    public StaggeredGridItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = space;

        //该View在整个RecyclerView中位置。
        pos = parent.getChildAdapterPosition(view);

        //取模

        //两列的左边一列
        if (pos % 2 == 0) {
            outRect.left = space;
            outRect.right = space / 2;
        }

        //两列的右边一列
        if (pos % 2 == 1) {
            outRect.left = space / 2;
            outRect.right = space;
        }
    }
}
