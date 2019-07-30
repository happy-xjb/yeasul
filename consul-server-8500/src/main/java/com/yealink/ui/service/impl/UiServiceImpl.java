package com.yealink.ui.service.impl;

import com.yealink.dao.*;
import com.yealink.entities.Check;
import com.yealink.entities.Node;
import com.yealink.entities.ServiceInstance;
import com.yealink.ui.service.UiService;
import com.yealink.ui.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UiServiceImpl implements UiService {
    @Autowired
    ServiceNameMapper serviceNameMapper;

    @Autowired
    ServiceTagMapper serviceTagMapper;

    @Autowired
    ServiceInstanceMapper serviceInstanceMapper;

    @Autowired
    CheckMapper checkMapper;
    @Autowired
    RegisterInfoMapper registerInfoMapper;
    @Autowired
    NodeMapper nodeMapper;
    @Value("${consul.config.datacenter}")
    String datacenter;

    @Override
    public Map<String, ServiceVO> getAllServiceDetails() {
        Map<String,ServiceVO> map = new HashMap<>();
//        List<String> serviceNameList = serviceNameMapper.selectAll();
        List<String> serviceNameList = registerInfoMapper.selectAllServiceInDatacenter(datacenter);
        for(String serviceName:serviceNameList){
            ServiceVO serviceVO = new ServiceVO();
            serviceVO.setTags(serviceTagMapper.selectByServiceName(serviceName));
            List<NodeAndCheckVO> nodeAndCheckVOList = new ArrayList<>();
            List<String> nodeIdList = registerInfoMapper.selectAllNodeIdByServiceInDatacenter(serviceName, datacenter);
            for(String nodeId : nodeIdList){
                NodeAndCheckVO nodeAndCheckVO = new NodeAndCheckVO();
                Node node = nodeMapper.selectByPrimaryKey(nodeId);
                nodeAndCheckVO.setNode(node);
                List<Check> checkList = checkMapper.selectAllByNodeNameAndService(node.getName(), serviceName);

                nodeAndCheckVO.setCheckList(checkList);
                nodeAndCheckVOList.add(nodeAndCheckVO);
            }

            serviceVO.setNodeAndCheckVOList(nodeAndCheckVOList);
            map.put(serviceName,serviceVO);
        }
        return map;
    }

    @Override
    public Map<String, NodeVO> getAllNodeDetails() {
        Map<String,NodeVO> map = new HashMap<>();
        List<String> nodeNameList = nodeMapper.selectAllNodeNameInDatacenter(datacenter);
        for(String nodeName : nodeNameList){
            NodeVO nodeVO = new NodeVO();
            Node node = nodeMapper.selectByNodeName(nodeName);
            nodeVO.setNode(node);
            List<ServiceWithTagsVO> serviceWithTagsVOList = new ArrayList<>();
            List<String> serviceIdList = registerInfoMapper.selectAllServiceIdByNodeIdAndDatacenter(node.getNodeId(), datacenter);
            for(String serviceId : serviceIdList){
                ServiceWithTagsVO serviceWithTagsVO = new ServiceWithTagsVO();
                serviceWithTagsVO.setServiceInstance(serviceInstanceMapper.selectByPrimaryKey(serviceId));
                serviceWithTagsVO.setTags(serviceTagMapper.selectByServiceId(serviceId));
                serviceWithTagsVOList.add(serviceWithTagsVO);
            }
            nodeVO.setServiceWithTagsVOList(serviceWithTagsVOList);
            nodeVO.setCheckList(checkMapper.selectAllByNodeName(nodeName));
            map.put(nodeName,nodeVO);
        }

        return map;
    }
}
