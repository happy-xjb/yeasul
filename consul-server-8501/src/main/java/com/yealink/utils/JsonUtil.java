package com.yealink.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.yealink.entities.Node;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonUtil {
    private static ObjectMapper objectMapper;

    private static Gson gson;
    static {
        gson = new Gson();
        objectMapper = new ObjectMapper();
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

    public static List<Node> getNodeListFromJson(String json){
        try {
            List<Map> list = objectMapper.readValue(json, List.class);
            List<Node> nodeList = new ArrayList<>();
            for (Map map : list){
                Node node = new Node();
                node.setPort((Integer) map.get("port"))
                        .setNodeId((String) map.get("nodeId"))
                        .setAddress((String) map.get("address"))
                        .setDatacenter((String) map.get("datacenter"))
                        .setName((String) map.get("name"));
                nodeList.add(node);
            }
            return nodeList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List<Node> getNodeListFromJson(HttpServletRequest request){
        try {
            String json = request.getReader().readLine();
            return getNodeListFromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
