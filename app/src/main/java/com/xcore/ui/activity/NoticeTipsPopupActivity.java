package com.xcore.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xcore.R;
import com.xcore.cache.CacheManager;

public class NoticeTipsPopupActivity extends Activity {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_popup);

        final Intent intent=getIntent();
        String msg=intent.getStringExtra("content");
        String title=intent.getStringExtra("title");

        TextView txt_content=findViewById(R.id.content);
        txt_content.setMovementMethod(LinkMovementMethod.getInstance());
        txt_content.setText(getClickableHtml(msg));


        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(100,null);
                finish();
            }
        });
    }

    private void setLinkClickable(final SpannableStringBuilder clickableHtmlBuilder,
                                  final URLSpan urlSpan) {
        int start = clickableHtmlBuilder.getSpanStart(urlSpan);
        int end = clickableHtmlBuilder.getSpanEnd(urlSpan);
        int flags = clickableHtmlBuilder.getSpanFlags(urlSpan);
        ClickableSpan clickableSpan = new ClickableSpan() {
            public void onClick(View view) {
                //Do something with URL here.
                String url = urlSpan.getURL();
                Log.e("TAG",url);
                Intent intent1 = new Intent( Intent.ACTION_VIEW );//https://www.potato.im/1avco1
                intent1.setData( Uri.parse(url) ); //这里面是需要调转的rul
                Intent intent = Intent.createChooser( intent1, null );
                startActivity(intent);
            }
            public void updateDrawState(TextPaint ds) {
                //设置颜色
                ds.setColor(Color.argb(255, 255, 192, 0));
                //设置是否要下划线
                ds.setUnderlineText(false);
            }
        };
        clickableHtmlBuilder.setSpan(clickableSpan, start, end, flags);
    }

    private CharSequence getClickableHtml(String html) {
        Spanned spannedHtml = Html.fromHtml(html);
        SpannableStringBuilder clickableHtmlBuilder = new SpannableStringBuilder(spannedHtml);
        URLSpan[] urls = clickableHtmlBuilder.getSpans(0, spannedHtml.length(), URLSpan.class);
        for (final URLSpan span : urls) {
            setLinkClickable(clickableHtmlBuilder, span);
        }
        return clickableHtmlBuilder;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            setResult(100,null);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
