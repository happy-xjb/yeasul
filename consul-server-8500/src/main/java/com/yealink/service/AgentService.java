package com.yealink.service;

import com.ecwid.consul.v1.agent.model.Check;
import com.ecwid.consul.v1.agent.model.NewCheck;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.agent.model.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface AgentService {
    Map<String, Service> getAgentServices();

    @Transactional
    void agentServiceRegister(NewService newService);

    @Transactional
    void agentServiceDeregister(String serviceId);

    Map<String, Check> getAgentChecks();

    @Transactional
    void agentCheckRegister(NewCheck newCheck);
}
