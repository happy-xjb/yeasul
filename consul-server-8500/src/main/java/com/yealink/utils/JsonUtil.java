package com.yealink.utils;

import com.ecwid.consul.v1.agent.model.NewService;
import com.google.gson.Gson;
import jodd.json.JsonParser;
import jodd.json.JsonSerializer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;

public class JsonUtil {

    private static Gson gson;
    static {
        gson = new Gson();
    }

    public static<T> T getObjectFromJson(HttpServletRequest request,Class<T> T){
        Object o = null;
        try {

            String line = request.getReader().readLine();
//            String val = URLDecoder.decode(line, "UTF-8");
            Object obj = JsonParser.create().parse(line);
            String json = JsonSerializer.create().deep(true).serialize(obj);
            o = gson.fromJson(json, T);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (T) o;
    }
}
