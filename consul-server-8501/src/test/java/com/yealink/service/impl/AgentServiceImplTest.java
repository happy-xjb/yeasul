package com.yealink.service.impl;

import com.ecwid.consul.v1.agent.model.NewCheck;
import com.yealink.service.AgentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AgentServiceImplTest {
    @Autowired
    AgentService agentService;

    @Test
    public void getAgentServices() {
    }

    @Test
    public void agentServiceRegister() {
    }

    @Test
    public void agentServiceDeregister() {
    }

    @Test
    public void getAgentChecks() {
    }

    @Test
    public void agentCheckRegister() {
        NewCheck newCheck = new NewCheck();
        newCheck.setTcp("127.0.0.1:8001");
        newCheck.setId("tcp check test id1");
        newCheck.setInterval("5s");
        newCheck.setTimeout("5s");
        agentService.agentCheckRegister(newCheck);
    }
}