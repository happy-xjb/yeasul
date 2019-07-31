package com.yealink.ui.service.impl;

import com.yealink.dao.CheckMapper;
import com.yealink.dao.ServiceInstanceMapper;
import com.yealink.dao.ServiceNameMapper;
import com.yealink.dao.ServiceTagMapper;
import com.yealink.entities.ServiceInstance;
import com.yealink.ui.service.UiService;
import com.yealink.ui.vo.ServiceAndCheckVO;
import com.yealink.ui.vo.ServiceVO;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public Map<String, ServiceVO> getAllServiceDetails() {
        Map<String,ServiceVO> map = new HashMap<>();
        List<String> serviceNameList = serviceNameMapper.selectAll();
        for(String serviceName:serviceNameList){
            ServiceVO serviceVO = new ServiceVO();
            serviceVO.setTags(serviceTagMapper.selectByServiceName(serviceName));
            List<ServiceAndCheckVO> serviceAndCheckVOList = new ArrayList<>();
            List<ServiceInstance> serviceInstanceList = serviceInstanceMapper.selectByServiceName(serviceName);
            for(ServiceInstance serviceInstance : serviceInstanceList){
                ServiceAndCheckVO serviceAndCheckVO = new ServiceAndCheckVO();
                serviceAndCheckVO.setServiceInstance(serviceInstance);
                serviceAndCheckVO.setChecks(checkMapper.selectByServiceId(serviceInstance.getServiceInstanceId()));
                serviceAndCheckVO.setTags(serviceTagMapper.selectByServiceId(serviceInstance.getServiceInstanceId()));
                serviceAndCheckVOList.add(serviceAndCheckVO);
            }
            serviceVO.setServiceAndCheckVOList(serviceAndCheckVOList);
            map.put(serviceName,serviceVO);
        }
        return map;
    }
}
