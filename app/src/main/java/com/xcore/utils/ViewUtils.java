package com.xcore.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.xcore.R;
import com.xcore.data.bean.TypeListDataBean;
import com.xcore.ui.activity.PVideoViewActivity;
import com.xcore.ui.activity.VideoActivity;

/**
 * 生成 VIEW
 */
public class ViewUtils {
    public static float dpToPx(Context context,float dp){
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
    public static TextView getText(Context context,String txt, int res){
        TextView textView=new TextView(context);
        textView.setText(txt);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textView.setPadding((int)dpToPx(context,10),
                (int)dpToPx(context,2),
                (int)dpToPx(context,10),
                (int)dpToPx(context,2));
        textView.setBackgroundResource(res);
        textView.setMaxEms(7);
        textView.setMaxLines(1);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTextColor(context.getResources().getColor(R.color.color_9c9c9c));
        return textView;
    }

    private static Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }
    /**
     * 设置图片颜色
     */
    public static void setImageColor(ImageView img,ColorStateList colorStateList){
        Drawable src =img.getDrawable();
        img.setImageDrawable(tintDrawable(src,colorStateList));
    }


    public static void toPlayer(Activity activity, View view,String shortId,String playerUrl,String position){
        Intent intent = new Intent(activity, PVideoViewActivity.class);
        intent.putExtra("shortId",shortId);
        if(!TextUtils.isEmpty(playerUrl)){
            intent.putExtra("playUrl",playerUrl);
        }
        if(!TextUtils.isEmpty(position)){
            intent.putExtra("position",Integer.valueOf(position));
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Pair pair = new Pair<>(view, "transitionImg");
            ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity, pair);
            ActivityCompat.startActivity(activity, intent, activityOptions.toBundle());
            activity.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        } else {
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

}
