package com.yealink.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtil {

    //获得时间单位
    public static TimeUnit getTimeUnit(String time){
        if(time.endsWith("ns")) return TimeUnit.NANOSECONDS;
        if(time.endsWith("us")) return TimeUnit.MICROSECONDS;
        if(time.endsWith("ms")) return TimeUnit.MILLISECONDS;
        if(time.endsWith("s")) return TimeUnit.SECONDS;
        if(time.endsWith("m")) return TimeUnit.MINUTES;
        if(time.endsWith("h")) return TimeUnit.HOURS;
        return null;
    }

    //获得时间的数字部分
    public static int getTimeNum(String time){
        StringBuffer sb = new StringBuffer(time);
        //最后一个字符必删除
        sb.deleteCharAt(sb.length()-1);
        //判断是否为还有字母
        char c = sb.charAt(sb.length() - 1);
        if(c<48||c>57)  sb.deleteCharAt(sb.length()-1);
        return Integer.parseInt(sb.toString());
    }
}
