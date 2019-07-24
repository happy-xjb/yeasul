package com.yealink.dao;

import com.yealink.entities.ServiceName;

import java.util.List;

public interface ServiceNameMapper {
    int deleteByPrimaryKey(String service);

    int insert(ServiceName record);

    int insertSelective(ServiceName record);

    ServiceName selectByService(String serviceName);

    List<String> selectAll();
}