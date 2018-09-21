package com.xcore.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.R;
import com.xcore.base.MvpActivity;
import com.xcore.data.bean.FeedbackBean;
import com.xcore.data.bean.FeedbackRecodeBean;
import com.xcore.data.bean.LikeBean;
import com.xcore.presenter.FeedbackPresenter;
import com.xcore.presenter.view.FeedbackView;
import com.xcore.ui.adapter.FeedbackAdapter;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

public class FeedbackRecodeActivity extends MvpActivity<FeedbackView,FeedbackPresenter> implements FeedbackView {
    public static final int IMAGE_PICKER = 100;
    private RefreshLayout refreshLayout;
    private FeedbackAdapter feedbackAdapter;
    private int pageIndex=1;
    private boolean isMore=true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feedback_recode;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("反馈记录");
        setEdit("");

        RecyclerView recyclerView= findViewById(R.id.recyclerView);
        refreshLayout=findViewById(R.id.refreshLayout);

        feedbackAdapter=new FeedbackAdapter(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(feedbackAdapter);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if(isMore==false){
                    refreshLayout.finishLoadMore(1000);
                    return;
                }
                initData();
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageIndex=1;
                initData();
            }
        });
    }

    @Override
    protected void initData() {
        presenter.getFeedList(pageIndex);
        pageIndex++;
    }

    @Override
    public FeedbackPresenter initPresenter() {
        return new FeedbackPresenter();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case 0:
//                if (data != null) {
//                    List<String> paths = (List<String>) data.getExtras().getSerializable("photos");//path是选择拍照或者图片的地址数组
//                    //处理代码
//                    for(String path : paths){
//                        Log.e("TAG",path);
//                    }
//                }
//                break;
//            default:
//                break;
//        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void pushCamera(String imagePath) {
        //通过图片路径获取图片并压缩后转换成String
        String base64Image = bitmapToString(imagePath);
        Log.e("TAG",base64Image);
    }


    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        Log.e("TAG","压缩的值："+options.inSampleSize);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    //把bitmap转换成String
    public static String bitmapToString(String filePath) {

        Bitmap bm = getSmallBitmap(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 40, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }


    @Override
    public void onTypeResult(FeedbackBean feedbackBean) {
    }

    @Override
    public void onAddResult(LikeBean likeBean) {
    }

    @Override
    public void onRecodeResult(FeedbackRecodeBean recodeBean) {
        if(refreshLayout!=null){
            refreshLayout.finishLoadMore();
            refreshLayout.finishRefresh();
        }
        if(recodeBean.getList().size()<=0){
            isMore=false;
            return;
        }
        if(recodeBean.getPageIndex()==1){
            feedbackAdapter.setData(recodeBean.getList());
        }else{
            feedbackAdapter.dataList.addAll(recodeBean.getList());
            feedbackAdapter.notifyDataSetChanged();;
        }
    }
}
