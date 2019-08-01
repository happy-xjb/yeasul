package com.yealink.controller.agent;

import com.ecwid.consul.v1.agent.model.Check;
import com.ecwid.consul.v1.agent.model.NewCheck;
import com.yealink.controller.AgentController;
import com.yealink.service.AgentService;
import com.yealink.utils.CheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AgentCheckController extends AgentController {
    @Autowired
    AgentService agentService;

    @Autowired
    CheckUtil checkUtil;

    @GetMapping("/checks")
    public Map<String, Check> getAgentChecks(){
        return agentService.getAgentChecks();
    }

    @PutMapping("/check/register")
    public void agentCheckRegister(@RequestBody NewCheck newCheck){
        agentService.agentCheckRegister(newCheck);
    }

}
