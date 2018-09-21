package com.xcore.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyRecyclerViewItem extends HorizontalScrollView {

    public Handler hander;
    private boolean isLeft=true;//在左边

    public MyRecyclerViewItem(Context context) {
        super(context);
        init();
    }

    public MyRecyclerViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyRecyclerViewItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private TextView delete_txt;
    private int buttonWidth;
    private DisplayMetrics scale;

    private void init() {
        scale = getContext().getResources().getDisplayMetrics();
        buttonWidth = dp2px(120);// 布局的宽度
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.i(getClass().getSimpleName(), l+"-"+t+"-"+r+"-"+b);

        //调整布局
        LinearLayout linearLayout= (LinearLayout) getChildAt(0);
        LinearLayout linearLayoutLeft = (LinearLayout) linearLayout.getChildAt(0);
        LinearLayout linearLayoutRight = (LinearLayout) linearLayout.getChildAt(1);
        linearLayout.layout(linearLayout.getLeft(), linearLayout.getTop(), linearLayout.getLeft()+getMeasuredWidth()+ buttonWidth, linearLayout.getBottom());
        linearLayoutLeft.layout(linearLayoutLeft.getLeft(), linearLayoutLeft.getTop(),linearLayoutLeft.getLeft()+getMeasuredWidth(), linearLayoutLeft.getBottom());

        linearLayoutRight.layout(
                linearLayoutLeft.getLeft()+getMeasuredWidth(),
                linearLayoutLeft.getTop(),
                linearLayoutLeft.getLeft()+getMeasuredWidth()+ buttonWidth,
                linearLayoutLeft.getBottom());
        delete_txt=(TextView)linearLayoutRight.getChildAt(0);
    }


    //恢复状态
    public void reset(){
        isLeft=true;
        scrollTo(0,0);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction()== MotionEvent.ACTION_DOWN)
        {
            Log.e("TAG", "down");
            return true;
        }
        if (ev.getAction()==MotionEvent.ACTION_CANCEL || ev.getAction()== MotionEvent.ACTION_UP)
        {
            Log.e("TAG", "up");
            int range=70;
            if (isLeft)
            {
                if(hander!=null) {
                    hander.sendEmptyMessage(1);
                }

                if (getScrollX()>range)
                {
                    Log.e("TAg","left1...");
                    isLeft=false;
                    smoothScrollTo(buttonWidth, 0);
                }else {
                    Log.e("TAg","left2...");
                    isLeft=true;
                    smoothScrollTo(0,0);
                }
            }else {
                if(hander!=null) {
                    hander.sendEmptyMessage(0);
                }

                if (getScrollX()< (buttonWidth-range))
                {
                    Log.e("TAg","left3...");
                    isLeft=true;
                    smoothScrollTo(0,0);
                }else {
                    isLeft=false;
                    Log.e("TAg","left4...");
                    smoothScrollTo(buttonWidth, 0);
                }
            }
            return true;
        }
        Log.e("TAG", "end");
        return super.onTouchEvent(ev);
    }


    private int dp2px(float dpValue){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, scale);
    }





}