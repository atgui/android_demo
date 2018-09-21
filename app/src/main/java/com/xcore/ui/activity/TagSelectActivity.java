package com.xcore.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.nex3z.flowlayout.FlowLayout;
import com.xcore.R;
import com.xcore.base.MvpActivity;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.TabBean;
import com.xcore.data.bean.Tag;
import com.xcore.data.bean.TagBean;
import com.xcore.presenter.TagSelectPreseneter;
import com.xcore.presenter.view.TagSelectView;
import com.xcore.ui.adapter.TagSelectAdapter;
import com.xcore.ui.adapter.TypeTabAdapter;
import com.xcore.ui.fragment.TagSelectFragment;
import com.xcore.ui.touch.ITagOnClick;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagSelectActivity extends MvpActivity<TagSelectView,TagSelectPreseneter> implements TagSelectView,ITagOnClick {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tag_select;
    }
    private ViewPager mViewpager;
    private FlowLayout flowLayout;
    private TextView selectTxt;
    private RecyclerView change_recyclerView;
    private TagSelectAdapter changeAdapter;

    //已经选择了的标签
    private List<Tag> selectedCategories=new ArrayList<>();

    //正在选择的标签,可删除
    private List<Tag> changeCategories=new ArrayList<>();

    private List<TagSelectFragment> fragments;

    private String toTag="";

    public Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    break;
                case 1://选中
                    Tag tag= (Tag) msg.obj;
                    if(tag.isChecked()==true) {
                        boolean isBoo=false;
                        for(Tag t:changeCategories){
                            if(t.getName().equals(tag.getName())){
                                isBoo=true;
                                break;
                            }
                        }
                        if(isBoo==false) {
                            changeCategories.add(tag);
                        }
                    }else{
                        //从changeCategories中删除
                        for(Tag t:changeCategories){
                            if(t.getShortId().equals(tag.getShortId())){
                                changeCategories.remove(t);
                                break;
                            }
                        }
                    }
                    TagBean tagBean = new TagBean();
                    tagBean.setList(changeCategories);
                    //更新显示
                    changeAdapter.setData(Arrays.asList(tagBean));
                    break;
            }
        }
    };


    private float dpToPx(float dp){
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
    private TextView getText(final Tag tag, final int position){
        TextView textView=new TextView(this);
        textView.setTextColor(getResources().getColor(R.color.title_color));
        textView.setText(tag.getName());
        textView.setWidth((int) dpToPx(100));
        textView.setPadding((int)dpToPx(8),(int)dpToPx(5),(int)dpToPx(8),(int)dpToPx(5));
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,30);//4个参数按顺序分别是左上右下
        textView.setLayoutParams(layoutParams);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewpager.setCurrentItem(position);
                fragments.get(position).onTouch0(tag.getShortId());
//                changeTxt(textInfos.get(position),textInfos);
            }
        });
        return textView;
    }
    private void changeTxt(TextView curTxt,List<TextView> txts){
        curTxt.setBackgroundResource(R.drawable.tag_select_bg);
        for(TextView txt:txts){
            if(txt==curTxt){
                continue;
            }
            txt.setBackground(null);
        }
    }
    private List<TextView> textInfos=new ArrayList<>();
    private List<Tag> tags=new ArrayList<>();
    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("标签筛选");
        setEdit("确定",new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    close(100);
                }
            }
        );

        mViewpager=findViewById(R.id.mViewPager);

        flowLayout=findViewById(R.id.flowLayout);
        selectTxt=findViewById(R.id.selectTxt);
        change_recyclerView=findViewById(R.id.change_recyclerView);

        Intent intent= getIntent();
        toTag=intent.getStringExtra("toTag");
        String sTextArr=intent.getStringExtra("selectedTxtArr");
        if(sTextArr!=null&&sTextArr.length()>0){
            Type type = new TypeToken<List<Tag>>(){}.getType();
            selectedCategories= new Gson().fromJson(sTextArr,type);
        }

        String info="";
        int i=0;
        for(Tag item:selectedCategories){
            Tag tag=new Tag();
            tag.setName(item.getName());
            tag.setShortId(item.getShortId());
            tag.setChecked(true);
            changeCategories.add(tag);
            if(i>0){
                info+=" · "+item.getName();
            }else{
                info+=item.getName();
            }
            i++;
        }
        selectTxt.setText(info);

        TagBean tabBean=new TagBean();
        tabBean.setList(changeCategories);

        changeAdapter=new TagSelectAdapter(this,this);
        change_recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        change_recyclerView.setAdapter(changeAdapter);
        changeAdapter.setData(Arrays.asList(tabBean));


