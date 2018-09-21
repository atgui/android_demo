package com.xcore.ui.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.os.Bundle;
import android.widget.EditText;

import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.base.MvpActivity;
import com.xcore.data.bean.FeedbackBean;
import com.xcore.data.bean.FeedbackRecodeBean;
import com.xcore.data.bean.LikeBean;
import com.xcore.presenter.FeedbackPresenter;
import com.xcore.presenter.view.FeedbackView;
import com.xcore.ui.adapter.RadioAdapter;

import java.util.List;

public class FeedbackActivity extends MvpActivity<FeedbackView,FeedbackPresenter> implements FeedbackView,View.OnClickListener {
    private EditText txtEdit;
    private RadioAdapter radioAdapter;

    String typeId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
//        findViewById(R.id.my_feedback).setOnClickListener(this);
        setEdit("我的反馈",this);
        setTitle("意见反馈");

        findViewById(R.id.btn_add).setOnClickListener(this);
        txtEdit=findViewById(R.id.editTxt);

        radioAdapter=new RadioAdapter(this);
        RecyclerView recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.setAdapter(radioAdapter);

        radioAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<FeedbackBean.FeedbackItem>() {
            @Override
            public void onItemClick(FeedbackBean.FeedbackItem item, int position) {
                changeSelect(position);
            }
        });

    }
    private void changeSelect(int positon){
        List<FeedbackBean.FeedbackItem> feedbackItems= radioAdapter.dataList;
        for(int i=0;i<feedbackItems.size();i++){
            feedbackItems.get(i).selected=false;
            if(i==positon){
                feedbackItems.get(i).selected=true;
                typeId=feedbackItems.get(i).getShortId();
            }
        }
        radioAdapter.notifyDataSetChanged();
    }
    @Override
    protected void initData() {
        presenter.getFeedbackTypes();
    }
    @Override
    public FeedbackPresenter initPresenter() {
        return new FeedbackPresenter();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_txt:
                Intent intent=new Intent(this,FeedbackRecodeActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_add:
                sendToAddFeed();
                break;
        }
    }
    private void sendToAddFeed(){
        String txt=txtEdit.getText().toString().trim();
        if(txt.length()<=0){
            toast("请输入反馈内容");
            return;
        }
        List<FeedbackBean.FeedbackItem> items= radioAdapter.dataList;
        for(FeedbackBean.FeedbackItem item:items){
            if(item.selected){
                typeId=item.getShortId();
            }
        }
        if(typeId==null||typeId.length()<=0){
            toast("请选择类型");
            return;
        }
        if(txt.length()>100){
            toast("输入内容不能超过100个字符");
            return;
        }
        presenter.addFeedback(typeId,txt);
    }
    @Override
    public void onTypeResult(FeedbackBean feedbackBean) {
        radioAdapter.setData(feedbackBean.getData());
    }

    @Override
    public void onAddResult(LikeBean likeBean) {
        if(likeBean.getStatus()!=200){
            toast(likeBean.getMessage());
            return;
        }
        txtEdit.setText("");
        List<FeedbackBean.FeedbackItem> feedbackItems= radioAdapter.dataList;
        for(FeedbackBean.FeedbackItem item:feedbackItems){
            item.selected=false;
        }
        radioAdapter.notifyDataSetChanged();
        toast("反馈成功");
    }

    @Override
    public void onRecodeResult(FeedbackRecodeBean recodeBean) {
    }


}
