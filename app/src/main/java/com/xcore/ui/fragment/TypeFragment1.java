package com.xcore.ui.fragment;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.nex3z.flowlayout.FlowLayout;
import com.xcore.R;
import com.xcore.base.MvpFragment1;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.TabBean;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.bean.TypeTabBean;
import com.xcore.data.utils.DataUtils;
import com.xcore.presenter.TypePresenter;
import com.xcore.presenter.view.ITypeView;
import com.xcore.ui.activity.SearchActivity;
import com.xcore.ui.touch.OnFragmentListener;
import com.xcore.utils.ViewUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeFragment1 extends MvpFragment1<ITypeView,TypePresenter> implements ITypeView,OnFragmentListener {
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    private ViewPager mViewPager;
    private FlowLayout one_flowLayout;
    private FlowLayout two_flowLayout;

    private int pageCurrent=0;//当前页

    private boolean isLoad=false;

    private String t0="";//最近更新
    private String t1="";//国产
    private String t2="";//分类

    ViewPagerAdapter adapter;
    //分页
    Map<String,Integer> pageIndexs=new HashMap<>();

    //fragments  分类list
    private List<TypeSubFragment> itemFragments=new ArrayList<>();
    private List<CategoriesBean> cList=new ArrayList<>();
    //txt 种类
    private List<TextView> txtList0=new ArrayList<>();
    private List<TextView> txtList1=new ArrayList<>();

    private Map<String,Boolean> loads=new HashMap<>();

    public TypeFragment1() {
    }

    public static TypeFragment1 newInstance(String param1, String param2) {
        TypeFragment1 fragment = new TypeFragment1();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM2, param2);
//        args.putString(ARG_PARAM1, param1);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public TypePresenter initPresenter() {
        return new TypePresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_blank;
    }

    @Override
    protected void initView(View view) {
        final ImageView searchImage=view.findViewById(R.id.edit_search);
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), SearchActivity.class);
                getActivity().startActivity(intent);
            }
        });

        one_flowLayout=view.findViewById(R.id.one_flowLayout);
        two_flowLayout=view.findViewById(R.id.two_flowLayout);

        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        TabLayout mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
        if(DataUtils.typeTabBean!=null) {
            final TabBean tabBean = DataUtils.typeTabBean.getData();
            setTab(tabBean);
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                Log.e("TAG","切换page:"+position);
                pageCurrent=position;
                String sId=cList.get(pageCurrent).getShortId();
                t2=sId;
//                boolean boo=loads.get(sId);
//                if(boo==false) {//没有加载过才加载
                    refreshData();
//                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    //刷新数据
    public void refreshData(){
        if(DataUtils.typeTabBean==null){
            presenter.getTags();
            return;
        }
        String shortId = cList.get(pageCurrent).getShortId();
        int pageIndex = pageIndexs.get(shortId);
        presenter.getViewData(pageIndex, t0, t1, t2, false);
    }

    private void setTab(TabBean tabBean){
        one_flowLayout.removeAllViews();
        two_flowLayout.removeAllViews();
        txtList0.clear();
        txtList1.clear();

        List<CategoriesBean> sortBeans=tabBean.getSorttype();
        if(sortBeans.size()>0&&t0.equals("")){
            t0=sortBeans.get(0).getShortId();
        }
        int sortPosition=0;
        for(int sortI=0;sortI<sortBeans.size();sortI++){
            CategoriesBean sBean=sortBeans.get(sortI);
            TextView txtV0=getText0(sBean,R.drawable.tag_default);
            if(sBean.getShortId().equals(t0)){
                sortPosition=sortI;
            }
            txtList0.add(txtV0);
            one_flowLayout.addView(txtV0);
        }
        changeTxt(txtList0,txtList0.get(sortPosition));

        List<CategoriesBean> species=tabBean.getSpecies();
        if(species.size()>0&&t1.equals("")){
            t1=species.get(0).getShortId();
        }
        int sPosition=0;
        for(int sI=0;sI<species.size();sI++){
            CategoriesBean sBean=species.get(sI);
            TextView txtV1=getText1(sBean,R.drawable.tag_default);
            two_flowLayout.addView(txtV1);
            if(sBean.getShortId().equals(t1)){
                sPosition=sI;
            }
            txtList1.add(txtV1);
        }
        changeTxt(txtList1,txtList1.get(sPosition));

        List<CategoriesBean> categoriesBeans=tabBean.getCategories();
        if(categoriesBeans.size()>0&&t2.equals("")){
            t2=categoriesBeans.get(0).getShortId();
        }
        if(adapter==null) {
            adapter = new ViewPagerAdapter(getChildFragmentManager());
            for (CategoriesBean item : categoriesBeans) {
                TypeSubFragment itemFragment = new TypeSubFragment();
                itemFragment.setmListener(this);
                adapter.addFragment(itemFragment, item.getName());
                itemFragments.add(itemFragment);
                cList.add(item);
                pageIndexs.put(item.getShortId(),1);
                loads.put(item.getShortId(),false);
            }
        }
        mViewPager.setAdapter(adapter);
    }

    private TextView getText0(final CategoriesBean cBean, int res){
        final TextView textView= ViewUtils.getText(getContext(),cBean.getName(),R.drawable.tag_default);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t0=cBean.getShortId();
                changeTxt(txtList0,textView);
                String shortId=cList.get(pageCurrent).getShortId();

                pageIndexs.put(shortId,1);
                refreshData();
            }
        });
        return textView;
    }
    private TextView getText1(final CategoriesBean cBean, int res){
        final TextView textView=ViewUtils.getText(getContext(),cBean.getName(),R.drawable.tag_default);// new TextView(getContext());
//        textView.setText(cBean.getName());
//        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
//        textView.setPadding((int)dpToPx(10), (int)dpToPx(2), (int)dpToPx(10), (int)dpToPx(2));
//        textView.setBackgroundResource(res);
//        textView.setTextColor(getResources().getColor(R.color.item_txt_color));
//        txtList1.add(textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                itemFragments.get(pageCurrent).onTouch1(cBean.getShortId());
                t1=cBean.getShortId();
                changeTxt(txtList1,textView);
                String shortId=cList.get(pageCurrent).getShortId();

                pageIndexs.put(shortId,1);
                refreshData();
            }
        });
        return textView;
    }
    private void changeTxt(List<TextView> txts,TextView txt){
        txt.setTextColor(getContext().getResources().getColor(R.color.title_color));
        for(TextView item : txts){
            if(item==txt){
                continue;
            }
            item.setTextColor(getContext().getResources().getColor(R.color.item_txt_color));
        }
    }

    @Override
    public void onTagResult(TypeTabBean typeTabBean) {
        try {
            setTab(typeTabBean.getData());
            refreshData();
        }catch (Exception e){}
    }

    @Override
    public void onViewResult(TypeListBean typeListBean) {
        try {
            String shortId = cList.get(pageCurrent).getShortId();
            loads.put(shortId, true);
            itemFragments.get(pageCurrent).setData(typeListBean);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String msg) {
        try {
            itemFragments.get(pageCurrent).onError(msg);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh(boolean isLoadMore) {
        String shortId=cList.get(pageCurrent).getShortId();
        int pageIndex=pageIndexs.get(shortId);
        if(isLoadMore==true){
            //加载更多
            pageIndex++;
        }else{//刷新
            pageIndex=1;
        }
        pageIndexs.put(shortId,pageIndex);
        refreshData();
    }
    private boolean isLoad1=false;
    private boolean isFirst=false;

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if(DataUtils.typeTabBean==null){
            presenter.getTags();
            return;
        }
        if(isFirst==false&&isLoad1==false){
            refreshData();
            isFirst=true;
            isLoad1=true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * 第一次进入界面时
     */
    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        Log.e("TAG","第一次进入页面时");
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
