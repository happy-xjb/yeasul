package com.yealink.service;

import com.ecwid.consul.v1.agent.model.Check;
import com.ecwid.consul.v1.agent.model.NewCheck;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.agent.model.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface AgentService {
    Map<String, Service> getAgentServices();

    void agentServiceRegister(NewService newService);

    void agentServiceDeregister(String serviceId);

    Map<String, Check> getAgentChecks();

    void agentCheckRegister(NewCheck newCheck);

    void agentCheckDeregister(String checkId);
}
