package com.yealink.dao;

import com.yealink.entities.CheckInfo;

import java.util.List;

public interface CheckInfoMapper {
    int deleteByPrimaryKey(String checkId);

    int insert(CheckInfo record);

    int insertSelective(CheckInfo record);

    CheckInfo selectByPrimaryKey(String checkId);

    int updateByPrimaryKeySelective(CheckInfo record);

    int updateByPrimaryKey(CheckInfo record);

    List<CheckInfo> selectAll();
}