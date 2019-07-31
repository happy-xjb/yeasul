package com.yealink.dao;

import com.yealink.entities.ServiceInstance;

import java.util.List;

public interface ServiceInstanceMapper {
    int deleteByPrimaryKey(String serviceInstanceId);

    int insert(ServiceInstance record);

    int insertSelective(ServiceInstance record);

    ServiceInstance selectByPrimaryKey(String serviceInstanceId);

    int updateByPrimaryKeySelective(ServiceInstance record);

    int updateByPrimaryKey(ServiceInstance record);

    List<ServiceInstance> selectAll();

    List<ServiceInstance> selectByServiceName(String serviceName);
}