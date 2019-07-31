package com.yealink.service;

import java.util.List;

public interface HealthService {
    //返回的是集群中所有健康的服务
    List<com.ecwid.consul.v1.health.model.HealthService> getHealthServices(String service);

    //返回的是当前节点所有健康的服务
    List<com.ecwid.consul.v1.health.model.HealthService> getMyHealthServices(String service);
}
