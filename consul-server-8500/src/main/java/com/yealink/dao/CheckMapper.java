package com.yealink.dao;

import com.yealink.entities.Check;

import java.util.List;

public interface CheckMapper {
    int deleteByPrimaryKey(String checkId);

    int insert(Check record);

    int insertSelective(Check record);

    Check selectByPrimaryKey(String checkId);

    int updateByPrimaryKeySelective(Check record);

    int updateByPrimaryKey(Check record);

    List<Check> selectAll();

    List<Check> selectByServiceId(String serviceId);
}