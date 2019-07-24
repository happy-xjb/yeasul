package com.yealink.service.impl;

import com.ecwid.consul.v1.agent.model.Check;
import com.ecwid.consul.v1.agent.model.NewCheck;
import com.ecwid.consul.v1.agent.model.NewService;
import com.yealink.dao.*;
import com.yealink.entities.ServiceInstance;
import com.yealink.entities.ServiceName;
import com.yealink.entities.ServiceTag;
import com.yealink.service.AgentService;
import com.yealink.utils.CheckUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AgentServiceImpl implements AgentService {
    @Autowired
    CheckMapper checkMapper;

    @Autowired
    CloseableHttpClient httpClient;

    @Autowired
    CheckUtil checkUtil;

    @Autowired
    ServiceInstanceMapper serviceInstanceMapper;

    @Autowired
    ServiceTagMapper serviceTagMapper;

    @Autowired
    ServiceNameMapper serviceNameMapper;

    @Autowired
    NodeMapper nodeMapper;

    @Override
    public Map<String, com.ecwid.consul.v1.agent.model.Service> getAgentServices() {

        Map<String, com.ecwid.consul.v1.agent.model.Service> map = new HashMap<String, com.ecwid.consul.v1.agent.model.Service>();
        List<ServiceInstance> serviceInstances = serviceInstanceMapper.selectAll();
        for(ServiceInstance serviceInstance : serviceInstances){
            com.ecwid.consul.v1.agent.model.Service service = new com.ecwid.consul.v1.agent.model.Service();
            service.setAddress(serviceInstance.getAddress());
            service.setId(serviceInstance.getServiceInstanceId());
            service.setPort(serviceInstance.getPort());
            service.setService(serviceInstance.getService());
            service.setTags(serviceTagMapper.selectByServiceId(serviceInstance.getServiceInstanceId()));
            map.put(serviceInstance.getServiceInstanceId(),service);
        }
        return map;
    }

    @Override
    public void agentServiceRegister(NewService newService) {

        //服务名写入数据库
        //如果不存在再添加
        if(serviceNameMapper.selectByService(newService.getName()) == null)   serviceNameMapper.insert(new ServiceName().setService(newService.getName()));

        //将服务实例写入数据库
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setAddress(newService.getAddress());
        serviceInstance.setPort(newService.getPort());
        serviceInstance.setService(newService.getName());
        serviceInstance.setServiceInstanceId(newService.getId());
        if(serviceInstanceMapper.selectByPrimaryKey(newService.getId())!=null){
            serviceInstanceMapper.updateByPrimaryKey(serviceInstance);
            serviceTagMapper.deleteAllByServiceId(newService.getId());
        }else{
            serviceInstanceMapper.insert(serviceInstance);
        }

        //将标签写入数据库
        List<String> tags = newService.getTags();
        ServiceTag serviceTag = new ServiceTag();
        serviceTag.setService(newService.getName());
        serviceTag.setServiceId(newService.getId());
        for(String tag: tags){
            serviceTag.setValue(tag);
            serviceTagMapper.insert(serviceTag);
        }
        log.info("【服务注册成功】"+newService);

        //TODO 如果服务注册时有检查信息，开启一个线程检查此服务的健康状态
        checkUtil.startCheck(newService);

    }

    @Override
    public void agentServiceDeregister(String serviceId) {
        //删除标签
        serviceTagMapper.deleteAllByServiceId(serviceId);
        //从数据库删除服务实例
        ServiceInstance serviceInstance = serviceInstanceMapper.selectByPrimaryKey(serviceId);
        String service = serviceInstance.getService();
        serviceInstanceMapper.deleteByPrimaryKey(serviceId);
        //如果服务实例中没有对应的服务名，则从服务名表中删除对应服务名
        List<ServiceInstance> serviceInstanceList = serviceInstanceMapper.selectByServiceName(service);
        if(serviceInstanceList.size()==0){
            serviceNameMapper.deleteByPrimaryKey(service);
        }

        //TODO 删除对应的check
    }

    @Override
    public Map<String, Check> getAgentChecks() {
        List<com.yealink.entities.Check> checks = checkMapper.selectAll();
        Map<String,Check> map = new HashMap<>();
        for(com.yealink.entities.Check check: checks){
            Check checkVO = new Check();
            BeanUtils.copyProperties(check,checkVO);
            checkVO.setServiceTags(serviceTagMapper.selectByServiceId(check.getServiceId()));
            map.put(check.getCheckId(),checkVO);
        }
        return map;
    }

    @Override
    public void agentCheckRegister(NewCheck newCheck) {

    }


}
