package com.yealink.controller;

import com.ecwid.consul.v1.health.model.HealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/health")
public class HealthController {
    @Autowired
    com.yealink.service.HealthService healthService;

    @GetMapping("/service/{service}")
    public List<HealthService> getHealthServices(@PathVariable String service){
        List<HealthService> healthServiceList = healthService.getHealthServices(service);
        System.out.println(healthServiceList);
        return healthServiceList;
    }

    @GetMapping("/service/my/{service}")
    public List<HealthService> getMyHealthServices(@PathVariable String service){
        List<HealthService> healthServiceList = healthService.getMyHealthServices(service);
        System.out.println();
        return healthServiceList;
    }
}
