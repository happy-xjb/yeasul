package com.yealink.dao;

import com.yealink.entities.RegisterInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RegisterInfoMapper {
    int insert(RegisterInfo record);

    int insertSelective(RegisterInfo record);

    List<String> selectServiceIdByDatacenter(String datacenter);

    List<String> selectServiceIdListByDatacenterAndService(@Param("datacenter") String datacenter,@Param("service") String service);

    List<String> selectAllServiceInDatacenter(String datacenter);

    int updateDatacenterByNodeId(@Param("node_id") String nodeId,@Param("datacenter") String datacenter);

    List<String> selectAllNodeIdByServiceInDatacenter(@Param("service") String service,@Param("datacenter") String datacenter);

    List<String> selectAllServiceIdByNodeIdAndDatacenter(@Param("nodeId") String nodeId,@Param("datacenter") String datacenter);

    String selectNodeIdByServiceId(String serviceId);
}