//        String[] texts=new String[]{"全部","进阶","剧情","玩法","主角","职业","服装"};
        fragments =new ArrayList<>();

        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                fragments.get(position).onTouch0(tags.get(position).getShortId());
                changeTxt(textInfos.get(position),textInfos);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    @Override
    protected void initData() {
        presenter.getTags();
    }
    //重写返回
    @Override
    public void onBack() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里要把选择的东西传加到上一个页面
                close(99);
            }
        });
    }

//    返回
    private void close(int resultCode){
        //这里要把选择的东西传加到上一个页面
        Intent intent=null;
        String key="";
        if(changeCategories.size()>0){
            int position=0;
            for(Tag tag:changeCategories){
                if(position>0){
                    key+=","+tag.getName();
                }else{
                    key+=tag.getName();
                }
                position++;
            }
        }
        //没有选择标签 直接关闭
        if(key.length()<=0){
            finish();
            return;
        }

        if(toTag!=""&&toTag!=null){
            intent=new Intent(this,TagActivity.class);
            intent.putExtra("key",key);
            intent.putExtra("toTag","1");
            startActivity(intent);
        }else{
            intent=new Intent();
            intent.putExtra("key",key);
            intent.putExtra("toTag","1");
            setResult(resultCode,intent);
        }
        finish();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            close(99);
        }
        return false;
    }

    @Override
    public TagSelectPreseneter initPresenter() {
        return new TagSelectPreseneter();
    }
    //点击删除
    @Override
    public void onClick(Tag tag) {
        //删除fragment 中对应的数据
        for(TagSelectFragment fragment:fragments){
            fragment.onTouch1(tag.getShortId());
        }
        try {
            //从changeCategories中删除
            for (Tag t : changeCategories) {
                if (t.getShortId().equals(tag.getShortId())) {
                    changeCategories.remove(t);
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        TagBean tagBean = new TagBean();
        tagBean.setList(changeCategories);
        //更新显示
        changeAdapter.setData(Arrays.asList(tagBean));
    }

    @Override
    public void onTagResult(TagBean tagBean) {
        int position=0;
        List<Tag> tags1 =tagBean.getData();

        for(Tag str:tags1){
            TextView txt=getText(str,position);
            flowLayout.addView(txt);
            TagSelectFragment fragment=TagSelectFragment.newInstance(str.getShortId(),position+"");
            fragments.add(fragment);
            position++;
            textInfos.add(txt);
            tags.add(str);
        }
        changeTxt(textInfos.get(0),textInfos);

        TagSelectFramentAdapter framentAdapter=new TagSelectFramentAdapter(getSupportFragmentManager(),fragments);
        mViewpager.setAdapter(framentAdapter);
    }

    @Override
    public void onResult(TagBean tagBean) {
    }


    class TagSelectFramentAdapter extends FragmentPagerAdapter{
        List<TagSelectFragment> framents=new ArrayList<>();

        public TagSelectFramentAdapter(FragmentManager fm,List<TagSelectFragment> fs) {
            super(fm);
            this.framents=fs;
        }

        @Override
        public Fragment getItem(int position) {
            return framents.get(position);
        }

        @Override
        public int getCount() {
            return framents.size();
        }
    }
}


