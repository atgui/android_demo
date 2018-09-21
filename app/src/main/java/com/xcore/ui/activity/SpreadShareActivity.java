package com.xcore.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.base.BaseActivity;
import com.xcore.data.bean.PlayerBean;
import com.xcore.data.bean.UserInfo;
import com.xcore.data.utils.DataUtils;
import com.xcore.utils.QRCodeUtils;
import com.xcore.utils.SystemUtils;

public class SpreadShareActivity extends BaseActivity{
    String qcodeStr="http://www.baidu.com";
    String qrcodeUrl="";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_spread_share;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("推广分享");
        setEdit("");

        ImageView imageView=findViewById(R.id.qrecode);
        TextView mTextView=findViewById(R.id.infoTxt);

        TextView txt_shareCode=findViewById(R.id.txt_shareCode);

        PlayerBean playerBean= DataUtils.playerBean;
        if(playerBean!=null) {
            UserInfo userInfo = playerBean.getData();
            mTextView.setText(userInfo.getShareText());
            qcodeStr=userInfo.getShareUrl();
            qrcodeUrl=userInfo.getQrcodeUrl();
            txt_shareCode.setText(userInfo.getShareCode());
        }

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e("TAG","长按了");
                //跳网页
                Intent intent=new Intent( Intent.ACTION_VIEW );
                intent.setData( Uri.parse( qrcodeUrl) ); //这里面是需要调转的rul
                intent = Intent.createChooser( intent, null );
                startActivity(intent);
                return false;
            }
        });

        //生成二维码
        QRCodeUtils.qrCode(qrcodeUrl,imageView,200,200);
        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SystemUtils.captureScreen(SpreadShareActivity.this);
                saveQrcode();
            }
        });

        findViewById(R.id.btn_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            boolean boo=SystemUtils.copy(qcodeStr,SpreadShareActivity.this);
            if(boo){
                toast("复制成功,快去分享吧!!!");
            }
            }
        });
    }
    private void saveQrcode(){
        try {
            SystemUtils.captureScreen(SpreadShareActivity.this);
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
        }
    }

    @Override
    protected void initData() {
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        MainApplicationContext.onWindowFocusChanged(hasFocus,this);
//    }
}
