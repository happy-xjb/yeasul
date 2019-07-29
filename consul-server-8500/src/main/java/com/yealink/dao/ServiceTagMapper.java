package com.yealink.dao;

import com.yealink.entities.ServiceTag;

import java.util.List;
import java.util.Map;

public interface ServiceTagMapper {
    int insert(ServiceTag record);

    int insertSelective(ServiceTag record);

    List<String> selectByServiceId(String serviceId);

    int deleteAllByServiceId(String serviceId);

    List<String> selectByServiceName(String serviceName);

}