
#--------------------------1.固定配置区------------------------------#
#--------------------------1.1混淆指令区------------------------------#
-optimizationpasses 5 # 指定代码的压缩级别
-dontusemixedcaseclassnames # 是否使用大小写混合,包名不混合大小写
-dontskipnonpubliclibraryclasses #是否混淆第三方jar,不去忽略非公共的库类
-dontpreverify # 混淆时是否做预校验
#-ignorewarnings # 忽略警告，避免打包时某些警告出现
-dontskipnonpubliclibraryclassmembers
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
-dontoptimize #不优化输入的类文件
-overloadaggressively #混淆时应用侵入式重载
-verbose # 混淆时是否记录日志
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*  # 混淆时所采用的算法

#---------------------------1.2默认保留区----------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.ActivityGroup{
    *;
}
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-dontwarn android.support.**
-keep class android.support.** { *; }
-keep interface android.support.** { *; }

-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }

-dontwarn android.support.v7.**
-keep class android.support.v7.** {*;}
-keep interface android.support.v7.** {*;}

-keep class * implements android.os.Parcelable {*;} #保持 Parcelable 不被混淆
-keep class * implements java.io.Serializable {*;} #保持Serializable 不被混淆
-keep class * implements java.lang.Runnable {*;}
-keep class * implements java.lang.Cloneable {*;}

-keep public class * extends android.view.** {*;}
-keep public class * implements android.view.** {*;}
-keep public class * implements java.util.Observer {*;}
-keep public class * extends android.widget.**{*;}
-keep public class * implements android.widget.** {*;}
-keep public class * implements android.os.** {*;}

-keep public class android.os.AsyncTask{*;}

-keepclasseswithmembernames class * { #保持 native 方法不被混淆
    native <methods>;
}

-keepclasseswithmembers class * { #保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity { #保持自定义控件类不被混淆
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#Javascript接口
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
#webview
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String);
}
-keep class **.R$* {
 *;
}
-keepclassmembers class * {
    void *(**On*Event);
}

#打包时间要去除的  日志输出
#-assumenosideeffects class android.util.Log{
#    public static int e(...);
#}

#---------------------------------2.2.实体类---------------------------------
-keep class com.xcode.tinker.**{*;}
#---------------------------------2.3与js互相调用的类------------------------

#---------------------------------2.4反射相关的类和方法-----------------------
-keep class com.xcore.utils.StatusBarUtil {
    *;
}
#-dontwarn com.xcore.utils.StatusBarUtil

#-dontwarn fqcn.of.javascript.interface.for.Webview


#-keep class com.*.* {
#    *;
#}

#-keep class cn.dolit.** {
#    *;
#}
#-dontwarn cn.dolit.**

#-keep class android.media.ViviTV.player.widget.** {
#    *;
#}
#-dontwarn android.media.ViviTV.player.widget.**

#-keep class tv.danmaku.ijk.media.player.** {*;}
#-dontwarn tv.danmaku.ijk.media.player.**
##
#-keep class com.dou361.ijkplayer.widget.** {
#    *;
#}
#-keep class com.dou361.ijkplayer.widget.PlayerView{*;}

#-dontwarn com.dou361.ijkplayer.widget.IjkVideoView

#-dontwarn com.dou361.ijkplayer.widget.**


-keep @com.tencent.tinker.anno.DefaultLifeCycle public class *
-keep public class * extends android.app.Application {
    *;
}

-keep public class com.tencent.tinker.loader.app.ApplicationLifeCycle {
    *;
}
-keep public class * implements com.tencent.tinker.loader.app.ApplicationLifeCycle {
    *;
}

-keep public class com.tencent.tinker.loader.TinkerLoader {
    *;
}
-keep public class * extends com.tencent.tinker.loader.TinkerLoader {
    *;
}

-keep public class com.tencent.tinker.loader.TinkerTestDexLoad {
    *;
}

#your dex.loader pattern here
-keep class com.tencent.tinker.loader.**{*;}

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

#当然如果你确实不需要混淆okgo的代码,可以继续添加以下代码
#
#okgo
-keep class com.lzy.okgo.**{*;}
-dontwarn com.lzy.okgo.**

#okrx
#-dontwarn com.lzy.okrx.**
#-keep class com.lzy.okrx.**{*;}

#okrx2
#-dontwarn com.lzy.okrx2.**
#-keep class com.lzy.okrx2.**{*;}

#okserver
#-dontwarn com.lzy.okserver.**
#-keep class com.lzy.okserver.**{*;}


# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public class * extends com.bumptech.glide.module.AppGlideModule
##-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
##  **[] $VALUES;
##  public *;
##}
#-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
#  **[] $VALUES;
#  public *;
#}
#-keep class com.bumptech.** {
#    *;
#}
#-keepnames class com.xcode.utils.MyGlideModule
## for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule