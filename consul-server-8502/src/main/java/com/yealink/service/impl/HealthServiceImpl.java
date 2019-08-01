package com.yealink.service.impl;

import com.ecwid.consul.v1.health.model.Check;
import com.google.gson.Gson;
import com.yealink.dao.CheckMapper;
import com.yealink.dao.NodeMapper;
import com.yealink.dao.ServiceInstanceMapper;
import com.yealink.dao.ServiceTagMapper;
import com.yealink.entities.Node;
import com.yealink.entities.ServiceInstance;
import com.yealink.service.HealthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class HealthServiceImpl implements HealthService {
    @Value("${consul.debug-config.bind-address}")
    String address;
    @Value("${server.port}")
    Integer port;

    @Autowired
    ServiceInstanceMapper serviceInstanceMapper;
    @Autowired
    NodeMapper nodeMapper;
    @Autowired
    CheckMapper checkMapper;
    @Autowired
    ServiceTagMapper serviceTagMapper;
    @Autowired
    HttpClient httpClient;
    @Autowired
    Gson gson;

    @Override
    public List<com.ecwid.consul.v1.health.model.HealthService> getHealthServices(String service) {
        List<com.ecwid.consul.v1.health.model.HealthService> list = new ArrayList<>();
        //GET请求调用集群中每一个节点/v1/health/service/my/{service}，返回节点的healthServiceList，加入到list中
        List<Node> nodeList = nodeMapper.selectAll();
        for(Node node : nodeList){
            String url = "http://"+node.getAddress()+":"+node.getPort()+"/v1/health/service/my/"+service;

            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = null;
            try {
                response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();

                if(entity!=null){
                    String json = EntityUtils.toString(entity);
                    List listFromJson = gson.fromJson(json, List.class);
                    list.addAll(listFromJson);
                }

            } catch (IOException e) {
                log.warn("[Health] Connect to "+url+" failed : Connection refused: connect");
            }
        }


        return list;
    }

    @Override
    public List<com.ecwid.consul.v1.health.model.HealthService> getMyHealthServices(String service) {
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

            Node node = nodeMapper.selectByAddressAndPort(address,port);
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
