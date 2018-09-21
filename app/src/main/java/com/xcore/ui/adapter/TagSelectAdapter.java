package com.xcore.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.TabBean;
import com.xcore.data.bean.Tag;
import com.xcore.data.bean.TagBean;
import com.xcore.data.bean.TypeListDataBean;
import com.xcore.ui.touch.ITagOnClick;

import java.util.ArrayList;
import java.util.List;

public class TagSelectAdapter extends BaseRecyclerAdapter<TagBean,TagSelectAdapter.ViewHolder>{
    private ITagOnClick tagOnClick;

    public TagSelectAdapter(Context mContext,ITagOnClick iTagOnClick) {
        super(mContext);
        this.tagOnClick=iTagOnClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.adapter_tag_select,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        final TagBean tabBean= dataList.get(position);

        FlowLayout flowLayout= holder.itemView.findViewById(R.id.flowLayout);
        flowLayout.removeAllViews();
        List<Tag> categoriesBeans= tabBean.getList();
        for(int i=0;i<categoriesBeans.size();i++){
            Tag item=categoriesBeans.get(i);
            TextView txt=getText(item,i);
            flowLayout.addView(txt);
        }
    }

    private float dpToPx(float dp){
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources().getDisplayMetrics());
    }
    private TextView getText(final Tag tag,final int position){
        TextView textView=new TextView(mContext);
        textView.setTextColor(mContext.getResources().getColor(R.color.title_color));
        textView.setText(tag.getName()+"  X");
        textView.setBackgroundResource(R.drawable.tag_select_bg);
        textView.setPadding((int)dpToPx(8),(int)dpToPx(5),(int)dpToPx(8),(int)dpToPx(5));
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                changeTxt(textInfos.get(position),textInfos);
                if(tagOnClick!=null){
                    tagOnClick.onClick(tag);
                }
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

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
