package com.xcore.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewStructure;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.MvpActivity;
import com.xcore.base.MvpFragment;
import com.xcore.data.bean.PlayerBean;
import com.xcore.data.bean.UpgradeBean;
import com.xcore.data.bean.UserInfo;
import com.xcore.data.utils.DataUtils;
import com.xcore.presenter.UpgradePresenter;
import com.xcore.presenter.view.UpgradeView;
import com.xcore.utils.CacheFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UpgradeActivity extends MvpActivity<UpgradeView,UpgradePresenter> implements UpgradeView {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_upgrade;
    }

    private PlayerBean playerBean;

    TextView vip_0Txt;
    ImageView vip0Img;
    TextView exp_pro;
    TextView day_exp;
    ProgressBar pro_express;
    TextView chaExp;
    ImageView vip1Img;
    TextView vip_1Txt;
    TextView codeTxt;
    TextView uname;

    TextView cacheCountText;
    TextView playCountText;

    ImageView item_avatar;

    LinearLayout toQcode;//二维码
    TextView myShare;//我的分享

    TextView dayCountLabel;

    LinearLayout youxianLinearLayout;
    LinearLayout wuxianLinearLayout;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        playerBean= DataUtils.playerBean;
        setTitle("推广升级");
        setEdit("我的推广",new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(MainApplicationContext.isGuest==true){
                MainApplicationContext.showips("未登录",UpgradeActivity.this,"toLogin");
                return;
            }
            Intent intent=new Intent(UpgradeActivity.this,MyShareActivity.class);
            startActivity(intent);
            }
        });
        toQcode=findViewById(R.id.toQcode);

        toQcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainApplicationContext.isGuest==true){
                    MainApplicationContext.showips("未登录",UpgradeActivity.this,"toLogin");
                    return;
                }
                Intent intent=new Intent(UpgradeActivity.this,SpreadShareActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_ljtg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(MainApplicationContext.isGuest==true){
                MainApplicationContext.showips("未登录",UpgradeActivity.this,"toLogin");
                return;
            }
            Intent intent=new Intent(UpgradeActivity.this,SpreadShareActivity.class);
            startActivity(intent);
            }
        });
        dayCountLabel=findViewById(R.id.day_countLabel);
        exp_pro=findViewById(R.id.exp_pro);
        day_exp=findViewById(R.id.day_exp);
        vip_0Txt=findViewById(R.id.vip_0Txt);
        vip0Img=findViewById(R.id.vip0Img);
        chaExp=findViewById(R.id.chaExp);
        pro_express=findViewById(R.id.pro_express);
        vip1Img=findViewById(R.id.vip1Img);
        vip_1Txt=findViewById(R.id.vip_1Txt);
        uname=findViewById(R.id.uname);
        codeTxt=findViewById(R.id.codeTxt);
        playCountText=findViewById(R.id.playCountText);
        cacheCountText=findViewById(R.id.cacheCountText);
        item_avatar=findViewById(R.id.item_avatar);

        wuxianLinearLayout=findViewById(R.id.wuxian);
        wuxianLinearLayout.setVisibility(View.GONE);

        youxianLinearLayout=findViewById(R.id.youxian);

        if(this.playerBean!=null){
            updateUser(this.playerBean.getData());
        }

        codeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(MainApplicationContext.isGuest==true){
                Intent intent=new Intent(UpgradeActivity.this, LoginActivity.class);
                intent.putExtra("toLogin","comeLogin");
                startActivity(intent);
            }
            }
        });
    }
    //更新用户信息
    @SuppressWarnings("deprecation")
    private void updateUser(UserInfo userInfo){
        pro_express.setMax(userInfo.getNextExperienceRange());
        pro_express.setProgress(userInfo.getExperienceRange());
        exp_pro.setText(userInfo.getExperienceRange()+"/"+userInfo.getNextExperienceRange());

        String hExp="今日获取<font color='#F9D649' size='25'>"+userInfo.getExperienceRangeToday()+"</font>点经验值";
        day_exp.setText(Html.fromHtml(hExp));
        int cExp=userInfo.getNextExperienceRange()-userInfo.getExperienceRange();
        String vExp="距离下一等级还差<font color='#F9D649' size='25'>"+cExp+"</font>经验";
        chaExp.setText(Html.fromHtml(vExp));

        vip0Img.setImageResource(userInfo.getRes(userInfo.getVip()));
        vip_0Txt.setText(userInfo.getVipStr(userInfo.getVip()));
        if(MainApplicationContext.isGuest==false) {
            codeTxt.setText("邀请码:" + userInfo.getShareCode());
        }else{
            codeTxt.setText("登录/注册");
        }
        uname.setText(userInfo.getName());

        String xxHtml="<font color='#F9D649'>"+userInfo.getExperienceRange()
                +"/</font><font color='#ffffff'>"+userInfo.getNextExperienceRange()+"</font>";
        exp_pro.setText(Html.fromHtml(xxHtml));

        if(userInfo.isSuperVIP()==true){//是无限观看了
            wuxianLinearLayout.setVisibility(View.VISIBLE);
            youxianLinearLayout.setVisibility(View.GONE);
            findViewById(R.id.fgLayout).setVisibility(View.GONE);
            findViewById(R.id.toQcode).setVisibility(View.GONE);


            ImageView vipResImage=findViewById(R.id.vipRes);
            vipResImage.setImageResource(userInfo.getRes(userInfo.getVip()));

            TextView vipStrTxt=findViewById(R.id.vipStr);
            vipStrTxt.setText(userInfo.getVipStr(userInfo.getVip()));
            dayCountLabel.setVisibility(View.GONE);
        }else{
            wuxianLinearLayout.setVisibility(View.GONE);
            dayCountLabel.setVisibility(View.VISIBLE);
            findViewById(R.id.toQcode).setVisibility(View.VISIBLE);
        }

        if(userInfo.getNextVip()==0||userInfo.getNextVip()==null){
            vip1Img.setVisibility(View.GONE);
            vip_1Txt.setText("100%");
        }else{
            vip_1Txt.setText(userInfo.getVipStr(userInfo.getNextVip()));
            vip1Img.setImageResource(userInfo.getRes(userInfo.getNextVip()));
        }
        playCountText.setText(userInfo.getPlayStr());
        cacheCountText.setText(userInfo.getCacheStr());

        CacheFactory.getInstance().getImage(this,item_avatar,userInfo.getHeadUrl());
    }

    @Override
    protected void initData() {
        presenter.getUpgrade();
        if(playerBean==null){
            presenter.getUserInfo();
        }
    }

    @Override
    public UpgradePresenter initPresenter() {
        return new UpgradePresenter();
    }

    @Override
    public void onUpgradeResult(UpgradeBean upgradeBean) {
        List<UpgradeBean.Item> items= upgradeBean.getData();
        Collections.sort(items, new Sortbyroll());
        for(UpgradeBean.Item item:items){
            initResItem(item);
        }
    }
    //赋值item
    @SuppressWarnings("deprecation")
    private void initResItem(UpgradeBean.Item item){
        int res=item.getRes();
        ImageView img=null;
        TextView info=null;
        TextView desc=null;

        switch (item.getVip()){
            case 1:
                img=findViewById(R.id.vip_1);
                info=findViewById(R.id.info_1);
                desc=findViewById(R.id.desc_1);
                break;
            case 2:
                img=findViewById(R.id.vip_2);
                info=findViewById(R.id.info_2);
                desc=findViewById(R.id.desc_2);
                break;
            case 3:
                img=findViewById(R.id.vip_3);
                info=findViewById(R.id.info_3);
                desc=findViewById(R.id.desc_3);
                break;
            case 4:
                img=findViewById(R.id.vip_4);
                info=findViewById(R.id.info_4);
                desc=findViewById(R.id.desc_4);
                break;
            case 5:
                img=findViewById(R.id.vip_5);
                info=findViewById(R.id.info_5);
                desc=findViewById(R.id.desc_5);
                break;
        }
        if(img!=null){
            img.setImageResource(res);
        }
        if(info!=null){
            info.setText(Html.fromHtml(item.getVipInfo()));
        }
        if(desc!=null){
            desc.setText(item.getDescription());
        }
    }

    @Override
    public void onResult(PlayerBean playerBean) {
        this.playerBean=playerBean;
        updateUser(this.playerBean.getData());
    }

    class Sortbyroll implements Comparator<UpgradeBean.Item>
    {
        // Used for sorting in ascending order of
        // roll number
        public int compare(UpgradeBean.Item a, UpgradeBean.Item b)
        {
            return a.getVip() - b.getVip();
        }
    }
}
