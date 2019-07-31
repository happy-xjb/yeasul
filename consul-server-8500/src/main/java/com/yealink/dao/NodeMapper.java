package com.yealink.dao;

import com.yealink.entities.Node;

public interface NodeMapper {
    int deleteByPrimaryKey(String nodeId);

    int insert(Node record);

    int insertSelective(Node record);

    Node selectByPrimaryKey(String nodeId);

    int updateByPrimaryKeySelective(Node record);

    int updateByPrimaryKey(Node record);

    Node selectByAddress(String address);
}