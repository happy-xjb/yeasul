package com.yealink.service.impl;

import com.ecwid.consul.v1.agent.model.Check;
import com.ecwid.consul.v1.agent.model.NewCheck;
import com.ecwid.consul.v1.agent.model.NewService;
import com.yealink.dao.*;
import com.yealink.entities.*;
import com.yealink.service.AgentService;
import com.yealink.utils.CheckUtil;
import com.yealink.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AgentServiceImpl implements AgentService {
    @Value("${consul.debug-config.bind-address}")
    String nodeAddress;

    @Value("${consul.config.datacenter}")
    String datacenter;

    @Value("${consul.config.node-name}")
    String nodeName;

    @Autowired
    RegisterInfoMapper registerInfoMapper;

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

    @Autowired
    CheckInfoMapper checkInfoMapper;

    @Override
    public Map<String, com.ecwid.consul.v1.agent.model.Service> getAgentServices() {
        Node node = nodeMapper.selectByAddress(nodeAddress);
        List<String> serviceIdList = registerInfoMapper.selectServiceIdByDatacenter(node.getDatacenter());
        Map<String, com.ecwid.consul.v1.agent.model.Service> map = new HashMap<>();
//        List<ServiceInstance> serviceInstances = serviceInstanceMapper.selectAll();
//        for(ServiceInstance serviceInstance : serviceInstances){
//            com.ecwid.consul.v1.agent.model.Service service = new com.ecwid.consul.v1.agent.model.Service();
//            service.setAddress(serviceInstance.getAddress());
//            service.setId(serviceInstance.getServiceInstanceId());
//            service.setPort(serviceInstance.getPort());
//            service.setService(serviceInstance.getService());
//            service.setTags(serviceTagMapper.selectByServiceId(serviceInstance.getServiceInstanceId()));
//            map.put(serviceInstance.getServiceInstanceId(),service);
//        }
        for(String serviceId : serviceIdList){
            ServiceInstance serviceInstance = serviceInstanceMapper.selectByPrimaryKey(serviceId);
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

        //将注册信息写入数据库
        Node node = nodeMapper.selectByAddress(nodeAddress);
        RegisterInfo registerInfo = new RegisterInfo();
        registerInfo.setNodeId(node.getNodeId());
        registerInfo.setServiceInstanceId(newService.getId());
        registerInfo.setDatacenter(node.getDatacenter());
        registerInfo.setService(newService.getName());
        registerInfoMapper.insert(registerInfo);
        log.info("【服务注册成功】"+newService);

        //TODO 如果服务注册时有检查信息，开启一个线程检查此服务的健康状态
        NewService.Check newServiceCheck = newService.getCheck();
        if(newServiceCheck!=null){
            if(newServiceCheck.getHttp()!=null&&!newServiceCheck.getHttp().equals(""))  checkUtil.startHttpCheck(newService);
            else if(newServiceCheck.getTcp()!=null&&!newServiceCheck.getTcp().equals(""))   checkUtil.startTcpCheck(newService);
        }
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

        //删除对应的check
        List<com.yealink.entities.Check> checkList = checkMapper.selectByServiceId(serviceId);
        for(com.yealink.entities.Check check :checkList){
            checkMapper.deleteByPrimaryKey(check.getCheckId());
            checkInfoMapper.deleteByPrimaryKey(check.getCheckId());
            log.info("[SUCCESS] delete the check with check id : "+check.getCheckId());
        }
        //删除对应的register_info
        registerInfoMapper.deleteByServiceId(serviceId);
        log.info("[Deregister Service] deregister service "+serviceId+" successfully");
    }

    /**
     * 返回本数据中心注册的所有的服务的检查
     * @return
     */
    @Override
    public Map<String, Check> getAgentChecks() {
        Map<String,Check> map = new HashMap<>();
//        List<com.yealink.entities.Check> checks = checkMapper.selectAll();
//        for(com.yealink.entities.Check check: checks){
//            Check checkVO = new Check();
//            BeanUtils.copyProperties(check,checkVO);
//            checkVO.setServiceTags(serviceTagMapper.selectByServiceId(check.getServiceId()));
//            map.put(check.getCheckId(),checkVO);
//        }
        List<String> serviceIdList = registerInfoMapper.selectServiceIdByDatacenter(datacenter);
        List<com.yealink.entities.Check> checks = new ArrayList<>();
        for(String serviceId : serviceIdList){
            List<com.yealink.entities.Check> checkList = checkMapper.selectByServiceId(serviceId);
            for(com.yealink.entities.Check check : checkList){
                checks.add(check);
            }
        }

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
        System.out.println("传入service层的newcheck"+newCheck);
        //TODO 将check信息插入数据库
        com.yealink.entities.Check check_db = new com.yealink.entities.Check();
        BeanUtils.copyProperties(newCheck,check_db);
        check_db.setCheckId(newCheck.getId());
//        check_db.setServiceName("需要得到服务名");
        ServiceInstance serviceInstance = serviceInstanceMapper.selectByPrimaryKey(newCheck.getServiceId());
        if(serviceInstance!=null){
            check_db.setServiceName(serviceInstance.getService());
            check_db.setName("Service '"+serviceInstance.getService()+"' check");
        }

        checkMapper.insertSelective(check_db);

        //得到循环时间和超时时间
        String interval = newCheck.getInterval();
        long interval_time = TimeUtil.getTimeNum(interval);
        TimeUnit interval_unit = TimeUtil.getTimeUnit(interval);
        long timeout = 0;
        String timeout_str = newCheck.getTimeout();
        if(timeout_str ==null|| timeout_str.equals(""))   timeout= TimeUnit.MILLISECONDS.convert(interval_time,interval_unit);
        else{
            TimeUnit timeout_unit = TimeUtil.getTimeUnit(timeout_str);
            long timeout_time = TimeUtil.getTimeNum(timeout_str);
            timeout=TimeUnit.MILLISECONDS.convert(timeout_time,timeout_unit);
        }

        //判断检查类型
        if(newCheck.getTcp()!=null){
            String tcp = newCheck.getTcp();
            String[] strings = tcp.split(":");
            String host = strings[0];
            int port = Integer.parseInt(strings[1]);

            checkUtil.startTcpCheck(newCheck.getId(),host,port,interval_time,timeout,interval_unit);
        }
        else if(newCheck.getHttp()!=null){
            checkUtil.startHttpCheck(check_db.getCheckId(),newCheck.getHttp(),interval_time,timeout,interval_unit);
        }

        //将checkInfo信息持久化入数据库，比如url，interval,timeout
        CheckInfo checkInfo = new CheckInfo();
        checkInfo.setTimeout(newCheck.getTimeout()).setNode(nodeName).setInterval(newCheck.getInterval()).setCheckId(newCheck.getId());
        if(newCheck.getHttp()!=null&&!newCheck.getHttp().equals(""))    checkInfo.setKind("http").setUrl(newCheck.getHttp());
        else if(newCheck.getTcp()!=null&&!newCheck.getTcp().equals("")) checkInfo.setKind("tcp").setUrl(newCheck.getTcp());
        checkInfoMapper.insertSelective(checkInfo);
    }

    @Override
    public void agentCheckDeregister(String checkId) {
        checkMapper.deleteByPrimaryKey(checkId);
        checkInfoMapper.deleteByPrimaryKey(checkId);
        log.info("[SUCCESS] delete the check with check id : "+checkId);
    }


}
