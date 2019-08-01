package com.yealink.dao;

import com.yealink.entities.Node;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NodeMapper {
    int deleteByPrimaryKey(String nodeId);

    int insert(Node record);

    int insertSelective(Node record);

    Node selectByPrimaryKey(String nodeId);

    int updateByPrimaryKeySelective(Node record);

    int updateByPrimaryKey(Node record);

    Node selectByAddress(String address);

    List<Node> selectAll();

    Node selectByAddressAndPort(@Param("address") String address,@Param("port") Integer port);

    Node selectByNameAndAddressAndDatacenter(@Param("name") String name ,@Param("address") String address,@Param("datacenter") String datacenter);
}