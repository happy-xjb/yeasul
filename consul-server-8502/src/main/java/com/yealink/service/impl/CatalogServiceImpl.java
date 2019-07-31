package com.yealink.service.impl;

import com.yealink.dao.ServiceNameMapper;
import com.yealink.dao.ServiceTagMapper;
import com.yealink.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    private ServiceNameMapper serviceNameMapper;
    @Autowired
    private ServiceTagMapper serviceTagMapper;

    @Override
    public Map<String, List<String>> getCatalogServices() {
        Map<String,List<String>> map = new HashMap<String, List<String>>();
        List<String> serviceNameList = serviceNameMapper.selectAll();
        for(String serviceName:serviceNameList){
            List<String> tags = serviceTagMapper.selectByServiceName(serviceName);
            map.put(serviceName,tags);
        }
        return map;
    }
}
