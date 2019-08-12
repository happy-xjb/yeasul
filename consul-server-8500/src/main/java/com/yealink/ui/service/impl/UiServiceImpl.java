package com.yealink.ui.service.impl;

import com.yealink.dao.*;
import com.yealink.entities.Check;
import com.yealink.entities.Node;
import com.yealink.entities.ServiceInstance;
import com.yealink.entities.ServiceTag;
import com.yealink.ui.service.UiService;
import com.yealink.ui.vo.*;
import io.micrometer.core.instrument.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

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
    @Autowired
    KeyValueMapper keyValueMapper;
    @Value("${consul.config.datacenter}")
    String datacenter;

    @Override
    public Map<String, ServiceVO> getAllServiceDetails() {
        Map<String,ServiceVO> map = new HashMap<>();
//        List<String> serviceNameList = serviceNameMapper.selectAll();
        List<String> serviceNameList = registerInfoMapper.selectAllServiceInDatacenter(datacenter);
        for(String serviceName:serviceNameList){
            List<String> serviceIdList = registerInfoMapper.selectServiceIdListByDatacenterAndService(datacenter, serviceName);
            ServiceVO serviceVO = new ServiceVO();
            Set<String> tags = new HashSet<>();
            List<NodeAndCheckVO> nodeAndCheckVOList = new ArrayList<>();
            for(String serviceId : serviceIdList){
                List<String> tagListByServiceId = serviceTagMapper.selectByServiceId(serviceId);
                ServiceInstance serviceInstance = serviceInstanceMapper.selectByPrimaryKey(serviceId);
                NodeAndCheckVO nodeAndCheckVO = new NodeAndCheckVO();
                nodeAndCheckVO.setCheckList(checkMapper.selectByServiceId(serviceId));
                nodeAndCheckVO.setNode(nodeMapper.selectByPrimaryKey(registerInfoMapper.selectNodeIdByServiceId(serviceId)));

                nodeAndCheckVOList.add(nodeAndCheckVO);
                tags.addAll(tagListByServiceId);
            }
            serviceVO.setTags(tags);
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

    @Override
    public Map<String, String> getKVPathIndex(String currentPath) {
        List<String> pathList = Arrays.asList(currentPath.split("/"));
        Map<String,String> map  = new LinkedHashMap<>();
        StringBuffer sb = new StringBuffer();
        sb.append("/ui/kv/");
        for(String path : pathList){
            sb.append(path+"/");
            map.put(path,sb.toString());
        }
        return map;
    }

    @Override
    public Set<String> getKVCatalog(String currentPath) {
        List<String> keyList = keyValueMapper.selectByKeyPrefixAndDatacenter(currentPath, datacenter);
        Set<String> rsSet = new HashSet<>();
        for(String key : keyList){
            String nextPath = key.substring(currentPath.length());
            if(nextPath.indexOf('/')==-1)   rsSet.add(nextPath);
            else{
                int indexOfSeperator = nextPath.indexOf("/");
                rsSet.add(nextPath.substring(0,indexOfSeperator+1));
            }
        }
        return rsSet;
    }
}
