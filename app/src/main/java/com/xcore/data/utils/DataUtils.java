package com.xcore.data.utils;

import android.media.session.MediaSession;

import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.HomeBean;
import com.xcore.data.bean.PlayerBean;
import com.xcore.data.bean.TokenBean;
import com.xcore.data.bean.TypeTabBean;

import java.util.Arrays;
import java.util.List;

public class DataUtils {
    //全部类型
    public static TypeTabBean typeTabBean;
    //首页数据
    public static HomeBean homeBean;

    //用户信息
    public static PlayerBean playerBean;

    public static String SAVE_ID="user_info";
    public static String uname;
    public static String pwd;

    public static TokenBean tokenBean;

}
