package com.xcore.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.widget.vertical.VerticalScrollView;
import com.nex3z.flowlayout.FlowLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.base.MvpActivity;
import com.xcore.cache.beans.DictionaryBean;
import com.xcore.data.bean.SearchBean;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.bean.TypeListDataBean;
import com.xcore.presenter.SearchPresenter;
import com.xcore.presenter.view.SearchView;
import com.xcore.ui.adapter.SearchAdapter;
import com.xcore.ui.adapter.TypeItemAdapter;
import com.xcore.ui.touch.DrawableUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchActivity extends MvpActivity<SearchView,SearchPresenter> implements SearchView {
    private EditText searchEdit;

    private LinearLayout emptLayout;
    private LinearLayout contentLayout;
    private VerticalScrollView vScrollView;

    private FlowLayout hositoryFlowLayout;
    private ImageView btn_del;
    private LinearLayout hLayout;

    private SmartRefreshLayout refreshLayout;
    private TypeItemAdapter itemAdapter;
    private ImageView btnClose;

    private int pageIndex=1;
    private boolean isMore=true;
    private String tag="";

    private RecyclerView hotRecyclerView;
    private SearchAdapter searchAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
//        initTransition();

        emptLayout=findViewById(R.id.emptLayout);
        emptLayout.setVisibility(View.GONE);

        hLayout=findViewById(R.id.hLayout);
        hLayout.setVisibility(View.GONE);

        hositoryFlowLayout=findViewById(R.id.hFlowLayout);
        btn_del=findViewById(R.id.btn_del);
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });

        vScrollView=findViewById(R.id.vScrollView);

        contentLayout=findViewById(R.id.contentLayout);
        btnClose=findViewById(R.id.btn_close);
        btnClose.setVisibility(View.GONE);

        contentLayout.setVisibility(View.GONE);
        vScrollView.setVisibility(View.GONE);

        //设置搜索框
        searchEdit=findViewById(R.id.edit_search);
        Drawable drawable=getResources().getDrawable(R.drawable.search_bg);
        drawable.setBounds(5,0,70,70);
        searchEdit.setCompoundDrawables(drawable,null,null,null);
        new DrawableUtil(searchEdit, new DrawableUtil.OnDrawableListener() {
            @Override
            public void onLeft(View v, Drawable left) {
                toSearch();
            }
            @Override
            public void onRight(View v, Drawable right) {
                //toClose();
            }
        });
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_SEARCH) {//&&(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)
                    toSearch();
                    return true;
                }
                return false;
            }
        });
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String value=s.toString().trim();
                if(value.length()>0){
                    btnClose.setVisibility(View.VISIBLE);
                }else{
                    btnClose.setVisibility(View.GONE);
                }
            }
        });
        //点击取消
        findViewById(R.id.txt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchAdapter=new SearchAdapter(this);
        hotRecyclerView=findViewById(R.id.hotRecyclerView);
        hotRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        hotRecyclerView.setAdapter(searchAdapter);
        searchAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<SearchBean.SearchItem>() {
            @Override
            public void onItemClick(SearchBean.SearchItem item, int position) {
                searchEdit.setText(item.getName());
                pageIndex=1;
                toSearch();
            }
        });


        refreshLayout=findViewById(R.id.refreshLayout);
        RecyclerView recyclerView= findViewById(R.id.recyclerView);

        itemAdapter=new TypeItemAdapter(this);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(itemAdapter);

        itemAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<TypeListDataBean>() {
            @Override
            public void onItemClick(TypeListDataBean item, int position) {
                //跳到详情页面
//                Intent intent = new Intent(SearchActivity.this, VideoActivity.class);
//                String torrentUrl ="http://23.234.12.131:81/002.torrent";//"http://demo.flvurl.cn/dianyunMovieBT/4667496-1hd.mp4.torrent";//
//                String url = P2PTrans.getTorrentPlayUrl(BaseCommon.P2P_SERVER_PORT, torrentUrl);
//                intent.putExtra("url", url);
//                intent.putExtra("torrentUrl", torrentUrl);
//                intent.putExtra("shortId",item.getShortId());
//                startActivity(intent);
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageIndex=1;
                refreshData();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if(isMore==false){
                    refreshLayout.finishLoadMore(1000);
                    return;
                }
                refreshData();
            }
        });
    }

