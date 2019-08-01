package com.yealink.service;

import com.ecwid.consul.v1.agent.model.Check;
import com.ecwid.consul.v1.agent.model.NewCheck;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.agent.model.Service;
import com.yealink.entities.Node;

import java.util.List;
import java.util.Map;

public interface AgentService {
    Map<String, Service> getAgentServices();

    void agentServiceRegister(NewService newService);

    void agentServiceDeregister(String serviceId);

    Map<String, Check> getAgentChecks();

    void agentCheckRegister(NewCheck newCheck);

    void agentJoin(String address, int port);

    void agentJoin(String address);

    void sendAllNodesInClusterTo(Node node);

    void acceptNewNode(Node node);

    void copyNodeListToMyself(List<Node> nodeList);

    /**
     * 向数据库Node表中插入一条数据
     * @param node
     */
    void insertNewNode(Node node);
}
