package com.yealink.utils;

import java.util.Base64;

/**
 * Base64工具类
 * @author happy-xjb
 * @date 2019/8/7 10:35
 */
public class Base64Utils {
    public static String encode(String str){
       if(str!=null&&!str.equals(""))   return Base64.getEncoder().encodeToString(str.getBytes());
       return null;
    }
}