//    //过渡动画
//    private void initTransition() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            postponeEnterTransition();
//            ViewCompat.setTransitionName(findViewById(R.id.search_content), IMG_TRANSITION);
//            addTransitionListener();
//            startPostponedEnterTransition();
//        }else {
////            loadVideoInfo()
//        }
//    }
//    private Transition transition = null;
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    private void addTransitionListener() {
//        transition = getWindow().getSharedElementEnterTransition();
//        transition.addListener(new Transition.TransitionListener() {
//            @Override
//            public void onTransitionStart(Transition transition) {
//
//            }
//
//            @Override
//            public void onTransitionEnd(Transition transition) {
//                transition.removeListener(this);
//            }
//
//            @Override
//            public void onTransitionCancel(Transition transition) {
//
//            }
//
//            @Override
//            public void onTransitionPause(Transition transition) {
//
//            }
//
//            @Override
//            public void onTransitionResume(Transition transition) {
//
//            }
////            onTouchEvent()
////            onTransitionResume(p0: Transition?) {
////            }
////
////            override fun onTransitionPause(p0: Transition?) {
////            }
////
////            override fun onTransitionCancel(p0: Transition?) {
////            }
////
////            override fun onTransitionStart(p0: Transition?) {
////            }
////
////            override fun onTransitionEnd(p0: Transition?) {
////                //Logger.d("onTransitionEnd()------")
////
////                loadVideoInfo()
////                transition?.removeListener(this)
////            }
//
//        });
//    }

//    /**
//     * 退场动画
//     */
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    private void setUpExitAnimation() {
//        Fade fade = new Fade();
//        getWindow().setReturnTransition(fade);//.returnTransition = fade
//        fade.setDuration(300);//.duration = 300
//    }

    private void delete(){
        for(String item:hItems){
            presenter.delteKey(item);
        }
        presenter.getHDicKey();
    }
    @Override
    protected void initData() {
        presenter.getHDicKey();
        presenter.getHotDicKey();
    }
    @Override
    public void onBack() {
        //点击清除搜索
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toClose();
            }
        });
    }
    @Override
    public SearchPresenter initPresenter() {
        return new SearchPresenter();
    }
    private List<String> hItems=new ArrayList<>();
    @Override
    public void getHostoryDictionary(List<DictionaryBean> dictionaryBeans) {
        vScrollView.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);

        hLayout.setVisibility(View.GONE);
        hositoryFlowLayout.removeAllViews();
        hItems.clear();
        if(dictionaryBeans!=null&&dictionaryBeans.size()>0){
            for(DictionaryBean dic:dictionaryBeans){
                TextView textView=getText(dic.getDicName(),R.drawable.type_tag_play);
                hositoryFlowLayout.addView(textView);
                hItems.add(dic.getDicName());
            }
            hLayout.setVisibility(View.VISIBLE);
        }
    }
    private float dpToPx(float dp){
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, this.getResources().getDisplayMetrics());
    }
    private TextView getText(final String txt,int res){
        TextView textView=new TextView(this);
        textView.setText(txt);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textView.setBackgroundResource(R.drawable.tag_default);
        textView.setPadding((int)dpToPx(12), (int)dpToPx(2), (int)dpToPx(12), (int)dpToPx(2));
        textView.setTextColor(this.getResources().getColor(R.color.title_color));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEdit.setText(txt);
                toSearch();
            }
        });
        return textView;
    }

    @Override
    public void getHotDictionary(SearchBean searchBean) {
        vScrollView.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);

        List<SearchBean.SearchItem> searchItems= searchBean.getData();
        Collections.sort(searchItems,new SearchSort());
        searchAdapter.setData(searchItems);
    }

    class SearchSort implements Comparator<SearchBean.SearchItem> {

        @Override
        public int compare(SearchBean.SearchItem o1, SearchBean.SearchItem o2) {
            return o1.getSort()-o2.getSort();
        }
    }

    //得到搜索结果
    @Override
    public void onResult(TypeListBean typeListBean) {
        if(refreshLayout!=null){
            refreshLayout.finishLoadMore();
            refreshLayout.finishRefresh();
        }

        vScrollView.setVisibility(View.GONE);
        contentLayout.setVisibility(View.VISIBLE);

        if(typeListBean.getList().size()<=0){
            isMore=false;
            if(itemAdapter.dataList.size()<=0) {
                emptLayout.setVisibility(View.VISIBLE);
                refreshLayout.setVisibility(View.GONE);
            }
            return;
        }
        emptLayout.setVisibility(View.GONE);
        refreshLayout.setVisibility(View.VISIBLE);

        if(typeListBean.getPageIndex()==1){
            itemAdapter.setData(typeListBean.getList());
        }else{
            itemAdapter.dataList.addAll(typeListBean.getList());
            itemAdapter.notifyDataSetChanged();
        }
    }

    //去搜索
    private void toSearch(){
        tag=searchEdit.getText().toString().trim();
        if(tag.length()<=0){
            return;
        }
        //取消焦点
        searchEdit.clearFocus();
        //隐藏键盘
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);

        pageIndex=1;
        isMore=true;
        refreshData();

        //把搜索的保存到数据库中
        presenter.addDicKey(tag);
    }
    private void refreshData(){
        presenter.getSearchResult(pageIndex,tag);
        pageIndex++;
    }
    //关闭
    private void toClose(){
        pageIndex=1;
        tag="";
        isMore=true;
        vScrollView.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);

        searchEdit.setText("");
    }

}
