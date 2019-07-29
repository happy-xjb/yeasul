package com.yealink.dao;

import com.yealink.entities.RegisterInfo;

import java.util.List;

public interface RegisterInfoMapper {
    int insert(RegisterInfo record);

    int insertSelective(RegisterInfo record);

    List<String> selectServiceIdByDatacenter(String datacenter);
}