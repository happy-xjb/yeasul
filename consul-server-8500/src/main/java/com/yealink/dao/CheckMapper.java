package com.yealink.dao;

import com.yealink.entities.Check;
import org.apache.ibatis.annotations.Param;

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

    int updateStatusToPassingByPrimaryKey(String checkId);

    int updateStatusToFailingByPrimaryKey(String checkId);

    int updateOutputByPrimaryKey(@Param("checkId") String checkId,@Param("output") String output);

    List<Check> selectAllByNodeNameAndService(@Param("nodeName") String nodeName,@Param("service") String service);

    List<Check> selectAllByNodeName(String nodeName);
}