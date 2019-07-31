package com.yealink.utils;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JsonUtil {

    private static Gson gson;
    static {
        gson = new Gson();
    }

    public static<T> T getObjectFromJson(HttpServletRequest request,Class<T> T){
        Object o = null;
        try {
            o = gson.fromJson(request.getReader(),T);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (T) o;
    }
}
