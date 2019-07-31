package com.yealink.service;

import java.util.List;

public interface HealthService {
    List<com.ecwid.consul.v1.health.model.HealthService> getHealthServices(String service);
}
