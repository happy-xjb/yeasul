package com.yealink.ui.service.impl;

import com.google.gson.Gson;
import com.yealink.dao.*;
import com.yealink.entities.Check;
import com.yealink.entities.Node;
import com.yealink.entities.ServiceInstance;
import com.yealink.ui.service.UiService;
import com.yealink.ui.vo.NodeAndCheckVO;
import com.yealink.ui.vo.ServiceAndCheckVO;
import com.yealink.ui.vo.ServiceVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
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
    HttpClient httpClient;

    @Autowired
    NodeMapper nodeMapper;
    @Autowired
    Gson gson;

    @Override
    public Map<String, ServiceVO> getAllServiceDetails() {
        List<Node> nodeList = nodeMapper.selectAll();
        Map<String,ServiceVO> map = new HashMap<>();
        Set<String> serviceNameSet = getAllServices();
        for(String serviceName : serviceNameSet){
            ServiceVO serviceVO = new ServiceVO();

            Set<String> tags = new HashSet<>();
            //查询每个节点本服务的tags
            for(Node node : nodeList){
                String url = "http://"+node.getAddress()+":"+node.getPort()+"/ui/service/myTags/"+serviceName;
                HttpGet httpGet = new HttpGet(url);
                HttpResponse response = null;
                try {
                    response = httpClient.execute(httpGet);
                    HttpEntity entity = response.getEntity();
                    String json = EntityUtils.toString(entity);
                    List tagList = gson.fromJson(json, List.class);
                    tags.addAll(tagList);
                } catch (IOException e) {
                    log.warn("[UI] GET "+url+" failed. Cause: Connection refused.");
                    e.printStackTrace();
                }
            }

            //对于每个服务实例，都要有对应的NodeAndCheckVO
            List<NodeAndCheckVO> nodeAndCheckVOList = new ArrayList<>();
            for(Node node : nodeList){
                String url = "http://"+node.getAddress()+":"+node.getPort()+"/ui/service/myChecks/"+serviceName;
                HttpGet httpGet = new HttpGet(url);
                HttpResponse response = null;
                try {
                    response = httpClient.execute(httpGet);
                    HttpEntity entity = response.getEntity();
                    String json = EntityUtils.toString(entity);
                    List<List<Map>> list = gson.fromJson(json, List.class);
                    for(List<Map> checkList : list){
                        NodeAndCheckVO nodeAndCheckVO = new NodeAndCheckVO();
                        nodeAndCheckVO.setNode(node);
                        nodeAndCheckVO.setCheckList(checkList);
                        nodeAndCheckVOList.add(nodeAndCheckVO);
                    }
                } catch (IOException e) {
                    log.warn("[UI] GET "+url+" failed. Cause: Connection refused.");
                    e.printStackTrace();
                }
            }

            serviceVO.setTags(tags);
            serviceVO.setNodeAndCheckVOList(nodeAndCheckVOList);

            map.put(serviceName,serviceVO);
        }

//        List<String> serviceNameList = serviceNameMapper.selectAll();
//        for(String serviceName:serviceNameList){
//            ServiceVO serviceVO = new ServiceVO();
//            serviceVO.setTags(serviceTagMapper.selectByServiceName(serviceName));
//            List<ServiceAndCheckVO> serviceAndCheckVOList = new ArrayList<>();
//            List<ServiceInstance> serviceInstanceList = serviceInstanceMapper.selectByServiceName(serviceName);
//            for(ServiceInstance serviceInstance : serviceInstanceList){
//                ServiceAndCheckVO serviceAndCheckVO = new ServiceAndCheckVO();
//                serviceAndCheckVO.setServiceInstance(serviceInstance);
//                serviceAndCheckVO.setChecks(checkMapper.selectByServiceId(serviceInstance.getServiceInstanceId()));
//                serviceAndCheckVO.setTags(serviceTagMapper.selectByServiceId(serviceInstance.getServiceInstanceId()));
//                serviceAndCheckVOList.add(serviceAndCheckVO);
//            }
//            serviceVO.setServiceAndCheckVOList(serviceAndCheckVOList);
//            map.put(serviceName,serviceVO);
//        }
        return map;
    }

    @Override
    public Set<String> getAllServices() {
        Set<String> serviceSet = new HashSet<>();
        //查询每个节点的service_name表，返回所有记录，并放入serviceSet中
        List<Node> nodeList = nodeMapper.selectAll();
        for(Node node : nodeList){
            String url = "http://"+node.getAddress()+":"+node.getPort()+"/ui/myServices";
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = null;
            try {
                response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                List<String> serviceList = gson.fromJson(json, List.class);
                System.out.println(node.getPort()+" "+serviceList);
                for(String serviceName : serviceList){
                    serviceSet.add(serviceName);
                }
            } catch (IOException e) {
                log.warn("[UI] GET "+url+" failed. Cause: Connection refused.");
                e.printStackTrace();
            }
        }


        return serviceSet;
    }

    @Override
    public List<String> getMyServices() {
        return serviceNameMapper.selectAll();
    }

    @Override
    public List<String> getMyServiceTags(String service) {
        return serviceTagMapper.selectByServiceName(service);
    }

    @Override
    public List<List<Check>> getMyServiceChecks(String service) {
        List<List<Check>> list = new ArrayList<>();
        List<ServiceInstance> serviceInstanceList = serviceInstanceMapper.selectByServiceName(service);
        for(ServiceInstance serviceInstance : serviceInstanceList){
            List<Check> checkList = checkMapper.selectByServiceId(serviceInstance.getServiceInstanceId());
            list.add(checkList);
        }
        return list;
    }
}
