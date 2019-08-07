package com.yealink.dao;

import com.yealink.entities.KeyValue;
import org.apache.ibatis.annotations.Param;

public interface KeyValueMapper {
    int insert(KeyValue record);

    int insertSelective(KeyValue record);

    KeyValue selectByKeyAndDatacenter(@Param("key") String key,@Param("datacenter") String datacenter);

    int update(KeyValue record);

    void delectByKeyAndDataceneter(@Param("key")String key,@Param("datacenter") String datacenter);
}