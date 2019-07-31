package com.yealink.service.impl;

import com.ecwid.consul.v1.health.model.Check;
import com.yealink.dao.CheckMapper;
import com.yealink.dao.NodeMapper;
import com.yealink.dao.ServiceInstanceMapper;
import com.yealink.dao.ServiceTagMapper;
import com.yealink.entities.Node;
import com.yealink.entities.ServiceInstance;
import com.yealink.service.HealthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HealthServiceImpl implements HealthService {
    @Autowired
    ServiceInstanceMapper serviceInstanceMapper;
    @Autowired
    NodeMapper nodeMapper;
    @Autowired
    CheckMapper checkMapper;
    @Autowired
    ServiceTagMapper serviceTagMapper;

    @Override
    public List<com.ecwid.consul.v1.health.model.HealthService> getHealthServices(String service) {
        List<com.ecwid.consul.v1.health.model.HealthService> list = new ArrayList<>();
        List<ServiceInstance> serviceInstances = serviceInstanceMapper.selectByServiceName(service);
        for(ServiceInstance serviceInstance: serviceInstances){
            //对于每个服务实例，都将生成对应的HealthService对象
            com.ecwid.consul.v1.health.model.HealthService healthService = new com.ecwid.consul.v1.health.model.HealthService();
            //对于每个服务实例，都将生成ServiceVO对象，封装进HealthService对象
            com.ecwid.consul.v1.health.model.HealthService.Service serviceVO = new com.ecwid.consul.v1.health.model.HealthService.Service();
            //对于每个服务实例，都有相应节点，并封装进HealthService对象
            com.ecwid.consul.v1.health.model.HealthService.Node nodeVO = new com.ecwid.consul.v1.health.model.HealthService.Node();
            //对于每个服务实例，都有一个或多个检查，需封装进HealthService对象
            List<Check> checks =new ArrayList<>();
            //设置serviceVO
            BeanUtils.copyProperties(serviceInstance,serviceVO);
            serviceVO.setId(serviceInstance.getServiceInstanceId());
            serviceVO.setTags(serviceTagMapper.selectByServiceId(serviceInstance.getServiceInstanceId()));

            //设置nodeVO
            Node node = nodeMapper.selectByAddress(serviceInstance.getAddress());
            if(node!=null) {
                BeanUtils.copyProperties(node, nodeVO);
                nodeVO.setId(node.getNodeId());
                nodeVO.setNode(node.getName());
            }
            //设置checks
            List<com.yealink.entities.Check> checkList = checkMapper.selectByServiceId(serviceInstance.getServiceInstanceId());
            for(com.yealink.entities.Check checkFromDB: checkList){
                Check check = new Check();
                BeanUtils.copyProperties(checkFromDB,check);
                if(checkFromDB.getStatus().equals("passing"))   check.setStatus(Check.CheckStatus.PASSING);
                else if(checkFromDB.getStatus().equals("warning"))  check.setStatus(Check.CheckStatus.WARNING);
                else if(checkFromDB.getStatus().equals("critical"))  check.setStatus(Check.CheckStatus.CRITICAL);
                else if(checkFromDB.getStatus().equals("unknown"))  check.setStatus(Check.CheckStatus.UNKNOWN);
                check.setServiceTags(serviceTagMapper.selectByServiceId(serviceInstance.getServiceInstanceId()));
                checks.add(check);
            }
            //放入healthService
            healthService.setService(serviceVO);
            healthService.setNode(nodeVO);
            healthService.setChecks(checks);
            list.add(healthService);
        }
        return list;
    }
}
