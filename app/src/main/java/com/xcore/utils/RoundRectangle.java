package com.xcore.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.shapes.Shape;

public class RoundRectangle extends Shape {
    private int width;
    private int height;
    private int x=0;
    private int y=0;
    int color;
    Rect r;

    public RoundRectangle(int color,int width, int height,Rect rect) {
        this.width = width;
        this.height = height;
        this.x=x;
        this.y=y;
        this.r=rect;
        this.color=color;
    }
    Paint paint = new Paint();
    {
        paint.setAntiAlias(true);//用于防止边缘的锯齿
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);//设置样式为空心矩形
        paint.setStrokeWidth(3f);//设置空心矩形边框的宽度
        paint.setAlpha(1000);//设置透明度
    }

    public void draw(Canvas canvas, PointF point, float value, Paint paint) {
        canvas.drawRect(this.r,paint);//绘制矩形，并设置矩形框显示的位置
    }

//    public int getHeight() {
//        return 100;
//    }

    @Override
    public void draw(Canvas canvas, Paint paint) {

    }
}
