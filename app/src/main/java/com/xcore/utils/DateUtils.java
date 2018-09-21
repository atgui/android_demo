package com.xcore.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    /**
     * 当前时间戳得到
     * @param
     * @return
     */
    public static String getDate(String format){
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        String sd = sdf.format(new Date(System.currentTimeMillis()));      // 时间戳转换成时间
        return sd;
    }

    /**
     * 时间戳转成时间
     * @param
     * @return
     */
    public static String getDate(String format,long date){
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        String sd = sdf.format(new Date(date));// 时间戳转换成时间
        return sd;
    }

    /**
     * 得到系统当前日期的前或者后几天
     *
     * @param iDate
     *                如果要获得前几天日期，该参数为负数； 如果要获得后几天日期，该参数为正数
     * @see java.util.Calendar#add(int, int)
     * @return Date 返回系统当前日期的前或者后几天
     */
    public static Date getDateBeforeOrAfter(int iDate) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, iDate);
        return cal.getTime();
    }

    /**
     * 得到日期的前或者后几天
     *
     * @param iDate
     *     如果要获得前几天日期，该参数为负数； 如果要获得后几天日期，该参数为正数
     * @see java.util.Calendar#add(int, int)
     * @return Date 返回参数<code>curDate</code>定义日期的前或者后几天
     */
    public static Date getDateBeforeOrAfter(Date curDate, int iDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        cal.add(Calendar.DAY_OF_MONTH, iDate);
        return cal.getTime();
    }

    /**
     * 把时间戳转成 分钟.秒
     * @param time
     * @return
     */
    public static String getM_S(long time){
        long s=time%60;//得到剩余的秒数
        int m=(int)time/60;//得到分钟数
        return m+"分钟";//+s+"秒";
    }

    /**
     * 把时间戳转成分钟
     * @param time
     * @return
     */
    public static String getMiss(long time){
        double m=time*1.0/1000/60;//
        String value=String.format("%.1f", m);
        return  value+"分钟";
    }

}
