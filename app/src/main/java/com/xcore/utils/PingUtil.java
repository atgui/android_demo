package com.xcore.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PingUtil {
    /**
     * @param address 地址
     * @return 根据地址，返回该地址ping速度，如果不可用，返回-1f
     */
    public static float getSpeed(String address){
        String[] addrs = {"www.baidu.com"};
        if (addrs.length < 1) {
            System.out.println("syntax Error!");
        } else {
            for (int i = 0; i < addrs.length; i++) {
                String line = null;
                try {
                    Process pro = Runtime.getRuntime().exec("ping " + addrs[i] + " -l 1000 -n 4");
                    BufferedReader buf = new BufferedReader(new InputStreamReader(pro.getInputStream()));
                    while ((line = buf.readLine()) != null) {
                        int position = 0;
                        if ((position = line.indexOf("Average")) >= 0) {
                            Log.e("TAG", line);
                            String value = "/blog/line.substring(position+10,line.lastIndexOf(ms))";
                            Log.e("TAG", "your speed is:" + (1000 / Integer.parseInt(value)) + "KB");
                        }
                    }
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        return 0;
    }
}
